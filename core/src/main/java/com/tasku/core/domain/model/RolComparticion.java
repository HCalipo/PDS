package com.tasku.core.domain.model;

public enum RolComparticion {
    VIEWER,
    EDITOR,
    ADMIN;

    public boolean canEdit() {
        return this == EDITOR || this == ADMIN;
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }
}


