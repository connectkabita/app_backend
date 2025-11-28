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

    private String name;
    private Double minAmount;
    private Double maxAmount;
    private Double ratePercentage;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private String description;
}
