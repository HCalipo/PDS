package com.tasku.core.application.tablero.usecase.dto;

import com.tasku.core.domain.model.Email;
import com.tasku.core.domain.model.RolComparticion;
import com.tasku.core.domain.model.TableroUrl;

public record ShareBoardRequest(
        TableroUrl boardUrl,
        Email email,
        RolComparticion role,
        Email actorEmail
) {
    public ShareBoardRequest(TableroUrl boardUrl, Email email, RolComparticion role) {
        this(boardUrl, email, role, null);
    }
}
