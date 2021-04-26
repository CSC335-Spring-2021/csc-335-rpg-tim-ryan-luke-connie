package components;

import java.util.ArrayList;
import java.util.List;

import models.Player;

/**
 * Class representing a single tile within our board.
 *
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 *
 */
public class Tile {

	public enum terrainTypes {
		FIELD, HILL, SWAMP, WATER, MOUNTAIN
	}

	private terrainTypes terrainType;
	private int movementBonus;
	private double attackMult;
	private String resourceType = null;
	private City ownerCity = null;
	private boolean isCityTile = false;
	private Unit unitHere = null;
	private List<Player> revealedTo = new ArrayList<Player>();

	/**
	 * When initially making a game, create every tile with a terrain type in mind.
	 * This will allow for map creation.
	 */
	public Tile(terrainTypes terrainType) {
		this.terrainType = terrainType;

		if (terrainType.equals(terrainTypes.HILL)) {
			this.movementBonus = -1;
			this.attackMult = 1.4;
		} else if (terrainType.equals(terrainTypes.SWAMP)) {
			this.movementBonus = -1;
			this.attackMult = .5;
		} else if (terrainType.equals(terrainTypes.FIELD)) {
			this.movementBonus = 0;
			this.attackMult = 1;
		} else {
			// terrain type is either a mountain or water, either way it is impassable.
			this.movementBonus = Integer.MIN_VALUE;
			this.attackMult = 0;
		}
	}


	/**
	 * Found a city on this tile. Returns false if failed.
	 *
	 * TODO: This code does not interact with settlers correctly and needs to be
	 * updated depending on how Tim wants cities to work. Should the controller use
	 * a settler charge then immediately call this, or should the settler do that?
	 *
	 * @param city city created by a settler attempting to be made on this tile.
	 * @return boolean representing whether city founding was a success
	 */
	public boolean foundCity(City city) {
		if (this.ownerCity == null) { // && this.unitHere instanceOf Settler?
			this.ownerCity = city;
			this.isCityTile = true;
			this.movementBonus = 0; // TODO: figure out bonuses for units in cities
			this.attackMult = 1; // subject to change
			return true;
		}
		return false;
	}

	/**
	 * Retrieve the terrain type for use in the view
	 *
	 * @return The terrainType assigned to this tile
	 */
	public terrainTypes getTerrainType() {
		return this.terrainType;
	}

	/**
	 * Get movement reduction or bonus to be *added* to unit movement depending on
	 * tile type.
	 *
	 * @return int representing terrain bonus to be added to unit movement value
	 */
	public int getMovementModifier() {
		return this.movementBonus;
	}

	/**
	 * Get attack reduction or bonus to be *multiplied* by unit attack depending on
	 * tile type.
	 *
	 * @return double representing attack multiplier.
	 */
	public double getAttackModifier() {
		return this.attackMult;
	}

	/**
	 * Return type of resource on this tile, of null if there is no resource. For
	 * the average tile this should be null, but we have yet to figure out how
	 * resources are going to work exactly so this is still a TODO
	 *
	 * @return String represeting the resource type on the tile, or null if there is
	 *         no resrource. We can make a resource class later if that seems like a
	 *         good idea.
	 */
	public String getResourceType() {
		return this.resourceType;
	}

	/**
	 * Return the city object that owns this tile. That does not mean that the tile
	 * is a city, per se, but that some city's area of influence has reached this
	 * tile.
	 *
	 * @return City object representing the city which claims ownership of the tile
	 */
	public City getOwnerCity() {
		return this.ownerCity;
	}

	/**
	 * Getter for if this tile is a city tile, not to be confused with being owned
	 * by a city
	 * 
	 * @return boolean representing if this tile contains a city.
	 */
	public boolean isCityTile() {
		return this.isCityTile;
	}

	/**
	 * Return unit stationed on this tile. This method will be necessary for attack
	 * and movement logic
	 *
	 * @return Unit on this tile object, or null if the tile contains no unit.
	 */
	public Unit getUnit() {
		return this.unitHere;
	}

	/**
	 * Place a unit on this tile, will be used if a unit moves here or if a unit
	 * kills the unit stationed here.
	 *
	 * @param unit that is now stationed here.
	 */
	public void setUnit(Unit unit) {
		unitHere = unit;
	}

	/**
	 * Figure out if current player is allowed to see the current tile.
	 *
	 * @param player Player object representing the player in question
	 * @return boolean representing whether the player passed in can see the tile
	 */
	public boolean canSeeTile(Player player) {
		return revealedTo.contains(player);
	}

	/**
	 * reveal this tile to the player passed in.
	 *
	 * @param player Player that the tile will be revealed to.
	 */
	public void revealTile(Player player) {
		revealedTo.add(player);
	}

}
