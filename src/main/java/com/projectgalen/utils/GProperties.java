package com.projectgalen.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class GProperties extends Properties {

    public GProperties(URL url) { this(url, null); }

    public GProperties(String filename) { this(filename, null); }

    public GProperties(InputStream inputStream) { this(inputStream, null); }

    /**
     * Creates an empty property list with no default values.
     */
    public GProperties() { this((Properties)null); }

    public GProperties(URL url, Properties defaults) {
        super(defaults);
        try { load(url); } catch(IOException e) { throw new RuntimeException(e); }
    }

    public GProperties(String filename, Properties defaults) {
        super(defaults);
        try { load(filename); } catch(IOException e) { throw new RuntimeException(e); }
    }

    public GProperties(InputStream inputStream, Properties defaults) {
        super(defaults);
        try { load(inputStream); } catch(IOException e) { throw new RuntimeException(e); }
    }

    /**
     * Creates an empty property list with the specified defaults.
     *
     * @param defaults the defaults.
     */
    public GProperties(Properties defaults) { super(defaults); }

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
    public synchronized Object get(Object key) { return get(key, 100); }

    public void load(URL url) throws IOException {
        try(InputStream inputStream = url.openConnection().getInputStream()) { load(inputStream); }
    }

    public void load(String filename) throws IOException {
        try(InputStream inputStream = new FileInputStream(filename)) { load(inputStream); }
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
    private String get(Object key, int deadMan) {
        Object prop = super.get(key);
        return ((prop instanceof String) ? Macros.replaceMacros((String)prop, name -> ((deadMan > 0) ? get(name, deadMan - 1) : null)) : null);
    }
}
