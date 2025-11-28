package np.edu.nast.payroll.Payroll.service;

import np.edu.nast.payroll.Payroll.entity.SystemConfig;
import java.util.List;

public interface SystemConfigService {
    SystemConfig saveConfig(SystemConfig config);
    List<SystemConfig> getAllConfigs();
    SystemConfig getConfigById(Integer id);
    void deleteConfig(Integer id);
}
