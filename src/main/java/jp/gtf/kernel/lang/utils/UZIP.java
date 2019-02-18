/*
 * Code sample by FXD
 * more infromation please visit https://gtf.jp
 */
package jp.gtf.kernel.lang.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Deque;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * UZip
 *
 * @author FXD
 */
public class UZIP {

    /**
     * ファイルを圧縮する
     *
     * @param directory
     * @param zipfile
     * @throws IOException
     */
    public static void zip(File directory, File zipfile) throws IOException {
        URI base = directory.toURI();
        Deque<File> queue = new LinkedList<>();
        queue.push(directory);
        OutputStream out = new FileOutputStream(zipfile);

        try (ZipOutputStream zout = new ZipOutputStream(out)) {
            while (!queue.isEmpty()) {
                directory = queue.pop();
                for (File kid : directory.listFiles()) {
                    String name = base.relativize(kid.toURI()).getPath();
                    if (kid.isDirectory()) {
                        queue.push(kid);
                        name = name.endsWith("/") ? name : name + "/";
                        zout.putNextEntry(new ZipEntry(name));
                    } else {
                        zout.putNextEntry(new ZipEntry(name));
                        copy(kid, zout);
                        zout.closeEntry();
                    }
                }
            }
        }
    }

    /**
     * アンZIPする
     *
     * @param zipFilePath
     * @param dir
     * @throws IOException
     */
    public static void unzip(File zipFilePath, File dir) throws IOException {
        String destDir = dir.getAbsolutePath();
        FileInputStream fis;
        byte[] buffer = new byte[1024];
        fis = new FileInputStream(zipFilePath);
        try (ZipInputStream zis = new ZipInputStream(fis)) {
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                if (ze.isDirectory()) {
                    boolean mkdirs = newFile.mkdirs();
                    if (!mkdirs) {
                        throw new IOException("Unable to create path");
                    }
                } else {
                    if (!newFile.getParentFile().mkdirs()) {
                        throw new IOException("Unable to create path");
                    }
                    try (FileOutputStream fos = new FileOutputStream(newFile.getAbsolutePath())) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                System.out.println("Unzipping to " + newFile.getAbsolutePath());
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
        }
        fis.close();
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        while (true) {
            int readCount = in.read(buffer);
            if (readCount < 0) {
                break;
            }
            out.write(buffer, 0, readCount);
        }
    }

    private static void copy(File file, OutputStream out) throws IOException {
        try (InputStream in = new FileInputStream(file)) {
            copy(in, out);
        }
    }

}
