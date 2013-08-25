package inputevents;

import game.Entity;

import java.awt.Rectangle;

public class DelayedEntity extends GlobalEvent {

	public Entity entity;

	public DelayedEntity(Object source, Entity e) {
		super(source);
		this.entity = e;
	}

}
