package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.

import org.example.entity.Employee;
import org.example.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    // Please insert some record on application startup into the db in employee table
    // You can use CommandLineRunner or ApplicationRunner for this purpose
    // Example:
     @Bean
     public CommandLineRunner initData(EmployeeRepository employeeRepository) {
         return args -> {
             employeeRepository.save(new Employee(null, "John Doe", "Developer", 60000));
             employeeRepository.save(new Employee(null, "Jane Smith", "Manager", 80000));
         };
     }
}