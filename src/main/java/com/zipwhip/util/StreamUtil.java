package com.zipwhip.util;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: Michael
 * Date: 11/1/12
 * Time: 11:14 AM
 */
public class StreamUtil {

    /**
     * @param is
     * @param os
     * @return The number of bytes copied
     * @throws IOException
     */
    public static long copy(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[256];
        long result = 0;

        try {
            int read;
            while ((read = is.read(buffer)) != -1) {
                result += read;
                os.write(buffer, 0, read);
            }
            os.flush();
        } finally {
            is.close();
            os.close();
        }

        return result;
    }

    /**
     * Save Bitmap image to disk
     *
     * @param file - image file
     * @param data - data to save as a byte array
     * @throws java.io.IOException
     */
    public static void saveByteArrayToFile(File file, byte[] data) throws IOException {
        BufferedOutputStream os = null;

        try {
            if (data != null && data.length > 0) {
                os = new BufferedOutputStream(new FileOutputStream(file));
                os.write(data);
            }
        } finally {
            if (os != null) {
                os.flush();
                os.close();
            }
        }
    }

    public static String getString(InputStream resource) throws IOException {
        if (resource == null) {
            return null;
        }

        ByteArrayOutputStream os = new ByteArrayOutputStream(resource.available());

        StreamUtil.copy(resource, os);

        return new String(os.toByteArray());
    }
}
