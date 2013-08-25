package game;

import inputevents.GatherResource;

import java.awt.Color;
import java.awt.Graphics2D;

import sprites.AnimatedSprite;
import sprites.CharacterSprite;
import sprites.CharacterSprite.CharacterAction;
import sprites.CharacterSprite.CharacterDir;

public class VillagerEntity extends MoveableEntity {

	public static final int[] MAKE_TOWER_COST = new int[] { 0, 200, 200 };

	public static final int WALK = 0;
	public static final int GATHER = 1;
	public static final int RETURN = 2;
	public static final int WALK_TO_BUILD = 3;
	public static final int BUILD = 4;

	public static final double VILLAGER_HEALTH = 20;
	public static final double VILLAGER_SPEED = 50;
	public static final int VILLAGER_RADIUS = 5;

	public static final int MAX_RESOURCE_CARRY = 5;

	public int resource_carrying = 0;
	public int carrying = 0;
	public int mode;

	public ResourceGrid resource_grid;

	public TowerEntity parent;

	public Game game;

	public VillagerEntity(double x, double y, Grid grid, Game game,
			ResourceGrid resource_grid) {
		super(x, y, VILLAGER_RADIUS, VILLAGER_SPEED, grid, Color.blue);
		this.health = VILLAGER_HEALTH;
		this.parent = grid.getClosestTower(x, y, 500);
		this.game = game;
		this.resource_grid = resource_grid;
		switchMode(WALK);
	}

	public void startBuilding(TowerEntity entity) {
		this.parent = entity;
		switchMode(WALK_TO_BUILD);
	}

	public void switchMode(int mode) {
		this.mode = mode;
		dx = 0;
		dy = 0;
		goali = -1;
		goalj = -1;

		switch (mode) {
		case WALK:
			moveable = true;
			action = CharacterAction.WALK;
			break;
		case GATHER:
			moveable = false;
			action = CharacterAction.ACT;
			break;
		case RETURN:
			moveable = true;
			action = CharacterAction.WALK_HOLD;
			break;
		case WALK_TO_BUILD:
			moveable = true;
			action = CharacterAction.WALK;
			break;
		case BUILD:
			moveable = false;
			action = CharacterAction.ACT;
			break;
		}
	}

	private int frame;
	private static final int ANIM_FRAMES = 2;

	private double elapsed_time = 0;

	public void draw(Graphics2D g, int xoff, int yoff) {
		int x = (int) point.x + xoff - radius;
		int y = (int) point.y + yoff - radius;
		AnimatedSprite animsprite = CharacterSprite.getSprite(action, dir);

		if ((Math.abs(dx) > 0 || Math.abs(dy) > 0)
				|| action == CharacterAction.ACT) {
			if (elapsed_time > 1.0 / 30.0) {
				frame++;
				elapsed_time = 0;
			}
			frame = frame % (animsprite.length() * ANIM_FRAMES);
			g.drawImage(animsprite.getImage((int) frame / ANIM_FRAMES), x, y,
					animsprite.getWidth(), animsprite.getHeight(), null);
		} else {
			frame = animsprite.getIdleFrame() * ANIM_FRAMES;
			g.drawImage(animsprite.getImage(), x, y, animsprite.getWidth(),
					animsprite.getHeight(), null);
		}
		if (selected) {
			g.setColor(Color.green);
			int off = 5;
			g.drawOval(x - off, y - off, radius * 2 + off * 2, radius * 2 + off
					* 2);
		}

		g.setColor(Color.blue);
		g.fillRect((int) x, (int) y - 5, (int) ((1.0 + health)
				/ (0.0 + VILLAGER_HEALTH) * (0.0 + radius) * 2.0), 3);
	}

	private int goali = -1;
	private int goalj = -1;

	public static final double GATHER_SPEED = 1;
	public static final double BUILD_SPEED = 1;
	private double elapsed_dt = 0;

	public void think(double dt) {
		elapsed_time += dt;

		if (parent == null || parent.health < 1) {
			parent = grid.getClosestTower(point.x, point.y, 600);
		}

		switch (mode) {
		case WALK:
			if (parent != null) {
				dx = (parent.gatherx - point.x);
				dy = (parent.gathery - point.y);
			} else {
				dx = 0;
				dy = 0;
			}

			if (goali == -1 && goalj == -1) {
				int[] index = resource_grid.getClosestResource(point.x,
						point.y, 100);
				if (index != null) {
					goali = index[0];
					goalj = index[1];
				}
			} else {
				if (!resource_grid.voxelGatherable(goali, goalj)) {
					goali = -1;
					goalj = -1;
				} else {
					dx = (resource_grid.getX(goali) - point.x);
					dy = (resource_grid.getY(goalj) - point.y);
				}
			}

			if (dx == 0 && dy == 0)
				super.think(dt);
			else {
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

			int i = resource_grid.getI(point.x);
			int j = resource_grid.getJ(point.y);
			if (resource_grid.voxelGatherable(i, j)) {
				resource_grid.locked[i][j] = true;
				switchMode(GATHER);
			}

			break;

		case GATHER:
			i = resource_grid.getI(point.x);
			j = resource_grid.getJ(point.y);
			elapsed_dt += dt;

			if (elapsed_dt > GATHER_SPEED) {
				int gather = resource_grid.gatherResource(i, j);
				if (gather != 0) {
					resource_carrying = gather;
					carrying += 1;

					if (carrying >= MAX_RESOURCE_CARRY) {
						resource_grid.locked[i][j] = false;
						switchMode(RETURN);
					}
				} else {
					resource_grid.locked[i][j] = false;
					switchMode(RETURN);
				}
				elapsed_dt = 0;
			}
			break;

		case RETURN:
			if (parent != null) {
				dx = (parent.point.x - point.x);
				dy = (parent.point.y - point.y);

				if (parent.point.distance(point) < parent.radius + radius) {
					game.fireGlobalEvent(new GatherResource(this,
							resource_carrying, carrying));
					resource_carrying = 0;
					carrying = 0;
					switchMode(WALK);
				}
			} else {
				dx = 0;
				dy = 0;
			}

			super.think(dt);

			break;

		case WALK_TO_BUILD:
			if (parent != null) {
				dx = (parent.point.x - point.x);
				dy = (parent.point.y - point.y);

				if (parent.point.distance(point) < parent.radius + radius) {
					switchMode(BUILD);
				}
			} else {
				dx = 0;
				dy = 0;
			}

			super.think(dt);
			break;

		case BUILD:
			elapsed_dt += dt;

			if (elapsed_dt > BUILD_SPEED) {
				parent.percent_complete += TowerEntity.BUILD_RATE;
				if (parent.percent_complete > 1) {
					switchMode(WALK);
				}
				elapsed_dt = 0;
			}

			break;
		}
	}
}
