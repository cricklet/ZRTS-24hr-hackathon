package inputevents;

import java.awt.Rectangle;

public class AddEntity extends GlobalEvent {

	public int type;
	public int x;
	public int y;

	public AddEntity(Object source, int type, int x, int y) {
		super(source);
		this.type = type;
		this.x = x;
		this.y = y;
	}

}
