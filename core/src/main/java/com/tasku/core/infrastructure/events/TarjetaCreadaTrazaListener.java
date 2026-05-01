package com.tasku.core.infrastructure.events;

import com.tasku.core.application.tablero.usecase.TrazaActividadUseCaseService;
import com.tasku.core.application.tablero.usecase.dto.RegisterTraceRequest;
import com.tasku.core.application.tablero.usecase.event.TarjetaCreadaEvent;
import com.tasku.core.domain.board.port.ListaTableroStore;
import com.tasku.core.domain.board.port.TarjetaStore;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TarjetaCreadaTrazaListener {
    private final TrazaActividadUseCaseService trazaService;
    private final ListaTableroStore listStore;
    private final TarjetaStore cardStore;

    public TarjetaCreadaTrazaListener(TrazaActividadUseCaseService trazaService,
                                      ListaTableroStore listStore,
                                      TarjetaStore cardStore) {
        this.trazaService = trazaService;
        this.listStore = listStore;
        this.cardStore = cardStore;
    }

    @EventListener
    public void onCardCreated(TarjetaCreadaEvent event) {
        String cardTitle = cardStore.findById(event.cardId())
                .map(c -> c.title())
                .orElse("una tarjeta");
        String listName = listStore.findById(event.listId())
                .map(l -> l.name())
                .orElse("lista desconocida");

        String description = "Tarjeta '" + cardTitle + "' creada en la lista '" + listName + "'";
        trazaService.registerTrace(new RegisterTraceRequest(
                event.boardUrl(),
                event.authorEmail(),
                description,
                event.createdAt()
        ));
    }
}
