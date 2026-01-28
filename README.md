Lead-Submission-Observability-Lab
Overview

Lead Submission Observability Lab simulates lead generation from multiple sources and endpoints and produces AI-based daily summaries derived from lead submission logs.

The system is built as an observability-first backend project, focusing on:

Tracking real submission behavior

Identifying failures and anomalies

Explaining system behavior using AI-assisted analysis

AI summaries are generated using Ollama with the LLaMA 3.1 model (8B parameter variant) and are strictly observational in nature.

Live Demo / Dashboard Access

Please use the link below to try the project or check the live dashboard:

https://smoke-violations-operates-reform.trycloudflare.com

Availability Note
The server is running on my personal laptop, so availability is limited.

Expected downtime:
12:30 A.M. – 8:00 A.M.

If you need to access the project during the expected downtime window, or if you want to collaborate on a project, feel free to reach out:

jhaharsh101@gmail.com

Do reach out if you want information on any part of the project.

Happy Coding!

What This Project Does
Lead Submission Tracking

Simulates lead submissions from multiple sources

Tracks attempts, successes, and failures

Captures per-user and per-day activity

Maintains immutable historical logs

Dashboard & Metrics

Overall system metrics

Day-wise submission metrics

Success and failure rates

Visual trends over recent days

AI-Based Daily Analysis

Generates date-wise summaries from stored logs

Explains user behavior patterns

Highlights system-level behavior

Detects possible misconfiguration signals

Includes confidence and scope notes for transparency

AI does not modify data or make automated decisions.

Design Philosophy

Observability before optimization

AI is advisory, not authoritative

Past data is immutable

Failures are surfaced, not hidden

System behavior must be explainable

Known Limitations (Current Phase)

The following limitations are known and intentionally accepted at this stage:

AI analysis calls can take 20–60 seconds

The system currently uses synchronous HTTP for AI execution

Long-running requests combined with tunnels or proxies may cause silent stalls

Restarting services clears stale tunnel or connection state

These are architectural constraints, not defects.

Future Improvements

Planned enhancements include:

Fire-and-forget AI analysis jobs

Persisting AI execution status in the database

UI polling for analysis completion

Ensuring no long-lived connections are held upstream

Full decoupling of AI execution from user-facing requests

System Overview (High-Level)

Backend service handles lead ingestion, logging, aggregation, and AI orchestration

AI service runs independently and is accessed via HTTP

Dashboard UI consumes backend APIs for metrics and analysis

Analysis results are persisted and reused for immutable past dates

Today’s data remains mutable and re-analyzable

The system is designed to evolve toward asynchronous, resilient analysis workflows without changing core data models.