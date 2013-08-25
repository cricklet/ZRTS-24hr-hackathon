package inputevents;

import java.awt.Point;

public class RightClickEvent extends GlobalEvent {

	public Point point;
	public boolean ctrl;

	public RightClickEvent(Object source, Point p, boolean ctrl) {
		super(source);
		this.point = p;
		this.ctrl = ctrl;
	}

}
