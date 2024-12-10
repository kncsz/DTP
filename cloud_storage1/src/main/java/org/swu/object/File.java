package org.swu.object;

import jakarta.persistence.*;

@Entity(name="File")
@Table(name="file")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String userid;
    @Column
    private String path;
    @Column
    private String name;
    @Column
    private String datestamp;
    @Column
    private String lastmodified;
    @Column
    private long size;
    @Column
    private String type; //表示是文件还是目录
    @Column
    private String description;
    @Column
    private String deletetime;
    @Column
    private boolean intrash;
    @Column
    private boolean multiupload;
    public File(){};

    public File(String userid, String name, String path, String datestamp, String lastmodified, long
            size, String type, String description,
    String deletetime, boolean intrash, boolean multiupload) {
        this.userid = userid;
        this.name = name;
        this.path = path;
        this.datestamp = datestamp;
        this.lastmodified = lastmodified;
        this.size = size;
        this.type = type;
        this.description = description;
        this.deletetime = deletetime;
        this.intrash = intrash;
        this.multiupload = multiupload;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLastmodified() {
        return lastmodified;
    }

    public void setLastmodified(String lastmodified) {
        this.lastmodified = lastmodified;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDatestamp() {
        return datestamp;
    }

    public void setDatestamp(String datestamp) {
        this.datestamp = datestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public boolean isIntrash() {
        return intrash;
    }

    public void setIntrash(boolean intrash) {
        this.intrash = intrash;
    }

    public boolean isMultiupload() {
        return multiupload;
    }

    public void setMultiupload(boolean multiupload) {
        this.multiupload = multiupload;
    }

    public String getDeletetime() {
        return deletetime;
    }

    public void setDeletetime(String deletetime) {
        this.deletetime = deletetime;
    }
}
