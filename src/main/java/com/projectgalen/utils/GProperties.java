package com.projectgalen.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.util.Properties;

public class GProperties extends Properties {

    public GProperties(@NotNull URL url)                 { this(url, null); }

    public GProperties(@NotNull String filename)         { this(filename, null); }

    public GProperties(@NotNull InputStream inputStream) { this(inputStream, null); }

    /**
     * Creates an empty property list with no default values.
     */
    public GProperties() { this((Properties)null); }

    public GProperties(@NotNull URL url, @Nullable Properties defaults) {
        super(defaults);
        try { load(url); } catch(IOException e) { throw new RuntimeException(e); }
    }

    public GProperties(@NotNull String filename, @Nullable Properties defaults) {
        super(defaults);
        try { load(filename); } catch(IOException e) { throw new RuntimeException(e); }
    }

    public GProperties(@NotNull InputStream inputStream, @Nullable Properties defaults) {
        super(defaults);
        try { load(inputStream); } catch(IOException e) { throw new RuntimeException(e); } finally { IO.closeQuietly(inputStream); }
    }

    /**
     * Creates an empty property list with the specified defaults.
     *
     * @param defaults the defaults.
     */
    public GProperties(@Nullable Properties defaults) { super(defaults); }

    public GProperties(@Nullable InputStream inputStream, boolean ignoreNullInputStream) {
        super();
        if(inputStream == null) { if(!ignoreNullInputStream) throw new NullPointerException("Input stream is null."); }
        else { try { load(inputStream); } catch(Exception e) { throw new RuntimeException(e); } }
    }

    /**
     * Returns the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code (key.equals(k))}, then this method returns {@code v}; otherwise it returns {@code null}.  (There can be at most one such
     * mapping.)
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the key
     * @throws NullPointerException if the specified key is null
     * @see #put(Object, Object)
     */
    @Override
    @Nullable
    public synchronized Object get(@NotNull Object key) { return get(key, 100); }

    public void load(@NotNull URL url) throws IOException {
        try(InputStream inputStream = url.openConnection().getInputStream()) { load(inputStream); }
    }

    public void load(@NotNull String filename) throws IOException {
        try(InputStream inputStream = new FileInputStream(filename)) { load(inputStream); }
    }

    /**
     * Reads a property list (key and element pairs) from the input byte stream. The input stream is in a simple line-oriented format as specified in {@link #load(Reader)
     * load(Reader)} and is assumed to use the ISO 8859-1 character encoding; that is each byte is one Latin1 character. Characters not in Latin1, and certain special characters,
     * are represented in keys and elements using Unicode escapes as defined in section 3.3 of
     * <cite>The Java&trade; Language Specification</cite>.
     * <p>
     * The specified stream will be closed when this method returns.
     *
     * @param inStream the input stream.
     * @throws IOException              if an error occurred when reading from the input stream.
     * @throws IllegalArgumentException if the input stream contains a malformed Unicode escape sequence.
     * @since 1.2
     */
    @Override
    public synchronized void load(@NotNull InputStream inStream) throws IOException {
        try { super.load(inStream); } finally { IO.closeQuietly(inStream); }
    }

    /**
     * Reads a property list (key and element pairs) from the input character stream in a simple line-oriented format.
     * <p>
     * Properties are processed in terms of lines. There are two kinds of line, <i>natural lines</i> and <i>logical lines</i>. A natural line is defined as a line of characters
     * that is terminated either by a set of line terminator characters ({@code \n} or {@code \r} or {@code \r\n}) or by the end of the stream. A natural line may be either a blank
     * line, a comment line, or hold all or some of a key-element pair. A logical line holds all the data of a key-element pair, which may be spread out across several adjacent
     * natural lines by escaping the line terminator sequence with a backslash character {@code \}.  Note that a comment line cannot be extended in this manner; every natural line
     * that is a comment must have its own comment indicator, as described below. Lines are read from input until the end of the stream is reached.
     *
     * <p>
     * A natural line that contains only white space characters is considered blank and is ignored.  A comment line has an ASCII {@code '#'} or {@code '!'} as its first non-white
     * space character; comment lines are also ignored and do not encode key-element information.  In addition to line terminators, this format considers the characters space
     * ({@code ' '}, {@code '\u005Cu0020'}), tab ({@code '\t'}, {@code '\u005Cu0009'}), and form feed ({@code '\f'}, {@code '\u005Cu000C'}) to be white space.
     *
     * <p>
     * If a logical line is spread across several natural lines, the backslash escaping the line terminator sequence, the line terminator sequence, and any white space at the start
     * of the following line have no affect on the key or element values. The remainder of the discussion of key and element parsing (when loading) will assume all the characters
     * constituting the key and element appear on a single natural line after line continuation characters have been removed.  Note that it is <i>not</i> sufficient to only examine
     * the character preceding a line terminator sequence to decide if the line terminator is escaped; there must be an odd number of contiguous backslashes for the line terminator
     * to be escaped. Since the input is processed from left to right, a non-zero even number of 2<i>n</i> contiguous backslashes before a line terminator (or elsewhere) encodes
     * <i>n</i> backslashes after escape processing.
     *
     * <p>
     * The key contains all of the characters in the line starting with the first non-white space character and up to, but not including, the first unescaped {@code '='}, {@code
     * ':'}, or white space character other than a line terminator. All of these key termination characters may be included in the key by escaping them with a preceding backslash
     * character; for example,<p>
     * <p>
     * {@code \:\=}<p>
     * <p>
     * would be the two-character key {@code ":="}.  Line terminator characters can be included using {@code \r} and {@code \n} escape sequences.  Any white space after the key is
     * skipped; if the first non-white space character after the key is {@code '='} or {@code ':'}, then it is ignored and any white space characters after it are also skipped.
     * All remaining characters on the line become part of the associated element string; if there are no remaining characters, the element is the empty string {@code ""}.  Once
     * the raw character sequences constituting the key and element are identified, escape processing is performed as described above.
     *
     * <p>
     * As an example, each of the following three lines specifies the key {@code "Truth"} and the associated element value {@code "Beauty"}:
     * <pre>
     * Truth = Beauty
     *  Truth:Beauty
     * Truth                    :Beauty
     * </pre>
     * As another example, the following three lines specify a single property:
     * <pre>
     * fruits                           apple, banana, pear, \
     *                                  cantaloupe, watermelon, \
     *                                  kiwi, mango
     * </pre>
     * The key is {@code "fruits"} and the associated element is:
     * <pre>"apple, banana, pear, cantaloupe, watermelon, kiwi, mango"</pre>
     * Note that a space appears before each {@code \} so that a space will appear after each comma in the final result; the {@code \}, line terminator, and leading white space on
     * the continuation line are merely discarded and are <i>not</i> replaced by one or more other characters.
     * <p>
     * As a third example, the line:
     * <pre>cheeses
     * </pre>
     * specifies that the key is {@code "cheeses"} and the associated element is the empty string {@code ""}.
     * <p>
     * <a name="unicodeescapes"></a>
     * Characters in keys and elements can be represented in escape sequences similar to those used for character and string literals (see sections 3.3 and 3.10.6 of
     * <cite>The Java&trade; Language Specification</cite>).
     * <p>
     * The differences from the character escape sequences and Unicode escapes used for characters and strings are:
     *
     * <ul>
     * <li> Octal escapes are not recognized.
     *
     * <li> The character sequence {@code \b} does <i>not</i>
     * represent a backspace character.
     *
     * <li> The method does not treat a backslash character,
     * {@code \}, before a non-valid escape character as an
     * error; the backslash is silently dropped.  For example, in a
     * Java string the sequence {@code "\z"} would cause a
     * compile time error.  In contrast, this method silently drops
     * the backslash.  Therefore, this method treats the two character
     * sequence {@code "\b"} as equivalent to the single
     * character {@code 'b'}.
     *
     * <li> Escapes are not necessary for single and double quotes;
     * however, by the rule above, single and double quote characters
     * preceded by a backslash still yield single and double quote
     * characters, respectively.
     *
     * <li> Only a single 'u' character is allowed in a Unicode escape
     * sequence.
     *
     * </ul>
     * <p>
     * The specified stream will be close when this method returns.
     *
     * @param reader the input character stream.
     * @throws IOException              if an error occurred when reading from the input stream.
     * @throws IllegalArgumentException if a malformed Unicode escape appears in the input.
     * @since 1.6
     */
    @Override
    public synchronized void load(@NotNull Reader reader) throws IOException {
        try { super.load(reader); } finally { IO.closeQuietly(reader); }
    }

    /**
     * Returns the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code (key.equals(k))}, then this method returns {@code v}; otherwise it returns {@code null}.  (There can be at most one such
     * mapping.)
     *
     * @param key     the key whose associated value is to be returned
     * @param deadMan A dead man counter to protect against circular references.
     * @return the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the key
     * @throws NullPointerException if the specified key is null
     * @see #put(Object, Object)
     */
    @Nullable
    private String get(@NotNull Object key, int deadMan) {
        Object prop = super.get(key);
        return ((prop instanceof String) ? Macros.replaceMacros((String)prop, name -> ((deadMan > 0) ? get(name, deadMan - 1) : null)) : null);
    }

    @NotNull
    protected static GProperties _getInstance() { return Holder.INSTANCE; }

    private static final class Holder {
        private static final GProperties INSTANCE = new GProperties(GProperties.class.getResourceAsStream("settings.properties"), true);
    }
}
