package com.tasku.core.application.tablero.usecase.dto;

import com.tasku.core.domain.model.ListaTableroId;
import com.tasku.core.domain.model.TableroUrl;

public record DeleteListRequest(
        TableroUrl boardUrl,
        ListaTableroId listId
) {
}
