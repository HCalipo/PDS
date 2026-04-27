package com.tasku.core.application.tablero.usecase.dto;

import com.tasku.core.domain.model.Email;
import com.tasku.core.domain.model.ListaTableroId;
import com.tasku.core.domain.model.TarjetaId;

public record MoveCardRequest(
        TarjetaId cardId,
        ListaTableroId destinationListId,
        Email authorEmail
) {
}
