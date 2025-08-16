// src/main/java/org/example/exception/EmployeeNotFoundException.java
package org.example.exception;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String message) {
        super(message);
    }
}