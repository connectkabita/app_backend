package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.SystemConfig;
import np.edu.nast.payroll.Payroll.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system-config")
@CrossOrigin(origins = "http://localhost:5173")

public class SystemConfigController {

    @Autowired
    private SystemConfigService service;

    @PostMapping
    public SystemConfig createOrUpdateConfig(@RequestBody SystemConfig config) {
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

    @GetMapping("/key/{keyName}")
    public SystemConfig getConfigByKey(@PathVariable String keyName) {
        return service.getConfigByKey(keyName);
    }

    @DeleteMapping("/{id}")
    public void deleteConfig(@PathVariable Integer id) {
        service.deleteConfig(id);
    }
}
