package br.com.alg.scg.domain.common.valueobject;

import java.math.BigDecimal;

/**
 * Utilitário para conversão entre diferentes unidades de medida.
 * Centraliza toda lógica de conversão para evitar erros de cálculo.
 */
public class UnitConverter {

    /**
     * Converte uma quantidade de uma unidade para outra unidade compatível.
     * 
     * @param fromQuantity Quantidade original
     * @param toUnit Unidade de destino
     * @return Nova Quantity convertida para a unidade de destino
     * @throws IllegalArgumentException se as unidades não são compatíveis para conversão
     */
    public static Quantity convert(Quantity fromQuantity, UnitMeasurement toUnit) {
        if (fromQuantity.unitMeasurement() == toUnit) {
            return fromQuantity; // Não precisa converter
        }

        // Conversões de peso
        if (isWeightUnit(fromQuantity.unitMeasurement()) && isWeightUnit(toUnit)) {
            return convertWeight(fromQuantity, toUnit);
        }

        // Conversões de volume
        if (isVolumeUnit(fromQuantity.unitMeasurement()) && isVolumeUnit(toUnit)) {
            return convertVolume(fromQuantity, toUnit);
        }

        throw new IllegalArgumentException(
            String.format("Não é possível converter de %s para %s - unidades incompatíveis", 
                fromQuantity.unitMeasurement(), toUnit)
        );
    }

    private static boolean isWeightUnit(UnitMeasurement unit) {
        return unit == UnitMeasurement.GRAMA || unit == UnitMeasurement.KILOGRAM;
    }

    private static boolean isVolumeUnit(UnitMeasurement unit) {
        return unit == UnitMeasurement.MILILITRO || unit == UnitMeasurement.LITRO;
    }

    private static Quantity convertWeight(Quantity fromQuantity, UnitMeasurement toUnit) {
        BigDecimal value = fromQuantity.value();
        UnitMeasurement fromUnit = fromQuantity.unitMeasurement();

        // Converte tudo para gramas primeiro, depois para a unidade desejada
        BigDecimal valueInGrams;
        switch (fromUnit) {
            case GRAMA -> valueInGrams = value;
            case KILOGRAM -> valueInGrams = value.multiply(new BigDecimal("1000"));
            default -> throw new IllegalArgumentException("Unidade de peso não reconhecida: " + fromUnit);
        }

        BigDecimal finalValue;
        switch (toUnit) {
            case GRAMA -> finalValue = valueInGrams;
            case KILOGRAM -> finalValue = valueInGrams.divide(new BigDecimal("1000"), 6, java.math.RoundingMode.HALF_UP);
            default -> throw new IllegalArgumentException("Unidade de peso não reconhecida: " + toUnit);
        }

        return new Quantity(finalValue, toUnit);
    }

    private static Quantity convertVolume(Quantity fromQuantity, UnitMeasurement toUnit) {
        BigDecimal value = fromQuantity.value();
        UnitMeasurement fromUnit = fromQuantity.unitMeasurement();

        // Converte tudo para mililitros primeiro, depois para a unidade desejada
        BigDecimal valueInMl;
        switch (fromUnit) {
            case MILILITRO -> valueInMl = value;
            case LITRO -> valueInMl = value.multiply(new BigDecimal("1000"));
            default -> throw new IllegalArgumentException("Unidade de volume não reconhecida: " + fromUnit);
        }

        BigDecimal finalValue;
        switch (toUnit) {
            case MILILITRO -> finalValue = valueInMl;
            case LITRO -> finalValue = valueInMl.divide(new BigDecimal("1000"), 6, java.math.RoundingMode.HALF_UP);
            default -> throw new IllegalArgumentException("Unidade de volume não reconhecida: " + toUnit);
        }

        return new Quantity(finalValue, toUnit);
    }

    /**
     * Verifica se duas unidades são compatíveis para conversão
     */
    public static boolean areCompatible(UnitMeasurement unit1, UnitMeasurement unit2) {
        return (isWeightUnit(unit1) && isWeightUnit(unit2)) || 
               (isVolumeUnit(unit1) && isVolumeUnit(unit2)) ||
               (unit1 == unit2);
    }
}