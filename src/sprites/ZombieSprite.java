package sprites;

import java.awt.Graphics;

import sprites.CharacterSprite.CharacterAction;
import sprites.CharacterSprite.CharacterDir;

public class ZombieSprite {

	public static int FRAMES_BETWEEN_ANIM = 3;

	public static final AnimatedSprite walk_E = new AnimatedSprite(
			"sprites/zombie_east.gif", 2);
	public static final AnimatedSprite walk_W = new AnimatedSprite(
			"sprites/zombie_west.gif", 2);
	public static final AnimatedSprite walk_N = new AnimatedSprite(
			"sprites/zombie_north.gif", 3);
	public static final AnimatedSprite walk_S = new AnimatedSprite(
			"sprites/zombie_south.gif", 3);

	public static AnimatedSprite getSprite(CharacterAction action,
			CharacterDir dir) {
		AnimatedSprite temp = null;
		if (dir == CharacterDir.E)
			temp = walk_E;
		if (dir == CharacterDir.W)
			temp = walk_W;
		if (dir == CharacterDir.N)
			temp = walk_N;
		if (dir == CharacterDir.S)
			temp = walk_S;
		return temp;
	}

}
