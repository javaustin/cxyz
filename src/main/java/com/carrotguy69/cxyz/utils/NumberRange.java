package com.carrotguy69.cxyz.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.Random;

public class NumberRange {

    public static boolean strictMode = true;
    private Number min;
    private Number max;

    public NumberRange(Number min, Number max) {
        if (compare(min, max) == -1) {
            if (strictMode) // strictMode -> do not allow `min > max`
                throw new RuntimeException(String.format("Minimum value (%s) cannot be greater than maximum value (%s)!", min, max));
            else { // swap values so that `min <= max`
                this.min = max;
                this.max = min;
                return;
            }
        }

        this.min = min;
        this.max = max;
    }

    public static NumberRange fromString(String input) {
        // Supports "1-2" as min=1, max=2, or "1" as min=1, max=1

        String[] args = input.split("-");

        if (args.length == 1) {
            // Convert "1" to "1-1" to allow same logic
            args = new String[]{args[0], args[0]};
        }

        if (args.length >= 2) {
            // Ignore any arguments beyond the second one (args[1]).
            args = ObjectUtils.slice(args, 0, 2);
        }

        if (!ObjectUtils.isValidNumber(args[0]) || !ObjectUtils.isValidNumber(args[1])) {
            throw new RuntimeException(String.format("\"%s\" is not a valid range! Both numbers must be valid.", input));
        }

        Number min = ObjectUtils.parseAs(Double.class, args[0]);
        Number max = ObjectUtils.parseAs(Double.class, args[1]);

        NumberRange range = new NumberRange(min, max);

        if (compare(min, max) == -1) {
            if (strictMode) // strictMode -> do not allow `min > max`
                throw new RuntimeException(String.format("Minimum value (%s) cannot be greater than maximum value (%s)!", min, max));
            else { // swap values so that `min <= max`
                range.min = max;
                range.max = min;
                return range;
            }
        }

        range.min = min;
        range.max = max;

        return range;
    }


    private static int compare(Number min, Number max) {

        if (min.doubleValue() == max.doubleValue()) {
            return 0;
        }

        return Double.compare(max.doubleValue(), min.doubleValue());
    }

    public Number generateRandom(int precision) {
        return generateRandom(new Random(), precision);
    }

    public Number min() {
        return min;
    }

    public Number max() {
        return max;
    }

    public Number generateRandom(Random r, int precision) {
        if (Objects.equals(this.min, this.max)) {
            return this.min;
        }

        if (precision == 0) {
            return r.nextInt(this.min.intValue(), this.max.intValue() + 1); // "+ 1" to correct the max value being excluded
        }

        double randomValue = r.nextDouble(this.min.doubleValue(), this.max.doubleValue() + (1.0/(precision + 1))); //

        BigDecimal bd = new BigDecimal(randomValue).setScale(precision, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public String toPrettyString() {
        return min + "-" + max;
    }

    @Override
    public String toString() {
        return String.format("NumberRange{%s-%s}", min, max);
    }
}