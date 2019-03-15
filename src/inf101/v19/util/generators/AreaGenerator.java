package inf101.v19.util.generators;

import inf101.v19.grid.IArea;
import inf101.v19.grid.RectArea;
import inf101.v19.util.IGenerator;

import java.util.Random;

public class AreaGenerator extends AbstractGenerator<IArea> {
	/**
	 * Generator for the x-coordinate
	 */
	private final IGenerator<Integer> xGenerator;
	/**
	 * Generator for the y-coordinate
	 */
	private final IGenerator<Integer> yGenerator;

	/**
	 * Generate random Areas between (1,1) and (100,100)
	 */
	public AreaGenerator() {
		this.xGenerator = new IntGenerator(1, 100);
		this.yGenerator = new IntGenerator(1, 100);
	}

	/**
	 * Generate random Areas between (0,0) and (maxWidth,maxHeight)
	 *
	 * @param maxWidth
	 * @param maxHeight
	 */
	public AreaGenerator(int maxWidth, int maxHeight) {
		if (maxWidth < 1 || maxHeight < 1) {
			throw new IllegalArgumentException("Width and height must be 0 or greater");
		}

		this.xGenerator = new IntGenerator(1, maxWidth);
		this.yGenerator = new IntGenerator(1, maxHeight);
	}

	@Override
	public IArea generate(Random r) {
		return new RectArea(xGenerator.generate(r), yGenerator.generate(r));
	}
}
