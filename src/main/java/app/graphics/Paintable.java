package app.graphics;

import java.awt.Graphics2D;

public interface Paintable {

    void paint(Graphics2D g, int xDisplayOffset, int yDisplayOffset, double xDisplayFactor, double yDisplayFactor, boolean decorated);
    
}
