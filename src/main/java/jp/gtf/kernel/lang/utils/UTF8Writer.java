package jp.gtf.kernel.lang.utils;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UTF8 File Writer
 *
 * @author F
 */
public class UTF8Writer implements Closeable {

    private File fileDir = null;
    private Writer out = null;

    /**
     * ファイルを開く
     *
     * @param file ファイル
     * @return UTF８
     */
    public static UTF8Writer open(String file) {
        UTF8Writer inst = new UTF8Writer();
        inst.fileDir = new File(file);
        try {
            inst.out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(inst.fileDir), "UTF8"));
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            Logger.getLogger(UTF8Writer.class.getName()).log(Level.SEVERE, null, e);
        }
        return inst;
    }

    /**
     * 行を追加
     *
     * @param value 値
     */
    public void append(String value) {
        try {
            out.write(value);
            out.write("\n");
        } catch (IOException e) {
            Logger.getLogger(UTF8Writer.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * 複数のオブジェクトを追加
     *
     * @param splitWord 囲む文字
     * @param values 値リスト
     */
    public void append(String splitWord, Object... values) {
        StringBuilder sb = new StringBuilder();
        String pref = "";
        for (Object obj : values) {
            sb.append(pref);
            sb.append(obj);
            pref = splitWord;
        }
        try {
            out.write(sb.toString());
            out.write("\n");
        } catch (IOException ex) {
            Logger.getLogger(UTF8Writer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * 行を追加<br>
     * 囲む文字は「,」
     *
     * @param values 値
     */
    public void append(Object... values) {
        StringBuilder sb = new StringBuilder();
        String pref = "";
        for (Object obj : values) {
            sb.append(pref);
            sb.append(obj);
            pref = ", ";
        }
        try {
            out.write(sb.toString());
            out.write("\n");
        } catch (IOException e) {
            Logger.getLogger(UTF8Writer.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * ファイルを閉じる
     */
    @Override
    public void close() {
        if (out != null) {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                Logger.getLogger(UTF8Writer.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        out = null;
    }
}
