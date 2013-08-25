package game;
import inputevents.CharacterPressedEvent;
import inputevents.RightClickEvent;
import inputevents.SelectEvent;
import inputevents.SelectPointEvent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.JPanel;

public class DrawingPanel extends JPanel implements MouseListener,
		MouseMotionListener {

	private final Color BACKGROUND = new Color(115, 156, 58);

	private int world_width, world_height;

	public Rectangle view;
	public BufferedImage buffer;

	public Game game;

	public DrawingPanel(int width, int height, int world_width,
			int world_height, Game game) {
		this.setPreferredSize(new Dimension(width, height));
		this.setBackground(BACKGROUND);

		this.view = new Rectangle(0, 0, width, height);
		this.buffer = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);

		this.world_width = world_width;
		this.world_height = world_height;

		this.game = game;

		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public void render(LinkedList<VoxelGrid> voxels, LinkedList<Entity> entities) {
		int x = view.x;
		int y = view.y;
		int width = view.width;
		int height = view.height;

		Graphics2D g = (Graphics2D) buffer.getGraphics();

		/* erase the image */
		g.setColor(BACKGROUND);
		g.fillRect(0, 0, width, height);

		/* draw world */
		double min = 9999999;
		double max = -9999999;
		for (VoxelGrid voxel : voxels) {
			double[] minmax = voxel.getMinMax(view);
			if (minmax[0] < min)
				min = minmax[0];
			if (minmax[1] > max)
				max = minmax[1];
		}
		for (VoxelGrid voxel : voxels) {
			voxel.draw(g, view, min, max);
		}

		/* render each entity */
		for (Entity entity : entities) {
			if (view.contains(entity.point)) {
				entity.draw(g, -x, -y);
			}
		}

		/* draw selection rectangle */
		if (drawRect != null) {
			g.setColor(Color.gray);
			g.drawRect((int) drawRect.getX(), (int) drawRect.getY(),
					(int) drawRect.getWidth(), (int) drawRect.getHeight());
		}

		getGraphics().drawImage(buffer, 0, 0, width, height, null);

		g.dispose();

	}

	public void moveViewport(int dx, int dy) {
		view.setLocation(view.x + dx, view.y + dy);
	}

	public void pan(double dx, double dy) {
		if (view.getX() + view.getWidth() + dx > world_width)
			dx = world_width - (view.getX() + view.getWidth());
		if (view.getX() + dx < 0)
			dx = -(view.getX());
		if (view.getY() + view.getHeight() + dy > world_height)
			dy = world_height - (view.getY() + view.getHeight());
		if (view.getY() + dy < 0)
			dy = -(view.getY());
		moveViewport((int) dx, (int) dy);
	}

	private Rectangle drawRect;

	private Point initial;
	private Point lastAbs = new Point(0, 0);
	private boolean pan;

	public boolean ctrl;

	public void mousePressed(MouseEvent e) {
		initial = null;
		if (e.getButton() == MouseEvent.BUTTON2) {
			pan = true;
		} else if (e.getButton() == MouseEvent.BUTTON1) {
			initial = new Point(e.getX(), e.getY());
		} else {
		}
		lastAbs.setLocation(e.getLocationOnScreen());
	}

	public void mouseReleased(MouseEvent e) {
		if (initial != null) {
			int width = Math.abs(e.getX() - initial.x);
			int height = Math.abs(e.getY() - initial.y);
			int x = Math.min(initial.x, e.getX());
			int y = Math.min(initial.y, e.getY());
			game.fireGlobalEvent(new SelectEvent(view, new Rectangle((int) view
					.getX()
					+ x, (int) view.getY() + y, width, height), ctrl));
			initial = null;
			drawRect = null;
		} else if (pan) {
			pan = false;
		} else {
		}
	}

	public void mouseDragged(MouseEvent e) {
		if (initial != null) {
			int width = Math.abs(e.getX() - initial.x);
			int height = Math.abs(e.getY() - initial.y);
			int x = Math.min(initial.x, e.getX());
			int y = Math.min(initial.y, e.getY());
			drawRect = new Rectangle(x, y, width, height);
		} else if (pan) {
			// calculation of move
			int dx = lastAbs.x - e.getXOnScreen();
			int dy = lastAbs.y - e.getYOnScreen();
			pan(dx, dy);
		} else {
		}
		lastAbs.setLocation(e.getLocationOnScreen());
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1)
			game.fireGlobalEvent(new SelectPointEvent(this, new Point(e.getX()
					+ (int) view.getX(), e.getY() + (int) view.getY()), ctrl));
		if (e.getButton() == MouseEvent.BUTTON3)
			game.fireGlobalEvent(new RightClickEvent(view, new Point(e.getX()
					+ (int) view.getX(), e.getY() + (int) view.getY()), ctrl));
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

}
