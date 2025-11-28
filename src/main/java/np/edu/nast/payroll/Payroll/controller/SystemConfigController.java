package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.SystemConfig;
import np.edu.nast.payroll.Payroll.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system-config")
public class SystemConfigController {

    @Autowired
    private SystemConfigService service;

    @PostMapping
    public SystemConfig createConfig(@RequestBody SystemConfig config) {
        return service.saveConfig(config);
    }

    @GetMapping
    public List<SystemConfig> getAllConfigs() {
        return service.getAllConfigs();
    }

    @GetMapping("/{id}")
    public SystemConfig getConfig(@PathVariable Integer id) {
        return service.getConfigById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteConfig(@PathVariable Integer id) {
        service.deleteConfig(id);
    }
}
