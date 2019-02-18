package jp.gtf.kernel.lang.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.util.DigestUtils;

public class UString {

    static ClassLoader loader = null;

    static {
        loader = UString.class.getClassLoader();
    }

    public static final String LF = "\n";
    public static final String SPACE = " ";
    public static final String SPACE4 = "    ";
    public static final String EMPTY = "";
    public static final String MARK_SHARP = "#";
    public static final String MARK_SINGLE_QUOTE = "'";
    public static final String MARK_DOT = ".";
    public static final String MARK_DOT_SLICKE = "./";
    public static final String MARK_EQUALS = "=";
    public static final String MARK_UNDERBAR = "_";
    public static final String MARK_SLICE = "/";
    public static final String MARK_COMMANER = ",";
    public static final String MARK_BREAKLINE = "\n";
    public static final String MARK_SEMICOLON = ";";

    public static final String REGEX_MARK_DOT = "\\.";
    public static final String REGEX_PATTERN_ALL = ".*";
    public static final String REGEX_PATTERN_ALNUM = "^[a-zA-Z0-9]+$";
    public static final String REGEX_PATTERN_ALPHA = "^[a-zA-Z]+$";
    public static final String REGEX_PATTERN_ALPHANUMERIC = "^[a-zA-Z0-9_@]*$";
    public static final String REGEX_PATTERN_NUMBER = "^[0-9]+$";
    public static final String REGEX_PATTERN_POSTALCODE = "^\\d{3}-\\d{4}";
    public static final String REGEX_PATTERN_MAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static String breakLine() {
        return MARK_BREAKLINE;
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    public static String fixedLength(String string, int length) {
        if (string.length() > length) {
            return new StringBuilder(string.substring(0, length)).append("...").toString();
        }
        return padRight(string, length);
    }

    public static String null2blank(Object object) {
        if (object == null) {
            return "";
        }
        return String.valueOf(object);
    }

    public static boolean match(String str, int min, int max, String regex) {
        if (min > 0 && UString.isNull(str)) {
            return false;
        }
        if (str.length() < min || str.length() > max) {
            return false;
        }
        return str.matches(regex);
    }

    public static boolean isNull(String str) {
        if (str == null) {
            return true;
        }
        return str.isEmpty() || "null".equals(str);
    }

    public static boolean isEMail(String str) {
        if (str == null) {
            return false;
        }
        return match(str, 1, 36, REGEX_PATTERN_MAIL);
    }

    public static boolean isNumber(String value) {
        if (UString.isNull(value)) {
            return false;
        }
        return value.matches("^[-+]?\\d+(\\.\\d+)?$");
    }

    public static int toNumber(String value, int defaultValue) {
        if (UString.isNumber(value)) {
            return Integer.parseInt(value);
        }
        return defaultValue;
    }

    public static long toNumber(String value, long defaultValue) {
        if (UString.isNumber(value)) {
            return Long.parseLong(value);
        }
        return defaultValue;
    }

    public static String asPostalCode(String value) {
        if (UString.isNull(value)) {
            return UString.EMPTY;
        }
        if (value.contains("-")) {
            return value;
        }
        return value.substring(0, 3).concat("-").concat(value.substring(3));
    }

    public static InputStream toStream(String text) throws Exception {
        InputStream in = new ByteArrayInputStream(text.getBytes("UTF-8"));
        return in;
    }

    public static String toString(InputStream inputStream) throws Exception {
        return IOUtils.toString(inputStream);
    }

    /**
     * クラスパスからStringを取得する
     *
     * @param loader クラスロード
     * @param path パス。例：jp.gtf.data.xxx.vm (頭文字「/」不要)
     * @return 結果
     */
    public static String loadStringFromClasspath(ClassLoader loader, String path) {
        try {
            return IOUtils.toString(loader.getResourceAsStream(path));
        } catch (IOException ex) {
            Logger.getLogger(UString.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String toString(Properties properties) throws Exception {
        StringWriter writer = new StringWriter();
        properties.list(new PrintWriter(writer));
        return writer.getBuffer().toString();
    }

    public static String toString(Object... objects) {
        StringBuilder sb = new StringBuilder();
        String px = "";
        for (Object obj : objects) {
            sb.append(px);
            sb.append(obj);
            px = ",";
        }
        return sb.toString();
    }

    public static <T> String toString(Iterable<T> objects) {
        StringBuilder sb = new StringBuilder();
        String px = "";
        for (Object obj : objects) {
            sb.append(px);
            sb.append(obj);
            px = ",";
        }
        return sb.toString();
    }

    public static String combine(String splitC, String... strs) {
        String prefix = "";
        StringBuilder sb = new StringBuilder();
        for (String str : strs) {
            sb.append(prefix);
            sb.append(str == null ? "" : str);
            prefix = splitC;
        }
        return sb.toString();
    }

    public static String getTextFromClasspath(String templatePath) {
        try {
            return IOUtils.toString(loader.getResourceAsStream(templatePath));
        } catch (IOException ex) {
            Logger.getLogger(UString.class.getName()).log(Level.SEVERE, null, ex);
        }
        return UString.EMPTY;
    }

    public static List<Long> splitToLongs(String value) {
        List<Long> ids = new ArrayList<>();
        for (String v : value.split(",", -1)) {
            if (UString.isNumber(v)) {
                ids.add(Long.parseLong(v.trim()));
            }
        }
        return ids;
    }

    public static List<Float> splitToFloats(String value) {
        List<Float> ids = new ArrayList<>();
        for (String v : value.split(",", -1)) {
            if (UString.isNumber(v)) {
                ids.add(Float.parseFloat(v.trim()));
            }
        }
        return ids;
    }

    public static String md5(String value) {
        return DigestUtils.md5DigestAsHex(value.getBytes());
    }

    public static String limitLength(String value, int maxLeng) {
        if (UString.isNull(value)) {
            return value;
        }
        if (value.length() > maxLeng - 3) {
            return new StringBuilder(value.substring(0, maxLeng - 3)).append("...").toString();
        }
        return value;
    }

    public static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Logger.getLogger(UString.class.getName()).log(Level.SEVERE, null, e);
        }
        return UString.EMPTY;
    }

    public static String escapeHtml(String value) {
        return StringEscapeUtils.escapeHtml4(value);
    }

    public static String escapeCsv(String value) {
        return StringEscapeUtils.escapeCsv(value);
    }

    public static String safeSubstring(String value, int start, int end) {
        if (UString.isNull(value)) {
            return UString.EMPTY;
        }
        return value.substring(start, Math.min(end, value.length()));
    }

    public static String safeSubstring(String value, int end) {
        return safeSubstring(value, 0, end);
    }

    public static String null2Zero(String value) {
        if (UString.isNull(value)) {
            return "0";
        }
        return value;
    }

    public static String asJavaType(String str) {
        if ("int".equals(str)) {
            return "int";
        }

        StringBuilder sb = new StringBuilder();
        for (String s : str.split("_|-", -1)) {
            sb.append(firstCharUp(s));
        }
        return sb.toString();
    }

    public static String asJavaVariable(String str) {
        if (UString.isNull(str)) {
            return "";
        }
        return firstCharLow(asJavaType(str));
    }

    public static String firstCharUp(String str) {
        if (UString.isNull(str)) {
            return "";
        }
        if (str.contains(".")) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public static String firstCharLow(String str) {
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    public static String getMultipleName(String name) {
        if (UString.isNull(name)) {
            throw new RuntimeException("input paramter is null");
        }
        if (name.endsWith("s") || name.endsWith("sh") || name.endsWith("ch")) {
            return name + "es";
        }
        if (!(name.endsWith("ay") || name.endsWith("iy") || name.endsWith("uy") || name.endsWith("ey") || name.endsWith("oy")) && name.endsWith("y")) {
            return name.substring(0, name.length() - 1) + "ies";
        }
        if (name.endsWith("f")) {
            return name.substring(0, name.length() - 1) + "ves";
        }
        if (name.endsWith("fe")) {
            return name.substring(0, name.length() - 2) + "ves";
        }
        return name + "s";
    }

    public static List<String> extractFromBrackets(String value) {
        if (value.contains("<")) {
            return extractCurlyBrackets(value);
        } else if (value.contains("(")) {
            return extractFromRoundBrackets(value);
        } else if (value.contains("{")) {
            return extractFromSquareBrackets(value);
        }
        return Collections.emptyList();
    }

    public static List<String> extractFromSquareBrackets(String value) {
        List<String> values = new ArrayList<>();
        Matcher m = Pattern.compile("\\{(.*?)\\}").matcher(value);
        while (m.find()) {
            values.add(m.group(1));
        }
        return values;
    }

    public static List<String> extractFromRoundBrackets(String value) {
        List<String> values = new ArrayList<>();
        Matcher m = Pattern.compile("\\((.*?)\\)").matcher(value);
        while (m.find()) {
            values.add(m.group(1));
        }
        return values;
    }

    public static List<String> extractCurlyBrackets(String value) {
        List<String> values = new ArrayList<>();
        Matcher m = Pattern.compile("<(.*?)>").matcher(value);
        while (m.find()) {
            values.add(m.group(1));
        }
        return values;
    }

    public static String base64Encode(String in) {
        return new String(Base64.encodeBase64(
                in.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8);
    }

    public static String base64Encode(byte[] in) {
        return new String(Base64.encodeBase64(in), StandardCharsets.UTF_8);
    }

    public static String base64Decode(byte[] in) {
        return new String(Base64.decodeBase64(in), StandardCharsets.UTF_8);
    }

    public static String base64Decode(String in) {
        return new String(Base64.decodeBase64(in.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8);
    }

    public static String prettyPrintAsHtml(String text) {
        StringBuilder sb = new StringBuilder();
        for (String line : text.split("(\r\n|\n)")) {
            if (line.startsWith("「") && line.endsWith("」")) {
                sb.append("<strong>").append(line).append("</strong>").append("<br/>");
            } else {
                sb.append(line).append("<br/>");
            }
        }
        return sb.toString();
    }

    /**
     * ファイルパスを正規化
     *
     * @param v ファイルパス
     * @return ファイルパス
     */
    public static String formatFilePath(final String v) {
        return v.replace('\\', '/');
    }

    /**
     * フォルダーパスを正規化
     *
     * @param v フォルダーパス
     * @return フォルダーパス
     */
    public static String formatFolderPath(final String v) {
        String x = formatFilePath(v);
        if (!x.endsWith("/")) {
            return new StringBuilder(x).append("/").toString();
        }
        return x;
    }
}
