package com.roamgram.travelDiary.domain.model.wallet;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@EqualsAndHashCode(callSuper = true)
@Embeddable
@Data
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class Amount extends Number {

    public static final Amount ZERO = new Amount(0);

    @Column(name = "amount", precision = 38, scale = 3)
    private BigDecimal value;

    public <T extends Number> Amount(final T value) {
        this.value = new BigDecimal(value.toString());
    }

    public Amount negate() {
        return new Amount(value.negate());
    }

    public <T extends Number> Amount add(final T addendValue) {
        final BigDecimal addend = new BigDecimal(addendValue.toString());
        return new Amount(value.add(addend));
    }

    public <T extends Number> Amount subtract(final T subtrahendValue) {
        final BigDecimal subtrahend = new BigDecimal(subtrahendValue.toString());
        return new Amount(value.subtract(subtrahend));
    }

    public <T extends Number> Amount multiply(final T multiplicandValue) {
        final BigDecimal multiplicand = new BigDecimal(multiplicandValue.toString());
        return new Amount(value.multiply(multiplicand));
    }

    public <T extends Number> Amount divide(final T divisorValue) {
        final BigDecimal divisor = new BigDecimal(divisorValue.toString());
        return new Amount(value.divide(divisor, 3, RoundingMode.HALF_UP));
    }

    public <T extends Number> int compareTo(final T targetValue) {
        return this.value.compareTo(new BigDecimal(targetValue.toString()));
    }

    @Override
    public int intValue() {
        return value.intValue();
    }

    @Override
    public long longValue() {
        return value.longValue();
    }

    @Override
    public float floatValue() {
        return value.floatValue();
    }

    @Override
    public double doubleValue() {
        return value.doubleValue();
    }
}
