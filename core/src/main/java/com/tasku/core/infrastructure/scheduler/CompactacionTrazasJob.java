package com.tasku.core.infrastructure.scheduler;

import com.tasku.core.application.tablero.usecase.TrazaActividadUseCaseService;
import com.tasku.core.infrastructure.config.CompactacionTrazasProperties;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CompactacionTrazasJob {
    private final TrazaActividadUseCaseService TrazaActividadUseCaseService;
    private final CompactacionTrazasProperties properties;

    public CompactacionTrazasJob(TrazaActividadUseCaseService TrazaActividadUseCaseService, CompactacionTrazasProperties properties) {
        this.TrazaActividadUseCaseService = TrazaActividadUseCaseService;
        this.properties = properties;
    }

    @Scheduled(cron = "${tasku.traces.compaction-cron:0 0 3 * * *}")
    public void compact() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(properties.getRetentionDays());
        TrazaActividadUseCaseService.compactOlderThan(cutoffDate);
    }
}
