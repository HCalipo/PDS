package com.tasku.core.application.tablero.usecase;

import com.tasku.core.application.tablero.usecase.dto.RegisterTraceRequest;
import com.tasku.core.application.util.UseCaseValidator;
import com.tasku.core.domain.model.TableroUrl;
import com.tasku.core.domain.model.TrazaActividad;
import com.tasku.core.domain.board.port.TrazaStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class TrazaActividadUseCaseService {

    private final TrazaStore traceStore;

    public TrazaActividadUseCaseService(TrazaStore traceStore) {
        this.traceStore = traceStore;
    }

    @Transactional
    public TrazaActividad registerTrace(RegisterTraceRequest request) {
        Objects.requireNonNull(request, "La solicitud de traza no puede ser nula");
        Objects.requireNonNull(request.boardUrl(), "La url del tablero es obligatoria");
        Objects.requireNonNull(request.authorEmail(), "El email del autor es obligatorio");
        UseCaseValidator.requireText(request.description(), "La descripcion de la traza es obligatoria");

        LocalDateTime traceDate = request.date() == null ? LocalDateTime.now() : request.date();
        TrazaActividad trace = new TrazaActividad(
                UUID.randomUUID(),
                request.boardUrl(),
                request.authorEmail(),
                request.description(),
                traceDate
        );
        return traceStore.save(trace);
    }

    @Transactional(readOnly = true)
    public List<TrazaActividad> getBoardTraces(TableroUrl boardUrl) {
        Objects.requireNonNull(boardUrl, "La url del tablero es obligatoria");
        return traceStore.findByBoardUrl(boardUrl);
    }

    @Transactional
    public long compactOlderThan(LocalDateTime cutoffDate) {
        Objects.requireNonNull(cutoffDate, "La fecha limite de compactacion no puede ser nula");
        return traceStore.deleteByDateBefore(cutoffDate);
    }
}
