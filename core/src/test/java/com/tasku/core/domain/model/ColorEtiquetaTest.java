package com.tasku.core.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ColorEtiquetaTest {

    @Test
    void shouldCreateColorEtiquetaWithValidColor() {
        ColorEtiqueta color = new ColorEtiqueta("#FF5733");
        assertEquals("#FF5733", color);
    }

    @Test
    void shouldTrimWhitespaceFromColor() {
        ColorEtiqueta color = new ColorEtiqueta("  #FF5733  ");
        assertEquals("#FF5733", color);
    }

    @Test
    void shouldThrowExceptionWhenColorIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new ColorEtiqueta(null));
    }

    @Test
    void shouldThrowExceptionWhenColorIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new ColorEtiqueta("   "));
    }

    @Test
    void shouldThrowExceptionWhenColorIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new ColorEtiqueta(""));
    }

    @Test
    void shouldEqualTwoColorEtiquetasWithSameValue() {
        ColorEtiqueta color1 = new ColorEtiqueta("#FF5733");
        ColorEtiqueta color2 = new ColorEtiqueta("#FF5733");
        assertEquals(color1, color2);
    }

    @Test
    void shouldNotEqualTwoColorEtiquetasWithDifferentValues() {
        ColorEtiqueta color1 = new ColorEtiqueta("#FF5733");
        ColorEtiqueta color2 = new ColorEtiqueta("#00FF00");
        assertNotEquals(color1, color2);
    }
}
