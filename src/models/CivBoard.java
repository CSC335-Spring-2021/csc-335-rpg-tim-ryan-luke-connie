package models;

import java.util.Random;

import components.Tile;

/**
 * Holds the collection of individual tiles that make up a single Civ map.
 *
 * @field tiles 2D array containing our tile objects
 * @field size int specifying the size of our board --> board is size x size
 *        tiles
 *
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 */
public class CivBoard {

	public Tile[][] tiles;
	public int size;

	/**
	 * Constructor for our board
	 *
	 * @param size
	 */
	public CivBoard(int size) {
		this.size = size;
		Tile[][] board = new Tile[size][size];
		int i = 0;
		int j;
		Random rng = new Random();
		int oneThird = size/3;
		int twoThird = size * 2/3;
		
		while (i < size - 1) {
			j = 0;
			while (j < size - 1) {
				boolean isTopCorner = ((i < (oneThird) && j < (oneThird)));
				boolean isBottomCorner = (i > twoThird) && (j > twoThird); 
				int coordSum = i + j;
				boolean isMiddleStrip = (coordSum < (size + oneThird)) && (coordSum > (size - oneThird));
				int type = rng.nextInt(10);
				if (type > 2  && (isTopCorner || isBottomCorner)){// top left and bot. right
					board[i][j] = new Tile(Tile.terrainTypes.SWAMP); // corners are swamp or water
				}
				else if (type <= 2  && (isTopCorner || isBottomCorner)) { 
					board[i][j] = new Tile(Tile.terrainTypes.WATER);
				}
				else if (type > 3 && isMiddleStrip) { // diagonal strip down the middle is mostly hills
					board[i][j] = new Tile(Tile.terrainTypes.HILL);
				}
				else // rest are fields. 
					board[i][j] = new Tile(Tile.terrainTypes.FIELD);
				j++;
			}
			i++;
		}
		i = 0;
		j = 0;
		while (i < size) { // set border to water
			board[i][0] = new Tile(Tile.terrainTypes.WATER);
			board[0][i] = new Tile(Tile.terrainTypes.WATER);
			board[size - 1][i] = new Tile(Tile.terrainTypes.WATER);
			board[i][size - 1] = new Tile(Tile.terrainTypes.WATER);
			i++;
		}
		this.tiles = board;
	}

	public Tile getTile(int x, int y) {
		if (x < 0 || x >= size || y < 0 || y >= size) return null;
		return this.tiles[y][x];
	}

	public int getSize() {
		return this.size;
	}

}
