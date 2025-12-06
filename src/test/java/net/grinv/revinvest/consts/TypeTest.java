package net.grinv.revinvest.consts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TypeTest {
    @Test
    @DisplayName("Should successfully return the correct Type enum for valid input")
    void getTypeByString_shouldReturnCorrectType_whenInputIsValid() {
        Type buy = Type.getTypeByString("BUY - MARKET");
        Type dividend = Type.getTypeByString("DIVIDEND");

        Assertions.assertAll(
                "Correct input validation",
                () -> Assertions.assertEquals(
                        Type.Buy, buy, "The method should successfully map \"BUY - MARKET\" to Type.Buy"),
                () -> Assertions.assertEquals(
                        Type.Dividend, dividend, "The method should successfully map \"DIVIDEND\" to Type.Dividend"));
    }

    @Test
    @DisplayName("Should ignore case and surrounding whitespace in the input")
    void getTypeByString_shouldBeCaseInsensitiveAndTrim() {
        Type result = Type.getTypeByString("  CaSh WiThDrAwAl  ");
        Assertions.assertEquals(
                Type.Withdraw, result, "The method must correctly normalize and find the Type.Withdraw");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for unknown label")
    void getTypeByString_shouldThrowException_whenInputIsUnknown() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Type.getTypeByString("RANDOM STRING"),
                "The method must throw IllegalArgumentException for unhandled types");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for null input")
    void getTypeByString_shouldThrowException_whenInputIsNull() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Type.getTypeByString(null),
                "The method must throw IllegalArgumentException when the input is null");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for blank/empty input")
    void getTypeByString_shouldThrowException_whenInputIsBlank() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Type.getTypeByString("  \t  "),
                "The method must throw IllegalArgumentException when the input is blank (whitespace only)");
    }
}
