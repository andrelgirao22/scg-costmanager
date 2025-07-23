package br.com.alg.scg.domain.common.valueobject;

public enum UnitMeasurement {

    GRAMA("g"),
    KILOGRAM("kg"),
    UNIT("un"),
    LITRO("L"),
    MILILITRO("ml");

    private final String unit;

    UnitMeasurement(String abreviacao) {
        this.unit = abreviacao;
    }

    public String getUnit() {
        return unit;
    }

}
