package Com.base.utilites;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class AirthmaticsOpeartion {

    // round a double to the given number of decimal places (e.g. 12.3456, 2 -> 12.35)
    public static double roundOffDoubleValue(double value, int decimalPlaces) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}