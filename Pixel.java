package IMAGE;

public class Pixel {
    int r, g, b, a;
    int gray, G;
    int rank = 8;
    static float base = 2.0F;

    // 构造方法
    public Pixel() {
        this.r = 0;
        this.g = 0;
        this.b = 0;
        this.a = 255;
    }

    public Pixel(int r, int g, int b) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = 255;
    }

    public Pixel(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Pixel(Pixel p) {
        this.r = p.r;
        this.g = p.g;
        this.b = p.b;
        this.a = p.a;
    }


    public float setRank(int rank){
        for (float i = 10; i >= 0; i -= 0.005F) {
            // System.out.println(i + " " + Math.log(255) / Math.log(i));
            if (Math.log(255) / Math.log(i) > rank - 1) {
                base = i;
                // System.out.println(base);
                return i;
            }
        }
        return 2.0F;
    }

    public void setRank(int rank, float base){
        this.base = base;
        this.rank = rank;
    }
    // 计算灰度值
    public void calcuG() {
        this.gray = (int) (0.299 * (double) this.r + 0.587 * (double) this.g + 0.114 * (double) this.b);
        this.G = (int) (Math.log(this.gray) / Math.log(base));
        if (this.G < 0) this.G = 0;
        if (this.G > rank) this.G = rank;
    }

    public String toString() {
        return "(" + r + ", " + g + ", " + b + ", " + a + ")";
    }

}
