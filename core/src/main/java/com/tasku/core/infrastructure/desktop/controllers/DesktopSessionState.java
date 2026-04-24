package com.tasku.core.infrastructure.desktop.controllers;

import java.util.UUID;

public final class DesktopSessionState {
    private static String ownerEmail;
    private static String ownerName;
    private static String currentBoardUrl;
    private static UUID currentListId;

    private DesktopSessionState() {
    }

    public static synchronized void setUser(String email, String name) {
        ownerEmail = email;
        ownerName = name;
    }

    public static synchronized String getOwnerEmail() {
        return ownerEmail;
    }

    public static synchronized String getOwnerName() {
        return ownerName;
    }

    public static synchronized void setCurrentBoard(String boardUrl) {
        currentBoardUrl = boardUrl;
    }

    public static synchronized String getCurrentBoardUrl() {
        return currentBoardUrl;
    }

    public static synchronized void setCurrentListId(UUID listId) {
        currentListId = listId;
    }

    public static synchronized UUID getCurrentListId() {
        return currentListId;
    }
}