package game;
public class GridLocation {

	public Entity parent;

	public GridLocation next;
	public GridLocation prev;

	public GridLocation(Entity parent) {
		this.parent = parent;
	}

	public void moveToNewList(GridLocation new_location) {
		removeFromList();
		insertBefore(new_location);
	}

	public void removeFromList() {
		GridLocation prev_node = prev;
		GridLocation next_node = next;

		prev_node.next = next_node;
		next_node.prev = prev_node;

		next = null;
		prev = null;
	}

	public void insertBefore(GridLocation new_location) {
		GridLocation prev_node = new_location.prev;
		GridLocation next_node = new_location;

		this.prev = prev_node;
		this.next = next_node;
		prev_node.next = this;
		next_node.prev = this;
	}

}
