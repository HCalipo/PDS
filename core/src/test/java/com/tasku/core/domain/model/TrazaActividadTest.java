package com.tasku.core.domain.model;

import com.tasku.core.domain.board.exception.DomainValidationException;
import org.junit.jupiter.api.Test;


import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TrazaActividadTest {

    private final String BOARD_URL = "tasku://tablero/" + UUID.randomUUID();
    private final String AUTHOR_EMAIL = "author@test.com";

    @Test
    void createNow_creaTrazaFechaActual() {
        TrazaActividad trace = TrazaActividad.createNow(BOARD_URL, AUTHOR_EMAIL, "Test trace");
        assertThat(trace.id()).isNotNull();
        assertThat(trace.boardUrl()).isEqualTo(BOARD_URL);
        assertThat(trace.authorEmail()).isEqualTo(AUTHOR_EMAIL);
        assertThat(trace.date()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void constructor_validaCamposNoNulos() {
        UUID id = UUID.randomUUID();
        TableroUrl url = new TableroUrl(BOARD_URL);
        Email email = new Email(AUTHOR_EMAIL);
        LocalDateTime date = LocalDateTime.now();

        assertThatThrownBy(() -> new TrazaActividad(null, url, email, "desc", date))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("id");
        assertThatThrownBy(() -> new TrazaActividad(id, null, email, "desc", date))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("url");
        assertThatThrownBy(() -> new TrazaActividad(id, url, null, "desc", date))
                .isInstanceOf(NullPointerException.class).hasMessageContaining("autor");
    }

    @Test
    void constructor_validaDescripcionNoVacia() {
        UUID id = UUID.randomUUID();
        TableroUrl url = new TableroUrl(BOARD_URL);
        Email email = new Email(AUTHOR_EMAIL);
        LocalDateTime date = LocalDateTime.now();

        assertThatThrownBy(() -> new TrazaActividad(id, url, email, null, date))
                .isInstanceOf(DomainValidationException.class).hasMessageContaining("descripcion");
        assertThatThrownBy(() -> new TrazaActividad(id, url, email, "   ", date))
                .isInstanceOf(DomainValidationException.class).hasMessageContaining("descripcion");
    }
}
