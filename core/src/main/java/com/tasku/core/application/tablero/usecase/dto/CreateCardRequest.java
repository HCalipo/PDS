package com.tasku.core.application.tablero.usecase.dto;

import com.tasku.core.domain.model.Email;
import com.tasku.core.domain.model.EtiquetaTarjeta;
import com.tasku.core.domain.model.TipoTarjeta;
import com.tasku.core.domain.model.ElementoChecklist;
import com.tasku.core.domain.model.ListaTableroId;

import java.util.List;
import java.util.Set;

public record CreateCardRequest(
        ListaTableroId listId,
        TipoTarjeta type,
        String title,
        String description,
        Set<EtiquetaTarjeta> labels,
        List<ElementoChecklist> checklistItems,
        Email authorEmail
) {
}


