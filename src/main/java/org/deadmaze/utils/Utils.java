package org.deadmaze.utils;

// Imports
import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;

public class Utils {
    /**
     * Compressed the bytes using zlib.
     * @param data The given bytes.
     * @return The compressed bytes.
     */
    public static byte[] compressZlib(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            outputStream.write(buffer, 0, deflater.deflate(buffer));
        }

        return outputStream.toByteArray();
    }

    /**
     * Gets the unix timestamp.
     * @return Seconds.
     */
    public static long getUnixTime() {
        return System.currentTimeMillis() / 1000;
    }
}