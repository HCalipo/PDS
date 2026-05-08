package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TarjetaChecklistTest {

    @Test
    void shouldCreateChecklistCard() {
        TarjetaChecklist card = TarjetaChecklist.createNew(
                new ListaTableroId(), "Checklist", "Desc",
                Set.of(new EtiquetaTarjeta("release", "#16A34A")),
                List.of(new ElementoChecklist("Build", false))
        );
        assertNotNull(card.id());
        assertEquals(TipoTarjeta.CHECKLIST, card.type());
        assertEquals("Checklist", card.title());
        assertEquals(1, card.items().size());
        assertEquals(1, card.checklistItems().size());
    }

    @Test
    void shouldToggleItem() {
        TarjetaChecklist card = TarjetaChecklist.createNew(
                new ListaTableroId(), "Checklist", "Desc", Set.of(),
                List.of(new ElementoChecklist("Build", false))
        );
        assertFalse(card.items().get(0).completed());
        card.toggleItem(0, true);
        assertTrue(card.items().get(0).completed());
    }

}
