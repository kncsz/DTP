package priv.cgroup.object;

import jakarta.persistence.*;
import org.springframework.web.multipart.MultipartFile;

@Entity(name="File")
@Table(name="file")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

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

    public File(){};

    public File(String name, String path, String datestamp, String lastmodified, long size, String type, String description) {
        this.name = name;
        this.path = path;
        this.datestamp = datestamp;
        this.lastmodified = lastmodified;
        this.size = size;
        this.type = type;
        this.description = description;
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
}
