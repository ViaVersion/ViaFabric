package com.github.creeper123123321.viarift.util;

import java.util.function.Predicate;

public class IntegerFormatFilter implements Predicate<String> {
    @Override
    public boolean test(String s) {
        if (s.isEmpty()) return true;
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
