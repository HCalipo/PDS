package com.tasku.core.infrastructure.persistence.jpa.adapter;

import com.tasku.core.domain.board.exception.DomainNotFoundException;
import com.tasku.core.domain.model.board.Tarjeta;
import com.tasku.core.domain.board.port.TarjetaStore;
import com.tasku.core.infrastructure.persistence.jpa.entity.ListaTableroJpaEntity;
import com.tasku.core.infrastructure.persistence.jpa.mapper.TarjetaJpaMapper;
import com.tasku.core.infrastructure.persistence.jpa.repository.SpringDataListaTableroRepository;
import com.tasku.core.infrastructure.persistence.jpa.repository.SpringDataTarjetaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaTarjetaStoreAdapter implements TarjetaStore {
    private final SpringDataTarjetaRepository repository;
    private final SpringDataListaTableroRepository listaTableroRepository;
    private final TarjetaJpaMapper mapper;

    public JpaTarjetaStoreAdapter(SpringDataTarjetaRepository repository,
                               SpringDataListaTableroRepository listaTableroRepository,
                               TarjetaJpaMapper mapper) {
        this.repository = repository;
        this.listaTableroRepository = listaTableroRepository;
        this.mapper = mapper;
    }

    @Override
    public Tarjeta save(Tarjeta tarjeta) {
        ListaTableroJpaEntity listEntity = listaTableroRepository.findById(tarjeta.listId())
                .orElseThrow(() -> new DomainNotFoundException("No existe la lista para persistir la tarjeta"));
        return mapper.toDomain(repository.save(mapper.toJpa(tarjeta, listEntity)));
    }

    @Override
    public Optional<Tarjeta> findById(UUID cardId) {
        return repository.findById(cardId).map(mapper::toDomain);
    }

    @Override
    public long countByListId(UUID listId) {
        return repository.countByListId(listId);
    }

    @Override
    public List<Tarjeta> findByListId(UUID listId) {
        return repository.findByListId(listId).stream().map(mapper::toDomain).toList();
    }
}

