package controllers;

import components.City;
import components.Tile;
import components.Unit;
import models.CivModel;

/**
 * Provides methods to calculate data about game state or act as a computer
 * player that updates game state.
 *
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 */
public class CivController {

	private final CivModel model;

	/**
	 * Constructor for controller
	 *
	 * @param model model that the controller will interact with to store the
	 *              results of the operations it performs
	 */
	public CivController(CivModel model) {
		this.model = model;
	}

	/**
	 * Returns the Tile located at the board location row, col
	 * 
	 * @param row int for row of board positionn
	 * @param col int for col of board position
	 * 
	 * @return the Tile at row,col on the board
	 * 
	 */
	public Tile getTileAt(int row, int col) {
		return model.getTileAt(row, col);
	}

	/**
	 * Moves a unit from its old location to the new player-specified location.
	 * 
	 * Unit moves one tile at a time
	 * 
	 * @param oldx int of old row location of unit
	 * @param oldy int of old col location of unit
	 * @param newx int of new row location of unit
	 * @param newy int of new col location of unit
	 * @return true if the unit was successfully moved, false otherwise
	 */
	public boolean moveUnit(int oldx, int oldy, int newx, int newy) {
		Unit unit = getTileAt(oldx, oldy).getUnit();
		if (unit == null)
			return false;
		int move = unit.getMovement();
		// TODO
		return true;
	}

	/**
	 * Creates a unit on the given tile if the city has enough in its production
	 * reserve to make that unit.
	 * 
	 * View should pass the correct Tile object when a player tries to create a unit
	 * (i.e., only an actual city Tile can produce a unit). Updates the tile so that
	 * it has the new unit on it.
	 * 
	 * @param tile     the Tile (city) creating a unit
	 * @param unitType String representing the type of unit to create
	 * @return true if the unit was successfully created and added to the board,
	 *         false otherwise
	 */
	public boolean createUnit(Tile tile, String unitType) {
		City city = tile.getOwnerCity();
		if (city.getProductionReserve() >= Unit.unitCosts.get(unitType)) {
			Unit newUnit = city.produceUnit(unitType);
			tile.setUnit(newUnit);
			return true;
		}
		return false;
	}

}
