package com.tasku.core.infrastructure.persistence.jpa.adapter;

import com.tasku.core.domain.board.exception.DomainNotFoundException;
import com.tasku.core.domain.model.TableroUrl;
import com.tasku.core.domain.model.TrazaActividad;
import com.tasku.core.domain.board.port.TrazaStore;
import com.tasku.core.infrastructure.persistence.jpa.entity.TableroJpaEntity;
import com.tasku.core.infrastructure.persistence.jpa.mapper.TrazaJpaMapper;
import com.tasku.core.infrastructure.persistence.jpa.repository.SpringDataTableroRepository;
import com.tasku.core.infrastructure.persistence.jpa.repository.SpringDataTrazaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class JpaTrazaStoreAdapter implements TrazaStore {
    private final SpringDataTrazaRepository repository;
    private final SpringDataTableroRepository tableroRepository;
    private final TrazaJpaMapper mapper;

    public JpaTrazaStoreAdapter(SpringDataTrazaRepository repository,
                                SpringDataTableroRepository tableroRepository,
                                TrazaJpaMapper mapper) {
        this.repository = repository;
        this.tableroRepository = tableroRepository;
        this.mapper = mapper;
    }

    @Override
    public TrazaActividad save(TrazaActividad trace) {
        TableroJpaEntity boardEntity = tableroRepository.findById(trace.boardUrlValue().value())
                .orElseThrow(() -> new DomainNotFoundException("No existe el tablero asociado a la traza"));
        return mapper.toDomain(repository.save(mapper.toJpa(trace, boardEntity)));
    }

    @Override
    public long deleteByDateBefore(LocalDateTime cutoffDate) {
        return repository.deleteByDateBefore(cutoffDate);
    }

    @Override
    public List<TrazaActividad> findByBoardUrl(TableroUrl boardUrl) {
        return repository.findByBoardUrl(boardUrl.value()).stream().map(mapper::toDomain).toList();
    }
}


