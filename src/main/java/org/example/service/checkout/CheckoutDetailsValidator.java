package org.example.service.checkout;

import java.time.Duration;
import lombok.NonNull;
import org.example.persistence.data.ValidationErrors;

public class CheckoutDetailsValidator {
  public static ValidationErrors validateCheckoutDetails(@NonNull Duration days, int discount) {
    ValidationErrors errors = new ValidationErrors();

    if (days.toDays() < 1) {
      errors.add("The number of rental days must be 1 or greater.");
    }

    if (discount < 0 || discount > 100) {
      errors.add("The discount must be between 0 and 1");
    }

    return errors;
  }
}
