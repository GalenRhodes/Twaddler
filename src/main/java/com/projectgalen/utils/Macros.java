package com.projectgalen.utils;

import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;

public class Macros {
    @RegExp public static final String MACRO_PATTERN = "\\$\\{([^}]+)}";

    private Macros() { }

    public interface GetMacroReplacement {
        @Nullable String getReplacement(@NotNull String name);
    }

    public static String replaceMacros(@Nullable String str, @NotNull GetMacroReplacement handler) {
        if(str == null) return null;
        Matcher matcher = Regex.getMatcher(MACRO_PATTERN, str);
        if(!matcher.find()) return str;
        StringBuffer sb = new StringBuffer();

        do {
            String replacement = handler.getReplacement(matcher.group(1));
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement == null ? matcher.group() : replacement));
        }
        while(matcher.find());

        return matcher.appendTail(sb).toString();
    }
}
