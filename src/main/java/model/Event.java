package model;

import com.google.gson.annotations.Expose;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Expose
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    @Expose
    private File file;

    @Column(name = "download_date", nullable = false)
    @Expose
    private LocalDateTime downloadDate;

    public Event(User user, File file) {
        this.user = user;
        this.file = file;
        this.downloadDate = LocalDateTime.now();
    }

    public Event() {
        this.downloadDate = LocalDateTime.now();
    }

    public Event(User user, File file, LocalDateTime downloadDate) {
        this.user = user;
        this.file = file;
        this.downloadDate = downloadDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public LocalDateTime getDownloadDate() {
        return downloadDate;
    }

    public void setDownloadDate(LocalDateTime downloadDate) {
        this.downloadDate = downloadDate;
    }
}
