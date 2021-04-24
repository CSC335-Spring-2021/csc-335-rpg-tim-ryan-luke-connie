package views;

import controllers.CivController;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import models.CivModel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * A GUI, eventually.
 *
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 */
@SuppressWarnings("deprecation")
public class CivView extends Application implements Observer {

	// game data + controller
	private CivController controller;
	private CivModel model;

	// viz constants
	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 600;
	private static final int TILE_SIZE = 120;
	private static final double ISO_FACTOR = 0.6;
	private static final int SCROLL_GUTTER = 240;

	// viz derived constants (for convenience)
	private int isoBoardWidth;
	private int isoBoardHeight;


	@Override
	public void start(Stage stage) {
		this.model = new CivModel();
		this.controller = new CivController(model);

		model.addObserver(this);

		// calculate derived constants (less spaghetti later on)
		isoBoardWidth = model.getSize() * TILE_SIZE;
		isoBoardHeight = (int) (isoBoardWidth * ISO_FACTOR);

		// assemble ui
		StackPane window = new StackPane();
		buildUI(window);

		// build the application window
		Scene scene = new Scene(window, WINDOW_WIDTH, WINDOW_HEIGHT);
		scene.getStylesheets().add("views/CivView.css");
		stage.setScene(scene);
		stage.setTitle("Sid Meier's Civilization 0.5");

		stage.show();
	}


	/**
	 * Update the UI when the model changes.
	 *
	 * @param observable The observable that's been updated
	 * @param o Arbitrary data
	 */
	@Override
	public void update(Observable observable, Object o) {
		// todo
	}


	/**
	 * Create and assemble the game UI.
	 *
	 * @param window The main pane that will contain all UI elements
	 */
	private void buildUI(StackPane window) {
		ScrollPane mapContainer = new ScrollPane();
		mapContainer.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		mapContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		mapContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		mapContainer.setPannable(true);
		window.getChildren().add(mapContainer);

		Pane mapGridContainer = new Pane();
		mapGridContainer.setPadding(new Insets(0, SCROLL_GUTTER, SCROLL_GUTTER, 0));

		Canvas canvas = new Canvas(isoBoardWidth, isoBoardHeight);
		mapGridContainer.getChildren().add(canvas);
		canvas.setLayoutX(SCROLL_GUTTER);
		canvas.setLayoutY(SCROLL_GUTTER);
		GraphicsContext context = canvas.getGraphicsContext2D();

		for (int[] coords : getDrawTraversal()) {
			try {
				Image tileImage = new Image(new FileInputStream("src/assets/tiles/field-1.png"));
				int[] isoCoords = gridToIso(coords[0], coords[1]);
				context.drawImage(tileImage, isoCoords[0], isoCoords[1], TILE_SIZE, TILE_SIZE * ISO_FACTOR);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		mapContainer.setContent(mapGridContainer);
	}


	/**
	 * Get a serialized list of board coordinates in diagonal-traversal order starting from the
	 * top-left of the board.
	 *
	 * <p>This is necessary because the board must be drawn back-to-front in order for perspective
	 * overlapping to appear correct.
	 *
	 * @return A list of two-element int arrays, where the first int is the board x index and the
	 * second is the board y index
	 */
	private List<int[]> getDrawTraversal() {
		List<int[]> result = new ArrayList<>();

		// we'll start slices from each edge space on the left and bottom (one shared). The slice
		// starting at the bottom-left corner will be the turning point
		int diagonalSlices = model.getSize() * 2 - 1;
		int mid = diagonalSlices / 2;
		int sliceSize = 0;
		int startX;
		int startY;

		for (int slice = 0; slice < diagonalSlices; slice++) {
			if (slice <= mid) {
				sliceSize++;
				startX = 0;
				startY = slice;
			} else {
				sliceSize--;
				startX = slice - mid;
				startY = model.getSize() - 1;
			}

			for (int i = 0; i < sliceSize; i++) {
				int[] coord = new int[2];
				coord[0] = startX + i;
				coord[1] = startY - i;
				result.add(coord);
			}
		}

		return result;
	}


	/**
	 * Given a set of grid space coordinates, return the pixel coordinates of the tile in our
	 * isometric rendering space.
	 *
	 * <p>The coordinates returned indicate the top-left bound of the iso space. Use in combination
	 * with this class' TILE_SIZE and ISO_FACTOR constants to do additional math afterwards (like
	 * finding the center point of the space, etc).
	 *
	 * @param x The x index of the space in the map grid to find
	 * @param y The y index of the space in the map grid to find
	 * @return A two-element int array containing the x and y coordinates of the top-left pixel
	 * of the input tile in iso space
	 */
	private int[] gridToIso(int x, int y) {
		int[] result = new int[2];

		// our origin point is [half of the real rendered width, 0] (the very top corner of the
		// rendered board)
		int originX = isoBoardWidth / 2;
		result[0] = originX;

		// movements along board x add iso x and iso y
		result[0] += x * TILE_SIZE / 2;
		result[1] += (int) (x * TILE_SIZE / 2 * ISO_FACTOR);

		// movements along board y subtract iso x and add iso y
		result[0] -= y * TILE_SIZE / 2;
		result[1] += (int) (y * TILE_SIZE / 2 * ISO_FACTOR);

		// coord currently points at top-center of space. Normalize to top-left corner
		result[0] -= TILE_SIZE / 2;

		return result;
	}


	private int[] isoToGrid(int x, int y) {
		return null;
	}

}
