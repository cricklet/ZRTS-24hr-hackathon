package game;

import java.awt.Color;
import java.awt.Graphics2D;

import sprites.AnimatedSprite;
import sprites.CharacterSprite;
import sprites.ZombieSprite;
import sprites.CharacterSprite.CharacterAction;
import sprites.CharacterSprite.CharacterDir;

public class ZombieEntity extends MoveableEntity {

	public static final double ZOMBIE_HEALTH = 20;
	public static final double ZOMBIE_SPEED = 50;
	public static final int ZOMBIE_RADIUS = 5;

	public static final int ZOMBIE_FOLLOW_RADIUS = 100;
	public static final int ZOMBIE_ATTACK_RADIUS = 30;
	public static final int ZOMBIE_DAMAGE = 20;

	public ZombieEntity(int x, int y, Grid grid) {
		super(x, y, ZOMBIE_RADIUS, ZOMBIE_SPEED, grid, Color.red);
		this.health = ZOMBIE_HEALTH;
		switchMode(WALK);
	}

	private int frame;
	private static final int ANIM_FRAMES = 2;

	public void draw(Graphics2D g, int xoff, int yoff) {
		int x = (int) point.x + xoff - radius;
		int y = (int) point.y + yoff - radius;
		AnimatedSprite animsprite = ZombieSprite.getSprite(action, dir);

		if (Math.abs(dx) > 0 || Math.abs(dy) > 0) {
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

		g.setColor(Color.blue);
		g.fillRect((int) x, (int) y - 5, (int) ((1.0 + health)
				/ (0.0 + ZOMBIE_HEALTH) * (0.0 + radius) * 2.0), 3);
	}

	public static final int WALK = 0;
	public static final int ATTACK = 1;
	public int mode;

	private static final double ATTACK_TIME = 1;
	private double elapsed_time = 0;

	public void switchMode(int mode) {
		this.mode = mode;
		
		dx = 0;
		dy = 0;

		switch (mode) {
		case WALK:
			moveable = true;
			action = CharacterAction.WALK;
			break;
		case ATTACK:
			moveable = false;
			action = CharacterAction.ACT;
			break;
		}
	}

	public void think(double dt) {
		switch (mode) {
		case WALK:
			VillagerEntity closest_villager = grid.getClosestVillager(point.x,
					point.y, ZOMBIE_FOLLOW_RADIUS);
			TowerEntity closest_tower = grid.getClosestTower(point.x, point.y,
					ZOMBIE_FOLLOW_RADIUS);

			Entity closest_entity = null;
			if (closest_tower != null) {
				closest_entity = closest_tower;
			} else if (closest_villager != null) {
				closest_entity = closest_villager;
			}

			if (closest_entity != null) {
				dx = closest_entity.point.x - point.x;
				dy = closest_entity.point.y - point.y;
			}

			if (closest_entity != null
					&& closest_entity.point.distance(point) < ZOMBIE_ATTACK_RADIUS) {
				switchMode(ATTACK);
				closest_entity.health -= ZOMBIE_DAMAGE;
			}

			break;
		case ATTACK:
			elapsed_time += dt;
			if (elapsed_time > ATTACK_TIME) {
				switchMode(WALK);
				elapsed_time = 0;
			}
			break;
		}

		super.think(dt);

	}
}
