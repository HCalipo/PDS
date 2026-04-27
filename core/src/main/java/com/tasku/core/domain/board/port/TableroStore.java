package com.tasku.core.domain.board.port;

import com.tasku.core.domain.model.Tablero;
import com.tasku.core.domain.model.Email;
import com.tasku.core.domain.model.TableroUrl;

import java.util.List;
import java.util.Optional;

public interface TableroStore {
    Tablero save(Tablero board);

    Optional<Tablero> findByUrl(TableroUrl url);

    List<Tablero> findByOwnerEmailIgnoreCase(Email ownerEmail);

    List<Tablero> findBySharedEmailIgnoreCase(Email email);

    boolean existsByOwnerEmailAndNameIgnoreCase(Email ownerEmail, String boardName);
}


