package com.tasku.core.domain.board.port;

import com.tasku.core.domain.model.ListaTablero;

import java.util.Optional;
import java.util.UUID;

public interface ListaTableroStore {
    Optional<ListaTablero> findById(UUID listId);
}


