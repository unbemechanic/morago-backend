package morago.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "file")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "interpreterProfileId")
    private InterpreterProfile interpreterProfile;

    private String fileName;
    private String fileUrl;
    private String fileType;

    private LocalDateTime uploadAt;
    @PrePersist
    public void onCreate() {
        this.uploadAt = LocalDateTime.now();
    }
}
