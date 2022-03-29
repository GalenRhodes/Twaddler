package com.projectgalen.utils;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class IO {

    public static final Charset DEFAULT_CHARSET;

    private IO() { }

    public static void closeQuietly(@NotNull Closeable cls) {
        try { cls.close(); } catch(Exception ignore) { }
    }

    public static long copy(@NotNull InputStream in, @NotNull OutputStream out, boolean closeOutput) throws IOException {
        try {
            byte[] buf     = new byte[65536];
            long   totalCc = 0;
            int    cc      = in.read(buf);
            while(cc >= 0) {
                out.write(buf, 0, cc);
                totalCc += cc;
                cc = in.read(buf);
            }
            return totalCc;
        }
        finally {
            closeQuietly(in);
            if(closeOutput) closeQuietly(out);
        }
    }

    public static long copy(@NotNull Reader in, @NotNull Writer out, boolean closeOutput) throws IOException {
        try {
            char[] cbuf    = new char[65536];
            long   totalCc = 0;
            int    cc      = in.read(cbuf);
            while(cc >= 0) {
                out.write(cbuf, 0, cc);
                totalCc += cc;
                cc = in.read(cbuf);
            }
            return totalCc;
        }
        finally {
            closeQuietly(in);
            if(closeOutput) closeQuietly(out);
        }
    }

    public static long copy(@NotNull Reader in, @NotNull Writer out) throws IOException {
        return copy(in, out, true);
    }

    public static long copy(@NotNull InputStream in, @NotNull OutputStream out) throws IOException {
        return copy(in, out, true);
    }

    @NotNull
    public static Future<Long> copy(@NotNull ExecutorService executor, @NotNull InputStream in, @NotNull OutputStream out, boolean closeOutput) {
        return executor.submit(() -> copy(in, out, closeOutput));
    }

    @NotNull
    public static Future<Long> copy(@NotNull ExecutorService executor, @NotNull Reader in, @NotNull Writer out, boolean closeOutput) {
        return executor.submit(() -> copy(in, out, closeOutput));
    }

    @NotNull
    public static Future<Long> copy(@NotNull ExecutorService executor, @NotNull InputStream in, @NotNull OutputStream out) {
        return executor.submit(() -> copy(in, out, true));
    }

    @NotNull
    public static Future<Long> copy(@NotNull ExecutorService executor, @NotNull Reader in, @NotNull Writer out) {
        return executor.submit(() -> copy(in, out, true));
    }

    @NotNull
    public static Future<String> readFile(@NotNull ExecutorService executor, @NotNull Reader reader) {
        return executor.submit(() -> readFile(reader));
    }

    @NotNull
    public static Future<String> readFile(@NotNull ExecutorService executor, @NotNull InputStream inputStream, @NotNull Charset cs) {
        return executor.submit(() -> readFile(inputStream, cs));
    }

    @NotNull
    public static Future<String> readFile(@NotNull ExecutorService executor, @NotNull String filename, @NotNull Charset cs) {
        return executor.submit(() -> readFile(filename, cs));
    }

    @NotNull
    public static Future<String> readFile(@NotNull ExecutorService executor, @NotNull File file, @NotNull Charset cs) {
        return executor.submit(() -> readFile(file, cs));
    }

    @NotNull
    public static Future<String> readFile(@NotNull ExecutorService executor, @NotNull URL url, @NotNull Charset cs) {
        return executor.submit(() -> readFile(url, cs));
    }

    @NotNull
    public static Future<String> readFile(@NotNull ExecutorService executor, @NotNull URLConnection urlConnection, @NotNull Charset cs) {
        return executor.submit(() -> readFile(urlConnection, cs));
    }

    @NotNull
    public static Future<String> readFile(@NotNull ExecutorService executor, @NotNull InputStream inputStream) {
        return executor.submit(() -> readFile(inputStream, DEFAULT_CHARSET));
    }

    @NotNull
    public static Future<String> readFile(@NotNull ExecutorService executor, @NotNull String filename) {
        return executor.submit(() -> readFile(filename, DEFAULT_CHARSET));
    }

    @NotNull
    public static Future<String> readFile(@NotNull ExecutorService executor, @NotNull File file) {
        return executor.submit(() -> readFile(file, DEFAULT_CHARSET));
    }

    @NotNull
    public static Future<String> readFile(@NotNull ExecutorService executor, @NotNull URL url) {
        return executor.submit(() -> readFile(url, DEFAULT_CHARSET));
    }

    @NotNull
    public static Future<String> readFile(@NotNull ExecutorService executor, @NotNull URLConnection urlConnection) {
        return executor.submit(() -> readFile(urlConnection, DEFAULT_CHARSET));
    }

    @NotNull
    public static String readFile(@NotNull Reader reader) throws IOException {
        try {
            StringBuilder sb   = new StringBuilder();
            char[]        cbuf = new char[65536];
            int           cc   = reader.read(cbuf);
            while(cc >= 0) {
                sb.append(cbuf, 0, cc);
                cc = reader.read(cbuf);
            }
            return sb.toString();
        }
        finally {
            closeQuietly(reader);
        }
    }

    @NotNull
    public static String readFile(@NotNull InputStream inputStream, @NotNull Charset cs) throws IOException {
        return readFile(new InputStreamReader(inputStream, cs));
    }

    @NotNull
    public static String readFile(@NotNull InputStream inputStream) throws IOException {
        return readFile(inputStream, DEFAULT_CHARSET);
    }

    @NotNull
    public static String readFile(@NotNull File file, @NotNull Charset cs) throws IOException {
        return readFile(new FileInputStream(file), cs);
    }

    @NotNull
    public static String readFile(@NotNull File file) throws IOException {
        return readFile(file, DEFAULT_CHARSET);
    }

    @NotNull
    public static String readFile(@NotNull @NonNls String filename, @NotNull Charset cs) throws IOException {
        return readFile(new File(filename), cs);
    }

    @NotNull
    public static String readFile(@NotNull @NonNls String filename) throws IOException {
        return readFile(filename, DEFAULT_CHARSET);
    }

    @NotNull
    public static String readFile(@NotNull URL url, @NotNull Charset cs) throws IOException {
        return readFile(url.openConnection(), cs);
    }

    @NotNull
    public static String readFile(@NotNull URL url) throws IOException {
        return readFile(url, DEFAULT_CHARSET);
    }

    @NotNull
    public static String readFile(@NotNull URLConnection uconn, @NotNull Charset cs) throws IOException {
        InputStream inputStream = uconn.getInputStream();
        String      enc         = uconn.getContentEncoding();
        Charset     _cs         = null;

        if(enc != null) try { _cs = Charset.forName(enc); } catch(Exception ignored) { }
        if(_cs == null) _cs = cs;
        return readFile(inputStream, cs);
    }

    @NotNull
    public static String readFile(@NotNull URLConnection uconn) throws IOException {
        return readFile(uconn, DEFAULT_CHARSET);
    }

    static {
        Charset cs = null;
        try { cs = Charset.forName(GProperties._getInstance().getProperty("default.encoding", "UTF-8")); }
        catch(Exception e) { cs = Charset.defaultCharset(); }
        DEFAULT_CHARSET = cs;
    }
}
