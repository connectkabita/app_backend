package np.edu.nast.payroll.Payroll.service.impl;

import np.edu.nast.payroll.Payroll.entity.Role;
import np.edu.nast.payroll.Payroll.service.RoleService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final List<Role> roleList = new ArrayList<>();

    @Override
    public Role saveRole(Role role) {
        roleList.add(role);
        return role;
    }

    @Override
    public Role updateRole(Role role) {
        for (int i = 0; i < roleList.size(); i++) {
            if (roleList.get(i).getRoleId().equals(role.getRoleId())) {
                roleList.set(i, role);
                return role;
            }
        }
        return null;
    }

    @Override
    public void deleteRole(Integer roleId) {
        roleList.removeIf(role -> role.getRoleId().equals(roleId));
    }

    @Override
    public Role getRoleById(Integer roleId) {
        return roleList.stream()
                .filter(role -> role.getRoleId().equals(roleId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleList;
    }
}
