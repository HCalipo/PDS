package com.tasku.core.infrastructure.persistence.jpa.repository;

import com.tasku.core.infrastructure.persistence.jpa.entity.TableroJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataTableroRepository extends JpaRepository<TableroJpaEntity, String> {
    @EntityGraph(attributePaths = {"lists", "sharedBoards"})
    List<TableroJpaEntity> findByOwnerEmailIgnoreCase(String ownerEmail);

    boolean existsByOwnerEmailIgnoreCaseAndNameIgnoreCase(String ownerEmail, String name);

    @EntityGraph(attributePaths = {"lists", "sharedBoards"})
    @Query("select distinct b from TableroJpaEntity b join b.sharedBoards s where lower(s.email) = lower(:email)")
    List<TableroJpaEntity> findSharedBoardsByEmailIgnoreCase(@Param("email") String email);

    @Override
    @EntityGraph(attributePaths = {"lists", "sharedBoards"})
    java.util.Optional<TableroJpaEntity> findById(String boardUrl);
}
