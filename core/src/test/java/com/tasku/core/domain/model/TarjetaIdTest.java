package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TarjetaIdTest {

    @Test
    void shouldCreateWithValidUUID() {
        UUID uuid = UUID.randomUUID();
        TarjetaId id = new TarjetaId(uuid);
        assertEquals(uuid, id.id());
    }

    @Test
    void shouldEqualSameUUID() {
        UUID uuid = UUID.randomUUID();
        TarjetaId a = new TarjetaId(uuid);
        TarjetaId b = new TarjetaId(uuid);
        assertEquals(a, b);
    }

}
