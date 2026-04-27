package com.tasku.core.infrastructure.persistence.jpa.mapper;

import com.tasku.core.domain.model.Tarjeta;
import com.tasku.core.domain.model.EtiquetaTarjeta;
import com.tasku.core.domain.model.TarjetaChecklist;
import com.tasku.core.domain.model.ElementoChecklist;
import com.tasku.core.domain.model.ListaTableroId;
import com.tasku.core.domain.model.TarjetaId;
import com.tasku.core.domain.model.TarjetaTarea;
import com.tasku.core.infrastructure.persistence.jpa.entity.ListaTableroJpaEntity;
import com.tasku.core.infrastructure.persistence.jpa.entity.TarjetaJpaEntity;
import com.tasku.core.infrastructure.persistence.jpa.entity.EtiquetaTarjetaEmbeddable;
import com.tasku.core.infrastructure.persistence.jpa.entity.TarjetaChecklistJpaEntity;
import com.tasku.core.infrastructure.persistence.jpa.entity.ElementoChecklistEmbeddable;
import com.tasku.core.infrastructure.persistence.jpa.entity.TarjetaTareaJpaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class TarjetaJpaMapper {
    public TarjetaJpaEntity toJpa(Tarjeta domain, ListaTableroJpaEntity listEntity) {
        TarjetaJpaEntity entity;
        if (domain instanceof TarjetaChecklist tarjetaChecklist) {
            TarjetaChecklistJpaEntity checklistEntity = new TarjetaChecklistJpaEntity();
            checklistEntity.setItems(mapItemsToJpa(tarjetaChecklist.items()));
            entity = checklistEntity;
        } else if (domain instanceof TarjetaTarea) {
            entity = new TarjetaTareaJpaEntity();
        } else {
            throw new IllegalArgumentException("Tipo de tarjeta no soportado para persistencia: " + domain.getClass().getName());
        }

        entity.setId(domain.id());
        entity.setList(listEntity);
        entity.setTitle(domain.title());
        entity.setDescription(domain.description());
        entity.setArchived(domain.archived());
        entity.setLabels(mapLabelsToJpa(domain.labels()));
        return entity;
    }

    public Tarjeta toDomain(TarjetaJpaEntity entity) {
        Set<EtiquetaTarjeta> labels = mapLabelsToDomain(entity.getLabels());
        if (entity instanceof TarjetaChecklistJpaEntity checklistEntity) {
            List<ElementoChecklist> items = mapItemsToDomain(checklistEntity.getItems());
            return new TarjetaChecklist(
                    new TarjetaId(checklistEntity.getId()),
                    new ListaTableroId(checklistEntity.getList().getId()),
                    checklistEntity.getTitle(),
                    checklistEntity.getDescription(),
                    checklistEntity.isArchived(),
                    labels,
                    items
            );
        }

        if (entity instanceof TarjetaTareaJpaEntity tarjetaTareaJpaEntity) {
            return new TarjetaTarea(
                new TarjetaId(tarjetaTareaJpaEntity.getId()),
                new ListaTableroId(tarjetaTareaJpaEntity.getList().getId()),
                tarjetaTareaJpaEntity.getTitle(),
                tarjetaTareaJpaEntity.getDescription(),
                tarjetaTareaJpaEntity.isArchived(),
                    labels
            );
        }

        throw new IllegalArgumentException("Tipo de tarjeta JPA no soportado: " + entity.getClass().getName());
    }

    private Set<EtiquetaTarjetaEmbeddable> mapLabelsToJpa(Set<EtiquetaTarjeta> labels) {
        Set<EtiquetaTarjetaEmbeddable> mapped = new LinkedHashSet<>();
        for (EtiquetaTarjeta label : labels) {
            mapped.add(new EtiquetaTarjetaEmbeddable(label.name(), label.colorHex()));
        }
        return mapped;
    }

    private Set<EtiquetaTarjeta> mapLabelsToDomain(Set<EtiquetaTarjetaEmbeddable> labels) {
        Set<EtiquetaTarjeta> mapped = new LinkedHashSet<>();
        for (EtiquetaTarjetaEmbeddable label : labels) {
            mapped.add(new EtiquetaTarjeta(label.getName(), label.getColorHex()));
        }
        return mapped;
    }

    private List<ElementoChecklistEmbeddable> mapItemsToJpa(List<ElementoChecklist> items) {
        List<ElementoChecklistEmbeddable> mapped = new ArrayList<>();
        for (ElementoChecklist item : items) {
            mapped.add(new ElementoChecklistEmbeddable(item.description(), item.completed()));
        }
        return mapped;
    }

    private List<ElementoChecklist> mapItemsToDomain(List<ElementoChecklistEmbeddable> items) {
        List<ElementoChecklist> mapped = new ArrayList<>();
        for (ElementoChecklistEmbeddable item : items) {
            mapped.add(new ElementoChecklist(item.getDescription(), item.isCompleted()));
        }
        return mapped;
    }
}


