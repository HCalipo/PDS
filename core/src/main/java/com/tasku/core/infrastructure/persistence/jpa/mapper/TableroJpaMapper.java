package com.tasku.core.infrastructure.persistence.jpa.mapper;

import com.tasku.core.domain.model.Tablero;
import com.tasku.core.domain.model.TableroUrl;
import com.tasku.core.domain.model.ListaTablero;
import com.tasku.core.domain.model.ListaTableroId;
import com.tasku.core.domain.model.TableroCompartido;
import com.tasku.core.domain.model.EstadoTablero;
import com.tasku.core.domain.model.Email;
import com.tasku.core.domain.model.RolComparticion;
import com.tasku.core.infrastructure.persistence.jpa.entity.TableroJpaEntity;
import com.tasku.core.infrastructure.persistence.jpa.entity.ListaTableroJpaEntity;
import com.tasku.core.infrastructure.persistence.jpa.entity.TableroCompartidoJpaEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class TableroJpaMapper {
    public TableroJpaEntity toJpa(Tablero domain) {
        TableroJpaEntity entity = new TableroJpaEntity(
                domain.url(),
                domain.name(),
                domain.ownerEmail(),
                domain.color(),
                domain.description(),
                domain.status().name()
        );

        List<ListaTableroJpaEntity> listEntities = new ArrayList<>();
        for (ListaTablero list : domain.lists()) {
            ListaTableroJpaEntity listEntity = new ListaTableroJpaEntity(
                    list.id(),
                    entity,
                    list.name(),
                    list.cardLimit(),
                    list.colorHex()
            );
            listEntities.add(listEntity);
        }
        entity.replaceLists(listEntities);

        Set<TableroCompartidoJpaEntity> sharedEntities = new LinkedHashSet<>();
        for (TableroCompartido share : domain.sharedWith()) {
            TableroCompartidoJpaEntity sharedEntity = new TableroCompartidoJpaEntity(
                    share.id(),
                    entity,
                    share.email(),
                    share.role().name()
            );
            sharedEntities.add(sharedEntity);
        }
        entity.replaceSharedBoards(sharedEntities);

        return entity;
    }

    public Tablero toDomain(TableroJpaEntity entity) {
        List<ListaTablero> lists = new ArrayList<>();
        for (ListaTableroJpaEntity listEntity : entity.getLists()) {
            lists.add(new ListaTablero(
                    new ListaTableroId(listEntity.getId()),
                    new TableroUrl(listEntity.getBoard().getUrl()),
                    listEntity.getName(),
                    listEntity.getCardLimit(),
                    listEntity.getColorHex()
            ));
        }

        Set<TableroCompartido> shares = new LinkedHashSet<>();
        for (TableroCompartidoJpaEntity sharedEntity : entity.getSharedBoards()) {
            shares.add(new TableroCompartido(
                    sharedEntity.getId(),
                new TableroUrl(sharedEntity.getBoard().getUrl()),
                new Email(sharedEntity.getEmail()),
                    RolComparticion.valueOf(sharedEntity.getRole())
            ));
        }

        return new Tablero(
            new TableroUrl(entity.getUrl()),
                entity.getName(),
            new Email(entity.getOwnerEmail()),
                entity.getColor(),
                entity.getDescription(),
                EstadoTablero.valueOf(entity.getStatus()),
                lists,
                shares
        );
    }

    public ListaTablero toDomain(ListaTableroJpaEntity entity) {
        return new ListaTablero(
                new ListaTableroId(entity.getId()),
                new TableroUrl(entity.getBoard().getUrl()),
                entity.getName(),
                entity.getCardLimit(),
                entity.getColorHex()
        );
    }
}


