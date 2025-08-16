package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ImportResult {
    private int rowsRead;
    private int inserted;
    private int updated;
    private int failed;
    private List<String> errors = new ArrayList<>();
}