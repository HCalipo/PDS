package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EtiquetaTarjetaTest {

    @Test
    void constructor_creaEtiquetaValida() {
        EtiquetaTarjeta etiqueta = new EtiquetaTarjeta("Urgent", "red");
        assertThat(etiqueta.name()).isEqualTo("Urgent");
        assertThat(etiqueta.colorHex()).isEqualTo("red");
    }

    @Test
    void constructor_lanzaExcepcion_nombreInvalido() {
        assertThatThrownBy(() -> new EtiquetaTarjeta(null, "red"))
                .isInstanceOf(DomainValidationException.class).hasMessageContaining("nombre");
        assertThatThrownBy(() -> new EtiquetaTarjeta("   ", "red"))
                .isInstanceOf(DomainValidationException.class).hasMessageContaining("nombre");
    }

    @Test
    void constructor_lanzaExcepcion_colorInvalido() {
        assertThatThrownBy(() -> new EtiquetaTarjeta("Name", null))
                .isInstanceOf(DomainValidationException.class).hasMessageContaining("color");
        assertThatThrownBy(() -> new EtiquetaTarjeta("Name", "   "))
                .isInstanceOf(DomainValidationException.class).hasMessageContaining("color");
    }

    @Test
    void constructor_recortaEspacios() {
        EtiquetaTarjeta etiqueta = new EtiquetaTarjeta("  Urgent  ", "  red  ");
        assertThat(etiqueta.name()).isEqualTo("Urgent");
        assertThat(etiqueta.colorHex()).isEqualTo("red");
    }
}
