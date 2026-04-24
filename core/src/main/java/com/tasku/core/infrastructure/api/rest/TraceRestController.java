package com.tasku.core.infrastructure.api.rest;

import com.tasku.core.application.tablero.usecase.TrazaActividadUseCaseService;
import com.tasku.core.infrastructure.api.rest.mapper.ApiRestMapper;
import com.tasku.core.infrastructure.api.rest.response.TraceApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/traces")
public class TraceRestController {
    private final TrazaActividadUseCaseService traceService;
    private final ApiRestMapper mapper;

    public TraceRestController(TrazaActividadUseCaseService traceService, ApiRestMapper mapper) {
        this.traceService = traceService;
        this.mapper = mapper;
    }

    @GetMapping
    public List<TraceApiResponse> getBoardTraces(@RequestParam("boardUrl") String boardUrl) {
        return traceService.getBoardTraces(boardUrl).stream()
                .map(mapper::toTraceResponse)
                .toList();
    }
}
