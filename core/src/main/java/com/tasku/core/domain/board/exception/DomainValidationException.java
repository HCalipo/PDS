package com.tasku.core.domain.board.exception;

public final class DomainValidationException extends RuntimeException {
    public DomainValidationException(String message) {
        super(message);
    }
}
