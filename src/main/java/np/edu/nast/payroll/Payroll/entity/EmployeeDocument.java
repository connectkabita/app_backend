package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_document")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmployeeDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer documentId;

    @ManyToOne
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    private String documentType;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private LocalDate issueDate;
    @Column(nullable = false)
    private LocalDate expiryDate;
    @Column(nullable = false)
    // storing path; change to @Lob byte[] if storing file content
    private String filePath;
    @Column(nullable = false)
    private String contentType;

    @ManyToOne
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;
    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadedAt;
}
