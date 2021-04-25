package controllers;

import components.City;
import components.Settler;
import components.Tile;
import components.Unit;
import models.CivModel;
import models.Player;

import java.awt.*;

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
	 * Configure the map with the units that should exist at the start of a new game.
	 */
	public void placeStartingUnits() {
		Settler settler = new Settler(model.getCurPlayer(), new Point(0, 0));
		model.getTileAt(0, 0).setUnit(settler);

		City city = new City(model.getCurPlayer(), 1, 1);
		model.getTileAt(1, 1).foundCity(city);
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
	public Tile getTileAt(int x, int y) {
		return model.getTileAt(x, y);
	}

	/**
	 * Starts a player's turn by doing all of the "housekeeping" automatic game
	 * events for a player turn
	 *
	 * All Units have their movement reset, all Cities owned by a Player are
	 * incremented
	 *
	 * @param player
	 */
	public void startTurn() {
		curPlayer = model.getCurPlayer();
		for (Unit u : curPlayer.getUnits())
			u.resetMovement();
		for (City c : curPlayer.getCities())
			c.cityIncrement();
		if (!curPlayer.isHuman())
			computerTurn();
		model.changeAndNotify();
	}

	/**
	 * Human player ends their turn, the model moves on to the next player. This
	 * will update the curPlayer for when the next turn begins.
	 */
	public void endTurn() {
		model.nextPlayer();
		model.changeAndNotify();
	}

	/**
	 * When there is only 1 player left, the game is won.
	 *
	 * @return true if the game is over, false otherwise
	 */
	public boolean gameOver() {
		return model.numPlayers() == 1;
	}

	/**
	 * does all the computer turn's AI stuff
	 */
	public void computerTurn() {
		// TODO AI logic
		endTurn();
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
	 * @return true if the unit successfully moved/attacked, false otherwise
	 */
	public boolean moveUnit(int oldX, int oldY, int newX, int newY) {
		Tile moveFrom = getTileAt(oldX, oldY);
		Unit unit = moveFrom.getUnit(); // unit to move
		if (unit == null)
			return false;
		int movement = unit.getMovement();
		// this conditional checks that the unit is only moving 1 space
		if (Math.abs(newX - oldX) > 1 || Math.abs(newY - oldY) > 1)
			return false;
		Tile moveTo = getTileAt(newX, newY);
		int cost = moveTo.getMovementModifier();
		if (cost + 1 > movement)
			return false;
		Unit onTile = moveTo.getUnit();
		boolean movesOnto = true;
		if (onTile != null) { // unit exists here, attack it
			movesOnto = attack(moveFrom, moveTo);
			cost = unit.getMovement() - 1; // have to deplete to if successful move
		}
		// eventually have to change the city check to isCityTile()
		else if (moveTo.getTerrainType() == Tile.terrainTypes.CITY
				&& !moveTo.getOwnerCity().getOwner().equals(curPlayer)) // city, atatck
			movesOnto = attack(moveFrom, moveTo.getOwnerCity());
		if (movesOnto) {
			moveFrom.setUnit(null); // unit gone
			moveTo.setUnit(unit); // successfully moves to new tile
			unit.move(cost + 1, newX, newY); // update costs and unit location
			revealTiles(newX, newY); // reveal tiles around unit
		}
		model.changeAndNotify();
		return true;
	}

	/**
	 * Set all the tiles in a 1-tile radius around the given location as revealed
	 * for the current player.
	 *
	 * @param row int of row location middle tile
	 * @param col int of col location middle tile
	 */
	private void revealTiles(int x, int y) {
		for (int i = -1; i < 2; i++) {
			for (int j = -1; j < 2; j++) {
				int toRevealRow = x + i;
				int toRevealCol = y + j;
				Tile toRevealTile = getTileAt(toRevealRow, toRevealCol);
				if (!toRevealTile.canSeeTile(curPlayer))
					toRevealTile.revealTile(curPlayer);
			}
		}
	}

	/**
	 * Unit on attakcerTile attacks the Unit on defenderTile.
	 *
	 * Unit gets attack modifier based on its current terrain; defender gets to
	 * counterattack. Movement for attacker Unit is set to 0, as attack can only
	 * happen once.
	 *
	 * @param attackerTile
	 * @param defenderTile
	 * @return
	 */
	private boolean attack(Tile attackerTile, Tile defenderTile) {
		Unit attacker = attackerTile.getUnit();
		Unit defender = defenderTile.getUnit();
		double attack = attacker.getAttackValue();
		attack *= attackerTile.getAttackModifier();
		defender.takeAttack(attack);
		if (defender.getHP() < 0) {
			defender.getOwner().removeUnit(defender);
			return true;
		}
		double counterattack = defender.getAttackValue();
		counterattack *= defenderTile.getAttackModifier();
		attacker.takeAttack(counterattack);
		attacker.move(attacker.getMovement(), attacker.getX(), attacker.getY()); // failed move
		return false;
	}

	/**
	 * Overloaded method, to be called when attacking a City
	 *
	 * Attacker attacks the city; city's health is checked. If <= 0, the City is
	 * defeated and removed from the owner's list of cities. This is how the game
	 * ends, so checks the end game condition as well.
	 *
	 * @param attackerTile
	 * @param defender
	 * @return
	 */
	private boolean attack(Tile attackerTile, City defender) {
		Unit attacker = attackerTile.getUnit();
		double attack = attacker.getAttackValue();
		attack *= attackerTile.getAttackModifier();
		defender.takeAttack(attack);
		if (defender.getRemainingHP() <= 0) {
			curPlayer.removeCity(defender);
			Player lostACity = defender.getOwner();
			if (lostACity.getCities().size() == 0) {
				model.removePlayer(lostACity); // player has no cities left, remove from game
			}
		}
		attacker.move(attacker.getMovement(), attacker.getX(), attacker.getY()); // set move to 0
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
	public boolean createUnit(int x, int y, String unitType) {
		Tile tile = getTileAt(x, y);
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

	/**
	 * Found a city on the Tile at row, col on the board
	 *
	 * Adds city to the current player's list of cities as well. Assumes that this
	 * was only called on a valid tile (Settler on the tile).
	 *
	 * @param row int of row location of new city
	 * @param col int of col location of new city
	 */
	public void foundCity(int x, int y) {
		Tile tile = getTileAt(x, y);
		Settler settler = (Settler) tile.getUnit();
		City city = settler.foundCity();
		tile.foundCity(city);
		model.changeAndNotify();
	}

}
