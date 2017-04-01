package ru.binarysimple.sbrf;

import java.math.BigDecimal;
import java.math.RoundingMode;

class CurrOps {

    private static String replace(String str1) {
        return str1.replace(",", ".");
    }

    public static String mult(String num, String denom) {
        num = replace(num);
        denom = replace(denom);
        BigDecimal d1 = new BigDecimal(num);
        BigDecimal d2 = new BigDecimal(denom);
        BigDecimal d3 = d1.multiply(d2);
        return d3.toString();
    }

    public static String currRound(String num, Integer precizion) {
        num = replace(num);
        BigDecimal d1 = new BigDecimal(num);
        return d1.setScale(precizion, RoundingMode.HALF_UP).toString();
    }
}
