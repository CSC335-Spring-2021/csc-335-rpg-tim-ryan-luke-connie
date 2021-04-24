package models;
import java.util.Random;

import components.Tile;
/**
 * Holds the collection of individual tiles that make up a single Civ map.
 *
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 */
public class CivBoard {

	public Tile[][] tiles;
	public int size;


	public CivBoard(int size) {
		this.size = size;
		Tile[][] board = new Tile[size][size]; 
		int i = 0;
		int j;
		Random rng = new Random();
		while (i < size-1) {
			j = 0;
			while (j < size-1) {
				int type = rng.nextInt(1);
				if (type == 0 && (i < size/2 || j < size/2))
					board[i][j] =  new Tile(Tile.terrainTypes.FIELD);
				else if (type == 1 && (i < size/2 || j < size/2))
					board[i][j] = new Tile(Tile.terrainTypes.HILL);
				else if (type == 0 && (i >= size/2 || j >= size/2))
					board[i][j] = new Tile(Tile.terrainTypes.SWAMP);
				else
					board[i][j] = new Tile(Tile.terrainTypes.HILL);
				j++;
			}
			i++;
		}
		i = 0;
		j = 0;
		while (i < size) { // set border to water
			board[i][0] = new Tile(Tile.terrainTypes.WATER);
			board[0][i] = new Tile(Tile.terrainTypes.WATER);
			if (i == size-1) {
				while (j < size-1) {
					board[j][i] = new Tile(Tile.terrainTypes.WATER);
					j++;
					continue;
				}
			}
			i++;
		}
		this.tiles = board;
	}
	
	public Tile getTile(int row, int col) {
		return this.tiles[row][col];
	}
	public int getSize() {
		return this.size;
	}



}
