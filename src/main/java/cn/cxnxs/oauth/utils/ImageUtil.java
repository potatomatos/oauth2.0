package cn.cxnxs.oauth.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;

/**
 * 图片工具
 */
public class ImageUtil {


	private static final Logger logger= LoggerFactory.getLogger(ImageUtil.class);


	/**
	 * 生成验证码
	 * @param securityCode
	 * @param width
	 * @param height
	 * @return
	 */
    public static BufferedImage buildImageVerify(String securityCode, int width, int height) {
        int length = securityCode.length();
        int fSize = (height - 1) / 2;
        int fWidth = fSize + 1;
        // 图片宽度
        width = length * fWidth > width ? length * fWidth : width;
        // 图片
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.createGraphics();
        // 设置背景色
        g.setColor(Color.WHITE);
        // 填充背景
        g.fillRect(0, 0, width, height);
        // 设置边框颜色
        g.setColor(Color.LIGHT_GRAY);
        // 边框字体样式
        g.setFont(new Font("Arial", Font.BOLD, height - 2));
        // 绘制边框
        g.drawRect(0, 0, width - 1, height - 1);
        // 绘制噪点
        SecureRandom rand = new SecureRandom();
        // 设置噪点颜色
        g.setColor(Color.LIGHT_GRAY);
        for (int i = 0; i < length * 6; i++) {
            int x = rand.nextInt(width);
            int y = rand.nextInt(height);
            // 绘制1*1大小的矩形
            g.drawRect(x, y, 1, 1);
        }
        // 绘制验证码
        int codeY = height - 10;
        // 设置字体颜色和样式
        g.setColor(new Color(19, 148, 246));
        g.setFont(new Font("Georgia", Font.BOLD, fSize));
        for (int i = 0; i < length; i++) {
            g.drawString(String.valueOf(securityCode.charAt(i)), i * fSize + 5, codeY);
        }
        // 关闭资源
        g.dispose();
        return image;
    }

    /**
     * 返回验证码图片的流格式
     *
     * @param securityCode 验证码
     */
    public static void buildImageVerify(String securityCode, int width, int height, OutputStream os) {
        BufferedImage image = buildImageVerify(securityCode, width, height);
        convertImageToStream(image, os);
    }

    /**
     * 将BufferedImage转换成ByteArrayInputStream
     *
     * @param image 图片
     */
    private static void convertImageToStream(BufferedImage image, OutputStream os) {
        try {
            ImageIO.write(image, "JPEG", os);
        } catch (IOException e1) {
            logger.error("错误信息", e1);
        }
    }

}
