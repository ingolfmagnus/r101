package inf101.v19.util.generators;

import inf101.v19.grid.IGrid;
import inf101.v19.grid.MyGrid;
import inf101.v19.util.IGenerator;

import java.util.Random;

public class GridGenerator<T> extends AbstractGenerator<IGrid<T>> {
	/**
	 * Generator for the width of a random grid
	 */
	private final IGenerator<Integer> widthGenerator;
	/**
	 * Generator for the height of a random grid
	 */
	private final IGenerator<Integer> heightGenerator;
	/**
	 * Generator for one element of a random grid
	 */
	private final IGenerator<T> elementGenerator;

	/**
	 * Generator for random 2D grids. Each dimension will be from 1 to 100.
	 *
	 * @param elementGenerator
	 *            Generator for the elements
	 */
	public GridGenerator(IGenerator<T> elementGenerator) {
		this.elementGenerator = elementGenerator;
		this.widthGenerator = new IntGenerator(1, 100);
		this.heightGenerator = new IntGenerator(1, 100);
	}

	/**
	 * Generator for random 2D grids using the supplied generators to generate width
	 * and height.
	 *
	 * @param elementGenerator
	 *            Generator for the elements
	 * @param widthGenerator
	 *            Should only generate positive numbers
	 * @param heightGenerator
	 *            Should only generate positive numbers
	 */
	public GridGenerator(IGenerator<T> elementGenerator, IGenerator<Integer> widthGenerator,
			IGenerator<Integer> heightGenerator) {
		this.elementGenerator = elementGenerator;
		this.widthGenerator = widthGenerator;
		this.heightGenerator = heightGenerator;
	}

	/**
	 * Generator for random 2D grids with the given max dimensions.
	 *
	 * @param elementGenerator
	 *            Generator for the elements
	 * @param maxWidth
	 * @param maxHeight
	 */
	public GridGenerator(IGenerator<T> elementGenerator, int maxWidth, int maxHeight) {
		if (maxWidth < 1 || maxHeight < 1) {
			throw new IllegalArgumentException("Width and height must be 1 or greater");
		}

		this.elementGenerator = elementGenerator;
		this.widthGenerator = new IntGenerator(1, maxWidth);
		this.heightGenerator = new IntGenerator(1, maxHeight);
	}

	@Override
	public IGrid<T> generate(Random r) {
		int w = widthGenerator.generate(r);
		int h = heightGenerator.generate(r);

		IGrid<T> grid = new MyGrid<>(w, h, (l) -> elementGenerator.generate(r));

		return grid;
	}
}
