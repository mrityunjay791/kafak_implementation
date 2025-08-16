package org.example.service;

import org.example.dto.EmployeeDto;
import org.example.entity.Employee;
import org.example.exception.EmployeeNotFoundException;
import org.example.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    // Define methods to handle business logic related to employees
    // For example, you can add methods to get, create, update, or delete employees

    @Autowired
    private EmployeeRepository employeeRepository;

    // Example method to get all employees
    public List<EmployeeDto> getAllEmployees() {
        // Logic to retrieve all employees from the database
        List<EmployeeDto> employees = employeeRepository.findAll().stream().map(emp ->
                new EmployeeDto(emp.getId(), emp.getName(), emp.getPosition(), emp.getSalary()))
                .toList();

        if (employees.isEmpty()) {
            throw new EmployeeNotFoundException("No employees found in the database.");
        }
        return employees;
    }

    public Employee getEmployeeById(Long id) throws EmployeeNotFoundException {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
    }

    public void deleteEmployee(Long id) throws EmployeeNotFoundException {
        if (!employeeRepository.existsById(id)) {
            throw new EmployeeNotFoundException("Employee not found with id: " + id);
        }
        employeeRepository.deleteById(id);
    }

    // Example method to create a new employee
    public Employee createEmployee(Employee employee) {
        // Logic to save the new employee to the database
        return employeeRepository.save(employee);
    }
}
