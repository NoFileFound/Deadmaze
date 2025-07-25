package org.deadmaze.utils;

// Imports
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import org.deadmaze.Application;
import org.deadmaze.libraries.SrcRandom;

public class Utils {
    /**
     * Builds a map of language communities and their corresponding flags.
     * @return A {@code Map<String, String>} where the keys are language community names and the values are their associated flag representations.
     */
    public static Map<String, String> buildLanguageMap() {
        Map<String, String> result = new HashMap<>();

        for (Map.Entry<String, String[]> entry : Application.getLanguageInfo().entrySet()) {
            String community = entry.getKey();
            String[] info = entry.getValue();

            String flag = info[1];
            result.put(community, flag);
        }
        return result;
    }

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
     * Gets the community id from the country language.
     * @param language The country language.
     * @return The community iso2 code.
     */
    public static String getCommunityFromLanguage(String language) {
        return switch (language) {
            case "AF" -> "ZA";
            case "MS" -> "MY";
            case "BI" -> "VU";
            case "BS" -> "BA";
            case "CA" -> "AD";
            case "NY" -> "MW";
            case "DA" -> "DK";
            case "ET" -> "EE";
            case "NA" -> "NR";
            case "EN" -> "GB";
            case "SM" -> "WS";
            case "KL" -> "GL";
            case "RN" -> "BI";
            case "SW" -> "KE";
            case "LB" -> "LU";
            case "QU" -> "BO";
            case "ST" -> "LS";
            case "TN" -> "BW";
            case "SQ" -> "AL";
            case "SS" -> "SZ";
            case "SL" -> "SI";
            case "SV" -> "SE";
            case "TL" -> "PH";
            case "VI" -> "VN";
            case "TK" -> "TM";
            case "WO" -> "SN";
            case "YO" -> "NG";
            case "CS" -> "CZ";
            case "EL" -> "GR";
            case "BE" -> "BY";
            case "KY" -> "KG";
            case "SR" -> "RS";
            case "TG" -> "TJ";
            case "UK" -> "UA";
            case "KK" -> "KZ";
            case "HY" -> "AM";
            case "HE" -> "IL";
            case "UR" -> "PK";
            case "AR" -> "IAR";
            case "FA" -> "IR";
            case "DV" -> "MV";
            case "NE" -> "NP";
            case "HI" -> "IN";
            case "BN" -> "BD";
            case "TA" -> "LK";
            case "LO" -> "LA";
            case "DZ" -> "BT";
            case "MY" -> "MM";
            case "KA" -> "GE";
            case "TI" -> "ER";
            case "AM" -> "ET";
            case "KM" -> "KH";
            case "ZH" -> "HK";
            case "JA" -> "JP";
            case "KO" -> "KR";
            default -> language;
        };
    }

    /**
     * Gets the content from the file name.
     * @param filename The file name.
     * @return Array of bytes.
     */
    public static byte[] getResourceFileContent(String filename) {
        try {
            return Files.readAllBytes(Path.of("resources/" + filename));
        } catch (IOException e) {
            return new byte[0];
        }
    }

    /**
     * Gets the time in minutes.
     */
    public static int getTribulleTime() {
        return (int) (System.currentTimeMillis() / 60000);
    }

    /**
     * Gets the unix timestamp.
     * @return Seconds.
     */
    public static long getUnixTime() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * Formats the text and censor the bad words.
     * @param text The text to format.
     * @return A formatted text without bad words.
     */
    public static String formatText(String text) {
        String symbols = "!@#$%&^";

        for (String word : Application.getBadWordsConfig()) {
            String replacement = word.chars()
                    .mapToObj(_ -> String.valueOf(symbols.charAt(SrcRandom.RandomNumber(0, symbols.length() - 1))))
                    .reduce("", String::concat);

            text = text.replaceAll("(?i)\\b" + Pattern.quote(word) + "\\b", Matcher.quoteReplacement(replacement));
        }
        return text;
    }

    /**
     * Formats the timestamp into date.
     * @param unixTime Unix timestamp.
     * @param pattern Pattern to format.
     * @return A formated date.
     */
    public static String formatUnixTime(long unixTime, String pattern) {
        LocalDateTime dateTime = Instant.ofEpochSecond(unixTime).atZone(ZoneId.systemDefault()).toLocalDateTime();
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }
}