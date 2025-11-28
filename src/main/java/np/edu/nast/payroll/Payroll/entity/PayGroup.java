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
    private Integer payGroupId;

    private String name;
    private String frequency;
    private LocalDate nextRunDate;
}
