package com.tasku.core.infrastructure.persistence.jpa.repository;

import com.tasku.core.infrastructure.persistence.jpa.entity.TrazaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpringDataTrazaRepository extends JpaRepository<TrazaJpaEntity, UUID> {
    long deleteByDateBefore(LocalDateTime cutoffDate);

    @Query("select t from TrazaJpaEntity t where t.board.url = :boardUrl order by t.date asc")
    List<TrazaJpaEntity> findByBoardUrl(@Param("boardUrl") String boardUrl);
}
