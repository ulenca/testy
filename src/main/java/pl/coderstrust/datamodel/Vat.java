package pl.coderstrust.datamodel;

import java.math.BigDecimal;

public enum Vat {
    VAT_0(BigDecimal.valueOf(0.00)),
    VAT_5(BigDecimal.valueOf(0.05)),
    VAT_8(BigDecimal.valueOf(0.08)),
    VAT_23(BigDecimal.valueOf(0.23));

    private BigDecimal value;

    Vat(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }
}
