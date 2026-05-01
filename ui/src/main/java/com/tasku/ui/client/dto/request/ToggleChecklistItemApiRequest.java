package com.tasku.ui.client.dto.request;

import java.util.UUID;

public record ToggleChecklistItemApiRequest(UUID cardId, int itemIndex, boolean completed) {}
