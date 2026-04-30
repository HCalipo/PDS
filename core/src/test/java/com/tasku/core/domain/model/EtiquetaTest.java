package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EtiquetaTest {

    @Test
    void constructor_conTextoYColor_creaEtiquetaValida() {
        ColorEtiqueta color = new ColorEtiqueta("red");
        Etiqueta etiqueta = new Etiqueta("Bug", color);
        assertThat(etiqueta.getTexto()).isEqualTo("Bug");
        assertThat(etiqueta.getColor()).isEqualTo(color);
        assertThat(etiqueta.getId()).isNotNull();
    }

    @Test
    void constructor_conIdExplicito_creaEtiquetaValida() {
        EtiquetaId id = new EtiquetaId();
        ColorEtiqueta color = new ColorEtiqueta("blue");
        Etiqueta etiqueta = new Etiqueta(id, "Feature", color);
        assertThat(etiqueta.getId()).isEqualTo(id);
        assertThat(etiqueta.getTexto()).isEqualTo("Feature");
    }

    @Test
    void constructor_lanzaExcepcion_textoNull() {
        ColorEtiqueta color = new ColorEtiqueta("red");
        assertThatThrownBy(() -> new Etiqueta(null, color))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("texto");
    }

    @Test
    void constructor_lanzaExcepcion_textoBlank() {
        ColorEtiqueta color = new ColorEtiqueta("red");
        assertThatThrownBy(() -> new Etiqueta("   ", color))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("texto");
    }

    @Test
    void constructor_lanzaExcepcion_colorNull() {
        assertThatThrownBy(() -> new Etiqueta("Texto", null))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("color");
    }

    @Test
    void constructor_lanzaExcepcion_idNull() {
        ColorEtiqueta color = new ColorEtiqueta("red");
        assertThatThrownBy(() -> new Etiqueta(null, "Texto", color))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("id");
    }

    @Test
    void getters_retornanValoresCorrectos() {
        EtiquetaId id = new EtiquetaId();
        ColorEtiqueta color = new ColorEtiqueta("green");
        Etiqueta etiqueta = new Etiqueta(id, "Improvement", color);
        assertThat(etiqueta.getId()).isEqualTo(id);
        assertThat(etiqueta.getTexto()).isEqualTo("Improvement");
        assertThat(etiqueta.getColor()).isEqualTo(color);
    }

    @Test
    void actualizarTexto_cambiaTexto() {
        Etiqueta etiqueta = new Etiqueta("Old", new ColorEtiqueta("red"));
        etiqueta.actualizarTexto("New");
        assertThat(etiqueta.getTexto()).isEqualTo("New");
    }

    @Test
    void actualizarTexto_lanzaExcepcion_textoInvalido() {
        Etiqueta etiqueta = new Etiqueta("Texto", new ColorEtiqueta("red"));
        assertThatThrownBy(() -> etiqueta.actualizarTexto(null))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("texto");
        assertThatThrownBy(() -> etiqueta.actualizarTexto("   "))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("texto");
    }

    @Test
    void actualizarColor_cambiaColor() {
        Etiqueta etiqueta = new Etiqueta("Bug", new ColorEtiqueta("red"));
        ColorEtiqueta newColor = new ColorEtiqueta("blue");
        etiqueta.actualizarColor(newColor);
        assertThat(etiqueta.getColor()).isEqualTo(newColor);
    }

    @Test
    void actualizarColor_lanzaExcepcion_colorNull() {
        Etiqueta etiqueta = new Etiqueta("Bug", new ColorEtiqueta("red"));
        assertThatThrownBy(() -> etiqueta.actualizarColor(null))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("color");
    }

    @Test
    void equals_comparaPorId() {
        EtiquetaId id = new EtiquetaId();
        ColorEtiqueta color = new ColorEtiqueta("red");
        Etiqueta e1 = new Etiqueta(id, "Same", color);
        Etiqueta e2 = new Etiqueta(id, "Same", color);
        assertThat(e1).isEqualTo(e2);
    }

    @Test
    void equals_diferentePorId() {
        ColorEtiqueta color = new ColorEtiqueta("red");
        Etiqueta e1 = new Etiqueta("One", color);
        Etiqueta e2 = new Etiqueta("Two", color);
        assertThat(e1).isNotEqualTo(e2);
    }

    @Test
    void hashCode_basadoEnId() {
        EtiquetaId id = new EtiquetaId();
        Etiqueta e1 = new Etiqueta(id, "Test", new ColorEtiqueta("red"));
        Etiqueta e2 = new Etiqueta(id, "Test", new ColorEtiqueta("red"));
        assertThat(e1.hashCode()).isEqualTo(e2.hashCode());
    }
}
