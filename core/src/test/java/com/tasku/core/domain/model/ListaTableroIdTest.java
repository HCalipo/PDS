package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ListaTableroIdTest {

    @Test
    void shouldCreateWithValidUUID() {
        UUID uuid = UUID.randomUUID();
        ListaTableroId id = new ListaTableroId(uuid);
        assertEquals(uuid, id.id());
    }

    @Test
    void shouldEqualSameUUID() {
        UUID uuid = UUID.randomUUID();
        ListaTableroId a = new ListaTableroId(uuid);
        ListaTableroId b = new ListaTableroId(uuid);
        assertEquals(a, b);
    }

}
