package priv.cgroup.object;


import jakarta.persistence.*;

@Entity(name="Task")
@Table(name="task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;
    @Column
    private String pid;
    @Column
    private String path;
    @Column
    private String datestamp;
    @Column
    private String description;
    @Column
    private boolean status;
    @Column
    private String totalTimeOfRecentRun;

    public Task(){}

    public Task(String name, String pid, String path, String datestamp, String description, boolean status, String totalTimeOfRecentRun) {
        this.name = name;
        this.pid = pid;
        this.path = path;
        this.datestamp = datestamp;
        this.description = description;
        this.status = status;
        this.totalTimeOfRecentRun = totalTimeOfRecentRun;
    }


    public String getPid() {
        return pid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDatestamp() {
        return datestamp;
    }

    public void setDatestamp(String datestamp) {
        this.datestamp = datestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getTotalTimeOfRecentRun() {
        return totalTimeOfRecentRun;
    }

    public void setTotalTimeOfRecentRun(String totalTimeOfRecentRun) {
        this.totalTimeOfRecentRun = totalTimeOfRecentRun;
    }
}
