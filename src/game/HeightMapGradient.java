package game;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;

public class HeightMapGradient extends VoxelGrid implements Gradient {

	public HeightMapGradient(int width, int height, int voxel_size) {
		super(width, height, voxel_size);
	}

	public void addGaussuan(double magnitude, int cx, int cy, int sigma) {
		int ci = getI(cx);
		int cj = getJ(cy);
		int iradius = getI(3 * sigma);
		int isigma = getI(sigma);

		for (int i = ci - iradius; i < ci + iradius; i++) {
			for (int j = cj - iradius; j < cj + iradius; j++) {
				if (i < 0 || j < 0 || i >= voxels.length
						|| j >= voxels[0].length)
					continue;
				double di = i - ci;
				double dj = j - cj;
				double idist = Math.sqrt(di * di + dj * dj);
				double value = voxel_size * iradius
						* Math.exp(-idist * idist / (2 * isigma * isigma));
				voxels[i][j] += value * magnitude;
			}
		}
	}

	public void multiplyBy(double factor, int cx, int cy, int radius) {
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

				if (idist < iradius)
					voxels[i][j] *= factor;
			}
		}
	}

	public void addCone(double magnitude, int cx, int cy,
			double towerAttractRadiusClose) {
		int ci = getI(cx);
		int cj = getJ(cy);
		int iradius = getI(towerAttractRadiusClose);

		for (int i = ci - iradius; i < ci + iradius; i++) {
			for (int j = cj - iradius; j < cj + iradius; j++) {
				if (i < 0 || j < 0 || i >= voxels.length
						|| j >= voxels[0].length)
					continue;
				double di = i - ci;
				double dj = j - cj;
				double idist = Math.sqrt(di * di + dj * dj);
				double value = (iradius - idist) * voxel_size;
				value = Math.max(0, value);
				voxels[i][j] += value * magnitude;
			}
		}
	}

	public double[] calculateDxDy(int cx, int cy, double vx, double vy) {
		int ci = getI(cx);
		int cj = getJ(cy);

		double dx = 0;
		double dy = 0;

		if (ci + 1 < voxels.length)
			dx = voxels[ci + 1][cj] - voxels[ci][cj];
		if (ci - 1 > 0)
			dx += voxels[ci][cj] - voxels[ci - 1][cj];
		if (cj + 1 < voxels[0].length)
			dy = voxels[ci][cj + 1] - voxels[ci][cj];
		if (cj - 1 > 0)
			dy += voxels[ci][cj] - voxels[ci][cj - 1];

		dx *= -1;
		dy *= -1;

		double dangle = Math.atan2(dy, dx);
		double vangle = Math.atan2(vy, vx);

		if (Math.abs(dangle + Math.PI - vangle) < Math.PI / 2
				|| Math.abs(dangle - Math.PI - vangle) < Math.PI / 2) {

			double diff = 0;
			if (Math.abs(dangle + Math.PI - vangle) < Math.PI / 2)
				diff = dangle + Math.PI - vangle;

			if (Math.abs(dangle - Math.PI - vangle) < Math.PI / 2)
				diff = dangle - Math.PI - vangle;

			double mag = Math.sqrt(dx * dx + dy * dy);
			if (diff < 0) {
				dangle -= (Math.PI / 2 - Math.abs(diff)) / 3;
			} else {
				dangle += (Math.PI / 2 - Math.abs(diff)) / 3;
			}
			dx = Math.cos(dangle) * mag;
			dy = Math.sin(dangle) * mag;
		}

		dx /= 2 * voxel_size;
		dy /= 2 * voxel_size;

		return new double[] { dx, dy };
	}
}
