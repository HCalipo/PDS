package com.tasku.core.domain.board.port;

import com.tasku.core.domain.model.Tarjeta;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TarjetaStore {
    Tarjeta save(Tarjeta card);

    Optional<Tarjeta> findById(UUID cardId);

    long countByListId(UUID listId);

    List<Tarjeta> findByListId(UUID listId);
}


