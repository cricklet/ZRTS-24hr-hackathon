package game;

import java.awt.Color;
import java.awt.Graphics2D;

import sprites.StaticSprite;
import sprites.CharacterSprite.CharacterAction;

public class TowerEntity extends Entity {

	public static final StaticSprite house = new StaticSprite(
			"sprites/house.gif");
	public static final StaticSprite house_transparent = new StaticSprite(
			"sprites/house_transparent.gif");

	public static final int TOWER_RADIUS = 32;

	public static final int[][] UPGRADE_COST = new int[][] { { 0, 100, 0 },
			{ 0, 200, 100 }, { 0, 400, 200 }, { 0, 800, 400 } };
	public static final int[] MAKE_VILLAGER_COST = new int[] { 0, 100, 0 };
	public static final Color[] COLOR = new Color[] { new Color(100, 0, 100),
			new Color(150, 0, 150), new Color(210, 0, 210),
			new Color(255, 0, 255) };
	public static final int[] FIRE_RANGE = new int[] { 100, 150, 200, 250 };
	public static final double[] FIRE_RATE = new double[] { 0.2, 0.1, 0.05,
			0.05 };
	public static final int[] FIRE_DAMAGE = new int[] { 2, 4, 8, 20 };
	public static final int[] HEALTH = new int[] { 100, 200, 400, 800 };

	public HeightMapGradient attract_tower;
	public HeightMapGradient avoid_tower;

	public ResourceGrid resource_grid;
	public int gatherx;
	public int gathery;

	public int level;

	public static final double BUILD_RATE = 0.1;
	public boolean built;
	public double percent_complete;

	public ZombieEntity fire_at;

	public TowerEntity(double x, double y, Grid grid,
			HeightMapGradient attract_tower, HeightMapGradient avoid_tower,
			ResourceGrid resource_grid) {
		super(x, y, TOWER_RADIUS, grid, COLOR[0]);

		this.attract_tower = attract_tower;
		this.avoid_tower = avoid_tower;

		attract_tower.addCone(Constants.TOWER_ATTRACT_MAG_CLOSE, (int) x,
				(int) y, Constants.TOWER_ATTRACT_RADIUS_CLOSE);
		attract_tower.addCone(Constants.TOWER_ATTRACT_MAG_MED, (int) x,
				(int) y, Constants.TOWER_ATTRACT_RADIUS_MED);
		attract_tower.addCone(Constants.TOWER_ATTRACT_MAG_FAR, (int) x,
				(int) y, Constants.TOWER_ATTRACT_RADIUS_FAR);

		avoid_tower.addCone(Constants.TOWER_AVOID_MAG, (int) x, (int) y, radius
				+ Constants.TOWER_AVOID_RADIUS);

		this.level = 0;
		this.health = HEALTH[level];
		this.resource_grid = resource_grid;
		this.built = false;
		this.percent_complete = 0;
	}

	public void autoFinish() {
		built = true;
		percent_complete = 1;
	}

	public void prepareDelete() {
		double x = point.x;
		double y = point.y;
		attract_tower.addCone(-Constants.TOWER_ATTRACT_MAG_CLOSE, (int) x,
				(int) y, Constants.TOWER_ATTRACT_RADIUS_CLOSE);
		attract_tower.addCone(-Constants.TOWER_ATTRACT_MAG_MED, (int) x,
				(int) y, Constants.TOWER_ATTRACT_RADIUS_MED);
		attract_tower.addCone(-Constants.TOWER_ATTRACT_MAG_FAR, (int) x,
				(int) y, Constants.TOWER_ATTRACT_RADIUS_FAR);

		avoid_tower.addCone(-Constants.TOWER_AVOID_MAG, (int) x, (int) y,
				radius + Constants.TOWER_AVOID_RADIUS);
	}

	public boolean upgrade() {
		if (built == false)
			return false;

		if (level < UPGRADE_COST.length - 1) {
			level++;
			health = HEALTH[level];
			color = COLOR[level];
			return true;
		}
		return false;
	}

	public double elapsed_time;

	public void think(double dt) {
		super.think(dt);

		if (built == false) {
			if (percent_complete > 1)
				built = true;
			return;
		}

		if (gatherx == -1 || gathery == -1) {
			int[] index = resource_grid.getClosestResource(point.x, point.y,
					600);
			if (index != null) {
				gatherx = (int) resource_grid.getX(index[0]);
				gathery = (int) resource_grid.getY(index[1]);
			}
		} else {
			int gatheri = resource_grid.getI(gatherx);
			int gatherj = resource_grid.getJ(gathery);
			if (resource_grid.voxels[gatheri][gatherj] == 0) {
				gatherx = -1;
				gathery = -1;
			}
		}

		elapsed_time += dt;
		if (elapsed_time > FIRE_RATE[level]) {
			fire_at = null;
			fire_at = grid
					.getClosestZombie(point.x, point.y, FIRE_RANGE[level]);
			if (fire_at != null)
				fire_at.hurt(FIRE_DAMAGE[level]);
			elapsed_time = 0;
		}
	}

	public void draw(Graphics2D g, int xoff, int yoff) {
		int x = (int) point.x + xoff - radius;
		int y = (int) point.y + yoff - radius;
		if (!built) {
			g.drawImage(house_transparent.getImage(), x, y, house_transparent
					.getWidth(), house_transparent.getHeight(), null);
		} else {
			g.drawImage(house.getImage(), x, y, house.getWidth(), house
					.getHeight(), null);
		}

		if (fire_at != null) {
			int x1 = (int) point.x + xoff;
			int y1 = (int) point.y + yoff;
			int x2 = (int) fire_at.point.x + xoff;
			int y2 = (int) fire_at.point.y + yoff;

			g.drawLine(x1, y1, x2, y2);
		}
		if (selected) {
			g.setColor(Color.green);
			int off = 5;
			g.drawOval(x - off, y - off, radius * 2 + off * 2, radius * 2 + off
					* 2);
		}
	}
}
