package inf101.v19.grid.tests;

import inf101.v19.grid.IGrid;
import inf101.v19.grid.ILocation;
import inf101.v19.util.IGenerator;
import inf101.v19.util.generators.GridGenerator;
import inf101.v19.util.generators.LocationGenerator;
import inf101.v19.util.generators.StringGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Function;

import org.junit.jupiter.api.Test;

public class GridRetting {
	private static final int N = 10000;

	private IGenerator<String> strGen = new StringGenerator();
	private IGenerator<IGrid<String>> gridGen = new GridGenerator<String>(strGen);

	public <T> void fillProperty1(IGrid<T> grid, T val) {
		grid.fill(val);
		for (ILocation l : grid.locations()) {
			assertEquals(val, grid.get(l));
		}
	}

	public <T> void fillProperty2(IGrid<T> grid, Function<ILocation, T> fun) {
		grid.fill(fun);
		for (ILocation l : grid.locations()) {
			assertEquals(fun.apply(l), grid.get(l));
		}
	}

	@Test
	public void fillTest1() {
		for (int i = 0; i < N / 10; i++) {
			IGrid<String> grid = gridGen.generate();

			String s = strGen.generate();
			fillProperty1(grid, s);
		}
	}

	@Test
	public void fillTest2() {
		for (int i = 0; i < N / 10; i++) {
			IGrid<String> grid = gridGen.generate();

			fillProperty2(grid, (l) -> l.toString());
		}
	}

	/** A set on (x1,y1) doesn't affect a get on a different (x2,y2) */
	public <T> void setGetIndependentProperty(IGrid<T> grid, ILocation l1, ILocation l2, T val) {
		if (!l1.equals(l2)) {
			T s = grid.get(l2);
			grid.set(l1, val);
			assertEquals(s, grid.get(l2));
		}
	}

	@Test
	public void setGetIndependentTest() {
		for (int j = 0; j < 10; j++) {
			IGrid<String> grid = gridGen.generate();
			IGenerator<ILocation> lGen = new LocationGenerator(grid.getArea());

			for (int i = 0; i < N; i++) {
				ILocation l1 = lGen.generate();
				ILocation l2 = lGen.generate();
				String s = strGen.generate();

				setGetIndependentProperty(grid, l1, l2, s);
			}
		}
	}

	/** get(x,y) is val after set(x, y, val) */
	public <T> void setGetProperty(IGrid<T> grid, ILocation l, T val) {
		grid.set(l, val);
		assertEquals(val, grid.get(l));
	}

	/** Test that get gives back the same value after set. */
	@Test
	public void setGetTest() {
		for (int j = 0; j < 10; j++) {
			IGrid<String> grid = gridGen.generate();
			IGenerator<ILocation> lGen = new LocationGenerator(grid.getArea());

			for (int i = 0; i < N; i++) {
				ILocation l = lGen.generate();
				String s = strGen.generate();

				setGetProperty(grid, l, s);
			}
		}
	}

	@Test
	public void uniqueLocations() {
		for (int i = 0; i < N / 10; i++) {
		}
	}

}
