package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;


import java.time.LocalDateTime;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MovimientoTest {

    @Test
    void constructor_validaCamposNoNulos() {
        MovimientoId id = new MovimientoId();
        LocalDateTime now = LocalDateTime.now();
        Email autor = new Email("autor@test.com");
        assertThatThrownBy(() -> new Movimiento(null, now, "detalle", autor))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("id");
        assertThatThrownBy(() -> new Movimiento(id, null, "detalle", autor))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("fecha");
        assertThatThrownBy(() -> new Movimiento(id, now, "detalle", null))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("autor");
    }

    @Test
    void constructor_validaDetalleNoVacio() {
        MovimientoId id = new MovimientoId();
        LocalDateTime now = LocalDateTime.now();
        Email autor = new Email("autor@test.com");
        assertThatThrownBy(() -> new Movimiento(id, now, null, autor))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("no puede ser nulo ni vacío");
        assertThatThrownBy(() -> new Movimiento(id, now, "   ", autor))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("no puede ser nulo ni vacío");
    }

    @Test
    void constructor_conDetalleYAutor_creaValido() {
        Movimiento mov = new Movimiento("Cambió el estado", new Email("autor@test.com"));
        assertThat(mov.getId()).isNotNull();
        assertThat(mov.getAccionDetalle()).isEqualTo("Cambió el estado");
        assertThat(mov.getAutor().email()).isEqualTo("autor@test.com");
        assertThat(mov.getFechaHora()).isNotNull();
    }

    @Test
    void equals_comparaPorId() {
        MovimientoId id = new MovimientoId();
        Movimiento m1 = new Movimiento(id, LocalDateTime.now(), "detalle1", new Email("a@test.com"));
        Movimiento m2 = new Movimiento(id, LocalDateTime.now(), "detalle2", new Email("b@test.com"));
        assertThat(m1).isEqualTo(m2);
    }
}
