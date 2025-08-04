package org.example.service;

import org.example.entity.Employee;
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
    public List<Employee> getAllEmployees() {
        // Logic to retrieve all employees from the database
        return employeeRepository.findAll();
    }

    // Example method to create a new employee
    public Employee createEmployee(Employee employee) {
        // Logic to save the new employee to the database
        return employeeRepository.save(employee);
    }
}
