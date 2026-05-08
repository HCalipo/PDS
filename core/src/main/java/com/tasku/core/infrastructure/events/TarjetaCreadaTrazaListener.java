package com.tasku.core.infrastructure.events;

import com.tasku.core.application.tablero.usecase.TrazaActividadUseCaseService;
import com.tasku.core.application.tablero.usecase.dto.RegisterTraceRequest;
import com.tasku.core.application.tablero.usecase.event.TarjetaCreadaEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TarjetaCreadaTrazaListener {

    private final TrazaActividadUseCaseService trazaService;

    public TarjetaCreadaTrazaListener(TrazaActividadUseCaseService trazaService) {
        this.trazaService = trazaService;
    }

    @EventListener
    public void onCardCreated(TarjetaCreadaEvent event) {
        String description = "Tarjeta '" + event.cardTitle() + "' creada en la lista '" + event.listName() + "'";
        trazaService.registerTrace(new RegisterTraceRequest(
                event.boardUrl(),
                event.authorEmail(),
                description,
                event.createdAt()
        ));
    }
}
