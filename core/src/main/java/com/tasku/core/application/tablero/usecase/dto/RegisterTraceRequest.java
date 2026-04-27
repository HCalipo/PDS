package com.tasku.core.application.tablero.usecase.dto;

import com.tasku.core.domain.model.Email;
import com.tasku.core.domain.model.TableroUrl;

import java.time.LocalDateTime;

public record RegisterTraceRequest(
        TableroUrl boardUrl,
        Email authorEmail,
        String description,
        LocalDateTime date
) {
}
