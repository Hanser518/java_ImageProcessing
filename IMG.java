import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class IMG {
    // 原则上除初始化外不对前三个属性进行
    int width;
    int height;
    int rank;
    Pixel[][] pixels;
    Map<String, Pixel[][]> imgCache = new HashMap<>();

    // 构造方法
    public IMG(String Name) throws IOException{
        new IMG(Name, 8);
    }

    private IMG(String path, int rank, boolean isGray) throws IOException {
        BufferedImage image = ImageIO.read(new File(path));
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.pixels = new Pixel[width][height];
        int[] list = image.getRGB(0, 0, width, height, null, 0, width);
        Pixel a = new Pixel();
        this.rank = rank;
        float base = a.setRank(rank);
        // int[] list_gray = image.get
        for(int i = 0; i < list.length; i++){
            int x = i % width;
            int y = i / width;
            this.pixels[x][y] = new Pixel();
            for (int j = 0; j < 4; j++){
                switch (j){
                    case 0:
                        this.pixels[x][y].a = (list[i] >> 24) & 0xFF;
                        break;
                    case 1:
                        this.pixels[x][y].r = (list[i] >> 16) & 0xFF;
                        break;
                    case 2:
                        this.pixels[x][y].g = (list[i] >> 8) & 0xFF;
                        break;
                    case 3:
                        this.pixels[x][y].b = (list[i]) & 0xFF;
                        break;
                }
            }
            this.pixels[x][y].setRank(rank, base);
            this.pixels[x][y].calcuG();
        }
    }

    public IMG(String Name, int rank) throws IOException {
        BufferedImage image = ImageIO.read(new File("./photo/" + Name));
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.pixels = new Pixel[width][height];
        int[] list = image.getRGB(0, 0, width, height, null, 0, width);
        Pixel a = new Pixel();
        this.rank = rank;
        float base = a.setRank(rank);
        // int[] list_gray = image.get
        for(int i = 0; i < list.length; i++){
            int x = i % width;
            int y = i / width;
            this.pixels[x][y] = new Pixel();
            for (int j = 0; j < 4; j++){
                switch (j){
                    case 0:
                        this.pixels[x][y].a = (list[i] >> 24) & 0xFF;
                        break;
                    case 1:
                        this.pixels[x][y].r = (list[i] >> 16) & 0xFF;
                        break;
                    case 2:
                        this.pixels[x][y].g = (list[i] >> 8) & 0xFF;
                        break;
                    case 3:
                        this.pixels[x][y].b = (list[i]) & 0xFF;
                        break;
                }
            }
            this.pixels[x][y].setRank(rank, base);
            this.pixels[x][y].calcuG();
        }
    }

    public IMG(IMG img){
        this.width = img.width;
        this.height = img.height;
        this.pixels = img.pixels;
        this.imgCache = img.imgCache;
    }

    // 清空缓存
    public void ClearCache() {
        imgCache.clear();
    }

    // 展示缓存中含哪些图片，仅输出名称
    public void showCache(){
        System.out.println("(IMG.showCache):");
        for(String key : imgCache.keySet()){
            System.out.println("name:" + key);
        }
    }

    // 向缓存中添加图片
    public void addIMG(String name, Pixel[][] img){
        String key = name;
        if(imgCache.containsKey(key)){
            Random r = new Random();
            key = name + r.nextInt(114514);
            System.out.println("(IMG.addIMG)name:" + name + " is exist, the name has been changed to:" + key);
        }
        imgCache.put(key, img);
    }

    // 从缓存中获取图片
    public Pixel[][] getIMG(String name){
        if(imgCache.containsKey(name)){
            return imgCache.get(name);
        }
        else{
            System.out.println("(IMG.getIMG)name:" + name + " is not exist");
            return null;
        }
    }

    // 从缓存中删除图片
    public void removeIMG(String name){
        if(imgCache.containsKey(name)){
            imgCache.remove(name);
        }
        else{
            System.out.println("(IMG.removeIMG)name:" + name + " is not exist");
        }
    }

    // 更改缓存中图片的key
    public void changeIMGKey(String oldName, String newName){
        if(imgCache.containsKey(oldName)){
            Pixel[][] img = imgCache.get(oldName);
            imgCache.remove(oldName);
            imgCache.put(newName, img);
        }
        else{
            System.out.println("(IMG.changeIMGKey)oldName:" + oldName + " is not exist");
        }
    }

    // 保存图片，默认保存在output文件夹下
    // 默认保存为png格式
    // 默认保存pixels中的数值，可选择imgCache中的数值
    public void save(String Name) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                int rgb = (pixels[i][j].a << 24) | (pixels[i][j].r << 16) | (pixels[i][j].g << 8) |pixels[i][j].b;
                image.setRGB(i, j, rgb);
            }
        }
        ImageIO.write(image, "png", new File("./output/" + Name + ".png"));
    }

    public void save(String saveName, String imgName) throws IOException {
        if(!imgCache.containsKey(imgName)){
            System.out.println("(IMG.save)imgName:" + imgName + " is not exist");
            return;
        }
        Pixel[][] picture = getIMG(imgName);
        int width = picture.length;
        int height = picture[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                int rgb = (picture[i][j].a << 24) | (picture[i][j].r << 16) | (picture[i][j].g << 8)|picture[i][j].b;
                image.setRGB(i, j, rgb);
            }
        }
        ImageIO.write(image, "png", new File("./output/" + saveName + ".png"));
    }

    public void save(String saveName, Pixel[][] img) throws IOException {
        Pixel[][] picture = img;
        int width = picture.length;
        int height = picture[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                int rgb = (picture[i][j].a << 24) | (picture[i][j].r << 16) | (picture[i][j].g << 8)|picture[i][j].b;
                image.setRGB(i, j, rgb);
            }
        }
        ImageIO.write(image, "png", new File("./output/" + saveName + ".png"));
    }

    // 获取图像灰度图
    public Pixel[][] GrayIMG() throws IOException {
        return GrayIMG(pixels);
        // removeIMG("raw");
    }

    public Pixel[][] GrayIMG(String name) throws IOException {
        if(!imgCache.containsKey(name)){
            System.out.println("(IMG.GrayIMG)name:" + name + " is not exist in the imgCache");
            return pixels;
        }
        return GrayIMG(imgCache.get(name));
    }

    public Pixel[][] GrayIMG(Pixel[][] pixel) throws IOException {
        Pixel[][] img = pixel;
        int width = img.length;
        int height = img[0].length;
        Pixel[][] result = new Pixel[width][height];
        for(int i = 0;i < width;i ++){
            for(int j = 0;j < height;j ++){
                result[i][j] = new Pixel(img[i][j].gray, img[i][j].gray, img[i][j].gray);
                result[i][j].calcuG();
            }
        }
        addIMG("Gray", result);
        // System.out.println("Gray has been put in the imgCache");
        return result;
    }

    // 对灰度图进行直方图均衡化

    public Pixel[][] histEqual() throws IOException {
        return histEqual(pixels);
        // removeIMG("raw");
    }

    public Pixel[][] histEqual(String name) throws IOException {
        if(!imgCache.containsKey(name)){
            System.out.println("(IMG.histEqual)name:" + name + " is not exist in the imgCache");
            return pixels;
        }
        return histEqual(imgCache.get(name));
    }

    public Pixel[][] histEqual(Pixel[][] pixel) throws IOException {
        Pixel[][] img = pixel;
        int width = img.length;
        int height = img[0].length;
        int[] G_count = new int[rank]; // 记录各个灰度值所占有的量
        int p_count = width * height;   // 总像素个数
        for(int i = 0;i < width;i ++){
            for (int j = 0;j < height;j ++){
                img[i][j].calcuG();
                G_count[img[i][j].G]++;
            }
        }
        float[] G_probably = new float[rank];  // 累加计算各灰度值出现的概率
        G_probably[0] = (float)G_count[0] / p_count;
        for(int i = 1;i < rank;i ++){
            G_probably[i] = (float)G_count[i] / p_count + G_probably[i - 1];
        }
        for(int i = 0;i < rank;i ++){
            System.out.println(G_count[i] + " " + G_probably[i]);
        }
        Pixel[][] result = new Pixel[width][height];
        for(int i = 0;i < width;i ++){
            for(int j = 0;j < height;j ++){
                int num = (int)(G_probably[img[i][j].G] * 255);
                result[i][j] = new Pixel(num, num, num);
                result[i][j].calcuG();
            }
        }
        addIMG("histEqual", result);
        return result;
    }

    // 对指定图像进行边缘提取
    // 未指定处理图像时，默认对原图进行计算，将原图添加到Cache中，计算完成后从Cache中删除
    public Pixel[][] Sobel() throws IOException {
        addIMG("raw", this.pixels);
        return Sobel("raw");
        // removeIMG("raw");
    }

    public Pixel[][] Sobel(String imgName) throws IOException {
        if(!imgCache.containsKey(imgName)){
            System.out.println("(IMG.EnhanceEdge)imgName:" + imgName + " is not exist");
            return pixels;
        }
        return Sobel(imgCache.get(imgName));
    }

    public Pixel[][] Sobel(Pixel[][] pixel) throws IOException {
        int width = pixel.length;
        int height = pixel[0].length;
        Pixel[][] img_fill = fill(pixel,1, 1);
        int[][] kernel_x = {
                {1, 0, -1},
                {2, 0, -2},
                {1, 0, -1}
        };
        int[][] kernel_y = {
                {1, 2, 1},
                {0, 0, 0},
                {-1, -2, -1}
        };
        Pixel[][] sobelX = Get_Sub(img_fill, kernel_x);
        Pixel[][] sobelY = Get_Sub(img_fill, kernel_y);
        Pixel[][] result = new Pixel[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                result[i][j] = new Pixel();
                result[i][j].r = (int) Math.sqrt(sobelX[i][j].r * sobelX[i][j].r + sobelY[i][j].r * sobelY[i][j].r);
                result[i][j].g = (int) Math.sqrt(sobelX[i][j].g * sobelX[i][j].g + sobelY[i][j].g * sobelY[i][j].g);
                result[i][j].b = (int) Math.sqrt(sobelX[i][j].b * sobelX[i][j].b + sobelY[i][j].b * sobelY[i][j].b);
                result[i][j].calcuG();
            }
        }
        addIMG("Sobel", result);
        return result;
    }

    public Pixel[][] HOG(Pixel[][] img) throws IOException {
        int width = img.length;
        int height = img[0].length;
        Pixel[][] result = new Pixel[width][height];
        Pixel[][] img_fill = fill(img,1, 1);
        int[][] kernel_x = {
                {1, 0, -1},
                {2, 0, -2},
                {1, 0, -1}
        };
        int[][] kernel_y = {
                {1, 2, 1},
                {0, 0, 0},
                {-1, -2, -1}
        };
        Pixel[][] sobelX = Get_Sub(img_fill, kernel_x);
        Pixel[][] sobelY = Get_Sub(img_fill, kernel_y);
        Pixel[][] mag = new Pixel[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                mag[i][j] = new Pixel();
                mag[i][j].r = (int)Math.sqrt(sobelX[i][j].r * sobelX[i][j].r + sobelY[i][j].r * sobelY[i][j].r);
                mag[i][j].g = (int)Math.sqrt(sobelX[i][j].g * sobelX[i][j].g + sobelY[i][j].g * sobelY[i][j].g);
                mag[i][j].b = (int)Math.sqrt(sobelX[i][j].b * sobelX[i][j].b + sobelY[i][j].b * sobelY[i][j].b);
                mag[i][j].calcuG();
                if(mag[i][j].G < 2){
                    mag[i][j].r = 0;
                    mag[i][j].g = 0;
                    mag[i][j].b = 0;
                    mag[i][j].calcuG();
                }
            }
        }
        System.out.println("mag");
        double[][] angle = new double[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                angle[i][j] = (Math.atan2(sobelY[i][j].gray, sobelX[i][j].gray) * 360 / Math.PI);
            }
        }
        System.out.println("angle");
        int cell_size = 8;
        Pixel[][] cell = new Pixel[width - cell_size][height - cell_size];
        for(int i = 0; i < cell.length; i++){
            for(int j = 0; j < cell[0].length; j++){
                cell[i][j] = calcuCell(mag, angle, cell_size, i, j);
            }
        }
        System.out.println("cell");
        int block_size = 2;
        Pixel[][] block = new Pixel[cell.length - block_size][cell[0].length - block_size];
        for(int i = 0; i < block.length; i++){
            for(int j = 0; j < block[0].length; j++){
                block[i][j] = calcuBlock(cell, i, j, block_size);
            }
        }
        System.out.println("block");
        return block;
    }

    // 高斯卷积核计算
    private int[][] getGasKernel(int size){
        int[][] Kernel = new int[size][size];
        int center = size / 2;
        double[] w = new double[size];
        double theta = 1.0 / Math.sqrt(size);
        for(int i = 0;i < size;i ++) {
            w[i] = Math.exp(-1.0 * (i - center) * (i - center) / 2 * theta * theta);
            // System.out.println(w[i]);
        }
        for(int i = 0;i < size;i ++){
            for(int j = 0;j < size;j ++){
                Kernel[i][j] = (int)(w[i] * w[j] * size * size);
            }
        }
        return Kernel;
    }

    private int[][] getGasKernel(int size, double theta){
        int[][] Kernel = new int[size][size];
        int center = size / 2;
        double[] w = new double[size];
        for(int i = 0;i < size;i ++) {
            w[i] = Math.exp(-1.0 * (i - center) * (i - center) / 2 * theta * theta);
            // System.out.println(w[i]);
        }
        for(int i = 0;i < size;i ++){
            for(int j = 0;j < size;j ++){
                Kernel[i][j] = (int)(w[i] * w[j] * size * size);
            }
        }
        return Kernel;
    }

    // 高斯计算
    public Pixel[][] Gaussian(int size) throws IOException {
        addIMG("raw", this.pixels);
        return Gaussian("raw", size);
        // removeIMG("raw");
    }

    public Pixel[][] Gaussian(String imgName, int size) throws IOException {
        if(!imgCache.containsKey(imgName)){
            System.out.println("(IMG.Gaussian)imgName:" + imgName + " is not exist");
            return pixels;
        }
        return Gaussian(imgCache.get(imgName), size);
    }

    public Pixel[][] Gaussian(Pixel[][] pixel, int size) throws IOException {
        Pixel[][] img = pixel;
        int width = img.length;
        int height = img[0].length;
        if(size / 2 == 0) size += 1;
        if(size < 0) size = 1;
        int[][] kernel = getGasKernel(size);
        Pixel[][] img_fill = fill(img, size / 2, size / 2);
        Pixel[][] result = new Pixel[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                result[i][j] = calcuMatrix1(img_fill, kernel, i, j);
                result[i][j].calcuG();
            }
        }
        addIMG("Gaussian", result);
        return result;
    }

    // LOG算子
    public Pixel[][] LOG() throws IOException {
        addIMG("raw", this.pixels);
        return LOG("raw");
    }

    public Pixel[][] LOG(String imgName) throws IOException {
        if(!imgCache.containsKey(imgName)){
            System.out.println("(IMG.Prewitt)imgName:" + imgName + " is not exist");
            return pixels;
        }
        return LOG(imgCache.get(imgName));
    }

    public Pixel[][] LOG(Pixel[][] img) throws IOException {
        int width = img.length;
        int height = img[0].length;
        // Pixel[][] Gas = Gaussian(img, 9);
        Pixel[][] result = new Pixel[width][height];
        int[][] kernel = {
                {0, 2, 0},
                {2,-8, 2},
                {0, 2, 0}
        };
        Pixel[][] img_fill = fill(img, 1, 1);
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                result[i][j] = calcuMatrix(img_fill, kernel, i, j);
                result[i][j].calcuG();
            }
        }
        addIMG("LOG", result);
        return result;
    }

    // DOG算子
    public Pixel[][] DOG() throws IOException {
        addIMG("raw", this.pixels);
        return DOG("raw");
        // removeIMG("raw");
    }

    public Pixel[][] DOG(String imgName) throws IOException {
        if(!imgCache.containsKey(imgName)){
            System.out.println("(IMG.DOG)imgName:" + imgName + " is not exist");
            return pixels;
        }
        return DOG(imgCache.get(imgName));
    }

    public Pixel[][] DOG(Pixel[][] img) throws IOException {
        int width = img.length;
        int height = img[0].length;
        Pixel[][] result = new Pixel[width][height];
        Pixel[][] img_fill = fill(img, 1, 1);
        Pixel[][] theta1 = new Pixel[width][height];
        Pixel[][] theta2 = new Pixel[width][height];
        Pixel[][] theta3 = new Pixel[width][height];
        Pixel[][] base = pixels;
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                theta1[i][j] = calcuMatrix1(img_fill, getGasKernel(3, 0.5), i, j);
                theta2[i][j] = calcuMatrix1(img_fill, getGasKernel(3, 1.0), i, j);
                theta3[i][j] = calcuMatrix1(img_fill, getGasKernel(3, 1.5), i, j);
            }
        }
        Pixel[][] low = deleteMatrix(base, theta3);
        Pixel[][] media = deleteMatrix(theta3, theta2);
        Pixel[][] up = deleteMatrix(theta2, theta1);
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                if((media[i][j].gray > low[i][j].gray && media[i][j].gray > up[i][j].gray) || (media[i][j].gray < low[i][j].gray && media[i][j].gray < up[i][j].gray)){
                    result[i][j] = new Pixel(233,233,233);
                }else{
                    result[i][j] = new Pixel(0,0,0);
                }
            }
        }
        return result;
    }

    // Canny算子
    public Pixel[][] Canny(int index1, int index2) throws IOException {
        addIMG("raw", this.pixels);
        return Canny("raw", index1, index2);
    }

    public Pixel[][] Canny(String imgName, int index1, int index2) throws IOException {
        if(!imgCache.containsKey(imgName)){
            System.out.println("(IMG.Canny)imgName:" + imgName + " is not exist");
            return pixels;
        }
        return Canny(imgCache.get(imgName), index1, index2);
    }

    public Pixel[][] Canny(Pixel[][] img, int index1, int index2) throws IOException {
        int width = img.length;
        int height = img[0].length;
        Pixel[][] result = GrayIMG(img);
        Pixel[][] img_fill = fill(img,1, 1);
        int[][] kernel_x = {
                {1, 0, -1},
                {2, 0, -2},
                {1, 0, -1}
        };
        int[][] kernel_y = {
                {1, 2, 1},
                {0, 0, 0},
                {-1, -2, -1}
        };
        Pixel[][] sobelX = Get_Sub(img_fill, kernel_x);
        Pixel[][] sobelY = Get_Sub(img_fill, kernel_y);
        Pixel[][] mag = new Pixel[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                mag[i][j] = new Pixel();
                mag[i][j].r = (int)Math.sqrt(sobelX[i][j].r * sobelX[i][j].r + sobelY[i][j].r * sobelY[i][j].r);
                mag[i][j].g = (int)Math.sqrt(sobelX[i][j].g * sobelX[i][j].g + sobelY[i][j].g * sobelY[i][j].g);
                mag[i][j].b = (int)Math.sqrt(sobelX[i][j].b * sobelX[i][j].b + sobelY[i][j].b * sobelY[i][j].b);
                mag[i][j].calcuG();
                if(mag[i][j].G < 2){
                    mag[i][j].r = 0;
                    mag[i][j].g = 0;
                    mag[i][j].b = 0;
                    mag[i][j].calcuG();
                }
            }
        }
        System.out.println("mag");
        int[][] angle = new int[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                angle[i][j] = (int) ((Math.atan2(sobelY[i][j].gray, sobelX[i][j].gray) * 360 / Math.PI) / 45);
            }
        }
        System.out.println("angle");
        float base = result[0][0].setRank(rank);
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                int locX = 0, locY = 0;
                switch (angle[i][j]){
                    case 0:
                        locX = 1;
                        break;
                    case 1:
                        locX = 1;
                        locY = 1;
                        break;
                    case 2:
                        locY = 1;
                        break;
                    case 3:
                        locX = -1;
                        locY = 1;
                        break;
                    case 4:
                        locX = -1;
                        break;
                    case 5:
                        locX = -1;
                        locY = -1;
                        break;
                    case 6:
                        locY = -1;
                        break;
                    case 7:
                        locX = 1;
                        locY = -1;
                }
                if(i + locX >= 0 && i + locX < width && j + locY >= 0 && j + locY < height){
                    if(mag[i][j].gray < mag[i + locX][j + locY].gray){
                        result[i][j] = new Pixel(0,0,0);
                    }else{
                        result[i][j] = new Pixel(mag[i][j].gray, mag[i][j].gray, mag[i][j].gray);
                    }
                }else{
                    result[i][j] = new Pixel(mag[i][j].gray, mag[i][j].gray, mag[i][j].gray);
                }
                result[i][j].setRank(rank, base);
                result[i][j].calcuG();
            }
        }
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                if(result[i][j].G < index1){
                    result[i][j] = new Pixel(0,0,0);
                }else if(result[i][j].G < index2 && result[i][j].G >= index1){
                    int g = (int) (result[i][j].gray * ((float)(result[i][j].G - index1) / (index2 - index1)));
                    result[i][j] = new Pixel(g,g,g);
                    result[i][j].setRank(rank, base);
                    result[i][j].calcuG();
                }
            }
        }
        return result;
    }
    private Pixel[][] deleteMatrix(Pixel[][] m1, Pixel[][] m2){
        Pixel[][] result = new Pixel[m1.length][m1[0].length];
        for(int i = 0; i < m1.length; i++){
            for(int j = 0; j < m1[0].length; j++){
                result[i][j] = new Pixel(m1[i][j]);
                result[i][j].r = result[i][j].r - m2[i][j].r > 0 ? result[i][j].r - m2[i][j].r : 0;
                result[i][j].g = result[i][j].g - m2[i][j].g > 0 ? result[i][j].g - m2[i][j].g : 0;
                result[i][j].b = result[i][j].b - m2[i][j].b > 0 ? result[i][j].b - m2[i][j].b : 0;
                result[i][j].calcuG();
            }
        }
        return result;
    }
    // Prewitt算子
    public Pixel[][] Prewitt() throws IOException {
        addIMG("raw", this.pixels);
        return Prewitt("raw");
        // removeIMG("raw");
    }

    public Pixel[][] Prewitt(String imgName) throws IOException {
        if(!imgCache.containsKey(imgName)){
            System.out.println("(IMG.Prewitt)imgName:" + imgName + " is not exist");
            return pixels;
        }
        return Prewitt(imgCache.get(imgName));
    }

    public Pixel[][] Prewitt(Pixel[][] img) throws IOException {
        int width = img.length;
        int height = img[0].length;
        Pixel[][] result = new Pixel[width][height];
        int[][] Kernel_x = {
                {-1, 0, 1},
                {-1, 0, 1},
                {-1, 0, 1}
        };
        int[][] Kernel_y = {
                {-1, -1, -1},
                {0, 0, 0},
                {1, 1, 1}
        };
        Pixel[][] sub_x = Get_Sub(img, Kernel_x);
        Pixel[][] sub_y = Get_Sub(img, Kernel_y);
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                int r = (int)Math.sqrt(Math.pow(sub_x[i][j].r, 2) + Math.pow(sub_y[i][j].r, 2));
                int g = (int)Math.sqrt(Math.pow(sub_x[i][j].g, 2) + Math.pow(sub_y[i][j].g, 2));
                int b = (int)Math.sqrt(Math.pow(sub_x[i][j].b, 2) + Math.pow(sub_y[i][j].b, 2));
                result[i][j] = new Pixel(r, g, b);
                result[i][j].calcuG();
            }
        }
        return result;
    }

    private Pixel[][] Get_Sub(Pixel[][] img, int[][] Kernel) throws IOException {
        int width = img.length;
        int height = img[0].length;
        Pixel[][] img_fill = fill(img, Kernel.length / 2, Kernel[0].length / 2);
        Pixel[][] result = new Pixel[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                result[i][j] = calcuMatrix(img_fill, Kernel, i, j);
                result[i][j].calcuG();
            }
        }
        return result;
    }

    // Roberts交叉算子
    public Pixel[][] Roberts() throws IOException {
        addIMG("raw", this.pixels);
        return Roberts("raw");
    }

    public Pixel[][] Roberts(String imgName) throws IOException {
        if(!imgCache.containsKey(imgName)){
            System.out.println("(IMG.Roberts)imgName:" + imgName + " is not exist");
            return pixels;
        }
        return Roberts(imgCache.get(imgName));
    }

    public Pixel[][] Roberts(Pixel[][] img) throws IOException {
        int width = img.length;
        int height = img[0].length;
        Pixel[][] result = new Pixel[width][height];
        int[][] Kernel_x = {
                {2, 0, 0},
                {0, -4, 0},
                {0, 0, 2}
        };
        int[][] Kernel_y = {
                {0, 0, 2},
                {0, -4, 0},
                {2, 0, 0}
        };
        Pixel[][] sub_x = Get_Sub(img, Kernel_x);
        Pixel[][] sub_y = Get_Sub(img, Kernel_y);
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                int r = (int)Math.sqrt(Math.pow(sub_x[i][j].r, 2) + Math.pow(sub_y[i][j].r, 2));
                int g = (int)Math.sqrt(Math.pow(sub_x[i][j].g, 2) + Math.pow(sub_y[i][j].g, 2));
                int b = (int)Math.sqrt(Math.pow(sub_x[i][j].b, 2) + Math.pow(sub_y[i][j].b, 2));
                result[i][j] = new Pixel(r, g, b);
                result[i][j].calcuG();
            }
        }
        return result;
    }
    // 对输入图像进行锐化
    public Pixel[][] Sharpen() throws IOException {
        addIMG("raw", this.pixels);
        return Sharpen("raw");
        // removeIMG("raw");
    }

    public Pixel[][] Sharpen(String imgName) throws IOException {
        if(!imgCache.containsKey(imgName)){
            System.out.println("(IMG.Sharpen)imgName:" + imgName + " is not exist");
            return pixels;
        }
        return Sharpen(imgCache.get(imgName));
    }

    public Pixel[][] Sharpen(Pixel[][] img) throws IOException {
        int width = img.length;
        int height = img[0].length;
        Pixel[][] result = new Pixel[width][height];
        int[][] kernel = {
                {-1, -1, -1},
                {-1, 9, -1},
                {-1, -1, -1}
        };
        Pixel[][] img_fill = fill(img, 1, 1);
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                result[i][j] = calcuMatrix(img_fill, kernel, i, j);
                result[i][j].calcuG();
            }
        }
        addIMG("Sharpen", result);
        return result;
    }

    public Pixel[][] changeExposure(int num) throws IOException {
        return changeExposure(this.pixels, num);
    }

    public Pixel[][] changeExposure(Pixel[][] img, int num) throws IOException {
        int width = img.length;
        int height = img[0].length;
        Pixel[][] result = new Pixel[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                // System.out.println(i + " " + j);
                int r = img[i][j].r;
                int g = img[i][j].g;
                int b = img[i][j].b;
                // 获取rgb中最大的值,令x为r,g,b的最大值
                int x = Math.max(r, g);
                x = Math.max(x, b);
                // System.out.println(x);
                int change = Math.min(num, 255 - x);
                result[i][j] = new Pixel(r + change, g + change, b + change);
                result[i][j].setRank(8, 2.0F);
                result[i][j].calcuG();
            }
        }
        return result;
    }

    public Pixel[][] changeExposure(Pixel[][] img, int num, int index) throws IOException {
        int width = img.length;
        int height = img[0].length;
        Pixel[][] result = new Pixel[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                // System.out.println(i + " " + j);
                int r = img[i][j].r;
                int g = img[i][j].g;
                int b = img[i][j].b;
                // 获取rgb中最大的值,令x为r,g,b的最大值
                int x = Math.max(r, g);
                x = Math.max(x, b);
                // System.out.println(x);
                int change = Math.min(num, 255 - x);
                if(img[i][j].G > index)
                    result[i][j] = new Pixel(r + change, g + change, b + change);
                else
                    result[i][j] = new Pixel(r, g, b);
                result[i][j].setRank(8, 2.0F);
                result[i][j].calcuG();
            }
        }
        return result;
    }

    public Pixel[][] halfGet(Pixel[][] p1, Pixel[][] p2) throws IOException {
        int width = p1.length;
        int height = p1[0].length;
        Pixel[][] result = new Pixel[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                if(i < width / 2)
                    result[i][j] = p1[i][j];
                else
                    result[i][j] = p2[i][j];
            }
        }
        return result;
    }

    public int[] getGList(Pixel[][] img){
        int width = img.length;
        int height = img[0].length;
        int[] result = new int[img[0][0].rank + 1];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                result[img[i][j].G]++;
            }
        }
        return result;
    }

    public int getVally(int[] list){
        int vally = list.length - 1;
        int i = list.length - 1;
        while(vally > 0){
            if(list[vally] > list[i]) break;
            else vally = i;
            i --;
        }
        return vally;
    }

    public Pixel[][] fixColor(Pixel[][] img){
        int width = img.length;
        int height = img[0].length;
        int[][] Kernel = {
                {1, 1, 1},
                {1, -5, 1},
                {1, 1, 1}

        };
        Pixel[][] result = new Pixel[width][height];
        Pixel[][] img_fill = fill(img, 1, 1);
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                result[i][j] = calcuMatrix1(img_fill, Kernel, i, j);
                result[i][j].calcuG();
            }
        }
        return result;
    }

    // 编写中值滤波
    public Pixel[][] Median(Pixel[][] img, int size) {
        int width = img.length;
        int height = img[0].length;
        if(size / 2 == 0) size += 1;
        if(size < 0) size = 1;
        Pixel[][] img_fill = fill(img, size / 2, size / 2);
        Pixel[][] result = new Pixel[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                result[i][j] = calcuMatrixMedian(img_fill, size, i, j);
                result[i][j].calcuG();
            }
        }
        return result;
    }

    public Pixel[][] enhanceEdge(Pixel[][] p1, Pixel[][] p2){
        int width = p1.length;
        int height = p1[0].length;
        Pixel[][] result = new Pixel[width][height];
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                // int r = (int) Math.sqrt(p1[i][j].r * p1[i][j].r + p2[i][j].r * p2[i][j].r);
                // int g = (int) Math.sqrt(p1[i][j].g * p1[i][j].g + p2[i][j].g * p2[i][j].g);
                // int b = (int) Math.sqrt(p1[i][j].b * p1[i][j].b + p2[i][j].b * p2[i][j].b);
                int r = (int) (p1[i][j].r * 0.63 + p2[i][j].r * 0.77);
                int g = (int) (p1[i][j].g * 0.63 + p2[i][j].g * 0.77);
                int b = (int) (p1[i][j].b * 0.63 + p2[i][j].b * 0.77);
                result[i][j] = new Pixel(r, g, b);
                result[i][j].setRank(8, 2.0F);
                result[i][j].calcuG();
            }
        }
        return result;
    }

    // 根据输入边界值的大小对输入的img数据进行填充
    private Pixel[][] fill(Pixel[][] img, int xSide, int ySide){
        int width = img.length;
        int height = img[0].length;
        Pixel[][] result = new Pixel[width + 2 * xSide][height + 2 * ySide];
        for(int i = 0; i < width + 2 * xSide; i++){
            for(int j = 0; j < height + 2 * ySide; j++){
                if(i < xSide || i >= width + xSide || j < ySide || j >= height + ySide){
                    result[i][j] = new Pixel(0,0,0);
                }
                else{
                    result[i][j] = img[i - xSide][j - ySide];
                }
            }
        }
        return result;
    }

    // 将传入的两个pixel矩阵按权重进行相加
    public Pixel[][] plusPixel(Pixel[][] p1, Pixel[][] p2, double weight1, double weight2){
        if(p1.length != p2.length || p1[0].length != p2[0].length){
            System.out.println("(IMG.addPixel)p1 and p2 size is not equal");
            return p1;
        }
        Pixel[][] result = new Pixel[p1.length][p1[0].length];
        for(int i = 0;i < p1.length;i++){
            for (int j = 0;j < p1[0].length;j++){
                result[i][j] = new Pixel(
                        (int)(p1[i][j].r * weight1 + p2[i][j].r * weight2),
                        (int)(p1[i][j].g * weight1 + p2[i][j].g * weight2),
                        (int)(p1[i][j].b * weight1 + p2[i][j].b * weight2)
                );
                result[i][j].calcuG();
            }
        }
        return result;
    }

    // 矩阵计算
    private Pixel calcuMatrix(Pixel[][] img, int[][] conV, int x, int y){
        int r = 0;
        int g = 0;
        int b = 0;
        for(int i = 0; i < conV.length; i++){
            for(int j = 0; j < conV[0].length; j++){
                r += conV[i][j] * img[x + i][y + j].r;
                g += conV[i][j] * img[x + i][y + j].g;
                b += conV[i][j] * img[x + i][y + j].b;
            }
        }
        if(r > 255){
            r = 255;
        }
        else if(r < 0){
            r = 0;
        }
        if(g > 255){
            g = 255;
        }
        else if(g < 0){
            g = 0;
        }
        if(b > 255){
            b = 255;
        }
        else if(b < 0){
            b = 0;
        }
        Pixel result = new Pixel(r, g, b);
        result.calcuG();
        return result;
    }

    // 矩阵计算归一化
    private Pixel calcuMatrix1(Pixel[][] img, int[][] conV, int x, int y){
        int r = 0;
        int g = 0;
        int b = 0;
        int count = 0;
        for(int i = 0; i < conV.length; i++){
            for(int j = 0; j < conV[0].length; j++){
                r += conV[i][j] * img[i + x][j + y].r;
                g += conV[i][j] * img[i + x][j + y].g;
                b += conV[i][j] * img[i + x][j + y].b;
                count += conV[i][j];
            }
        }
        r /= count;
        g /= count;
        b /= count;
        Pixel result = new Pixel(r, g, b);
        // System.out.println(result);
        result.calcuG();
        return result;
    }

    private Pixel calcuMatrixMedian(Pixel[][] img, int size, int x, int y){
        int r = 0;
        int g = 0;
        int b = 0;
        int[] count = new int[size * size];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                count[i * size + j] = img[i + x][j + y].gray;
            }
        }
        Arrays.sort(count);
        int aim = count[count.length / 2];
        int x1 = 0;
        int y1 = 0;
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(img[i + x][j + y].gray == aim){
                    x1 = i;
                    y1 = j;
                    break;
                }
            }
        }
        r = img[x1 + x][y1 + y].r;
        g = img[x1 + x][y1 + y].g;
        b = img[x1 + x][y1 + y].b;
        Pixel result = new Pixel(r, g, b);
        // System.out.println(result);
        result.calcuG();
        return result;
    }

    private Pixel calcuCell(Pixel[][] mag, double[][] angle, int size, int x, int y){
        int[] list = new int[size * size];
        int[] angleList = new int[8];
        for(int i = 0;i < size;i ++){
            for(int j = 0;j < size;j++){
                angleList[(int) (angle[i + x][j + y] / 45)] += 1;
            }
        }
        int max_angle = 0;
        for(int i = 0;i < 8;i++){
            if(angleList[i] > angleList[max_angle]){
                max_angle = i;
            }
        }
        // System.out.print(x + " " + y + " angleList");
        int max_mag = 0;
        for(int i = 0;i < size;i ++){
            for(int j = 0;j < size;j++){
                if(angle[i + x][j + y] == max_angle && mag[i + x][j + y].gray > max_mag){
                    max_mag = mag[i + x][j + y].gray;
                }
            }
        }
        Pixel result = new Pixel(max_mag, max_mag, max_mag);
        result.calcuG();
        // System.out.println(" OK");
        return result;
    }


    private Pixel calcuBlock(Pixel[][] cell, int x, int y, int blockSize) {
        int p = 0;
        for(int i = 0;i < blockSize;i++){
            for(int j = 0;j < blockSize;j++){
                p += cell[i + x][j + y].gray;
            }
        }
        p = p / (blockSize * blockSize);
        Pixel result = new Pixel(p, p, p);
        return result;
    }

    public ArrayList<Pixel[][]> splitIMG(Pixel[][] img, int width, int height, int count){
        ArrayList<Pixel[][]> result = new ArrayList<Pixel[][]>();
        Random r = new Random();
        for(int i = 0; i < count; i++){
            int x = r.nextInt(img.length - width - 1);
            int y = r.nextInt(img[0].length - height - 1);
            Pixel[][] temp = new Pixel[width][height];
            for(int j = 0; j < width; j++){
                for(int k = 0; k < height; k++){
                    temp[j][k] = img[x + j][y + k];
                }
            }
            result.add(temp);
        }
        return result;
    }

    public Pixel[][] imgEnhance(Pixel[][] img, double theta) throws IOException {
        int width = img.length;
        int height = img[0].length;
        Pixel[][] result = new Pixel[width][height];
        if(theta < 0.5) theta = 0.5;

        int[] G_list = getGList(img);   // 获取图像灰度分布比例
        System.out.println("G_list:");
        for(int i = 0;i < G_list.length;i ++){
            System.out.println(G_list[i] + "\t\t"); // i + ":" +
            // if(i % 2 == 1) System.out.println();
        }
        int crest;  // 图像最高有效灰度级数
        int sub_sum = 0;
        for(crest = G_list.length - 1; crest > 0; crest --){
            if(G_list[crest] > width * height * 0.05 - sub_sum) break;
            else sub_sum += G_list[crest];
        }
        int trough; // 图像最低有效灰度级数
        sub_sum = 0;
        for(trough = 0; trough < crest; trough ++){
            if(G_list[trough] > width * height * 0.05 - sub_sum) break;
            else sub_sum += G_list[trough];
        }

        // 计算获取两个阈值
        double goldenRatio = 0.618;
        double threshold1 = (crest - trough) * goldenRatio + trough;
        double threshold2 = threshold1 * (goldenRatio);

        // 计算阈值所对应的灰度值
        double[] G_ratio = new double[G_list.length];
        G_ratio[0] = (double)G_list[0] / (width * height);
        for(int i = 1;i < G_list.length;i ++){
            G_ratio[i] = G_ratio[i - 1] + (double)G_list[i] / (width * height);
        }
        System.out.println("\nG_ratio:");
        for(int i = 0;i < G_list.length;i ++){
            System.out.println(G_ratio[i] + "\t\t"); //i + ":" +
            // if(i % 2 == 1) System.out.println();
        }
        int index1 = (int) (255 * (G_ratio[(int) threshold1]
                + (G_ratio[(int) threshold1 + 1] - G_ratio[(int) threshold1])
                * (threshold1 - (int) threshold1)));
        int index2 = (int) (255 * (G_ratio[(int) threshold2]
                + (G_ratio[(int) threshold2 + 1] - G_ratio[(int) threshold2])
                * (threshold2 - (int) threshold2)));
        System.out.println("\nCrest:" + crest + "\tTrough:" + trough);
        System.out.println("Threshold1:" + threshold1 + "\tThreshold2:" + threshold2);
        System.out.println("Index1:" + index1 + "\tIndex2:" + index2);

        // 对图像进行增幅
        double maxIllu = theta * Math.pow(crest - trough, 2);
        System.out.println("maxIllumination:" + maxIllu);
        double a1 = maxIllu / (1 - 2 * index1 + index1 * index1);
        double b1 = -a1 * index1 * 2;
        double c1 = maxIllu - a1 - b1 + 1;
        double top = a1 * index2 * index2 + b1 * index2 + c1;
        double a2 = (maxIllu - top) / (1 - 2 * index2 + index2 * index2);
        double b2 = -a2 * index2 * 2;
        double c2 = maxIllu - a2 - b2 - top + 1;
        System.out.println("a1:" + a1 + "\tb1:" + b1 + "\tc1:" + c1);
        System.out.println("a2:" + a2 + "\tb2:" + b2 + "\tc2:" + c2);
        float count = 0;
        for(int i = 0;i < width;i ++){
            for(int j = 0;j < height;j ++){
                int r = img[i][j].r;
                int g = img[i][j].g;
                int b = img[i][j].b;
                int gray = img[i][j].gray;
                double num = 1;
                if(img[i][j].gray <= index1){
                    num = (a1 * gray * gray + b1 * gray + c1) / 255 + 1;        // 第一次增幅
                }
                if(img[i][j].gray <= index2){
                    double c = (a2 * gray * gray + b2 * gray + c2) / 255 + 1;   // 第二次增幅
                    num *= c;
                }
                double max = Math.max(r, Math.max(g, b));
                while(max * num > 254.0)
                    num *= (250.0 / (max * num));
                result[i][j] = new Pixel((int) (r * num), (int) (g * num), (int) (b * num));
                result[i][j].calcuG();
                // float percent = count ++ / (width * height);
                // System.out.printf("\r%.4f",percent);
            }
        }
        System.out.println("____OK");
        return result;
    }

    private int[][] transHue(Pixel[][] img) {
        int width = img.length; int height = img[0].length;
        double[][] hue = new double[width][height];
        int[][] result = new int[width][height];
        for(int i = 0;i < width;i ++){
            for(int j = 0;j < height;j ++){
                double r = img[i][j].r;
                double g = img[i][j].g;
                double b = img[i][j].b;
                double max = Math.max(r, Math.max(g, b));
                double min = Math.min(r, Math.min(g, b));
                double delta = max - min;
                if(delta < 20){
                    hue[i][j] = 0;
                }else if(max == r){
                    hue[i][j] = 1;
                }else if(max == g){
                    hue[i][j] = 2;
                }else{
                    hue[i][j] = 3;
                }
            }
        }
        return result;
    }

}
