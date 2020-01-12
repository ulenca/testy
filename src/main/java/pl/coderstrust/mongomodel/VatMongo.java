package pl.coderstrust.mongomodel;

public enum VatMongo {
    VAT_0(0.00f),
    VAT_5(0.05f),
    VAT_8(0.08f),
    VAT_23(0.23f);

    private final float value;

    VatMongo(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "VatMongo{"
                + "value=" + value
                + '}';
    }
}
