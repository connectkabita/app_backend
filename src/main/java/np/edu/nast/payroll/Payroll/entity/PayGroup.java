package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "pay_group")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PayGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer payGroupId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String frequency;
    @Column(nullable = false)
    private LocalDate nextRunDate;
}
