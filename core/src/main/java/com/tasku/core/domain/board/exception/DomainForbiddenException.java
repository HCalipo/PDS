package com.tasku.core.domain.board.exception;

public final class DomainForbiddenException extends RuntimeException {
    public DomainForbiddenException(String message) {
        super(message);
    }
}