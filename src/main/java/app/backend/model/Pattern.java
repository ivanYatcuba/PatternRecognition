package app.backend.model;

import app.util.ImageWrapper;

import javax.imageio.ImageIO;
import javax.persistence.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Entity
public class Pattern {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private long id;
    @Column
    private String name;
    @Transient
    private byte[] data;
    @Column
    private boolean isBenchmark;
    @Column
    private Long parentId;

    @Transient
    private ImageWrapper image;

    public Pattern() {

    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Access(AccessType.PROPERTY)
    @Column(name = "data", columnDefinition="LONGBLOB")
    public byte[] getData() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
            ImageIO.write(image.getBufferdImage(), "bmp", baos);
            baos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw  new IllegalStateException("error loading data");
    }

    public void setData(byte[] data) {
        this.data = data;
        InputStream in = new ByteArrayInputStream(data);
        try {
            BufferedImage bImageFromConvert = ImageIO.read(in);
            image = new ImageWrapper(bImageFromConvert);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isBenchmark() {
        return isBenchmark;
    }
    public void setBenchmark(boolean isBenchmark) {
        this.isBenchmark = isBenchmark;
    }

    public Long getParentId() {
        return parentId;
    }
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public void setPixel(int x, int y, int rgb) {
        image.setPixel(x, y, rgb);
    }

    public void setPixel(int xy, int rgb) {
        image.setPixel(xy, rgb);
    }

    public int getPixel(int x, int y) {
        return image.getPixel(x, y);
    }

    public int getPixel(int xy) {
        return image.getPixel(xy);
    }

    public void setImage(ImageWrapper image) {
        this.image = image;
    }


    public void revertPixel(int xy) {
        image.revertPixel(xy);
    }

    public void revertPixel(int x, int y) {
        image.revertPixel(x, y);
    }

    public ImageWrapper getImage() {
        try {
            return (ImageWrapper)image.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }
}
