package app.util;



import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class ImageWrapper implements Cloneable {

    private BufferedImage bufferdImage;
    private int width;
    private int height;

    public static final int WHITE;
    public static final int BLACK;
    public static final int RED;
    public static final int BLUE;

    static {
        WHITE = new Color(255, 255, 255).getRGB();
        BLACK = new Color(0, 0, 0).getRGB();
        RED = new Color(255, 0, 0).getRGB();
        BLUE = new Color(0, 0, 255).getRGB();

    }

    public ImageWrapper(BufferedImage bufferdImage) {
        this.bufferdImage = bufferdImage;
        height = bufferdImage.getData().getHeight();
        width = bufferdImage.getData().getWidth();


    }

    public void setPixel(int x, int y, int rgb) {
        bufferdImage.setRGB(x, y, rgb);
    }

    public void setPixel(int xy, int rgb) {
        int x = xy%width;
        int y = xy/height;
        bufferdImage.setRGB(x, y, rgb);
    }

    public int getPixel(int x, int y) {
        return bufferdImage.getRGB(x, y);
    }

    public int getPixel(int xy) {
        int x = xy%width;
        int y = xy/height;
        return bufferdImage.getRGB(x, y);
    }

    public void revertPixel(int x, int y) {
        int pixel = getPixel(x, y);
        if(pixel == WHITE) {
            setPixel(x, y, BLACK);
        } else {
            setPixel(x, y, WHITE);
        }
    }

    public void revertPixel(int xy) {
        int x = xy%width;
        int y = xy/height;
        revertPixel(x, y);
    }

    public BufferedImage getBufferdImage() {
        return bufferdImage;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDataSize() {
        return width*height;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ColorModel cm = bufferdImage.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster =  bufferdImage.copyData(null);
        return new ImageWrapper(new BufferedImage(cm, raster, isAlphaPremultiplied, null));
    }
}
