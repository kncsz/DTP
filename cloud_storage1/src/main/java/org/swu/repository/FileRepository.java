package org.swu.repository;

import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.swu.object.File;
import org.swu.object.FileStats;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Integer> {
    /**
     * 根据路径查询文件
     *
     * @param path 文件路径
     * @return List<File></File> 对象
     */
    @Query("SELECT f FROM File f WHERE f.path = :path")
    List<File> findAllByPath(@Param("path") String path);

    /**
     * 根据路径查询文件
     *
     * @param path 文件路径
     * @return File 对象
     */
    @Query("SELECT f FROM File f WHERE f.path = :path")
    File findByPath(@Param("path") String path);
    /**
     * 根据用户 ID 查询统计信息
     *
     * @param userid 用户 ID
     * @param regex 用户名模糊搜索s
     * @return List<File></File> 对象
     */
    // 使用正则表达式进行模糊匹配
    @Query("SELECT f FROM File f WHERE f.name LIKE %:regex% AND f.userid = :userid")
    List<File> findByNameLikeRegex(@Param("regex") String regex, @Param("userid")String userid);

    /**
     * 根据用户ID查询文件状态
     *
     * @param userid 用户 ID
     * @return FileStats 对象
     */
    @Query("SELECT f FROM FileStats  f WHERE f.userid = :userid")
    FileStats findByUserId(String userid);

    /**
     * 存储文件信息
     * @return FileStats 对象
     */
    @NotNull
    @Transactional
    @Modifying
    File save(File file);

    @NotNull
    @Transactional
    @Modifying
    FileStats save(FileStats filestats);

    /**
     * 查询文件是否在回收站
     *
     * @param dirName 父目录路径
     * @param fileName 文件名
     * @param b true or false
     * @param deletetime 删除时间
     */
    @Transactional
    @Modifying
    @Query("UPDATE File f SET f.name = :name, f.intrash = :intrash, f.deletetime = :deletetime WHERE f.path = :path")
    void updateIntrashStatus(@Param("path") String dirName, @Param("name") String fileName, @Param("intrash") boolean b,
                             @Param("deletetime")String deletetime);

    /**
     * 更新文件夹大小
     *
     * @param size 文件大小
     * @param dirName 父目录路径
     * @param name 文件名
     * @param type 文件类型
     */
    @Transactional
    @Modifying
    @Query("UPDATE File f SET f.size = f.size + :size WHERE f.path = :path AND f.type = :type AND f.name = :name")
    void updateByPathAndNameAndType(@Param("size") long size, @Param("path") String dirName, @Param("type") String type,
                                    @Param("name") String name);

    /**
     * 删除文件
     *
     * @param dirName 父目录路径
     * @param fileName 文件名
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM File f WHERE f.path = :path AND f.name = :name")
    void deleteFile(@Param("path") String dirName, @Param("name") String fileName);

    @Transactional
    @Modifying
    @Query("DELETE FROM File f WHERE f.path LIKE %:path%")
    void deleteDirectory(@Param("path") String path);

    @Transactional
    @Modifying
    @Query("UPDATE FileStats f SET f.total_size = f.total_size + :total_size, f.total_files  = f.total_files + :total_files," +
            "f.total_uploads = f.total_uploads + :total_uploads, f.total_images = f.total_images + :total_images " +
            "WHERE f.userid = :userid")
    void updateFileStatsWithImage(@Param("userid") String userid, @Param("total_size") long total_size,
                                  @Param("total_files") int total_files, @Param("total_uploads") int total_uploads,
                                  @Param("total_images") int total_images);

    @Transactional
    @Modifying
    @Query("UPDATE FileStats f SET f.total_size = f.total_size + :total_size, f.total_files  = f.total_files + :total_files," +
            "f.total_uploads = f.total_uploads + :total_uploads, f.total_videos = f.total_videos + :total_videos " +
            "WHERE f.userid = :userid")
    void updateFileStatsWithVideo(@Param("userid") String userid, @Param("total_size") long total_size,
                                  @Param("total_files") int total_files, @Param("total_uploads") int total_uploads,
                                  @Param("total_videos") int total_videos);

    @Transactional
    @Modifying
    @Query("UPDATE FileStats f SET f.total_size = f.total_size + :total_size, f.total_files  = f.total_files + :total_files," +
            "f.total_uploads = f.total_uploads + :total_uploads, f.total_audios = f.total_audios + :total_audios " +
            "WHERE f.userid = :userid")
    void updateFileStatsWithAudio(@Param("userid") String userid, @Param("total_size") long total_size,
                                  @Param("total_files") int total_files, @Param("total_uploads") int total_uploads,
                                  @Param("total_audios") int total_audios);

    @Transactional
    @Modifying
    @Query("UPDATE FileStats f SET f.total_size = f.total_size + :total_size, f.total_files  = f.total_files + :total_files," +
            "f.total_uploads = f.total_uploads + :total_uploads, f.total_documents = f.total_documents + :total_documents " +
            "WHERE f.userid = :userid")
    void updateFileStatsWithDoc(@Param("userid") String userid, @Param("total_size") long total_size,
                                @Param("total_files") int total_files, @Param("total_uploads") int total_uploads,
                                @Param("total_documents") int total_documents);

    @Transactional
    @Modifying
    @Query("UPDATE FileStats f SET f.total_size = f.total_size + :total_size, f.total_files  = f.total_files + :total_files," +
            "f.total_uploads = f.total_uploads + :total_uploads, f.total_others = f.total_others + :total_others " +
            "WHERE f.userid = :userid")
    void updateFileStatsWithOther(@Param("userid") String userid, @Param("total_size") long total_size,
                                  @Param("total_files") int total_files, @Param("total_uploads") int total_uploads,
                                  @Param("total_others") int total_others);
    @Transactional
    @Modifying
    @Query("UPDATE FileStats f SET f.total_downloads = f.total_downloads + :total_downloads WHERE f.userid = :userid")
    void updateFileStatsWithDownload(@Param("userid") String userid, @Param("total_downloads") int total_downloads);

    @Transactional
    @Modifying
    @Query("UPDATE File f SET f.userid = :newUserId WHERE f.userid = :oldUserId")
    void updateFileUserId(@Param("newUserId") String newUserId, @Param("oldUserId") String oldUserId);

    @Transactional
    @Modifying
    @Query("UPDATE FileStats f SET f.userid = :newUserId WHERE f.userid = :oldUserId")
    void updateFileStatsUserId(@Param("newUserId") String newUserId, @Param("oldUserId") String oldUserId);

    @Transactional
    @Modifying
    @Query("UPDATE FileStats f SET f.total_size = f.total_size - :total_size, f.total_files  = f.total_files - :total_files," +
            "f.total_images = f.total_images - :total_images " +
            "WHERE f.userid = :userid")
    void updateFileStatsWithImage1(@Param("userid") String userid, @Param("total_size") long total_size,
                                   @Param("total_files") int total_files, @Param("total_images") int total_images);

    @Transactional
    @Modifying
    @Query("UPDATE FileStats f SET f.total_size = f.total_size - :total_size, f.total_files  = f.total_files - :total_files," +
            "f.total_videos = f.total_videos - :total_videos " +
            "WHERE f.userid = :userid")
    void updateFileStatsWithVideo1(@Param("userid") String userid, @Param("total_size") long total_size,
                                   @Param("total_files") int total_files, @Param("total_videos") int total_videos);

    @Transactional
    @Modifying
    @Query("UPDATE FileStats f SET f.total_size = f.total_size - :total_size, f.total_files  = f.total_files - :total_files," +
            "f.total_audios = f.total_audios - :total_audios " +
            "WHERE f.userid = :userid")
    void updateFileStatsWithAudio1(@Param("userid") String userid, @Param("total_size") long total_size,
                                   @Param("total_files") int total_files, @Param("total_audios") int total_audios);

    @Transactional
    @Modifying
    @Query("UPDATE FileStats f SET f.total_size = f.total_size - :total_size, f.total_files  = f.total_files - :total_files," +
            "f.total_documents = f.total_documents - :total_documents " +
            "WHERE f.userid = :userid")
    void updateFileStatsWithDoc1(@Param("userid") String userid, @Param("total_size") long total_size,
                                 @Param("total_files") int total_files, @Param("total_documents") int total_documents);

    @Transactional
    @Modifying
    @Query("UPDATE FileStats f SET f.total_size = f.total_size - :total_size, f.total_files  = f.total_files - :total_files," +
            "f.total_others = f.total_others - :total_others " +
            "WHERE f.userid = :userid")
    void updateFileStatsWithOther1(@Param("userid") String userid, @Param("total_size") long total_size,
                                   @Param("total_files") int total_files, @Param("total_others") int total_others);
}
