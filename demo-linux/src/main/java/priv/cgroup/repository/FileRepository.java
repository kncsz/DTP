package priv.cgroup.repository;

import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import priv.cgroup.object.File;

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

    @Transactional
    @Modifying
    @Query("DELETE FROM File f WHERE f.path = :path")
    void deleteByPath(@Param("path") String path);

    @Transactional
    @Modifying
    @Query("DELETE FROM File f WHERE f.path LIKE CONCAT(:path, '%')")
    void deleteByPathPrefix(@Param("path") String path);

    @Transactional
    @Modifying
    @Query("UPDATE File f SET f.size = f.size + :size WHERE f.path = :path AND f.type = :type AND f.name = :name")
    void updateFileSizeByPathAndType(@Param("size") long size, @Param("path") String path, @Param("type") String type, @Param("name") String name);

    @Transactional
    @Modifying
    @Query("DELETE FROM File f WHERE f.path = :path AND f.name = :name AND f.type = :type")
    void deleteByPathAndNameAndType(@Param("path") String path, @Param("name") String name, @Param("type") String type);

    @Transactional
    @Modifying
    @Query("DELETE FROM File f WHERE f.path = :path AND f.name = :name")
    void deleteByPathAndName(@Param("path") String parentDir, @Param("name") String name);
}
