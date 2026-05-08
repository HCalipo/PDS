package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TableroUrlTest {

    @Test
    void shouldCreateFromFullUrl() {
        UUID uuid = UUID.randomUUID();
        String fullUrl = "tasku://tablero/" + uuid;
        TableroUrl url = new TableroUrl(fullUrl);
        assertEquals(fullUrl, url.value());
    }

    @Test
    void shouldThrowWhenValueIsNull() {
        assertThrows(DomainValidationException.class, () -> new TableroUrl(null));
    }

    @Test
    void shouldThrowWhenValueIsBlank() {
        assertThrows(DomainValidationException.class, () -> new TableroUrl("   "));
    }

    @Test
    void shouldNormalizeUrl() {
        UUID uuid = UUID.randomUUID();
        TableroUrl a = new TableroUrl("tasku://tablero/" + uuid);
        TableroUrl b = new TableroUrl(uuid.toString());
        assertEquals(a, b);
    }

}
