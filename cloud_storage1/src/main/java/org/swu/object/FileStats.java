package org.swu.object;

import jakarta.persistence.*;

@Entity(name="FileStats")
@Table(name="filestats")
public class FileStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String userid;
    @Column
    private long total_size;
    @Column
    private int total_files;
    @Column
    private int total_uploads;
    @Column
    private int total_downloads;
    @Column
    private int total_documents;
    @Column
    private int total_images;
    @Column
    private int total_videos;
    @Column
    private int total_audios;
    @Column
    private int total_others;

    public FileStats() {

    }

    public FileStats(String userid, long total_size, int total_files, int total_uploads, int total_downloads,
                     int total_documents, int total_images, int total_videos, int total_audios, int total_others){
        this.userid = userid;
        this.total_size = total_size;
        this.total_files = total_files;
        this.total_uploads = total_uploads;
        this.total_downloads = total_downloads;
        this.total_documents = total_documents;
        this.total_images = total_images;
        this.total_videos = total_videos;
        this.total_audios = total_audios;
        this.total_others = total_others;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public long getTotal_size() {
        return total_size;
    }

    public void setTotal_size(long total_size) {
        this.total_size = total_size;
    }

    public int getTotal_files() {
        return total_files;
    }

    public void setTotal_files(int total_files) {
        this.total_files = total_files;
    }

    public int getTotal_uploads() {
        return total_uploads;
    }

    public void setTotal_uploads(int total_uploads) {
        this.total_uploads = total_uploads;
    }

    public int getTotal_downloads() {
        return total_downloads;
    }

    public void setTotal_downloads(int total_downloads) {
        this.total_downloads = total_downloads;
    }

    public int getTotal_documents() {
        return total_documents;
    }

    public void setTotal_documents(int total_documents) {
        this.total_documents = total_documents;
    }

    public int getTotal_images() {
        return total_images;
    }

    public void setTotal_images(int total_images) {
        this.total_images = total_images;
    }

    public int getTotal_videos() {
        return total_videos;
    }

    public void setTotal_videos(int total_videos) {
        this.total_videos = total_videos;
    }

    public int getTotal_audios() {
        return total_audios;
    }

    public void setTotal_audios(int total_audios) {
        this.total_audios = total_audios;
    }

    public int getTotal_others() {
        return total_others;
    }

    public void setTotal_others(int total_others) {
        this.total_others = total_others;
    }
}
