package br.com.alg.scg.domain.common.valueobject;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class UnitConverterTest {

    @Test
    void convert_gramToKilogram_shouldConvertCorrectly() {
        Quantity grams = new Quantity(new BigDecimal("500"), UnitMeasurement.GRAMA);
        
        Quantity result = UnitConverter.convert(grams, UnitMeasurement.KILOGRAM);
        
        assertEquals(new BigDecimal("0.500000"), result.value());
        assertEquals(UnitMeasurement.KILOGRAM, result.unitMeasurement());
    }

    @Test
    void convert_kilogramToGram_shouldConvertCorrectly() {
        Quantity kg = new Quantity(new BigDecimal("2.5"), UnitMeasurement.KILOGRAM);
        
        Quantity result = UnitConverter.convert(kg, UnitMeasurement.GRAMA);
        
        assertEquals(0, new BigDecimal("2500").compareTo(result.value()));
        assertEquals(UnitMeasurement.GRAMA, result.unitMeasurement());
    }

    @Test
    void convert_sameUnit_shouldReturnOriginal() {
        Quantity kg = new Quantity(new BigDecimal("1.0"), UnitMeasurement.KILOGRAM);
        
        Quantity result = UnitConverter.convert(kg, UnitMeasurement.KILOGRAM);
        
        assertSame(kg, result);
    }

    @Test
    void convert_incompatibleUnits_shouldThrowException() {
        Quantity weight = new Quantity(new BigDecimal("1.0"), UnitMeasurement.KILOGRAM);
        
        assertThrows(IllegalArgumentException.class, () -> {
            UnitConverter.convert(weight, UnitMeasurement.LITRO);
        });
    }

    @Test
    void areCompatible_weightUnits_shouldReturnTrue() {
        assertTrue(UnitConverter.areCompatible(UnitMeasurement.GRAMA, UnitMeasurement.KILOGRAM));
        assertTrue(UnitConverter.areCompatible(UnitMeasurement.KILOGRAM, UnitMeasurement.GRAMA));
    }

    @Test
    void areCompatible_volumeUnits_shouldReturnTrue() {
        assertTrue(UnitConverter.areCompatible(UnitMeasurement.LITRO, UnitMeasurement.MILILITRO));
        assertTrue(UnitConverter.areCompatible(UnitMeasurement.MILILITRO, UnitMeasurement.LITRO));
    }

    @Test
    void areCompatible_incompatibleUnits_shouldReturnFalse() {
        assertFalse(UnitConverter.areCompatible(UnitMeasurement.KILOGRAM, UnitMeasurement.LITRO));
        assertFalse(UnitConverter.areCompatible(UnitMeasurement.GRAMA, UnitMeasurement.UNIT));
    }

    @Test
    void convert_mililitroToLitro_shouldConvertCorrectly() {
        Quantity ml = new Quantity(new BigDecimal("1500"), UnitMeasurement.MILILITRO);
        
        Quantity result = UnitConverter.convert(ml, UnitMeasurement.LITRO);
        
        assertEquals(new BigDecimal("1.500000"), result.value());
        assertEquals(UnitMeasurement.LITRO, result.unitMeasurement());
    }
}