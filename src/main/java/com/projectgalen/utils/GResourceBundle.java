package com.projectgalen.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GResourceBundle extends ResourceBundle {
    private final LinkedHashMap<String, ResourceBundle> stack   = new LinkedHashMap<>();
    private       ResourceBundle[]                      bundles = null;

    public GResourceBundle(@NotNull String baseName) {
        this(baseName, getBundle(baseName));
    }

    public GResourceBundle(@NotNull String baseName, @NotNull Locale locale) {
        this(baseName, getBundle(baseName, locale));
    }

    public GResourceBundle(@NotNull String baseName, @NotNull ClassLoader classLoader) {
        this(baseName, getBundle(baseName, Locale.getDefault(), classLoader));
    }

    public GResourceBundle(@NotNull String baseName, @NotNull Control control) {
        this(baseName, getBundle(baseName, Locale.getDefault(), control));
    }

    public GResourceBundle(@NotNull String baseName, @NotNull Locale locale, @NotNull Control control) {
        this(baseName, getBundle(baseName, locale, control));
    }

    public GResourceBundle(@NotNull String baseName, @NotNull ClassLoader classLoader, @NotNull Control control) {
        this(baseName, getBundle(baseName, Locale.getDefault(), classLoader, control));
    }

    public GResourceBundle(@NotNull String baseName, @NotNull Locale locale, @NotNull ClassLoader classLoader) {
        this(baseName, getBundle(baseName, locale, classLoader));
    }

    public GResourceBundle(@NotNull String baseName, @NotNull Locale locale, @NotNull ClassLoader classLoader, @NotNull Control control) {
        this(baseName, getBundle(baseName, locale, classLoader, control));
    }

    private GResourceBundle(@NotNull String baseName, @NotNull ResourceBundle bundle) {
        super();
        synchronized(stack) { stack.put(baseName, bundle); }
    }

    public void addBundle(@NotNull String baseName) {
        addBundle(baseName, getBundle(baseName));
    }

    public void addBundle(@NotNull String baseName, @NotNull Locale locale) {
        addBundle(baseName, getBundle(baseName, locale));
    }

    public void addBundle(@NotNull String baseName, @NotNull ClassLoader classLoader) {
        addBundle(baseName, getBundle(baseName, Locale.getDefault(), classLoader));
    }

    public void addBundle(@NotNull String baseName, @NotNull Control control) {
        addBundle(baseName, getBundle(baseName, Locale.getDefault(), control));
    }

    public void addBundle(@NotNull String baseName, @NotNull Locale locale, @NotNull Control control) {
        addBundle(baseName, getBundle(baseName, locale, control));
    }

    public void addBundle(@NotNull String baseName, @NotNull ClassLoader classLoader, @NotNull Control control) {
        addBundle(baseName, getBundle(baseName, Locale.getDefault(), classLoader, control));
    }

    public void addBundle(@NotNull String baseName, @NotNull Locale locale, @NotNull ClassLoader classLoader) {
        addBundle(baseName, getBundle(baseName, locale, classLoader));
    }

    public void addBundle(@NotNull String baseName, @NotNull Locale locale, @NotNull ClassLoader classLoader, @NotNull Control control) {
        addBundle(baseName, getBundle(baseName, locale, classLoader, control));
    }

    public ResourceBundle removeBundle(@NotNull String baseName) {
        synchronized(stack) { return stack.remove(baseName); }
    }

    /**
     * Gets an object for the given key from this resource bundle. Returns null if this resource bundle does not contain an object for the given key.
     *
     * @param key the key for the desired object
     * @return the object for the given key, or null
     * @throws NullPointerException if <code>key</code> is <code>null</code>
     */
    @Override
    @Nullable
    protected Object handleGetObject(@NotNull String key) {
        return _get(key, 100);
    }

    /**
     * Returns an enumeration of the keys.
     *
     * @return an <code>Enumeration</code> of the keys contained in this <code>ResourceBundle</code> and its parent bundles.
     */
    @NotNull
    @Override
    public Enumeration<String> getKeys() {
        Set<String> keys = new TreeSet<>();
        synchronized(stack) { for(Map.Entry<String, ResourceBundle> e : stack.entrySet()) keys.addAll(e.getValue().keySet()); }
        Vector<String> v = new Vector<>(keys);
        return v.elements();
    }

    @Nullable
    private Object _get(@NotNull String key, int deadMan) {
        synchronized(stack) {
            if(bundles == null) bundles = stack.values().toArray(new ResourceBundle[0]);
            for(int i = 0, j = (bundles.length - 1); i < bundles.length; i++) {
                ResourceBundle b = bundles[j - i];
                try {
                    if(b.containsKey(key)) {
                        Object o = b.getObject(key);
                        if(o instanceof String) return Macros.replaceMacros((String)o, name -> {
                            if(deadMan <= 0) return null;
                            Object oo = _get(key, deadMan - 1);
                            return ((oo == null) ? null : oo.toString());
                        });
                    }
                }
                catch(Exception ignore) { }
            }
        }
        return null;
    }

    private void addBundle(@NotNull String baseName, @NotNull ResourceBundle bundle) {
        synchronized(stack) {
            bundles = null;
            stack.put(baseName, bundle);
        }
    }

    @NotNull
    protected static GResourceBundle _getInstance() { return Holder.INSTANCE; }

    private static final class Holder {
        private static final GResourceBundle INSTANCE = new GResourceBundle("com.projectgalen.utils.messages");
    }
}
