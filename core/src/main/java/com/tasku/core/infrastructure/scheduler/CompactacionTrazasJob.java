package com.tasku.core.infrastructure.scheduler;

import com.tasku.core.application.board.ActivityTraceService;
import com.tasku.core.infrastructure.config.CompactacionTrazasProperties;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CompactacionTrazasJob {
    private final ActivityTraceService activityTraceService;
    private final CompactacionTrazasProperties properties;

    public CompactacionTrazasJob(ActivityTraceService activityTraceService, CompactacionTrazasProperties properties) {
        this.activityTraceService = activityTraceService;
        this.properties = properties;
    }

    @Scheduled(cron = "${tasku.traces.compaction-cron:0 0 3 * * *}")
    public void compact() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(properties.getRetentionDays());
        activityTraceService.compactOlderThan(cutoffDate);
    }
}
