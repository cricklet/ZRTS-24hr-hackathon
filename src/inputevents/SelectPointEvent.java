package inputevents;

import java.awt.Point;

public class SelectPointEvent extends GlobalEvent {

	public Point point;
	public boolean ctrl;
	
	public SelectPointEvent(Object source, Point p, boolean ctrl) {
		super(source);
		this.point = p;
		this.ctrl = ctrl;
	}
	
}
