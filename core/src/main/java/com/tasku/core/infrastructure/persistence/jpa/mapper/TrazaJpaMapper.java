package com.tasku.core.infrastructure.persistence.jpa.mapper;

import com.tasku.core.domain.model.TrazaActividad;
import com.tasku.core.domain.model.Email;
import com.tasku.core.domain.model.TableroUrl;
import com.tasku.core.infrastructure.persistence.jpa.entity.TableroJpaEntity;
import com.tasku.core.infrastructure.persistence.jpa.entity.TrazaJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class TrazaJpaMapper {
    public TrazaJpaEntity toJpa(TrazaActividad domain, TableroJpaEntity boardEntity) {
        TrazaJpaEntity entity = new TrazaJpaEntity();
        entity.setId(domain.id());
        entity.setBoard(boardEntity);
        entity.setAuthorEmail(domain.authorEmailValue().email());
        entity.setDescription(domain.description());
        entity.setDate(domain.date());
        return entity;
    }

    public TrazaActividad toDomain(TrazaJpaEntity entity) {
        return new TrazaActividad(
                entity.getId(),
                new TableroUrl(entity.getBoard().getUrl()),
                new Email(entity.getAuthorEmail()),
                entity.getDescription(),
                entity.getDate()
        );
    }
}


