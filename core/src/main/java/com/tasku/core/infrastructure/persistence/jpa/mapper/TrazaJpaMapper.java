package com.tasku.core.infrastructure.persistence.jpa.mapper;

import com.tasku.core.domain.model.board.TrazaActividad;
import com.tasku.core.infrastructure.persistence.jpa.entity.TableroJpaEntity;
import com.tasku.core.infrastructure.persistence.jpa.entity.TrazaJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class TrazaJpaMapper {
    public TrazaJpaEntity toJpa(TrazaActividad domain, TableroJpaEntity boardEntity) {
        TrazaJpaEntity entity = new TrazaJpaEntity();
        entity.setId(domain.id());
        entity.setBoard(boardEntity);
        entity.setAuthorEmail(domain.authorEmail());
        entity.setDescription(domain.description());
        entity.setDate(domain.date());
        return entity;
    }

    public TrazaActividad toDomain(TrazaJpaEntity entity) {
        return new TrazaActividad(
                entity.getId(),
                entity.getBoard().getUrl(),
                entity.getAuthorEmail(),
                entity.getDescription(),
                entity.getDate()
        );
    }
}

