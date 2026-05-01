package com.tasku.ui.client.dto.request;

import java.util.UUID;

public record CompleteCardApiRequest(UUID cardId, String authorEmail) {
}
