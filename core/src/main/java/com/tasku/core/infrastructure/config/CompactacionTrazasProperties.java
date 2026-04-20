package com.tasku.core.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "tasku.traces")
public class CompactacionTrazasProperties {
    @Min(1)
    private int retentionDays = 30;

    @NotBlank
    private String compactionCron = "0 0 3 * * *";

    public int getRetentionDays() {
        return retentionDays;
    }

    public void setRetentionDays(int retentionDays) {
        this.retentionDays = retentionDays;
    }

    public String getCompactionCron() {
        return compactionCron;
    }

    public void setCompactionCron(String compactionCron) {
        this.compactionCron = compactionCron;
    }
}
