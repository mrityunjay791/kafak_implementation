package org.example.service;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.example.dto.ImportResult;
import org.example.entity.Person;
import org.example.repository.PersonRepository;
import org.example.utility.SpreadsheetParser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ImportService {

    private final PersonRepository repo;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public ImportService(PersonRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public ImportResult importFile(MultipartFile file) throws IOException {
        List<Person> rows = SpreadsheetParser.parse(file);
        ImportResult res = new ImportResult();
        res.setRowsRead(rows.size());

        List<Person> toSave = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            Person p = rows.get(i);

            Set<ConstraintViolation<Person>> violations = validator.validate(p);
            if (!violations.isEmpty()) {
                res.setFailed(res.getFailed() + 1);
                res.getErrors().add("Row " + (i + 2) + ": " + violations.iterator().next().getMessage());
                continue;
            }

            Optional<Person> existing = repo.findByEmail(p.getEmail());
            if (existing.isPresent()) {
                Person e = existing.get();
                e.setFirstName(p.getFirstName());
                e.setLastName(p.getLastName());
                e.setDateOfBirth(p.getDateOfBirth());
                toSave.add(e);
                res.setUpdated(res.getUpdated() + 1);
            } else {
                toSave.add(p);
                res.setInserted(res.getInserted() + 1);
            }
        }

        if (!toSave.isEmpty()) repo.saveAll(toSave);
        return res;
    }
}