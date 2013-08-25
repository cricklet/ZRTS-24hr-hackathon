package game;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedList;

import sprites.StaticSprite;

public class ResourceGrid extends VoxelGrid {

	public static final int NOTHING = 0;
	public static final int FOOD = 1;
	public static final int MINERALS = 2;

	public static final int STARTING_DENSITY = 20;

	public int[][] resource_density;
	public boolean[][] locked;

	public static final StaticSprite[] sprites = new StaticSprite[] { null,
			new StaticSprite("sprites/grass.gif"),
			new StaticSprite("sprites/diamond.gif") };

	public static final String[] RESOURCE_NAMES = new String[] { null, "Food",
			"Minerals" };

	public HeightMapGradient avoid_resources;

	public LinkedList<ResourceClump> clumps;

	public class ResourceClump {
		public int num_voxels;
		public int cx;
		public int cy;
		public int radius;

		public int type;

		public ResourceClump(int cx, int cy, int radius, int type) {
			this.cx = cx;
			this.cy = cy;
			this.radius = radius;
			this.num_voxels = 0;
			this.type = type;

			int ci = getI(cx);
			int cj = getJ(cy);
			int iradius = getI(radius);

			for (int i = ci - iradius; i < ci + iradius; i++) {
				for (int j = cj - iradius; j < cj + iradius; j++) {
					if (i < 0 || j < 0 || i >= voxels.length
							|| j >= voxels[0].length)
						continue;
					double di = i - ci;
					double dj = j - cj;
					double idist = Math.sqrt(di * di + dj * dj);

					if (idist < iradius) {
						voxels[i][j] = type;
						resource_density[i][j] = STARTING_DENSITY;
						locked[i][j] = false;
						num_voxels++;
					}
				}
			}

			addForce();
		}

		public void removeForce() {
			avoid_resources.addCone(-Constants.RESOURCE_AVOID_MAG, cx, cy,
					radius + Constants.RESOURCE_AVOID_RADIUS);
		}

		public void addForce() {
			avoid_resources.addCone(Constants.RESOURCE_AVOID_MAG, cx, cy,
					radius + Constants.RESOURCE_AVOID_RADIUS);
		}
	}

	public ResourceGrid(int width, int height, int voxel_size) {
		super(width, height, voxel_size);

		resource_density = new int[voxels.length][voxels[0].length];
		locked = new boolean[voxels.length][voxels[0].length];
		avoid_resources = new HeightMapGradient(width, height, voxel_size);

		clumps = new LinkedList<ResourceClump>();

		for (int i = 0; i < voxels.length; i++) {
			for (int j = 0; j < voxels[i].length; j++) {
				voxels[i][j] = 0;
			}
		}

		for (int x = 0; x < width; x += width / 5) {
			for (int y = 0; y < height; y += height / 5) {
				int cx = (int) (Math.random() * width / 5 + x);
				int cy = (int) (Math.random() * height / 5 + y);
				int radius = (int) (height / 10);
				clumps.add(new ResourceClump(cx, cy, radius, FOOD));
			}
		}

		for (int i = 0; i < 3; i++) {
			int cx = (int) (Math.random() * width);
			int cy = (int) (Math.random() * height);
			int radius = (int) (100 + Math.random() * 50);
			clearCircle(cx, cy, radius);
		}

		int cx = (int) (width / 2);
		int cy = (int) (height / 2);
		int radius = (int) (100 + Math.random() * 50);
		clearCircle(cx, cy, radius);

		for (int i = 0; i < 6; i++) {
			cx = (int) (Math.random() * width);
			cy = (int) (Math.random() * height);
			radius = (int) (50 + Math.random() * 50);
			clumps.add(new ResourceClump(cx, cy, radius, MINERALS));
		}

		Iterator<ResourceClump> clumpit = clumps.iterator();
		while (clumpit.hasNext()) {
			ResourceClump c = clumpit.next();
			if (c.num_voxels < 1) {
				c.removeForce();
				clumpit.remove();
			}
		}
	}

	public void clearCircle(int cx, int cy, int radius) {
		int ci = getI(cx);
		int cj = getJ(cy);
		int iradius = getI(radius);

		for (int i = ci - iradius; i < ci + iradius; i++) {
			for (int j = cj - iradius; j < cj + iradius; j++) {
				if (i < 0 || j < 0 || i >= voxels.length
						|| j >= voxels[0].length)
					continue;
				double di = i - ci;
				double dj = j - cj;
				double idist = Math.sqrt(di * di + dj * dj);

				if (idist < iradius) {
					voxels[i][j] = 0;
					double x = getX(i);
					double y = getY(j);
					for (ResourceClump clump : clumps) {
						double dx = clump.cx - x;
						double dy = clump.cy - y;
						if (dx * dx + dy * dy <= clump.radius * clump.radius) {
							clump.num_voxels--;
						}
					}
				}
			}
		}
	}

	public void draw(Graphics2D g, Rectangle view, double min, double max) {
		double x = view.x;
		double y = view.y;
		double endx = view.x + view.width;
		double endy = view.y + view.height;

		x = clipX(x);
		y = clipY(y);
		endx = clipX(endx);
		endy = clipY(endy);

		int xoff = -view.x;
		int yoff = -view.y;
		int starti = getI(x);
		int startj = getJ(y);
		int endi = getI(endx);
		int endj = getJ(endy);

		for (int i = starti; i <= endi; i++) {
			for (int j = startj; j <= endj; j++) {
				double value = voxels[i][j];
				if (sprites[(int) value] != null)
					g.drawImage(sprites[(int) value].getImage(), (int) getX(i)
							- voxel_size / 2 + xoff, (int) getY(j) - voxel_size
							/ 2 + yoff, sprites[(int) value].getWidth(),
							sprites[(int) value].getHeight(), null);
			}
		}
	}

	public boolean voxelGatherable(int i, int j) {
		if (voxels[i][j] == 0)
			return false;
		if (resource_density[i][j] <= 0)
			return false;
		if (locked[i][j])
			return false;
		return true;
	}

	public int[] getClosestResource(double x, double y, double radius) {
		boolean possible = false;
		for (ResourceClump clump : clumps) {
			double dx = clump.cx - x;
			double dy = clump.cy - y;
			if (Math.sqrt(dx * dx + dy * dy) <= clump.radius + radius) {
				possible = true;
			}
		}

		if (!possible)
			return null;

		int starti = getI(clipX(x));
		int startj = getJ(clipY(y));
		int endi = getI(clipX(x));
		int endj = getJ(clipY(y));
		int iradius_max = getI(radius);

		for (int iradius = 0; iradius < iradius_max; iradius++) {
			if (starti < 1)
				starti = 1;
			if (startj < 1)
				startj = 1;
			if (endi >= voxels.length - 1)
				endi = voxels.length - 2;
			if (endj >= voxels[0].length - 1)
				endj = voxels[0].length - 2;

			if (endi < starti)
				endi = starti;
			if (endj < startj)
				endj = startj;

			int curi, curj;
			// check top row
			curj = startj - 1;
			for (curi = starti - 1; curi <= endi + 1; curi++) {
				if (voxelGatherable(curi, curj))
					return new int[] { curi, curj };
			}

			// check bottom row
			curj = endj + 1;
			for (curi = starti - 1; curi <= endi + 1; curi++) {
				if (voxelGatherable(curi, curj))
					return new int[] { curi, curj };
			}

			// check left col
			curi = starti - 1;
			for (curj = startj - 1; curj <= endj + 1; curj++) {
				if (voxelGatherable(curi, curj))
					return new int[] { curi, curj };
			}

			// check right col
			curi = endi + 1;
			for (curj = startj - 1; curj <= endj + 1; curj++) {
				if (voxelGatherable(curi, curj))
					return new int[] { curi, curj };
			}
			starti--;
			startj--;
			endi++;
			endj++;
		}
		return null;
	}

	public int gatherResource(int i, int j) {
		double result = voxels[i][j];
		resource_density[i][j]--;

		if (resource_density[i][j] <= 0)
			killResource(i, j);

		return (int) result;
	}

	public void killResource(int i, int j) {
		voxels[i][j] = 0;
		locked[i][j] = false;

		double x = getX(i);
		double y = getY(j);
		for (ResourceClump clump : clumps) {
			double dx = clump.cx - x;
			double dy = clump.cy - y;
			if (dx * dx + dy * dy <= clump.radius * clump.radius) {
				clump.num_voxels--;
			}
		}

		Iterator<ResourceClump> clumpit = clumps.iterator();
		while (clumpit.hasNext()) {
			ResourceClump c = clumpit.next();
			if (c.num_voxels < 1) {
				c.removeForce();
				clumpit.remove();
			}
		}
	}
}
