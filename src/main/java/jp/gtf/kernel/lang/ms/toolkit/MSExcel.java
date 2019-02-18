package jp.gtf.kernel.lang.ms.toolkit;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFPicture;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * MSEXCEL操作
 *
 * @author F
 */
public class MSExcel {

    /**
     * ファイル入力ストリーム
     */
    protected FileInputStream inputStream = null;
    /**
     * EXCELファイルを保有する
     */
    protected File file = null;
    /**
     * EXCELのXSSFWorkbook
     */
    protected XSSFWorkbook workbook = null;
    /**
     * EXCELのXSSFSheet
     */
    protected XSSFSheet sheet = null;
    /**
     * CreationHelper写真等挿入する用
     */
    protected CreationHelper helper = null;
    /**
     * 数式を計算するよう
     */
    protected FormulaEvaluator evaluator = null;

    /**
     * ファイルを開く
     *
     * @param file ファイル
     */
    public void open(File file) {
        try {
            this.file = file;
            this.inputStream = new FileInputStream(file);
            this.workbook = new XSSFWorkbook(inputStream);
            this.helper = workbook.getCreationHelper();
            this.evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        } catch (IOException e) {
            Logger.getLogger(MSExcel.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * EXCELを新規作成
     */
    public void create() {
        this.workbook = new XSSFWorkbook();
        this.helper = workbook.getCreationHelper();
    }

    /**
     * シートを作成
     *
     * @param sheetname シート名
     */
    public void createSheet(String sheetname) {
        sheet = workbook.createSheet(sheetname);
        rowCnt = 0;
    }

    /**
     * シートを選択する
     *
     * @param sheetIndex シート番号
     */
    public void activeSheet(int sheetIndex) {
        sheet = workbook.getSheetAt(sheetIndex);
        rowCnt = 0;
    }

    /**
     * シートを選択する
     *
     * @param sheetname シート名
     */
    public void activeSheet(String sheetname) {
        sheet = workbook.getSheet(sheetname);
        rowCnt = 0;
    }

    /**
     * 閉じる
     */
    public void close() {
        try {
            inputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(MSExcel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private File convertImage(String in) {
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(new File(in));
            BufferedImage newBufferedImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            newBufferedImage.createGraphics().drawImage(bufferedImage, 0, 0, Color.WHITE, null);
            File tmpFile = File.createTempFile("tmp", null);
            ImageIO.write(newBufferedImage, "jpg", tmpFile);
            return tmpFile;
        } catch (IOException e) {
            Logger.getLogger(MSExcel.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }

    private int rowCnt = 0;

    /**
     * 一行データを挿入する
     *
     * @param objects オブジェクト
     */
    public void append(List<?> objects) {
        Row row = sheet.createRow((short) rowCnt);
        int i = 0;
        for (Object object : objects) {
            Cell cell = row.createCell(i);
            cell.setCellValue(String.valueOf(object));
            i++;
        }
        rowCnt++;
    }

    /**
     * 一行データを挿入する
     *
     * @param objects オブジェクト
     */
    public void append(Object... objects) {
        Row row = sheet.createRow((short) rowCnt);
        for (int i = 0; i < objects.length; ++i) {
            Cell cell = row.createCell(i);
            cell.setCellValue(String.valueOf(objects[i]));
        }
        rowCnt++;
    }

    /**
     * EXCELシートのセル幅を調整する
     *
     * @param sheetName シート名
     * @param indexs シート番号
     */
    public void autoSizeColumn(String sheetName, int... indexs) {
        XSSFSheet tmpSheet = workbook.getSheet(sheetName);
        if (null == tmpSheet) {
            return;
        }
        for (int i : indexs) {
            tmpSheet.autoSizeColumn(i);
        }
    }

    /**
     * 写真を挿入する
     *
     * @param imagePath 写真パス
     * @param x1 座標X（セル番号）
     * @param y1 座標Y（セル番号）
     * @throws Exception Exception
     */
    public void insertImage(String imagePath, int x1, int y1) throws Exception {
        insertImage(imagePath, x1, y1, -1, -1);
    }

    /**
     * 写真を挿入する
     *
     * @param imagePath 写真パス
     * @param x1 座標X（セル番号）
     * @param y1 座標Y（セル番号）
     * @param x2 座標X1（セル番号）
     * @param y2 座標Y1（セル番号）
     * @throws Exception Exception
     */
    public void insertImage(String imagePath, int x1, int y1, int x2, int y2) throws Exception {
        File tmpFile = null;
        if (imagePath.endsWith(".png") || imagePath.endsWith(".PNG")) {
            tmpFile = convertImage(imagePath);
        }

        try (FileInputStream stream = new FileInputStream(tmpFile == null ? new File(imagePath) : tmpFile)) {
            XSSFDrawing drawing = sheet.createDrawingPatriarch();
            ClientAnchor anchor = helper.createClientAnchor();
            anchor.setAnchorType(ClientAnchor.MOVE_AND_RESIZE);
            int pictureIndex = workbook.addPicture(IOUtils.toByteArray(stream), Workbook.PICTURE_TYPE_PNG);
            anchor.setCol1(x1);
            anchor.setRow1(y1);
            if (x2 > 0) {
                anchor.setCol2(x2);
            }
            if (y2 > 0) {
                anchor.setRow2(y2);
            }
            XSSFPicture pict = drawing.createPicture(anchor, pictureIndex);
            pict.resize();
        }

        if (tmpFile != null) {
            tmpFile.deleteOnExit();
        }
    }
//
//    /**
//     * テキストボクスを挿入する
//     * @param text テキスト
//     * @param dx1 dx1
//     * @param dy1 dy1
//     * @param dx2 dx2
//     * @param dy2 dy2
//     * @param col1 col1
//     * @param row1 row1
//     * @param col2 col2
//     * @param row2  row2
//     */
//    public void insertTextbox(String text, int dx1, int dy1, int dx2, int dy2, int col1, int row1, int col2, int row2) {
//        XSSFDrawing patriarch = sheet.createDrawingPatriarch();
//        XSSFTextBox textBox = patriarch.createTextbox(new XSSFClientAnchor(dx1, dy1, dx2, dy2, col1, row1, col2, row2));
//        textBox.setLineStyle(HSSFShape.LINESTYLE_SOLID);
//        textBox.setLineWidth(1);
//        XSSFRichTextString textContent = new XSSFRichTextString(text);
//        textBox.setText(textContent);
//    }
//
//    /**
//     * テキストボクスを挿入する
//     * @param text text
//     * @param col1 col1
//     * @param row1 row1
//     * @param col2 col2
//     * @param row2  row2
//     */
//    public void insertTextbox(String text, int col1, int row1, int col2, int row2) {
//        insertTextbox(text, 0, 0, 0, 0, col1, row1, col2, row2);
//    }

    /**
     * X,YからFrezzする
     *
     * @param x セルのX
     * @param y セルのY
     */
    public void freeze(int x, int y) {
        sheet.createFreezePane(x, y);
    }

    /**
     * セルから値を取得する
     *
     * @param cell セル
     * @return セルの値
     */
    public String getCellValue(Cell cell) {
        if (cell == null) {
            return EMPTY;
        }
        CellValue cellValue = evaluator.evaluate(cell);
        if (cellValue == null) {
            return EMPTY;
        }
        switch (cellValue.getCellType()) {
            case Cell.CELL_TYPE_BOOLEAN:
                return String.valueOf(cellValue.getBooleanValue());
            case Cell.CELL_TYPE_NUMERIC:
                return String.valueOf(cellValue.getNumberValue());
            case Cell.CELL_TYPE_STRING:
                return cellValue.getStringValue();
            case Cell.CELL_TYPE_BLANK:
                return EMPTY;
            case Cell.CELL_TYPE_ERROR:
                throw new RuntimeException(
                        "CELL_TYPE_ERROR->"
                        + sheet.getSheetName()
                        + " ["
                        + cell.getRowIndex()
                        + ":"
                        + cell.getColumnIndex()
                        + "]");
            // CELL_TYPE_FORMULA will never happen
            case Cell.CELL_TYPE_FORMULA:
                throw new RuntimeException(
                        "CELL_TYPE_FORMULA->"
                        + sheet.getSheetName()
                        + " ["
                        + cell.getRowIndex()
                        + ":"
                        + cell.getColumnIndex()
                        + "]");
        }
        return null;
    }

    /**
     * シートからデータを処理する
     *
     * @param sheetName シート名
     * @param startRow 開始行
     * @param maxRowCount 最大処理行数
     * @param reader 行処理プロセス
     */
    public void readFromSheet(String sheetName, int startRow, int maxRowCount, IExcelRowReader reader) {
        activeSheet(sheetName);
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getRowNum() < startRow) {
                continue;
            }
            if (!reader.parserByRow(row.getRowNum(), row2Values(row, maxRowCount))) {
                break;
            }
        }
    }

    /**
     * EXCEL行の読み取りクラス
     */
    public interface IExcelRowReader {

        /**
         * 各行を読み込む
         *
         * @param rowNumber 行番号
         * @param values 値
         * @return 後続処理有無（TRUEの場合、処理続ける）
         */
        public boolean parserByRow(int rowNumber, String... values);
    }

    /**
     * 一行の値を取得する<br>
     * COUNTを未満の場合、nullを詰め
     *
     * @param row 行
     * @param count 最低保証アカウント
     * @return 行データ
     */
    protected String[] row2Values(Row row, int count) {
        List<String> values = new ArrayList<>();
        for (int i = 0; i < count; ++i) {
            values.add(getCellValue(row.getCell(i)));
        }
        return values.toArray(new String[count]);
    }

    /**
     * 指定された場所に保存する
     *
     * @param outputFile 指定されたファイル
     */
    public void save(File outputFile) {
        if (!outputFile.getParentFile().exists()) {
            boolean mkdirs = outputFile.getParentFile().mkdirs();
            if (!mkdirs) {
                return;
            }
        }
        try {
            try (FileOutputStream out = new FileOutputStream(outputFile)) {
                workbook.write(out);
            }
        } catch (IOException e) {
            Logger.getLogger(MSExcel.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * 指定された場所に保存する
     *
     * @param path ファイルパス
     */
    public void save(String path) {
        save(new File(path));
    }

    /**
     * 保存する
     */
    public void save() {
        save(file);
    }

    private static final String EMPTY = "";
}
