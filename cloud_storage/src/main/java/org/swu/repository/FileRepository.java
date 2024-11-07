package org.swu.repository;

import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.swu.object.File;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Integer> {
    @Query("SELECT f FROM File f WHERE f.path = :path")
    List<File> findByPath(@Param("path") String path);

    @NotNull
    @Transactional
    @Modifying
    @Override
    File save(File file);
}
