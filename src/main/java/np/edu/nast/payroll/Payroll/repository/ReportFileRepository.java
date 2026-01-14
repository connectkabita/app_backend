package np.edu.nast.payroll.Payroll.repository;

import np.edu.nast.payroll.Payroll.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReportFileRepository extends JpaRepository<Report, Long> {
    // Fetches history ordered by newest first for the UI table
    List<Report> findAllByOrderByDateGeneratedDesc();
}