package game;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;

public class Grid {

	private Rectangle world_rect;

	private int world_width;
	private int world_height;

	private int grid_size;

	private GridLocation[][] grid;

	public Grid(int width, int height, int grid_size) {
		this.world_width = width;
		this.world_height = height;
		this.world_rect = new Rectangle(width, height);
		this.grid_size = grid_size;

		grid = new GridLocation[Math.round(width / grid_size) + 2][Math
				.round(height / grid_size) + 2];

		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++) {
				grid[i][j] = new GridLocation(null);
				grid[i][j].next = grid[i][j];
				grid[i][j].prev = grid[i][j];
			}
	}

	public LinkedList<Entity> getEntitiesIn(Rectangle selection) {
		LinkedList<Entity> result = new LinkedList<Entity>();
		LinkedList<GridLocation> grid_locations = getGridLocations(selection);
		for (GridLocation root : grid_locations) {
			GridLocation current = root.next;
			while (current != root) {
				Entity e = current.parent;
				if (selection.contains(e.point)) {
					result.add(e);
				}
				current = current.next;
			}
		}
		return result;
	}

	public LinkedList<Entity> getEntitiesAt(Point p) {
		LinkedList<Entity> result = new LinkedList<Entity>();

		GridLocation root = getGridLocation(p.x, p.y);
		GridLocation current = root.next;
		while (current != root) {
			Entity e = current.parent;
			if (p.distance(e.point) < e.radius) {
				result.add(e);
			}
			current = current.next;
		}

		return result;
	}

	public LinkedList<GridLocation> getGridLocations(Rectangle selection) {
		LinkedList<GridLocation> result = new LinkedList<GridLocation>();
		double startx = selection.x;
		double starty = selection.y;
		double endx = startx + selection.width;
		double endy = starty + selection.height;

		startx = clipX(startx);
		starty = clipY(starty);
		endx = clipX(endx);
		endy = clipY(endy);

		int starti = getI(startx);
		int startj = getJ(starty);
		int endi = getI(endx);
		int endj = getJ(endy);

		for (int i = starti; i <= endi; i++)
			for (int j = startj; j <= endj; j++) {
				result.add(grid[i][j]);
			}

		return result;
	}

	public GridLocation getGridLocation(double x, double y) {
		return grid[getI(x)][getJ(y)];
	}

	public double clipX(double x) {
		if (x < 0)
			x = 0;
		if (x > world_width)
			x = world_width;
		return x;
	}

	public double clipY(double y) {
		if (y < 0)
			y = 0;
		if (y > world_height)
			y = world_height;
		return y;
	}

	public int getI(double x) {
		return (int) (x / grid_size);
	}

	public int getJ(double y) {
		return (int) (y / grid_size);
	}

	public void updateEntityLocation(Entity entity, double oldx, double oldy) {
		double x = entity.point.x;
		double y = entity.point.y;
		x = clipX(x);
		y = clipY(y);
		entity.point.x = x;
		entity.point.y = y;

		GridLocation old_location = getGridLocation(oldx, oldy);
		GridLocation new_location = getGridLocation(x, y);
		if (old_location != new_location) {
			entity.grid_location.moveToNewList(new_location);
		}
	}

	public void addNewEntity(Entity entity) {
		double x = entity.point.x;
		double y = entity.point.y;
		x = clipX(x);
		y = clipY(y);
		entity.point.x = x;
		entity.point.y = y;

		GridLocation root = getGridLocation(x, y);
		entity.grid_location.insertBefore(root);
	}

	public String toString() {
		String result = super.toString() + "\n";
		for (int i = 0; i < grid.length; i++)
			for (int j = 0; j < grid[i].length; j++) {
				if (grid[i][j].next != grid[i][j]) {
					result += "Found grid_locs at " + i + "," + j + "\n";
				}
			}

		return result;
	}

	public ZombieEntity getZombieIn(GridLocation root) {
		GridLocation current = root.next;
		while (current != root) {
			Entity e = current.parent;
			if (e instanceof ZombieEntity) {
				return (ZombieEntity) e;
			}
			current = current.next;
		}
		return null;
	}

	public ZombieEntity getClosestZombie(double x, double y, double radius) {
		int starti = getI(clipX(x));
		int startj = getJ(clipY(y));
		int endi = getI(clipX(x));
		int endj = getJ(clipY(y));
		int iradius_max = getI(radius);

		{
			if (starti < 1)
				starti = 1;
			if (startj < 1)
				startj = 1;
			if (endi >= grid.length - 1)
				endi = grid.length - 2;
			if (endj >= grid[0].length - 1)
				endj = grid[0].length - 2;

			ZombieEntity e = getZombieIn(grid[starti][startj]);
			if (e != null)
				return e;
		}

		for (int iradius = 0; iradius < iradius_max; iradius++) {
			if (starti < 1)
				starti = 1;
			if (startj < 1)
				startj = 1;
			if (endi >= grid.length - 1)
				endi = grid.length - 2;
			if (endj >= grid[0].length - 1)
				endj = grid[0].length - 2;

			if (endi < starti)
				endi = starti;
			if (endj < startj)
				endj = startj;

			int curi, curj;
			// check top row
			curj = startj - 1;
			for (curi = starti - 1; curi <= endi + 1; curi++) {
				ZombieEntity e = getZombieIn(grid[curi][curj]);
				if (e != null)
					return e;
			}

			// check bottom row
			curj = endj + 1;
			for (curi = starti - 1; curi <= endi + 1; curi++) {
				ZombieEntity e = getZombieIn(grid[curi][curj]);
				if (e != null)
					return e;
			}

			// check left col
			curi = starti - 1;
			for (curj = startj - 1; curj <= endj + 1; curj++) {
				ZombieEntity e = getZombieIn(grid[curi][curj]);
				if (e != null)
					return e;
			}

			// check right col
			curi = endi + 1;
			for (curj = startj - 1; curj <= endj + 1; curj++) {
				ZombieEntity e = getZombieIn(grid[curi][curj]);
				if (e != null)
					return e;
			}
			starti--;
			startj--;
			endi++;
			endj++;
		}
		return null;
	}

	public TowerEntity getTowerIn(GridLocation root) {
		GridLocation current = root.next;
		while (current != root) {
			Entity e = current.parent;
			if (e instanceof TowerEntity && ((TowerEntity) e).built) {
				return (TowerEntity) e;
			}
			current = current.next;
		}
		return null;
	}

	public TowerEntity getClosestTower(double x, double y, double radius) {
		int starti = getI(clipX(x));
		int startj = getJ(clipY(y));
		int endi = getI(clipX(x));
		int endj = getJ(clipY(y));
		int iradius_max = getI(radius);

		{
			if (starti < 1)
				starti = 1;
			if (startj < 1)
				startj = 1;
			if (endi >= grid.length - 1)
				endi = grid.length - 2;
			if (endj >= grid[0].length - 1)
				endj = grid[0].length - 2;

			TowerEntity e = getTowerIn(grid[starti][startj]);
			if (e != null)
				return e;
		}

		for (int iradius = 0; iradius < iradius_max; iradius++) {
			if (starti < 1)
				starti = 1;
			if (startj < 1)
				startj = 1;
			if (endi >= grid.length - 1)
				endi = grid.length - 2;
			if (endj >= grid[0].length - 1)
				endj = grid[0].length - 2;

			if (endi < starti)
				endi = starti;
			if (endj < startj)
				endj = startj;

			int curi, curj;
			// check top row
			curj = startj - 1;
			for (curi = starti - 1; curi <= endi + 1; curi++) {
				TowerEntity e = getTowerIn(grid[curi][curj]);
				if (e != null)
					return e;
			}

			// check bottom row
			curj = endj + 1;
			for (curi = starti - 1; curi <= endi + 1; curi++) {
				TowerEntity e = getTowerIn(grid[curi][curj]);
				if (e != null)
					return e;
			}

			// check left col
			curi = starti - 1;
			for (curj = startj - 1; curj <= endj + 1; curj++) {
				TowerEntity e = getTowerIn(grid[curi][curj]);
				if (e != null)
					return e;
			}

			// check right col
			curi = endi + 1;
			for (curj = startj - 1; curj <= endj + 1; curj++) {
				TowerEntity e = getTowerIn(grid[curi][curj]);
				if (e != null)
					return e;
			}
			starti--;
			startj--;
			endi++;
			endj++;
		}
		return null;
	}

	public VillagerEntity getVillagerIn(GridLocation root) {
		GridLocation current = root.next;
		while (current != root) {
			Entity e = current.parent;
			if (e instanceof VillagerEntity) {
				return (VillagerEntity) e;
			}
			current = current.next;
		}
		return null;
	}

	public VillagerEntity getClosestVillager(double x, double y, double radius) {
		int starti = getI(clipX(x));
		int startj = getJ(clipY(y));
		int endi = getI(clipX(x));
		int endj = getJ(clipY(y));
		int iradius_max = getI(radius);

		{
			if (starti < 1)
				starti = 1;
			if (startj < 1)
				startj = 1;
			if (endi >= grid.length - 1)
				endi = grid.length - 2;
			if (endj >= grid[0].length - 1)
				endj = grid[0].length - 2;

			VillagerEntity e = getVillagerIn(grid[starti][startj]);
			if (e != null)
				return e;
		}

		for (int iradius = 0; iradius < iradius_max; iradius++) {
			if (starti < 1)
				starti = 1;
			if (startj < 1)
				startj = 1;
			if (endi >= grid.length - 1)
				endi = grid.length - 2;
			if (endj >= grid[0].length - 1)
				endj = grid[0].length - 2;
			int curi, curj;
			// check top row
			curj = startj - 1;
			for (curi = starti - 1; curi <= endi + 1; curi++) {
				VillagerEntity e = getVillagerIn(grid[curi][curj]);
				if (e != null)
					return e;
			}

			// check bottom row
			curj = endj + 1;
			for (curi = starti - 1; curi <= endi + 1; curi++) {
				VillagerEntity e = getVillagerIn(grid[curi][curj]);
				if (e != null)
					return e;
			}

			// check left col
			curi = starti - 1;
			for (curj = startj - 1; curj <= endj + 1; curj++) {
				VillagerEntity e = getVillagerIn(grid[curi][curj]);
				if (e != null)
					return e;
			}

			// check right col
			curi = endi + 1;
			for (curj = startj - 1; curj <= endj + 1; curj++) {
				VillagerEntity e = getVillagerIn(grid[curi][curj]);
				if (e != null)
					return e;
			}
			starti--;
			startj--;
			endi--;
			endj--;
		}
		return null;
	}

}
