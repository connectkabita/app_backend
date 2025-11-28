package np.edu.nast.payroll.Payroll.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bank")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bankId;

    private String bankName;
    private String branchName;
    private String branchCode;
    private String address;
    private String contactNumber;
}
