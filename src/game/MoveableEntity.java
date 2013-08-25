package game;
import java.awt.Color;
import java.util.LinkedList;

import sprites.CharacterSprite.CharacterAction;
import sprites.CharacterSprite.CharacterDir;

public abstract class MoveableEntity extends Entity {

	protected boolean moveable;

	protected CharacterAction action;
	protected CharacterDir dir;

	public LinkedList<Gradient> gradients;

	public double max_v;
	public double max_a;

	public double dx;
	public double dy;

	public MoveableEntity(double x, double y, int radius, double max_v,
			Grid grid, Color color) {
		super(x, y, radius, grid, color);
		this.max_v = max_v;
		this.max_a = 500;
		this.gradients = new LinkedList<Gradient>();
		this.action = CharacterAction.WALK;
		this.dir = CharacterDir.E;
		this.moveable = true;
	}

	public void addGradient(Gradient gradient) {
		gradients.add(gradient);
	}

	public void clearGradients() {
		gradients.clear();
		;
	}

	private double elapsed_t;

	public void think(double dt) {
		super.think(dt);

		if (!moveable)
			return;

		elapsed_t += dt;

		double ddx = 0;
		double ddy = 0;
		for (Gradient gradient : gradients) {
			double[] dxdy = gradient.calculateDxDy((int) point.x,
					(int) point.y, dx, dy);
			ddx += dxdy[0];
			ddy += dxdy[1];
		}

		dx += ddx * dt * max_a;
		dy += ddy * dt * max_a;

		double speed = Math.sqrt(dx * dx + dy * dy);
		if (speed == 0)
			speed = 1;
		dx = dx * max_v / speed;
		dy = dy * max_v / speed;

		if (Math.abs(dy) > Math.abs(dx)) {
			if (dy > 0)
				dir = CharacterDir.S;
			else
				dir = CharacterDir.N;
		} else {
			if (dx > 0)
				dir = CharacterDir.E;
			else
				dir = CharacterDir.W;
		}

		this.move(dx * dt, dy * dt);
	}

}
