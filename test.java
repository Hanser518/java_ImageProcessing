package IMAGE;

import java.io.IOException;
import java.util.ArrayList;

public class test {
    public static void main(String[] args) throws IOException {
        IMG img = new IMG("index_2.jpg", 16);
        // 获取LOG计算后的图像
        Pixel[][] e = img.LoG(img.GrayIMG());//
        e = img.changeExposure(img.Gaussian(e, 3), 150, 1);   // 整体亮度调整，增强阈值为灰度梯度＞1
        img.save("index2_LoG", e);
        // 获取Sobel算子计算后的图像
        e = img.Sobel();
        img.save("index2_Sobel_Color", e);
        e = img.Sobel(img.GrayIMG());
        img.save("index2_Sobel", e);
        // Prewitt算子
        e = img.Prewitt();
        img.save("index2_Prewitt_Color", e);
        e = img.Prewitt(img.GrayIMG());
        img.save("index2_Prewitt", e);
        // Roberts算子
        e = img.Roberts();
        img.save("index2_Roberts_Color", e);
        e = img.Roberts(img.GrayIMG());
        img.save("index2_Roberts", e);
    }
}
