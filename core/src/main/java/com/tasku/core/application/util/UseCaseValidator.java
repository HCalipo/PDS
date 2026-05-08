package com.tasku.core.application.util;

import com.tasku.core.domain.board.exception.DomainValidationException;

public final class UseCaseValidator {

    private UseCaseValidator() {}

    public static String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainValidationException(message);
        }
        return value.trim();
    }
}
