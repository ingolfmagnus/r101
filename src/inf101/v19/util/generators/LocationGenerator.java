package inf101.v19.util.generators;

import java.util.Random;

import inf101.v19.grid.IArea;
import inf101.v19.grid.ILocation;

public class LocationGenerator extends AbstractGenerator<ILocation> {
	private final IArea area;

	/**
	 * New LocationGenerator, will generate locations within the area
	 */
	public LocationGenerator(IArea area) {
		this.area = area;
	}

	@Override
	public ILocation generate(Random r) {
		return area.location(r.nextInt(area.getWidth()), r.nextInt(area.getHeight()));
	}

}
