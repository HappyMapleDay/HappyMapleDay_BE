package com.happymapleday.settlement.admin.repository.projection;

import java.math.BigInteger;
import java.time.LocalDate;

public interface DateBigIntegerValue {
    LocalDate getDate();
    BigInteger getValue();
}


