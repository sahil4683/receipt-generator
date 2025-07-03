package io.vv.receipt_generator.util;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.text.DecimalFormat;

@Component
public class NumberToWordsConverter {

    private static final String[] ONES = {
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"
    };

    private static final String[] TEENS = {
        "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", 
        "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };

    private static final String[] TENS = {
        "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
    };

    private static final String[] THOUSANDS = {
        "", "Thousand", "Million", "Billion", "Trillion"
    };

    public String convertToWords(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return "Zero Rupees Only";
        }

        // Split into rupees and paise
        long rupees = amount.longValue();
        int paise = (int) ((amount.subtract(new BigDecimal(rupees))).multiply(new BigDecimal(100)).longValue());

        StringBuilder result = new StringBuilder();
        
        if (rupees > 0) {
            result.append(convertNumberToWords(rupees)).append(" Rupees");
        }
        
        if (paise > 0) {
            if (result.length() > 0) {
                result.append(" and ");
            }
            result.append(convertNumberToWords(paise)).append(" Paise");
        }
        
        result.append(" Only");
        
        return result.toString();
    }

    private String convertNumberToWords(long number) {
        if (number == 0) {
            return "Zero";
        }

        String result = "";
        int groupIndex = 0;

        while (number > 0) {
            int group = (int) (number % 1000);
            if (group != 0) {
                String groupWords = convertHundreds(group);
                if (groupIndex > 0) {
                    groupWords += " " + THOUSANDS[groupIndex];
                }
                result = groupWords + (result.isEmpty() ? "" : " " + result);
            }
            number /= 1000;
            groupIndex++;
        }

        return result;
    }

    private String convertHundreds(int number) {
        String result = "";

        if (number >= 100) {
            result += ONES[number / 100] + " Hundred";
            number %= 100;
            if (number > 0) {
                result += " ";
            }
        }

        if (number >= 20) {
            result += TENS[number / 10];
            number %= 10;
            if (number > 0) {
                result += " " + ONES[number];
            }
        } else if (number >= 10) {
            result += TEENS[number - 10];
        } else if (number > 0) {
            result += ONES[number];
        }

        return result;
    }
}