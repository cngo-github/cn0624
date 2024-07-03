package org.example.service.checkout;

import lombok.NonNull;
import org.example.persistence.data.ValidationErrors;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class CheckoutDetailsValidator {
    public static ValidationErrors validateCheckoutDetails(@NonNull Duration days, int discount) {
        List<String> errors = new ArrayList<>();

        if (days.toDays() < 1) {
            errors.add("The number of rental days must be 1 or greater.");
        }

        if (discount < 0 || discount > 100) {
            errors.add("The discount must be between 0 and 1");
        }

        return ValidationErrors.of(errors);
    }
}
