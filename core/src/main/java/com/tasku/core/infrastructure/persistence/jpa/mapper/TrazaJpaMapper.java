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
    return new TrazaJpaEntity(
        domain.id(),
        boardEntity,
        domain.authorEmailValue().email(),
        domain.description(),
        domain.date()
    );
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


