package com.tasku.ui.client.dto.request;

public record CreateListApiRequest(
    String boardUrl,
    String name,
    int cardLimit,
    String colorHex
){
}
