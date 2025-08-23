package com.happymapleday.settlement.admin.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DateBigDecimalValue {
    LocalDate getDate();
    BigDecimal getValue();
}


