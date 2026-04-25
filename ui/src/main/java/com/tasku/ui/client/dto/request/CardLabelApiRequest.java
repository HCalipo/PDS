package com.tasku.ui.client.dto.request;

public record CardLabelApiRequest(
        String name,
        String colorHex
) {
}