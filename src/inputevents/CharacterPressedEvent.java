package inputevents;

import java.awt.Rectangle;

public class CharacterPressedEvent extends GlobalEvent {

	public char c;

	public CharacterPressedEvent(Object source, char c) {
		super(source);
		this.c = c;
	}

}