package inputevents;

import java.awt.Rectangle;

public class GatherResource extends GlobalEvent {

	public int type;
	public int amount;

	public GatherResource(Object source, int type, int amount) {
		super(source);
		this.type = type;
		this.amount = amount;
	}

}
