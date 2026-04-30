package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HistorialMovimientosTest {

    @Test
    void constructor_sinArgs_creaVacio() {
        HistorialMovimientos historial = new HistorialMovimientos();
        assertThat(historial.getMovimientos()).isEmpty();
    }

    @Test
    void constructor_conLista_creaValido() {
        Movimiento m1 = new Movimiento("Acción 1", new Email("a@test.com"));
        Movimiento m2 = new Movimiento("Acción 2", new Email("b@test.com"));
        HistorialMovimientos historial = new HistorialMovimientos(List.of(m1, m2));
        assertThat(historial.getMovimientos()).hasSize(2);
    }

    @Test
    void constructor_lanzaExcepcion_listaNula() {
        assertThatThrownBy(() -> new HistorialMovimientos(null))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("no puede ser nula");
    }

    @Test
    void registrar_anadeMovimiento() {
        HistorialMovimientos historial = new HistorialMovimientos();
        Movimiento mov = new Movimiento("Nueva acción", new Email("a@test.com"));
        historial.registrar(mov);
        assertThat(historial.getMovimientos()).hasSize(1);
    }

    @Test
    void registrar_lanzaExcepcion_movimientoNulo() {
        HistorialMovimientos historial = new HistorialMovimientos();
        assertThatThrownBy(() -> historial.registrar(null))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("no puede ser nulo");
    }

    @Test
    void getMovimientos_retornaCopiaInmutable() {
        HistorialMovimientos historial = new HistorialMovimientos();
        assertThatThrownBy(() -> historial.getMovimientos().add(null))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
