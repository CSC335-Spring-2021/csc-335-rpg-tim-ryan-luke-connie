package controllers;

import components.City;
import components.Tile;
import components.Unit;
import models.CivModel;
import models.Player;

/**
 * Provides methods to calculate data about game state or act as a computer
 * player that updates game state.
 *
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 */
public class CivController {

	private final CivModel model;
	private Player curPlayer;

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
	 * Starts a player's turn by doing all of the "housekeeping" automatic game
	 * events for a player turn
	 * 
	 * All Units have their movement reset, all Cities owned by a Player will
	 * produce; City level is checked for advancement
	 * 
	 * @param player
	 */
	public void startTurn() {
		curPlayer = model.getCurPlayer();
		for (Unit u : curPlayer.getUnits())
			u.resetMovement();
		for (City c : curPlayer.getCities()) {
			c.produce();
			c.checkLevelUp();
		}
		model.changeAndNotify();
	}

	public void endTurn() {
		model.nextPlayer();
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
	public boolean moveUnit(int oldRow, int oldCol, int newRow, int newCol) {
		Tile moveFrom = getTileAt(oldRow, oldCol);
		Unit unit = moveFrom.getUnit();
		if (unit == null || !unit.getOwner().equals(curPlayer))
			return false;
		int movement = unit.getMovement();
		if (Math.abs(newRow - oldRow) > 1 || Math.abs(newCol - oldCol) > 1)
			return false;
		Tile moveTo = getTileAt(newRow, newCol);
		int cost = moveTo.getMovementModifier();
		if (cost + 1 > movement)
			return false;
		Unit onTile = moveTo.getUnit();
		boolean movesOnto = true;
		if (onTile != null) // unit exists here, attack it
			movesOnto = attack(unit, onTile);
		else if (moveTo.getTerrainType() == Tile.terrainTypes.CITY
				&& !moveTo.getOwnerCity().getOwner().equals(curPlayer)) // city, atatck
			movesOnto = attack(unit, moveTo.getOwnerCity());
		if (movesOnto) {
			moveFrom.setUnit(null);
			moveTo.setUnit(unit);
		}
		model.changeAndNotify();
		return true;
	}

	// returns true if moves onto (defeats existing unit)
	private boolean attack(Unit attacker, Unit defender) {
		return false;
	}

	private boolean attack(Unit attacker, City defender) {

		return false;
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
	public boolean createUnit(int row, int col, String unitType) {
		Tile tile = getTileAt(row, col);
		City city = tile.getOwnerCity();
		if (city.getProductionReserve() >= Unit.unitCosts.get(unitType)) {
			Unit newUnit = city.produceUnit(unitType);
			tile.setUnit(newUnit);
			curPlayer.addUnit(newUnit);
			model.changeAndNotify();
			return true;
		}
		return false;
	}

	public boolean foundCity(int row, int col) {
		model.changeAndNotify();
		return false;
	}

}
