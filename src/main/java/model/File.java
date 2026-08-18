package model;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "files")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private Integer id;

    @Column(name = "name", nullable = false)
    @Expose
    private String name;

    @Column(name = "file_path", nullable = false)
    @Expose
    private String filePath;

    @OneToMany(mappedBy = "file", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Event> events = new ArrayList<>();

    public File(String name, String filePath) {
        this.name = name;
        this.filePath = filePath;
    }

    public File() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }


    public void addEvent(Event event) {
        this.events.add(event);
        event.setFile(this);
    }

    public void removeEvent(Event event) {
        this.events.remove(event);
        event.setFile(null);
    }

    @Override
    public String toString() {
        return "File{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", filePath='" + filePath + '\'' +
                ", events=" + events +
                '}';
    }
}
