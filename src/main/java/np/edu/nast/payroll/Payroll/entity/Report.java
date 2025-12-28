package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private LocalDateTime dateGenerated;

    @Column(nullable = false)
    private String filePath;

    private String fileSize; // Professional addition: e.g., "15.4 KB"
}