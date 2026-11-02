package ba.unze.kosnica.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usage_records")
public class UsageRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private String fileName;

    private long megabytes;

    private boolean reportedToStripe = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getMegabytes() {
        return megabytes;
    }

    public void setMegabytes(long megabytes) {
        this.megabytes = megabytes;
    }

    public boolean isReportedToStripe() {
        return reportedToStripe;
    }

    public void setReportedToStripe(boolean reportedToStripe) {
        this.reportedToStripe = reportedToStripe;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}