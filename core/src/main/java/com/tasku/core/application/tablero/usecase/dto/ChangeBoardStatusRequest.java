package com.tasku.core.application.tablero.usecase.dto;

import com.tasku.core.domain.model.EstadoTablero;
import com.tasku.core.domain.model.TableroUrl;

public record ChangeBoardStatusRequest(
        TableroUrl boardUrl,
        EstadoTablero status
) {
}

