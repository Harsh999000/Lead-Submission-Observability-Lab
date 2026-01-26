package com.leadsubmission.observability.analysis.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadsubmission.observability.analysis.dto.DailyAnalysisReport;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

/**
 * AI Analysis Service backed by a local Ollama LLM.
 *
 * Responsibilities:
 * - Send bounded prompts to local LLM
 * - Enforce structured JSON output
 * - Parse response safely
 * - Produce immutable DailyAnalysisReport
 *
 * AI is observational only.
 */
@Service
public class AiAnalysisService {

    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    private static final String MODEL_NAME = "mistral";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AiAnalysisService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public DailyAnalysisReport analyze(LocalDate day, String prompt) {

        try {
            // ---- Build request payload ----
            Map<String, Object> payload = Map.of(
                    "model", MODEL_NAME,
                    "prompt", prompt,
                    "stream", false
            );

            String requestBody = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OLLAMA_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // ---- Call Ollama ----
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "Ollama responded with status " + response.statusCode()
                );
            }

            // ---- Parse Ollama wrapper JSON ----
            JsonNode root = objectMapper.readTree(response.body());
            String aiRaw = root.path("response").asText();

            if (aiRaw == null || aiRaw.isBlank()) {
                throw new RuntimeException("AI returned empty response");
            }

            // ---- Parse AI JSON payload ----
            JsonNode aiJson = objectMapper.readTree(aiRaw);

            String executiveSummary =
                    aiJson.path("executiveSummary").asText("No executive summary generated.");

            String userBehaviorAnalysis =
                    aiJson.path("userBehaviorAnalysis").asText("No user behavior analysis generated.");

            String systemBehaviorAnalysis =
                    aiJson.path("systemBehaviorAnalysis").asText("No system behavior analysis generated.");

            String misconfigurationSignals =
                    aiJson.path("misconfigurationSignals").asText("No misconfiguration signals detected.");

            String analysisConfidence =
                    aiJson.path("analysisConfidence").asText("Confidence level not provided.");

            String analysisScopeNote =
                    aiJson.path("analysisScopeNote").asText(
                            "This analysis is limited to a single day and aggregated system data."
                    );

            // ---- Build report ----
            return new DailyAnalysisReport(
                    day,
                    LocalDateTime.now(IST_ZONE),
                    executiveSummary,
                    userBehaviorAnalysis,
                    systemBehaviorAnalysis,
                    misconfigurationSignals,
                    analysisConfidence,
                    analysisScopeNote
            );

        } catch (Exception e) {
            // ---- Fail safe ----
            return new DailyAnalysisReport(
                    day,
                    LocalDateTime.now(IST_ZONE),

                    "AI analysis failed to execute.",

                    "Error while generating user behavior analysis: " + e.getMessage(),
                    "Error while generating system behavior analysis: " + e.getMessage(),
                    "Error while generating misconfiguration signals: " + e.getMessage(),

                    "Low confidence due to AI execution failure.",
                    "This report was generated without AI assistance due to an internal error."
            );
        }
    }
}
