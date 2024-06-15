package org.example.persistence.data;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Price {
  public static BigDecimal from(float price) {
    BigDecimal bd = new BigDecimal(Float.toString(price));
    bd = bd.setScale(2, RoundingMode.HALF_UP);

    return bd;
  }
}
