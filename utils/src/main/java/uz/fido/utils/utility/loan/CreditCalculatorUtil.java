package uz.fido.utils.utility.loan;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CreditCalculatorUtil {

    public static BigDecimal calculate(BigDecimal amount, BigDecimal rate, int periods) {
        return calculatePresentValue(amount, rate).divide(
                BigDecimal.ONE.subtract(
                        (BigDecimal.ONE.add(
                                rate.divide(new BigDecimal(12), CalculationContext.mathContext())).pow(-1 * periods, CalculationContext.mathContext())
                        )), 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculatePresentValue(BigDecimal amount, BigDecimal rate) {
        return amount.multiply(rate.divide(new BigDecimal(12), CalculationContext.mathContext()));
    }

    public static BigDecimal calculatePresentValueFactor(BigDecimal rate, int periods) {
        return BigDecimal.ONE.add(rate).pow(periods);
    }
}
