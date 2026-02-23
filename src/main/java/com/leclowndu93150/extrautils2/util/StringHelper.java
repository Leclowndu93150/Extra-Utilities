package com.leclowndu93150.extrautils2.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class StringHelper {
    private StringHelper() {}

    public static String format(double number) {
        return NumberFormat.getInstance(Locale.UK).format(number);
    }

    public static String format(long number) {
        return NumberFormat.getInstance(Locale.UK).format(number);
    }

    public static String formatDurationSeconds(long ticks, boolean incExtras) {
        long t = ticks % 20L;
        long s = ticks % 1200L / 20L;
        long m = ticks % 72000L / 1200L;
        long h = ticks % 1728000L / 72000L;
        long d = ticks / 1728000L;
        StringBuilder builder = new StringBuilder();
        boolean flag = d > 0L;
        if (flag) {
            builder.append(d).append("d");
        }
        flag |= h > 0L;
        if (h > 0L || incExtras && flag) {
            builder.append(h).append("h");
        }
        flag |= m > 0L;
        if (m > 0L || incExtras && flag) {
            builder.append(m).append("m");
        }
        if (s > 0L || t > 0L || incExtras) {
            if (t == 0L && !incExtras) {
                builder.append(s).append("s");
            } else {
                builder.append(String.format(Locale.UK, "%.2f", (float) s + (float) t / 20.0F)).append("s");
            }
        }
        return builder.toString();
    }
}
