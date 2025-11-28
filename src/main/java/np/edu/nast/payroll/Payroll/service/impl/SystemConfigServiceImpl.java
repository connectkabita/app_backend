package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.SystemConfig;
import np.edu.nast.payroll.Payroll.service.SystemConfigService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SystemConfigServiceImpl implements SystemConfigService {

    private List<SystemConfig> configs = new ArrayList<>();

    @Override
    public SystemConfig saveConfig(SystemConfig config) {
        configs.add(config);
        return config;
    }

    @Override
    public List<SystemConfig> getAllConfigs() {
        return configs;
    }

    @Override
    public SystemConfig getConfigById(Integer id) {
        return configs.stream().filter(c -> c.getConfigId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public void deleteConfig(Integer id) {
        configs.removeIf(c -> c.getConfigId().equals(id));
    }
}
