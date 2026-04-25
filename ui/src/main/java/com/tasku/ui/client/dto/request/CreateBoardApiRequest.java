package com.tasku.ui.client.dto.request;

import java.util.List;

public record CreateBoardApiRequest(
        String ownerEmail,
        String name,
        String color,
        String description,
        List<InitialListApiRequest> initialLists
) {
}