package com.tasku.core.infrastructure.events;

import com.tasku.core.application.tablero.usecase.TrazaActividadUseCaseService;
import com.tasku.core.application.tablero.usecase.dto.RegisterTraceRequest;
import com.tasku.core.application.tablero.usecase.event.TarjetaMovidaEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TarjetaMovidaTrazaListener {

    private final TrazaActividadUseCaseService trazaService;

    public TarjetaMovidaTrazaListener(TrazaActividadUseCaseService trazaService) {
        this.trazaService = trazaService;
    }

    @EventListener
    public void onCardMoved(TarjetaMovidaEvent event) {
        String description = "Tarjeta '" + event.cardTitle() + "' movida de '" + event.sourceListName() + "' a '" + event.destinationListName() + "'";
        trazaService.registerTrace(new RegisterTraceRequest(
                event.boardUrl(),
                event.authorEmail(),
                description,
                event.movedAt()
        ));
    }
}
