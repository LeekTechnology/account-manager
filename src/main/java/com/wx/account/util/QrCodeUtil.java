package com.wx.account.util;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.wx.account.model.User;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码工具类
 */
public class QrCodeUtil {

    private static Logger logger = LoggerFactory.getLogger(QrCodeUtil.class);

    public static void main(String[] args) throws WriterException {

        try {

            String content = "niIEDPFetxbTKCFYAZ6asrrfHYGIaqSsUr7q8+jcciz6+m0sgdxPCZiVLsASh5ofQrTAA33d2tmZAOmu2MtEeA9wh9t3kSLUMi8REqr+a3Zd+Oa1OXsGWZOYEqdQ5E/YnIu/XKSweIg3r7jsd4dIEFiYeWBrSQZLDzRyZIu5o+BLpKk6oGm0RbCX8ylX0/DAMlWLecYZm9dWIx9gMVZ2NGr4dqTljnZOoWpwbJkvb8oPJyX21Fb+n+Yq1hAI6JLozF5w662N0nBUvK87UIQzOnK323rfmCZzgIZRpuoIzgsqcVmgjo3ez/MeksHk9HJ8pgBOjtbv+wWlTZ/WVIvrk2gJ4kje8pFssFGpsdxLR5Qe3wxg/IrJ48lsp+S2f4i1LowEygUDeEzTJi+34zHj2IX2s+uk+8BDxMfKVIDsmyJhfnJUpvFyM5kzEU/cPk84+isAI8kbqBNOg5Rj61I8GVV0lwxFf622BNCh/itZMH+xIc9ds7d7QFleJluE/ikMsTHkawgZTTbIT/F//QSmbh3B5MjKc6rqfjPI9X7Bhldywf7FGgVyVzNpT17ZHnW5gMGbp3gHQ8A6/64IHzPwk2X+jGZL6mtKf20xaJyI8l/BTw7HAXx9gRqZj3FkGD3Z8rnvHqjN3NfkUUr43vuK2xG5jjjcCPpYOhT9qUrw/pJXmMSyVc85+cRUf0kYCLQP+k+tndonaeYkkMJfSqYkS8HveRAeOjsCCgQeH3uOFeH3BKcUvVoAgZo8qPYiZkncAeedgSd38PSjOLsH3D3EdxfUK6+S926ELsblw2kdrZNNLPdJoRyDE/MzfKc2aR5eyG3esX/KA6z6HmbcT/Mq7Lq87JPQKLbxmwZ8ImjNUccjqrmhvU2biyOquaG9TZuL";

            String ticketInfoUrl = String.format(ConstantUtils.ticketInfo, ConstantUtils.accessToken);

            User user = null;
            String userInfoUrl = String.format(ConstantUtils.userInfoUrl, ConstantUtils.accessToken, "wx7a07162d1a779f62");
            String result = HttpUtil.get(userInfoUrl);
            if (result != null) {
                JSONObject json = JSONObject.parseObject(result);
                user = JSONObject.parseObject(json.toJSONString(), User.class);
            }


            createQrCodeImage(ticketInfoUrl, "F:\\images\\11.png", user.getHeadimgurl() , 300, 300);
            addLogoToQRCode(new File("F:\\image\\55.png"), new File("F:\\images\\11.png"), new LogoConfig());
            /**********************************************************/
//            String content = "http://weixin.qq.com/q/w0hPEGjlfcUEyn_RhGAJ";
//            File file = new File("D:/", new Date().getTime() + ".jpg");
//
//            QrCodeUtil zp = new QrCodeUtil();
//
//            BufferedImage bim = zp.getQRcodeBufferedImage(content, BarcodeFormat.QR_CODE, 1600, 1600, zp.getDecodeHintType());
//
//            ImageIO.write(bim, "jpeg", file);
//
//            zp.addLogoToQRCode(file, new File("D:/wxlogo.png"), new LogoConfig());

            //Thread.sleep(5000);
            //zp.parseQRCodeImage(new File("D:/newPic.jpg"));

            //addLogoToQRCode(new File("D:/789.jpg"),new File("D:/234.png"),new LogoConfig());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建二维码
     *
     * @param content  二维码内容
     * @param path     二维码图片路径
     * @param logoPath 二维码中logo的路径
     * @param width    宽度
     * @param height   高度
     * @return
     */
    public static String createQrCodeImage(String content, String path, String logoPath, int width, int height) {

        if (StringUtils.isEmpty(content)) {
            return "";
        }

        try {
            File file = new File(path);

            BufferedImage bim = getQRcodeBufferedImage(content, BarcodeFormat.QR_CODE, width, height, getDecodeHintType());

            ImageIO.write(bim, "png", file);

            if (StringUtils.isNotBlank(logoPath)) {
                addLogoToQRCode(file, new File(logoPath), new LogoConfig());
            }

            return path;
        } catch (IOException e) {
            logger.info("生成二维码失败!");
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 给二维码图片添加Logo
     *
     * @param qrPic
     * @param logoPic
     */
    public static void addLogoToQRCode(File qrPic, File logoPic, LogoConfig logoConfig) {
        try {
            if (!qrPic.isFile() || !logoPic.isFile()) {
                System.out.print("file not find !");
            }

            /**
             * 读取二维码图片，并构建绘图对象
             */
            BufferedImage image = ImageIO.read(qrPic);
            Graphics2D g = image.createGraphics();

            /**
             * 读取Logo图片
             */
            BufferedImage logo = ImageIO.read(logoPic);

            /**
             * 设置logo的大小,本人设置为二维码图片的20%,因为过大会盖掉二维码
             */
            int widthLogo = logo.getWidth() > image.getWidth() * 2 / 10 ? (image.getWidth() * 2 / 10) : logo.getWidth();
            int heightLogo = logo.getHeight() > image.getHeight() * 2 / 10 ? (image.getHeight() * 2 / 10) : logo.getWidth();

            // 计算图片放置位置
            int x = (image.getWidth() - widthLogo) / 2;
            int y = (image.getHeight() - widthLogo) / 2;

            //开始绘制图片
            g.drawImage(logo, x, y, widthLogo, heightLogo, null);
            g.drawRoundRect(x, y, widthLogo, heightLogo, 15, 15);
            g.setStroke(new BasicStroke(logoConfig.getBorder()));
            g.setColor(logoConfig.getBorderColor());
            g.drawRect(x, y, widthLogo, heightLogo);

            g.dispose();

            ImageIO.write(image, "png", qrPic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addLogoToQRCodeBound(File qrPic, File logoPic, LogoConfig logoConfig) {
        try {
            if (!qrPic.isFile() || !logoPic.isFile()) {
                System.out.print("file not find !");
            }

            /**
             * 读取二维码图片，并构建绘图对象
             */
            BufferedImage image = ImageIO.read(qrPic);
            Graphics2D g = image.createGraphics();

            /**
             * 读取Logo图片
             */
            BufferedImage logo = ImageIO.read(logoPic);

            /**
             * 设置logo的大小,本人设置为二维码图片的20%,因为过大会盖掉二维码
             */
            int widthLogo = logo.getWidth() > image.getWidth() * 2 / 10 ? (image.getWidth() * 2 / 7) : logo.getWidth();
            int heightLogo = logo.getHeight() > image.getHeight() * 2 / 10 ? (image.getWidth() * 2 / 7) : logo.getWidth();

            // 计算图片放置位置
            int x = (image.getWidth() - widthLogo*2);
            int y = (image.getHeight() - widthLogo*2);

            //开始绘制图片
            g.drawImage(logo, x, y, widthLogo, heightLogo, null);
            g.drawRoundRect(x, y, widthLogo, heightLogo, 15, 15);
            g.setStroke(new BasicStroke(logoConfig.getBorder()));
            g.setColor(logoConfig.getBorderColor());
            g.drawRect(x, y, widthLogo, heightLogo);

            g.dispose();

            ImageIO.write(image, "png", qrPic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 二维码的解析
     *
     * @param file
     */
    public static void parseQRCodeImage(File file) {
        try {
            MultiFormatReader formatReader = new MultiFormatReader();

            // File file = new File(filePath);
            if (!file.exists()) {
                return;
            }

            BufferedImage image = ImageIO.read(file);

            LuminanceSource source = new BufferedImageLuminanceSource(image);
            Binarizer binarizer = new HybridBinarizer(source);
            BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);

            Map hints = new HashMap();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            Result result = formatReader.decode(binaryBitmap, hints);

            System.out.println("result = " + result.toString());
            System.out.println("resultFormat = " + result.getBarcodeFormat());
            System.out.println("resultText = " + result.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将二维码生成为文件
     *
     * @param bm
     * @param imageFormat
     * @param file
     */
    public static void decodeQRCodeToImageFile(BitMatrix bm, String imageFormat, File file) {
        try {
            if (null == file || file.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("文件异常，或扩展名有问题！");
            }

            BufferedImage bi = fileToBufferedImage(bm);
            ImageIO.write(bi, "jpeg", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将二维码生成为输出流
     *
     * @param bm
     * @param imageFormat
     * @param os
     */
    public static void decodeQRCodeToOutputStream(BitMatrix bm, String imageFormat, OutputStream os) {
        try {
            BufferedImage image = fileToBufferedImage(bm);
            ImageIO.write(image, imageFormat, os);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建初始化二维码
     *
     * @param bm
     * @return
     */
    public static BufferedImage fileToBufferedImage(BitMatrix bm) {
        BufferedImage image = null;
        try {
            int w = bm.getWidth(), h = bm.getHeight();
            image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    image.setRGB(x, y, bm.get(x, y) ? 0xFF000000 : 0xFFCCDDEE);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 生成二维码bufferedImage图片
     *
     * @param content       编码内容
     * @param barcodeFormat 编码类型
     * @param width         图片宽度
     * @param height        图片高度
     * @param hints         设置参数
     * @return
     */
    public static BufferedImage getQRcodeBufferedImage(String content, BarcodeFormat barcodeFormat, int width, int height, Map<EncodeHintType, ?> hints) {
        MultiFormatWriter multiFormatWriter = null;
        BitMatrix bm = null;
        BufferedImage image = null;
        try {
            multiFormatWriter = new MultiFormatWriter();

            // 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
            bm = multiFormatWriter.encode(content, barcodeFormat, width, height, hints);
            bm = updateBit(bm, 1);

            int w = bm.getWidth();
            int h = bm.getHeight();
            image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

            // 开始利用二维码数据创建Bitmap图片，分别设为黑（0xFFFFFFFF）白（0xFF000000）两色
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    image.setRGB(x, y, bm.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            image = zoomInImage(image, width, height);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 设置二维码的格式参数
     *
     * @return
     */
    public static Map<EncodeHintType, Object> getDecodeHintType() {
        // 用于设置QR二维码参数
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        // 设置QR二维码的纠错级别（H为最高级别）具体级别信息
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 设置编码方式
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MAX_SIZE, 300);
        hints.put(EncodeHintType.MIN_SIZE, 300);

        return hints;
    }

    /**
     * 删除白边,并使用指定的白边宽度
     *
     * @param matrix
     * @param margin
     * @return
     */
    public static BitMatrix updateBit(BitMatrix matrix, int margin) {

        int tempM = margin * 2;
        int[] rec = matrix.getEnclosingRectangle();   //获取二维码图案的属性

        int resWidth = rec[2] + tempM;
        int resHeight = rec[3] + tempM;

        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight); // 按照自定义边框生成新的BitMatrix
        resMatrix.clear();

        // 开始利用二维码数据创建Bitmap图片，分别设为黑（0xFFFFFFFF）白（0xFF000000）两色
        // 循环，将二维码图案绘制到新的bitMatrix中
        for (int i = margin; i < resWidth - margin; i++) {
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
}

