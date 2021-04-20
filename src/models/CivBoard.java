package models;

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
    this.tiles = createRandomBoard(size);
  }


  /**
   * Generate a new board made up of random tiles.
   *
   * <p>todo: nonrandom right now. Make random once we have tile types
   *
   * @param size The size of the square board to generate
   * @return A new 2d array of tiles
   */
  private Tile[][] createRandomBoard(int size) {
    Tile[][] result = new Tile[size][size];

    for (int x = 0; x < result.length; x++) {
      for (int y = 0; y < result[x].length; y++) {
        result[x][y] = new Tile();
      }
    }

    return result;
  }


  static class Tile {
    public boolean isTraversible;

    public Tile() {
      this.isTraversible = true;
    }
  }
}
