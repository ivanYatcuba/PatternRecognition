package app.util.pattern;

import app.backend.model.Pattern;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class ImagePatternLoader {

    public void loadPattern(Pattern p, ImageView img) {
        try {
            InputStream in = new ByteArrayInputStream(p.getData());
            BufferedImage imgIo = ImageIO.read(in);
            Image image = SwingFXUtils.toFXImage(imgIo, null);
            img.setImage(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
