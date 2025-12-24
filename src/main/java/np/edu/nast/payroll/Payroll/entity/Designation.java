package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "designation")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Designation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer designationId;

    @Column(nullable = false)
    private String designationTitle;
}
