package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bank")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Bank {
    @Column(nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bankId;
    @Column(nullable = false)
    private String bankName;
    @Column(nullable = false)
    private String branchName;
    @Column(nullable = false)
    private String branchCode;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private String contactNumber;
}
