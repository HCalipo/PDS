package com.tasku.ui.client.dto.request;

import com.tasku.ui.client.dto.EstadoTablero;

public record ChangeBoardStatusApiRequest(
    String boardUrl, 
    EstadoTablero status
) {
}
