package game;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;

public abstract class Entity {

	public boolean selected;

	public Point2D.Double point;

	public GridLocation grid_location;
	public Grid grid;

	public int radius;

	public Color color;

	public double health;

	public Entity(double x, double y, int radius, Grid grid, Color color) {
		this.point = new Point2D.Double(x, y);
		this.grid_location = new GridLocation(this);
		this.radius = radius;
		this.grid = grid;
		grid.addNewEntity(this);
		this.color = color;
		this.health = 1;
	}

	public void hurt(double dhealth) {
		health -= dhealth;
	}

	public void think(double dt) {
	}

	public void move(double dx, double dy) {
		double oldx = point.x;
		double oldy = point.y;
		point.x += dx;
		point.y += dy;
		grid.updateEntityLocation(this, (int) oldx, (int) oldy);
	}

	public void moveTo(double x, double y) {
		double oldx = point.x;
		double oldy = point.y;
		point.x = x;
		point.y = y;
		grid.updateEntityLocation(this, (int) oldx, (int) oldy);
	}

	public void draw(Graphics2D g, int xoff, int yoff) {
		int x = (int) point.x + xoff - radius;
		int y = (int) point.y + yoff - radius;
		g.setColor(color);
		g.fillOval(x, y, radius * 2, radius * 2);
		if (selected) {
			g.setColor(Color.green);
			g.drawOval(x - 2, y - 2, radius * 2 + 4, radius * 2 + 4);
		}
	}

	public void prepareDelete() {
	}
}
