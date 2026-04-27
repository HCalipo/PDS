package com.tasku.core.domain.board.port;

import com.tasku.core.domain.model.Tarjeta;
import com.tasku.core.domain.model.TarjetaId;
import com.tasku.core.domain.model.ListaTableroId;

import java.util.List;
import java.util.Optional;

public interface TarjetaStore {
    Tarjeta save(Tarjeta card);

    Optional<Tarjeta> findById(TarjetaId cardId);

    long countByListId(ListaTableroId listId);

    List<Tarjeta> findByListId(ListaTableroId listId);
}


