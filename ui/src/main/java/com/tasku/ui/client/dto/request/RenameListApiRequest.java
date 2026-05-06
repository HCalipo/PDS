package com.tasku.ui.client.dto.request;

public record RenameListApiRequest(
        String boardUrl,
        String name
) {
}
