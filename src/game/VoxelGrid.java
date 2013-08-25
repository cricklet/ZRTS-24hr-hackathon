package game;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;

public class VoxelGrid {

	private int world_width;
	private int world_height;

	protected int voxel_size;

	protected double[][] voxels;
	private double[] color;

	public VoxelGrid(int width, int height, int voxel_size) {
		this.world_width = width;
		this.world_height = height;
		this.voxel_size = voxel_size;
		this.color = new double[] { 255, 255, 0 };

		voxels = new double[Math.round(width / voxel_size) + 2][Math
				.round(height / voxel_size) + 2];
		for (int i = 0; i < voxels.length; i++) {
			for (int j = 0; j < voxels[i].length; j++) {
				voxels[i][j] = 0;
			}
		}

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

	public double getX(int i) {
		return i * voxel_size + voxel_size / 2;
	}

	public double getY(int j) {
		return j * voxel_size + voxel_size / 2;
	}

	public int getI(double x) {
		return (int) (x / voxel_size);
	}

	public int getJ(double y) {
		return (int) (y / voxel_size);
	}

	public double[] getMinMax(Rectangle view) {
		double min = 9999999;
		double max = -9999999;

		double x = view.x;
		double y = view.y;
		double endx = view.x + view.width;
		double endy = view.y + view.height;

		x = clipX(x);
		y = clipY(y);
		endx = clipX(endx);
		endy = clipY(endy);

		int starti = getI(x);
		int startj = getJ(y);
		int endi = getI(endx);
		int endj = getJ(endy);

		for (int i = starti; i <= endi; i++) {
			for (int j = startj; j <= endj; j++) {
				if (voxels[i][j] < min)
					min = voxels[i][j];
				if (voxels[i][j] > max)
					max = voxels[i][j];
			}
		}

		return new double[] { min, max };
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
				double value = (voxels[i][j] - min) / (max - min);
				int R = (int) (value * color[0]);
				int G = (int) (value * color[1]);
				int B = (int) (value * color[2]);
				R = Math.min(R, 255);
				G = Math.min(G, 255);
				B = Math.min(B, 255);
				R = Math.max(R, 0);
				G = Math.max(G, 0);
				B = Math.max(B, 0);
				g.setColor(new Color(R, G, B));
				g.fillRect(i * voxel_size - voxel_size / 2 + xoff, j
						* voxel_size - voxel_size / 2 + yoff, voxel_size,
						voxel_size);
			}
		}
	}
}
