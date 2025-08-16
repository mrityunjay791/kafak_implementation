package org.example.controller;

import org.example.dto.EmployeeDto;
import org.example.entity.Employee;
import org.example.exception.EmployeeNotFoundException;
import org.example.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    // Define methods to handle HTTP requests here
    // For example, you can add methods to get, create, update, or delete employees

    @Autowired
    private EmployeeService employeeService;

//     Example method to get all employees
     @GetMapping
     public List<EmployeeDto> getAllEmployees() throws EmployeeNotFoundException {
         return employeeService.getAllEmployees();
     }

    @GetMapping("/{id}")
    public Employee getEmployeeById(@PathVariable("id") Long id) throws EmployeeNotFoundException {
        return employeeService.getEmployeeById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteEmployee(@PathVariable("id") Long id) throws EmployeeNotFoundException {
        employeeService.deleteEmployee(id);
    }

     @PostMapping
     public Employee createEmployee(@RequestBody Employee employee) {
         return employeeService.createEmployee(employee);
     }
}
