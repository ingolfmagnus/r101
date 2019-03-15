package inf101.v19.grid.tests;

import inf101.v19.grid.GridDirection;
import inf101.v19.grid.IArea;
import inf101.v19.grid.ILocation;
import inf101.v19.util.IGenerator;
import inf101.v19.util.generators.AreaGenerator;
import inf101.v19.util.generators.LocationGenerator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AreaRetting {
	private static final int N = 10000;

	private IGenerator<IArea> areaGen = new AreaGenerator();

	public void allAreaProperties() {

	}

	public void canGoProperty(ILocation l, GridDirection dir) {
		int x = l.getX() + dir.getDx();
		int y = l.getY() + dir.getDy();
		assertEquals(l.getArea().contains(x, y), l.canGo(dir));
		try {
			assertNotNull(l.go(dir));
			assertTrue(l.canGo(dir), String.format("Expected true: %s.canGo(%s) for %d,%d", l, dir, x, y));
		} catch (IndexOutOfBoundsException e) {
			assertFalse(l.canGo(dir), String.format("Expected false: %s.canGo(%s) for %d,%d", l, dir, x, y));
		}
	}

	public void distanceProperty(ILocation l1, ILocation l2) {
		assertEquals(l1.gridDistanceTo(l2), l2.gridDistanceTo(l1));
		assertEquals(l1.stepDistanceTo(l2), l2.stepDistanceTo(l1));
		assertTrue(l1.gridDistanceTo(l2) <= l1.stepDistanceTo(l2));
		assertTrue(l1.gridDistanceTo(l2) <= l1.geometricDistanceTo(l2));
	}

	public void gridLineProperty(ILocation l1, ILocation l2) {
		// System.out.println(l1.toString() + " .. " + l2.toString());
		List<ILocation> line = l1.gridLineTo(l2);
		assertEquals(l1.gridDistanceTo(l2), line.size());
		ILocation last = l1;
		for (ILocation l : line) {
			assertEquals(1, last.gridDistanceTo(l));
			last = l;
		}
		assertEquals(l2, last);
	}

	@Test
	public void gridLineTest() {
		for (int i = 0; i < 10; i++) {
			IArea area = areaGen.generate();
			IGenerator<ILocation> lGen = new LocationGenerator(area);
			for (int j = 0; j < N; j++) {
				ILocation l1 = lGen.generate();
				ILocation l2 = lGen.generate();
				distanceProperty(l1, l2);
				gridLineProperty(l1, l2);
			}
		}
	}

	@Test
	public void locationsTest() {
		for (int i = 0; i < 10; i++) {
			IArea area = areaGen.generate();
			for (ILocation l : area) {
				neighboursDistProperty(l);
				neighboursSymmetryProperty(l);
				for (GridDirection d : GridDirection.EIGHT_DIRECTIONS)
					canGoProperty(l, d);
			}
		}
	}

	public void neighboursDistProperty(ILocation loc) {
		for (ILocation l : loc.allNeighbours()) {
			assertEquals(1, loc.gridDistanceTo(l));
		}
	}

	public void neighboursSymmetryProperty(ILocation loc) {
		for (ILocation l : loc.allNeighbours()) {
			assertTrue(l.allNeighbours().contains(loc), "My neighbour should have me as a neighbour");
		}
	}

	@Test
	public void uniqueLocations() {
		for (int i = 0; i < N / 10; i++) {
			IArea area = areaGen.generate();
			uniqueLocationsProperty(area);
		}
	}

	public void uniqueLocationsProperty(IArea area) {
		Set<ILocation> set = new HashSet<>();
		for (ILocation l : area) {
			assertTrue( set.add(l), "Location should be unique: " + l);
		}
	}

	@Test
	public void validLocations() {
		for (int i = 0; i < N / 10; i++) {
			IArea area = areaGen.generate();
			validLocationsProperty(area);
		}
	}

	public void validLocationsProperty(IArea area) {
		for (ILocation l : area) {
			assertTrue(area.contains(l),"Location should be in area: " + l);
			assertTrue(area.contains(l.getX(), l.getY()),"Location should be in area: " + l);
		}
	}
}
