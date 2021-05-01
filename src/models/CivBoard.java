package models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

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
public class CivBoard implements Serializable {

	public Tile[][] tiles;
	public int size;
	private ArrayList<int[]> playerStartingCoords;

	/**
	 * Constructor for our board
	 *
<<<<<<< HEAD
	 * @param size 
=======
	 * @param size
>>>>>>> refs/remotes/origin/component_updates
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
				boolean resource = (type == 4 || type == 7);
				if (type > 2  && (isTopCorner || isBottomCorner)){// top left and bot. right
					if (resource) 
						board[i][j] = new Tile(Tile.terrainTypes.SWAMP,"horse"); // corners are swamp or water
					else if (type < 8)
						board[i][j] = new Tile(Tile.terrainTypes.SWAMP,"");
					else
						board[i][j] = new Tile(Tile.terrainTypes.FIELD,"");
				}
				else if (type <= 2  && (isTopCorner || isBottomCorner)) { 
					board[i][j] = new Tile(Tile.terrainTypes.WATER, "");
				}
				else if (type > 3 && isMiddleStrip) { // diagonal strip down the middle is mostly hills
					if (resource)
						board[i][j] = new Tile(Tile.terrainTypes.HILL, "iron");
					else if (type < 9)
						board[i][j] = new Tile(Tile.terrainTypes.HILL, "");
					else
						board[i][j] = new Tile(Tile.terrainTypes.MOUNTAIN, "");
				}
				else { // rest are fields. 
					if (resource)
						board[i][j] = new Tile(Tile.terrainTypes.FIELD, "wheat");
					else
						board[i][j] = new Tile(Tile.terrainTypes.FIELD, "");
				}
				j++;
			}
			i++;
		}
		i = 0;
		j = 0;
		while (i < size) { // set border to water
			board[i][0] = new Tile(Tile.terrainTypes.WATER, "");
			board[0][i] = new Tile(Tile.terrainTypes.WATER, "");
			board[size - 1][i] = new Tile(Tile.terrainTypes.WATER, "");
			board[i][size - 1] = new Tile(Tile.terrainTypes.WATER, "");
			i++;
		}
		board[size-2][size-2] = new Tile(Tile.terrainTypes.FIELD, "");
		board[1][1] = new Tile(Tile.terrainTypes.FIELD, "");
		board[size-2][1] = new Tile(Tile.terrainTypes.FIELD, "");
		board[1][size-2] = new Tile(Tile.terrainTypes.FIELD, "");
		this.tiles = board;
	}
	
	public CivBoard(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		unserializeBoard(ois);
	}

	public Tile getTile(int x, int y) {
		if (x < 0 || x >= size || y < 0 || y >= size) return null;
		return this.tiles[y][x];
	}
	public CivBoard(String file) {
		Scanner sc = null;
		try {
			File fileObj = new File(file);
			sc = new Scanner(fileObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String line = null;
		if (sc.hasNextLine()) {
			line = sc.nextLine();
		}
		this.size = Integer.valueOf(line);
		Tile[][] board = new Tile[size][size];
		int i = 0;
		int j = 0;
		Tile.terrainTypes type = null;
		String resource = "";
		while (sc.hasNextLine()) {
			line = sc.nextLine();
			String[] type_res = line.split(" ");
			if (type_res[0].equals("field"))
					type = Tile.terrainTypes.FIELD;
			else if (type_res[0].equals("swamp"))
				type = Tile.terrainTypes.SWAMP;
			else if (type_res[0].equals("hill"))
				type = Tile.terrainTypes.HILL;
			else if (type_res[0].equals("water"))
				type = Tile.terrainTypes.WATER;
			else if (type_res[0].equals("mountain"))
				type = Tile.terrainTypes.MOUNTAIN;
			if (type_res[1].equals("w"))
				resource = "wheat";
			else if (type_res[1].equals("h"))
				resource = "horse";
			else if (type_res[1].equals("i"))
				resource = "iron";
			else
				resource = "wheat";
			board[j][i] = new Tile(type, resource);
			j++;
			if (j == size) {
				i++;
				j = 0;
			}
			if (i == size) {
				break;
				//System.out.println("error with passed size");
			}
		}
		this.tiles = board;
		
		
	}


	public int getSize() {
		return this.size;
	}

	
	public void serializeBoard(ObjectOutputStream oos) throws IOException {
		oos.writeObject(this.tiles);
		oos.writeObject(this.size);
		oos.writeObject(this.playerStartingCoords);
	}
	public void unserializeBoard(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		this.tiles = (Tile[][]) ois.readObject();
		this.size = (int) ois.readObject();
		this.playerStartingCoords = (ArrayList<int[]>) ois.readObject();
	}

}
