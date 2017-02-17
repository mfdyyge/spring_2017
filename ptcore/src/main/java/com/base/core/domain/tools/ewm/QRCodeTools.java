package com.base.core.domain.tools.ewm;

import com.base.core.domain.tools.BaseTools;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Random;

/**
 * <ol>
 * <li>创建文档 2015-01-28</li>
 * <li>二维码生成、解析辅助工具类</li>
 * <li>添加生成有文件名的二维码(内嵌LOGO)并存放在指定目录中 encodePathFile</li>
 * </ol>
 *
 * @author <a href="mailto:12719889@qq.com">YangHongJian</a>
 * @version 2.9.x
 * @since 1.6
 */
public class QRCodeTools {
    private static final String CHARSET = "utf-8";
    private static final String FORMAT_NAME = "JPG";
    // 二维码尺寸
    private static int QRCODE_SIZE = 200;
    // LOGO宽度
    private static int WIDTH = 30;
    // LOGO高度
    private static int HEIGHT = 30;
    private static int MARGIN = 1;

    static {
        String qrcodeSize = BaseTools.getPropertiesByKey("RWM_QRCODE_SIZE");
        QRCODE_SIZE = StringUtils.isEmpty(qrcodeSize) ? 200 : Integer.parseInt(qrcodeSize);
        String logoWidth = BaseTools.getPropertiesByKey("RWM_RQCODE_LOGO_WIDTH");
        WIDTH = StringUtils.isEmpty(logoWidth) ? 30 : Integer.parseInt(logoWidth);
        String logoHeight = BaseTools.getPropertiesByKey("RWM_RQCODE_LOGO_HEIGHT");
        HEIGHT = StringUtils.isEmpty(logoHeight) ? 30 : Integer.parseInt(logoHeight);
        String margin = BaseTools.getPropertiesByKey("RWM_MARGIN");
        MARGIN = StringUtils.isEmpty(margin) ? 1 : Integer.parseInt(margin);
    }

    //该方法生成自定义白边框后的bitMatrix；
    private static BitMatrix updateBit(BitMatrix matrix, int margin) {
        int tempM = margin * 2;
        int[] rec = matrix.getEnclosingRectangle();   //获取二维码图案的属性
        int resWidth = rec[2] + tempM;
        int resHeight = rec[3] + tempM;
        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight); // 按照自定义边框生成新的BitMatrix
        resMatrix.clear();
        for (int i = margin; i < resWidth - margin; i++) {   //循环，将二维码图案绘制到新的bitMatrix中
            for (int j = margin; j < resHeight - margin; j++) {
                if (matrix.get(i - margin + rec[0], j - margin + rec[1])) {
                    resMatrix.set(i, j);
                }
            }
        }
        return resMatrix;
    }

    /**
     * 图片放大缩小
     */
    public static BufferedImage zoomInImage(BufferedImage originalImage, int width, int height) {
        BufferedImage newImage = new BufferedImage(width, height, originalImage.getType());
        Graphics g = newImage.getGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return newImage;

    }

    private static BufferedImage createImage(String content, String imgPath,
                                             boolean needCompress, int imgWidth, int imgHeight) throws Exception {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        //二维码容错率，分四个等级：H、L 、M、 Q
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        //设置二维码空白边框的大小 1-4，1是最小 4是默认
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix;
        if (imgWidth != 0 && imgHeight != 0) {
            bitMatrix = new MultiFormatWriter().encode(content,
                    BarcodeFormat.QR_CODE, imgWidth, imgHeight, hints);
        } else {
            bitMatrix = new MultiFormatWriter().encode(content,
                    BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        }
        //修改生成的二维码白边边框宽度和大小符合要求
//        int margin = 1;  //自定义白边边框宽度
        bitMatrix = updateBit(bitMatrix, MARGIN);
        //因为二维码生成时，白边无法控制，去掉原有的白边，再添加自定义白边后，
        //二维码大小与size大小就存在差异了，为了让新生成的二维码大小还是size大小，
        //根据设置的图片大小size重新生成图片是一致的

        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();

        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000
                        : 0xFFFFFFFF);
            }
        }
        //修改对象
        if (imgWidth != 0 && imgHeight != 0)
            image = zoomInImage(image, imgWidth, imgHeight);//根据size放大、缩小生成的二维码
        else
            image = zoomInImage(image, QRCODE_SIZE, QRCODE_SIZE);//根据size放大、缩小生成的二维码

//        ImageIO.write(bi, "png", target); //生成二维码图片


        if (imgPath == null || "".equals(imgPath)) {
            return image;
        }
        // 插入图片
        QRCodeTools.insertImage(image, imgPath, needCompress);
        return image;
    }

    private static BufferedImage createImageOld(String content, String imgPath,
                                                boolean needCompress, int imgWidth, int imgHeight) throws Exception {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        //二维码容错率，分四个等级：H、L 、M、 Q
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        //设置二维码空白边框的大小 1-4，1是最小 4是默认
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix;
        if (imgWidth != 0 && imgHeight != 0) {
            bitMatrix = new MultiFormatWriter().encode(content,
                    BarcodeFormat.QR_CODE, imgWidth, imgHeight, hints);
        } else {
            bitMatrix = new MultiFormatWriter().encode(content,
                    BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        }
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000
                        : 0xFFFFFFFF);
            }
        }
        if (imgPath == null || "".equals(imgPath)) {
            return image;
        }
        // 插入图片
        QRCodeTools.insertImage(image, imgPath, needCompress);
        return image;
    }

    /**
     * 插入LOGO
     *
     * @param source       二维码图片
     * @param imgPath      LOGO图片地址
     * @param needCompress 是否压缩
     * @throws Exception
     */
    private static void insertImage(BufferedImage source, String imgPath,
                                    boolean needCompress) throws Exception {
        File file = new File(imgPath);
        if (!file.exists()) {
            System.err.println("" + imgPath + "   该文件不存在！");
            return;
        }
        Image src = ImageIO.read(new File(imgPath));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (needCompress) { // 压缩LOGO
            if (width > WIDTH) {
                width = WIDTH;
            }
            if (height > HEIGHT) {
                height = HEIGHT;
            }
            Image image = src.getScaledInstance(width, height,
                    Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

    /**
     * 生成二维码(内嵌LOGO)
     *
     * @param content      内容
     * @param imgPath      LOGO地址
     * @param destPath     存放目录
     * @param needCompress 是否压缩LOGO
     * @param imgWidth     生成二维码的宽 0为取默认
     * @param imgHeight    生成二维码的高 0为取默认
     * @throws Exception
     */
    public static void encode(String content, String imgPath, String destPath,
                              boolean needCompress, int imgWidth, int imgHeight) throws Exception {
        BufferedImage image = QRCodeTools.createImage(content, imgPath,
                needCompress, imgWidth, imgHeight);
        mkdirs(destPath);
        String file = new Random().nextInt(99999999) + ".jpg";
        ImageIO.write(image, FORMAT_NAME, new File(destPath + "/" + file));
    }

    /**
     * 当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
     *
     * @param destPath 存放目录
     */
    public static void mkdirs(String destPath) {
        File file = new File(destPath);
        //当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }

    /**
     * 生成二维码(内嵌LOGO)
     *
     * @param content   内容
     * @param imgPath   LOGO地址
     * @param destPath  存储地址
     * @param imgWidth  生成二维码的宽
     * @param imgHeight 生成二维码的高
     * @throws Exception
     */
    public static void encode(String content, String imgPath, String destPath, int imgWidth, int imgHeight)
            throws Exception {
        QRCodeTools.encode(content, imgPath, destPath, false, imgWidth, imgHeight);
    }

    /**
     * 生成二维码
     *
     * @param content      内容
     * @param destPath     存储地址
     * @param needCompress 是否压缩LOGO
     * @param imgWidth     生成二维码的宽
     * @param imgHeight    生成二维码的高
     * @throws Exception
     */
    public static void encode(String content, String destPath,
                              boolean needCompress, int imgWidth, int imgHeight) throws Exception {
        QRCodeTools.encode(content, null, destPath, needCompress, imgWidth, imgHeight);
    }

    /**
     * 生成二维码
     *
     * @param content   内容
     * @param destPath  存储地址
     * @param imgWidth  生成二维码的宽
     * @param imgHeight 生成二维码的高
     * @throws Exception
     */
    public static void encode(String content, String destPath, int imgWidth, int imgHeight) throws Exception {
        QRCodeTools.encode(content, null, destPath, false, imgWidth, imgHeight);
    }

    /**
     * 生成二维码(内嵌LOGO)
     *
     * @param content      内容
     * @param imgPath      LOGO地址
     * @param output       输出流
     * @param needCompress 是否压缩LOGO
     * @param imgWidth     生成二维码的宽
     * @param imgHeight    生成二维码的高
     * @throws Exception
     */
    public static void encode(String content, String imgPath,
                              OutputStream output, boolean needCompress,
                              int imgWidth, int imgHeight) throws Exception {
        BufferedImage image = QRCodeTools.createImage(content, imgPath,
                needCompress, imgWidth, imgHeight);
        ImageIO.write(image, FORMAT_NAME, output);
    }

    /**
     * 生成有文件名的二维码(内嵌LOGO)并存放在指定目录中
     *
     * @param content      内容
     * @param imgPath      LOGO地址
     * @param destPath     存放目录
     * @param destFileName 存放名称(注意扩展名为jpg)
     * @param needCompress 是否压缩LOGO
     * @param imgWidth     生成二维码的宽
     * @param imgHeight    生成二维码的高
     * @throws Exception
     */
    public static void encodePathFile(String content, String imgPath,
                                      String destPath, String destFileName,
                                      boolean needCompress, int imgWidth, int imgHeight) throws Exception {
        BufferedImage image = QRCodeTools.createImage(content, imgPath,
                needCompress, imgWidth, imgHeight);
        mkdirs(destPath);
        ImageIO.write(image, FORMAT_NAME, new File(destPath + File.separator + destFileName));
    }

    /**
     * 生成有文件名的二维码(内嵌LOGO)并存放在指定目录中
     *
     * @param content      内容
     * @param imgPath      LOGO地址
     * @param destPath     存储地址
     * @param destFileName 存放名称(注意扩展名为jpg)
     * @param imgWidth     生成二维码的宽
     * @param imgHeight    生成二维码的高
     * @throws Exception
     */
    public static void encodePathFile(String content, String imgPath, String destPath,
                                      String destFileName, int imgWidth, int imgHeight) throws Exception {
        QRCodeTools.encodePathFile(content, imgPath, destPath, destFileName, false, imgWidth, imgHeight);
    }

    /**
     * 生成有文件名的二维码(内嵌LOGO)并存放在指定目录中
     *
     * @param content      内容
     * @param destPath     存储地址
     * @param needCompress 是否压缩LOGO
     * @param destFileName 存放名称(注意扩展名为jpg)
     * @param imgWidth     生成二维码的宽
     * @param imgHeight    生成二维码的高
     * @throws Exception
     */
    public static void encodePathFile(String content, String destPath, String destFileName,
                                      boolean needCompress, int imgWidth, int imgHeight) throws Exception {
        QRCodeTools.encodePathFile(content, null, destPath, destFileName, needCompress, imgWidth, imgHeight);
    }

    /**
     * 生成有文件名的二维码(内嵌LOGO)并存放在指定目录中
     *
     * @param content      内容
     * @param destPath     存储地址
     * @param destFileName 存放名称(注意扩展名为jpg)
     * @param imgWidth     生成二维码的宽
     * @param imgHeight    生成二维码的高
     * @throws Exception
     */
    public static void encodePathFile(String content, String destPath, String destFileName,
                                      int imgWidth, int imgHeight) throws Exception {
        QRCodeTools.encodePathFile(content, null, destPath, destFileName, false, imgWidth, imgHeight);
    }


    /**
     * 生成二维码
     *
     * @param content   内容
     * @param output    输出流
     * @param imgWidth  生成二维码的宽 0为默认
     * @param imgHeight 生成二维码的高 0为默认
     * @throws Exception
     */
    public static void encode(String content, OutputStream output, int imgWidth, int imgHeight)
            throws Exception {
        QRCodeTools.encode(content, null, output, false, imgWidth, imgHeight);
    }

    /**
     * 解析二维码
     *
     * @param file 二维码图片
     * @return 解析结果
     * @throws Exception
     */
    public static String decode(File file) throws Exception {
        BufferedImage image;
        image = ImageIO.read(file);
        if (image == null) {
            return null;
        }
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(
                image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result;
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
        hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
        result = new MultiFormatReader().decode(bitmap, hints);
        return result.getText();
    }

    /**
     * 解析二维码
     *
     * @param path 二维码图片地址
     * @return 解析结果
     * @throws Exception
     */
    public static String decode(String path) throws Exception {
        return QRCodeTools.decode(new File(path));
    }

    public static void main(String[] args) throws Exception {
        String text = "http://user.qzone.qq.com/12719889/infocenter?ptsig=*1CfU*n69al7XDOeNHR1hLFipHIw0FBNLT1wcIBIKzk_";
//        QRCodeUtil.encode(text, "c:/me.jpg", "c:/barcode", true);
//        BaseTools.getInstanceCtx("base-ptcore.xml");

//        QRCodeTools.encode(text, "", BaseTools.getPropertiesByKey("RWM_PATH"), true,0,0);
        encodePathFile(text, BaseTools.getPropertiesByKey("RWM_PATH"), "yhj_qq.jpg", true, 0, 0);
    }
}
