package org.example.persistence.data;

import lombok.Getter;

@Getter
public record Reservation(String id, Tool tool, RentalPrice price) {
}
