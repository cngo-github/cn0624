package org.example.persistence.data;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public record ValidationErrors(List<String> errors) {
    public ValidationErrors() {
        this(new ArrayList<>());
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void add(String... newErrors) {
        errors.addAll(List.of(newErrors));
    }

    @Override
    public String toString() {
        return StringUtils.join(errors, ", ");
    }
}
