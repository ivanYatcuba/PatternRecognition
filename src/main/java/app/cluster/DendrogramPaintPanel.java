package app.cluster;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class DendrogramPaintPanel extends JPanel {

    private Cluster root;
    private int leaves;
    private int levels;
    private int heightPerLeaf;
    private int widthPerLevel;
    private int currentY;
    private double maxDistance;

    public DendrogramPaintPanel(Cluster cluster) {
        root = cluster;
    }

    private int countLeaves(Cluster node) {
        List<Cluster> children = node.getChildren();
        if (children.size() == 0) {
            return 1;
        }
        Cluster child0 = children.get(0);
        Cluster child1 = children.get(1);
        return countLeaves(child0) + countLeaves(child1);
    }

    private int countLevels(Cluster node) {
        List<Cluster> children = node.getChildren();
        if (children.size() == 0) {
            return 1;
        }
        Cluster child0 = children.get(0);
        Cluster child1 = children.get(1);
        return 1+Math.max(countLevels(child0), countLevels(child1));
    }

    private  int getWidthPerNode(double distance) {
        return (int)(getWidth()/(maxDistance*(levels-1)) * distance);
    }

    @Override
    protected void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        Graphics2D g = (Graphics2D)gr;

        int margin = 25;
        leaves = countLeaves(root);
        levels = countLevels(root);
        maxDistance = root.getDistanceBetweenChildern();
        heightPerLeaf = (getHeight() - margin - margin) / leaves;
        widthPerLevel = (getWidth() - margin - margin)/ levels;
        currentY = 0;

        g.translate(margin, margin);
        draw(g, root, 0);
    }


    private Point draw(Graphics g, Cluster node, int y) {
        List<Cluster> children = node.getChildren();
        if (children.size() == 0) {
            int x = getWidth() - widthPerLevel;
            g.drawString(String.valueOf(node.getName()), x-2, currentY+8);
            int resultX = x;
            int resultY = currentY;
            currentY += heightPerLeaf;
            return new Point(resultX, resultY);
        }
        if (children.size() >= 2) {
            Cluster child0 = children.get(0);
            Cluster child1 = children.get(1);
            Point p0 = draw(g, child0, y);
            Point p1 = draw(g, child1, y+heightPerLeaf);

            g.fillRect(p0.x-2, p0.y-2, 4, 4);
            g.fillRect(p1.x-2, p1.y-2, 4, 4);
            int dx = getWidthPerNode(node.getDistanceBetweenChildern());
            int vx = Math.min(p0.x-dx, p1.x-dx);
            g.drawLine(vx, p0.y, p0.x, p0.y);
            g.drawLine(vx, p1.y, p1.x, p1.y);
            g.drawLine(vx, p0.y, vx, p1.y);
            Point p = new Point(vx, p0.y+(p1.y - p0.y)/2);
            return p;
        }
        // Should never happen
        return new Point();
    }

}
