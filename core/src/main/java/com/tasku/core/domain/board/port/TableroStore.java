package com.tasku.core.domain.board.port;

import com.tasku.core.domain.model.Tablero;

import java.util.List;
import java.util.Optional;

public interface TableroStore {
    Tablero save(Tablero board);

    Optional<Tablero> findByUrl(String url);

    List<Tablero> findByOwnerEmailIgnoreCase(String ownerEmail);

    List<Tablero> findBySharedEmailIgnoreCase(String email);

    boolean existsByOwnerEmailAndNameIgnoreCase(String ownerEmail, String boardName);
}


