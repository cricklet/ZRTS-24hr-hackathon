package inputevents;

import java.awt.Rectangle;

public class SelectEvent extends GlobalEvent {

	public Rectangle selection;
	public boolean ctrl;

	public SelectEvent(Object source, Rectangle rect, boolean ctrl) {
		super(source);
		this.selection = rect;
		this.ctrl = ctrl;
	}

}
