package np.edu.nast.payroll.Payroll.controller;

import np.edu.nast.payroll.Payroll.entity.User;
import np.edu.nast.payroll.Payroll.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService svc;

    public UserController(UserService svc) { this.svc = svc; }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) { return svc.create(user); }

    @GetMapping
    public List<User> getAll() { return svc.getAll(); }

    @GetMapping("/{id}")
    public User getById(@PathVariable Integer id) { return svc.getById(id); }

    @PutMapping("/{id}")
    public User update(@PathVariable Integer id, @RequestBody User user) { return svc.update(id, user); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) { svc.delete(id); }
}
