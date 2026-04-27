package com.tasku.core.infrastructure.events;

import com.tasku.core.application.tablero.usecase.TrazaActividadUseCaseService;
import com.tasku.core.application.tablero.usecase.dto.RegisterTraceRequest;
import com.tasku.core.application.tablero.usecase.event.TarjetaMovidaEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TarjetaMovidaTrazaListener {
    private final TrazaActividadUseCaseService TrazaActividadUseCaseService;

    public TarjetaMovidaTrazaListener(TrazaActividadUseCaseService TrazaActividadUseCaseService) {
        this.TrazaActividadUseCaseService = TrazaActividadUseCaseService;
    }

    @EventListener
    public void onCardMoved(TarjetaMovidaEvent event) {
        String description = "Tarjeta " + event.cardId().id() + " movida de " + event.sourceListId().id() + " a "
            + event.destinationListId().id();
        TrazaActividadUseCaseService.registerTrace(new RegisterTraceRequest(
                event.boardUrl(),
                event.authorEmail(),
                description,
                event.movedAt()
        ));
    }
}
