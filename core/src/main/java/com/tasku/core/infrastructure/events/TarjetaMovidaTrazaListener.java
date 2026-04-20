package com.tasku.core.infrastructure.events;

import com.tasku.core.application.board.ActivityTraceService;
import com.tasku.core.application.board.dto.RegisterTraceRequest;
import com.tasku.core.application.board.event.CardMovedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class TarjetaMovidaTrazaListener {
    private final ActivityTraceService activityTraceService;

    public TarjetaMovidaTrazaListener(ActivityTraceService activityTraceService) {
        this.activityTraceService = activityTraceService;
    }

    @EventListener
    public void onCardMoved(CardMovedEvent event) {
        String description = "Tarjeta " + event.cardId() + " movida de " + event.sourceListId() + " a "
                + event.destinationListId();
        activityTraceService.registerTrace(new RegisterTraceRequest(
                event.boardUrl(),
                event.authorEmail(),
                description,
                event.movedAt()
        ));
    }
}
