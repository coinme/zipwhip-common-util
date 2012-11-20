package com.zipwhip.util;

import java.util.zip.Deflater;

/**
 * Utility that detects various properties specific to the current runtime
 * environment, such as Java version and the availability of the
 * {@code sun.misc.Unsafe} object.
 *
 * <br>
 * You can disable the use of {@code sun.misc.Unsafe} if you specify
 * the System property <strong>org.jboss.netty.tryUnsafe</strong> with
 * value of <code>false</code>. Default is <code>true</code>.
 */
public final class DetectionUtil {

    private static final int JAVA_VERSION = javaVersion0();
    private static final boolean IS_WINDOWS;
    static {
        String os = System.getProperty("os.name").toLowerCase();
        // windows
        IS_WINDOWS =  os.indexOf("win") >= 0;
    }

    /**
     * Return <code>true</code> if the JVM is running on Windows
     *
     */
    public static boolean isWindows() {
        return IS_WINDOWS;
    }

    public static int javaVersion() {
        return JAVA_VERSION;
    }

    private static int javaVersion0() {
        try {
            // Check if its android, if so handle it the same way as java6.
            //
            // See https://github.com/netty/netty/issues/282
            Class.forName("android.app.Application");
            return 6;
        } catch (ClassNotFoundException e) {
            //Ignore
        }

        try {
            Deflater.class.getDeclaredField("SYNC_FLUSH");
            return 7;
        } catch (Exception e) {
            // Ignore
        }

        try {
            Double.class.getDeclaredField("MIN_NORMAL");
            return 6;
        } catch (Exception e) {
            // Ignore
        }

        return 5;
    }

    private DetectionUtil() {
        // only static method supported
    }
}
