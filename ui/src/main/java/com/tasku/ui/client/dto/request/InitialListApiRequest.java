package com.tasku.ui.client.dto.request;

public record InitialListApiRequest(
        String name,
        int cardLimit
) {
}