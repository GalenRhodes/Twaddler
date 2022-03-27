package com.projectgalen.utils;

import org.intellij.lang.annotations.RegExp;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    private static final Map<CacheKey, Pattern> CACHE = new TreeMap<>();

    private Regex() { }

    /**
     * Get a new matcher from a chached Regular Expression regex.
     * <p>
     * If the regex does not exist in the cache then it is compiled and placed into the cache for future calls. This will save time for patterns that are used frequently.
     *
     * @param regex The expression to be compiled.
     * @param input The character sequence to be matched.
     * @return The matcher.
     * @throws java.util.regex.PatternSyntaxException if the syntax of the regex is bad.
     */
    public static Matcher getMatcher(@NotNull @RegExp String regex, @NotNull CharSequence input) { return getMatcher(regex, input, 0); }

    /**
     * Get a new matcher from a chached Regular Expression regex.
     * <p>
     * If the regex does not exist in the cache then it is compiled and placed into the cache for future calls. This will save time for patterns that are used frequently.
     *
     * @param regex The expression to be compiled.
     * @param input The character sequence to be matched.
     * @param flags Match flags, a bit mask that may include {@link Pattern#CASE_INSENSITIVE}, {@link Pattern#MULTILINE}, {@link Pattern#DOTALL}, {@link Pattern#UNICODE_CASE},
     *              {@link Pattern#CANON_EQ}, {@link Pattern#UNIX_LINES}, {@link Pattern#LITERAL}, {@link Pattern#UNICODE_CHARACTER_CLASS} and {@link Pattern#COMMENTS}.
     * @return A new matcher for this pattern.
     * @throws java.util.regex.PatternSyntaxException If the expression's syntax is invalid.
     * @throws IllegalArgumentException               If bit values other than those corresponding to the defined match flags are set in flags.
     */
    @NotNull
    public static Matcher getMatcher(@NotNull @RegExp String regex, @NotNull CharSequence input, int flags) {
        synchronized(CACHE) {
            CacheKey key = new CacheKey(regex, flags);
            Pattern  p   = CACHE.get(key);
            if(p == null) CACHE.put(key, p = Pattern.compile(regex, flags));
            return p.matcher(input);
        }
    }

    private static class CacheKey implements Comparable<CacheKey> {
        @NotNull @RegExp final String regex;
        final                  int    flags;

        protected CacheKey(@NotNull @RegExp String regex, int flags) {
            this.regex = regex;
            this.flags = flags;
        }

        /**
         * Compares this object with the specified object for order.  Returns a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than
         * the specified object.
         *
         * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
         * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This implies that <tt>x.compareTo(y)</tt> must throw an exception iff
         * <tt>y.compareTo(x)</tt> throws an exception.)
         *
         * <p>The implementor must also ensure that the relation is transitive:
         * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
         * <tt>x.compareTo(z)&gt;0</tt>.
         *
         * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
         * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for all <tt>z</tt>.
         *
         * <p>It is strongly recommended, but <i>not</i> strictly required that
         * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
         * class that implements the <tt>Comparable</tt> interface and violates this condition should clearly indicate this fact.  The recommended language is "Note: this class has
         * a natural ordering that is inconsistent with equals."
         *
         * <p>In the foregoing description, the notation
         * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
         * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
         * <tt>0</tt>, or <tt>1</tt> according to whether the value of
         * <i>expression</i> is negative, zero or positive.
         *
         * @param o the object to be compared.
         * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
         * @throws NullPointerException if the specified object is null
         * @throws ClassCastException   if the specified object's type prevents it from being compared to this object.
         */
        @Override
        public int compareTo(@NotNull CacheKey o) {
            int c = regex.compareTo(o.regex);
            return ((c == 0) ? (flags - o.flags) : c);
        }

        @Override
        public int hashCode() {
            return Objects.hash(regex, flags);
        }

        @Override
        public boolean equals(Object o) {
            return ((this == o) || ((o != null) && (getClass() == o.getClass()) && _equals((CacheKey)o)));
        }

        private boolean _equals(@NotNull CacheKey cacheKey) {
            return ((flags == cacheKey.flags) && regex.equals(cacheKey.regex));
        }
    }
}
