/*
 * The MIT License
 *
 * Copyright 2019 F.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jp.gtf.kernel.lang.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
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

/**
 * 文字列操作
 *
 * @author F
 */
public class UString {

    private static ClassLoader loader = null;

    static {
        loader = UString.class.getClassLoader();
    }

    /**
     * 改行コードLF
     */
    public static final String LF = "\n";
    /**
     * 空文字列
     */
    public static final String EMPTY = "";
    /**
     * 記号: {@code #}
     */
    public static final String MARK_SHARP = "#";
    /**
     * 記号: {@code "}
     */
    public static final String MARK_SINGLE_QUOTE = "'";
    /**
     * 記号: {@code .}
     */
    public static final String MARK_DOT = ".";
    /**
     * 記号: {@code ./}
     */
    public static final String MARK_DOT_SLICKE = "./";
    /**
     * 記号: {@code =}
     */
    public static final String MARK_EQUALS = "=";
    /**
     * 記号: {@code _}
     */
    public static final String MARK_UNDERBAR = "_";
    /**
     * 記号: {@code /}
     */
    public static final String MARK_SLICE = "/";
    /**
     * 記号: {@code ,}
     */
    public static final String MARK_COMMANER = ",";
    /**
     * 記号: {@code ;}
     */
    public static final String MARK_SEMICOLON = ";";
    /**
     * 正規表現: {@code ;}
     */
    public static final String REGEX_MARK_DOT = "\\.";
    /**
     * 正規表現: {@code .*}
     */
    public static final String REGEX_PATTERN_ALL = ".*";
    /**
     * 正規表現: {@code ^[a-zA-Z0-9]+$}
     */
    public static final String REGEX_PATTERN_ALNUM = "^[a-zA-Z0-9]+$";
    /**
     * 正規表現: {@code ^[a-zA-Z]+$}
     */
    public static final String REGEX_PATTERN_ALPHA = "^[a-zA-Z]+$";
    /**
     * 正規表現: {@code ^[a-zA-Z0-9_@]*$}
     */
    public static final String REGEX_PATTERN_ALPHANUMERIC = "^[a-zA-Z0-9_@]*$";
    /**
     * 正規表現: {@code ^[0-9]+$}
     */
    public static final String REGEX_PATTERN_NUMBER = "^[0-9]+$";
    /**
     * 正規表現: {@code ^\\d{3}-\\d{4}}<br>
     * 郵便番号
     */
    public static final String REGEX_PATTERN_POSTALCODE = "^\\d{3}-\\d{4}";
    /**
     * 正規表現: メールアドレスマッチング<br>
     *
     */
    public static final String REGEX_PATTERN_MAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /**
     * Velocity Template等利用する
     *
     * @return 改行コード
     */
    public static String breakLine() {
        return "\n";
    }

    /**
     * 空白で右PADDINGする
     *
     * @param s 文字列
     * @param n 桁数
     * @return PADDING済み文字列
     */
    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    /**
     * 空白で左PADDINGする
     *
     * @param s 文字列
     * @param n 桁数
     * @return PADDING済み文字列
     */
    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    /**
     * 文字列はlengthを超える場合、"..."を追加する
     *
     * @param string 文字列
     * @param length 最大桁数
     * @return 結果
     */
    public static String fixedLength(String string, int length) {
        if (string.length() > length) {
            return new StringBuilder(string.substring(0, length)).append("...").toString();
        }
        return padRight(string, length);
    }

    /**
     * オブジェクトを文字列変換<br>
     * nullの場合、""を返却すうｒ
     *
     * @param object オブジェクト
     * @return 結果
     */
    public static String null2blank(Object object) {
        if (object == null) {
            return "";
        }
        return String.valueOf(object);
    }

    /**
     * 指定された文字列をチェックする
     *
     * @param str チェック対象文字列
     * @param min 最小桁数
     * @param max 最大桁数
     * @param regex 正規表現
     * @return チェック結果
     */
    public static boolean match(String str, int min, int max, String regex) {
        if (min > 0 && UString.isNull(str)) {
            return false;
        }
        if (str.length() < min || str.length() > max) {
            return false;
        }
        return str.matches(regex);
    }

    /**
     * 文字列は空白かを判断する
     *
     * @param str 文字列
     * @return 判断結果
     */
    public static boolean isNull(String str) {
        if (str == null) {
            return true;
        }
        return str.isEmpty() || "null".equals(str);
    }

    /**
     * 指定された文字列はメールアドレスではないかをチェックする
     *
     * @param str 文字列
     * @return 判断結果
     */
    public static boolean isEMail(String str) {
        if (str == null) {
            return false;
        }
        return match(str, 1, 36, REGEX_PATTERN_MAIL);
    }

    /**
     * 指定された文字列は数字（小数含め）ではないかをチェックする
     *
     * @param value 文字列
     * @return 判断結果
     */
    public static boolean isNumber(String value) {
        if (UString.isNull(value)) {
            return false;
        }
        return value.matches("^[-+]?\\d+(\\.\\d+)?$");
    }

    /**
     * 文字列を数字返却する
     *
     * @param value 文字列
     * @param defaultValue 規定値
     * @return 数字
     */
    public static int toNumber(String value, int defaultValue) {
        if (UString.isNumber(value)) {
            return Integer.parseInt(value);
        }
        return defaultValue;
    }

    /**
     * 文字列を数字返却する
     *
     * @param value 文字列
     * @param defaultValue 規定値
     * @return 数字
     */
    public static long toNumber(String value, long defaultValue) {
        if (UString.isNumber(value)) {
            return Long.parseLong(value);
        }
        return defaultValue;
    }

    /**
     * 文字列を郵便番号を整形する。<br>
     * input : 1234567<br>
     * output: 123-4567
     *
     * @param value 文字列
     * @return 郵便番号
     */
    public static String asPostalCode(String value) {
        if (UString.isNull(value)) {
            return UString.EMPTY;
        }
        if (value.contains("-")) {
            return value;
        }
        return value.substring(0, 3).concat("-").concat(value.substring(3));
    }

    /**
     * テキストとStream変換する
     *
     * @param text テキスト
     * @return Stream
     * @throws Exception Exception
     */
    public static InputStream toStream(String text) throws Exception {
        InputStream in = new ByteArrayInputStream(text.getBytes("UTF-8"));
        return in;
    }

    /**
     * Streamを文字列変換
     *
     * @param inputStream Stream
     * @return 文字列
     * @throws Exception Exception
     */
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

    /**
     * Java共通Propertiesファイルを文字列変換
     *
     * @param properties ファイル
     * @return 文字列
     * @throws Exception Exception
     */
    public static String toString(Properties properties) throws Exception {
        StringWriter writer = new StringWriter();
        properties.list(new PrintWriter(writer));
        return writer.getBuffer().toString();
    }

    /**
     * オブジェクトを文字列変換<br>
     * input: a,b,c,d <br>
     * output: "a,b,c,d"
     *
     * @param objects 複数オブジェクト
     * @return 文字列
     */
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

    /**
     * オブジェクトを文字列変換<br>
     * input: a,b,c,d <br>
     * output: "a,b,c,d"
     *
     * @param <T> オブジェクトタイプ
     * @param objects 複数オブジェクト
     * @return 文字列
     */
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

    /**
     * クラスパスからリソースを取得する
     *
     * @param resourceFilePath リソースパス
     * @return 文字列
     */
    public static String getTextFromClasspath(String resourceFilePath) {
        try {
            return IOUtils.toString(loader.getResourceAsStream(resourceFilePath));
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

    private static final Charset UTF8 = Charset.forName("UTF-8");

    public static String md5(String value) {
        return DigestUtils.md5DigestAsHex(value.getBytes(UTF8));
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
