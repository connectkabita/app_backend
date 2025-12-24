package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "tax_slab")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaxSlab {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer taxSlabId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Double minAmount;
    @Column(nullable = false)
    private Double maxAmount;
    @Column(nullable = false)
    private Double ratePercentage;
    @Column(nullable = false)
    private LocalDate effectiveFrom;
    @Column(nullable = false)
    private LocalDate effectiveTo;
    @Column(nullable = false)
    private String description;
}
