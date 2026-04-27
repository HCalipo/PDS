package com.tasku.core.infrastructure.persistence.jpa.adapter;

import com.tasku.core.domain.model.Tablero;
import com.tasku.core.domain.model.Email;
import com.tasku.core.domain.model.TableroUrl;
import com.tasku.core.domain.board.port.TableroStore;
import com.tasku.core.infrastructure.persistence.jpa.mapper.TableroJpaMapper;
import com.tasku.core.infrastructure.persistence.jpa.repository.SpringDataTableroRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaTableroStoreAdapter implements TableroStore {
    private final SpringDataTableroRepository repository;
    private final TableroJpaMapper mapper;

    public JpaTableroStoreAdapter(SpringDataTableroRepository repository, TableroJpaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Tablero save(Tablero board) {
        return mapper.toDomain(repository.save(mapper.toJpa(board)));
    }

    @Override
    public Optional<Tablero> findByUrl(TableroUrl url) {
        return repository.findById(url.value()).map(mapper::toDomain);
    }

    @Override
    public List<Tablero> findByOwnerEmailIgnoreCase(Email ownerEmail) {
        return repository.findByOwnerEmailIgnoreCase(ownerEmail.email()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Tablero> findBySharedEmailIgnoreCase(Email email) {
        return repository.findSharedBoardsByEmailIgnoreCase(email.email()).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByOwnerEmailAndNameIgnoreCase(Email ownerEmail, String boardName) {
        return repository.existsByOwnerEmailIgnoreCaseAndNameIgnoreCase(ownerEmail.email(), boardName);
    }
}


