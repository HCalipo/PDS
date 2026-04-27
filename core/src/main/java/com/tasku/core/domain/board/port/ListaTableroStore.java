package com.tasku.core.domain.board.port;

import com.tasku.core.domain.model.ListaTablero;
import com.tasku.core.domain.model.ListaTableroId;

import java.util.Optional;

public interface ListaTableroStore {
    Optional<ListaTablero> findById(ListaTableroId listId);
}


