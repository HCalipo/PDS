package com.tasku.core.application.tablero.usecase.event;

import com.tasku.core.domain.model.Email;
import com.tasku.core.domain.model.ListaTableroId;
import com.tasku.core.domain.model.TableroUrl;
import com.tasku.core.domain.model.TarjetaId;

import java.time.LocalDateTime;

public record TarjetaCreadaEvent(
        TarjetaId cardId,
        ListaTableroId listId,
        TableroUrl boardUrl,
        Email authorEmail,
        LocalDateTime createdAt
) {
}
