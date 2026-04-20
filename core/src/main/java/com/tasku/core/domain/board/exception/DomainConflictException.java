package com.tasku.core.domain.board.exception;

public final class DomainConflictException extends RuntimeException {
    public DomainConflictException(String message) {
        super(message);
    }
}
