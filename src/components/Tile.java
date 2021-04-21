package components;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a single tile within our board.
 * 
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 *
 */
public class Tile {

	private String terrainType = null;
	private int movementBonus;
	private double attackMult;
	private String resourceType = null;
	private City ownerCity = null;
	private Unit unitHere = null;
	// Used string to represent players, could also make an object but idk yet
	private List<String> revealedTo = new ArrayList<String>();

	/**
	 * When initially making a game, create every tile with a terrain type in mind.
	 * This will allow for map creation.
	 */
	public Tile(String terrainType) {
		this.terrainType = terrainType;

		if (terrainType.equals("Hill")) {
			this.movementBonus = -1;
			this.attackMult = 1.4;
		} else if (terrainType.equals("Swamp")) {
			this.movementBonus = -1;
			this.attackMult = .5;
		} else if (terrainType.equals("Grassland")) {
			this.movementBonus = 0;
			this.attackMult = 1;
		} else {
			// terrain type is either a mountain or water, either way it is impassable.
			// movement bonus set to -5 if impassable, change if necessary
			this.movementBonus = -5;
			this.attackMult = 0;
		}
	}

	/**
	 * Get movement reduction or bonus to be *added* to unit movement depending on
	 * tile type.
	 * 
	 * NOTE: I currently think 'City' should be a terrain type, and the controller
	 * can call getOwnerCity to retrieve the city object. This makes it easy to
	 * differentiate a tile owned by a city from the city itself.
	 * 
	 * @return int representing terrain bonus to be added, terrain type doesnt have
	 *         to be a string if something else works better, terrain types also
	 *         dont have to be those I included.
	 */
	public int getMovementModifier() {
		return this.movementBonus;
	}

	/**
	 * Get attack reduction or bonus to be *multiplied* by unit attack depending on
	 * tile type.
	 * 
	 * @return double representing multiplier.
	 */
	public double getAttackModifier() {
		return this.attackMult;
	}

	/**
	 * Not sure why this method might be necessary but I'm including it for
	 * convenience.
	 * 
	 * @return String representing terrain type here. Will be 'City' if this
	 *         specific tile is a city.
	 */
	public String getTerrainType() {
		return this.terrainType;
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
	 * Return the city object that owns this tile, that does not mean that the tile
	 * is a city, per se, but that some city's area of influence has reached this
	 * tile.
	 * 
	 * @return City object representing the city located on this tile object
	 */
	public City getOwnerCity() {
		return this.ownerCity;
	}

	/**
	 * Found a city on this tile. Returns false if failed.
	 * 
	 * @param player
	 * @return
	 */
	public boolean foundCity(String player) {
		if (this.ownerCity == null) { // && this.unitHere instanceOf Settler?
			this.terrainType = "City";
			this.ownerCity = new City(); // TODO: Expand this
			this.movementBonus = 0; // subject to change
			this.attackMult = 1; // subject to change
			return true;
		}
		return false;
	}

	/**
	 * Return unit stationed on this tile. This method will be necessary for attack
	 * and movement logic, but how that will be implemented is still a TODO
	 * 
	 * @return Unit on this tile object, or null if the tile contains no unit.
	 */
	public Unit getUnit() {
		return this.unitHere;
	}

	/**
	 * Figure out if current player is allowed to see the current tile.
	 * 
	 * @param player String representing player name
	 * @return boolean representing whether the player passed in can see the tile
	 */
	public boolean canSeeTile(String player) {
		return revealedTo.contains(player);
	}

	/**
	 * reveal this tile to the player passed in.
	 * 
	 * @param player String representing player name
	 */
	public void revealTile(String player) {
		revealedTo.add(player);
	}

}
