package com.leadsubmission.observability.controller;

import com.leadsubmission.observability.dto.TrackVisitRequest;
import com.leadsubmission.observability.service.PageVisitLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class VisitTrackingController {

    private final PageVisitLogService pageVisitLogService;

    public VisitTrackingController(PageVisitLogService pageVisitLogService) {
        this.pageVisitLogService = pageVisitLogService;
    }

    @PostMapping("/track-visit")
    public ResponseEntity<Void> trackVisit(
            @RequestBody TrackVisitRequest request,
            HttpServletRequest httpRequest
    ) {
        String ipAddress = extractClientIp(httpRequest);
        pageVisitLogService.logVisit(ipAddress, request.getPage());

        System.out.println("TRACK_VISIT HIT | IP=" + ipAddress + " | PAGE=" + request.getPage());
        return ResponseEntity.ok().build();
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}
