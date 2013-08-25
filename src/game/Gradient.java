package game;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;

public interface Gradient {

	public double[] calculateDxDy(int cx, int cy, double vx, double vy);

}
