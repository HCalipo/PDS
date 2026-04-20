package com.tasku.core.domain.board.exception;

public final class DomainNotFoundException extends RuntimeException {
    public DomainNotFoundException(String message) {
        super(message);
    }
}
