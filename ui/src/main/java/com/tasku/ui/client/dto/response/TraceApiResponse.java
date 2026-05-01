package com.tasku.ui.client.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record TraceApiResponse(UUID id, String boardUrl, String authorEmail, String description, LocalDateTime date) {}
