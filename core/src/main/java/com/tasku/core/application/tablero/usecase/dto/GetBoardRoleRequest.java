package com.tasku.core.application.tablero.usecase.dto;

import com.tasku.core.domain.model.Email;
import com.tasku.core.domain.model.TableroUrl;

public record GetBoardRoleRequest(
        TableroUrl boardUrl,
        Email email
) {
}
