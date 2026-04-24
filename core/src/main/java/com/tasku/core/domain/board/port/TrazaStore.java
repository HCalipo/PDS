package com.tasku.core.domain.board.port;

import com.tasku.core.domain.model.TrazaActividad;

import java.time.LocalDateTime;
import java.util.List;

public interface TrazaStore {
    TrazaActividad save(TrazaActividad trace);

    long deleteByDateBefore(LocalDateTime cutoffDate);

    List<TrazaActividad> findByBoardUrl(String boardUrl);
}


