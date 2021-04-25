package views;

import components.*;
import controllers.CivController;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
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

	// map hooks
	private ScrollPane mapScrollContainer;
	private Pane mapElementContainer;
	private Canvas mapCanvas;
	private ImageView mapHoverCursor;
	private ImageView mapSelectedCursor;
	private FadeTransition mapSelectedTransition;

	// sprite hooks
	private List<ImageView> spriteImages;
	private List<GridPane> hpBars;

	// ui hooks
	private VBox unitPane;
	private Unit selectedUnit;

	// viz constants
	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 600;
	private static final int TILE_SIZE = 120;
	private static final int CITY_SIZE = 100;
	private static final int SPRITE_SIZE = 60;
	private static final double ISO_FACTOR = 0.6;
	private static final int SCROLL_GUTTER = 240;

	// viz derived constants (for convenience)
	private int isoBoardWidth;
	private int isoBoardHeight;


	/**
	 * Build the UI, start the game, and regulate game flow.
	 *
	 * @param stage The stage automatically passed when called Application.launch()
	 */
	@Override
	public void start(Stage stage) {
		this.model = new CivModel(2);
		this.controller = new CivController(model);
		this.spriteImages = new ArrayList<>();
		this.hpBars = new ArrayList<>();

		model.addObserver(this);

		// calculate derived constants (less spaghetti later on)
		isoBoardWidth = model.getSize() * TILE_SIZE;
		isoBoardHeight = (int) (isoBoardWidth * ISO_FACTOR);

		// assemble ui
		Pane window = new Pane();
		buildUI(window);
		focusMap(model.getSize() / 2, model.getSize() / 2);

		// populate initial map state
		controller.placeStartingUnits();
		renderAllSprites();

		// add global events
		mapCanvas.setOnMouseClicked(ev -> handleMapClick(ev));
		mapCanvas.setOnMouseMoved(ev -> handleMapHover(ev));

		// build the application window
		Scene scene = new Scene(window, WINDOW_WIDTH, WINDOW_HEIGHT);
		scene.getStylesheets().add("assets/CivView.css");
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
	private void buildUI(Pane window) {
		window.getStyleClass().add("ui");

		// scrollable container that houses our map group
		mapScrollContainer = new ScrollPane();
		mapScrollContainer.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		mapScrollContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		mapScrollContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		mapScrollContainer.setPannable(true);
		mapScrollContainer.getStyleClass().add("map");
		window.getChildren().add(mapScrollContainer);

		// pane to contain the map canvas. This interim layer lets us add padding without screwing
		// up canvas click math, etc
		mapElementContainer = new Pane();
		mapElementContainer.setPadding(new Insets(0, SCROLL_GUTTER, SCROLL_GUTTER, 0));
		mapScrollContainer.setContent(mapElementContainer);

		// terrain map: canvas element
		mapCanvas = new Canvas(isoBoardWidth, isoBoardHeight);
		mapElementContainer.getChildren().add(mapCanvas);
		mapCanvas.setLayoutX(SCROLL_GUTTER);
		mapCanvas.setLayoutY(SCROLL_GUTTER);

		// terrain map: bg
		GraphicsContext context = mapCanvas.getGraphicsContext2D();
		context.setFill(Color.BLACK);
		context.fillRect(0, 0, isoBoardWidth, isoBoardHeight);

		// terrain map: tiles
		for (int[] coords : getDrawTraversal()) {
			Tile tile = model.getTileAt(coords[0], coords[1]);
			if (tile == null) continue;
			Image tileImage = getTileImage(tile.getTerrainType());
			int[] isoCoords = gridToIso(coords[0], coords[1]);
			context.drawImage(tileImage, isoCoords[0], isoCoords[1], TILE_SIZE, TILE_SIZE * ISO_FACTOR);
		}

		try {
			// store hover cursor image for later so we don't have to keep loading and unloading it
			Image hoverCursorImage = new Image(
					new FileInputStream("src/assets/tiles/hover.png")
			);
			mapHoverCursor = new ImageView(hoverCursorImage);
			mapHoverCursor.setFitWidth(TILE_SIZE);
			mapHoverCursor.setFitHeight(TILE_SIZE * ISO_FACTOR);
			mapHoverCursor.setMouseTransparent(true);
			mapElementContainer.getChildren().add(mapHoverCursor);

			// same with "selected" image
			Image selectedImage = new Image(
					new FileInputStream("src/assets/tiles/selected.png")
			);
			mapSelectedCursor = new ImageView(selectedImage);
			mapSelectedCursor.setFitWidth(TILE_SIZE);
			mapSelectedCursor.setFitHeight(TILE_SIZE * ISO_FACTOR);
			mapSelectedCursor.setMouseTransparent(true);
			mapElementContainer.getChildren().add(mapSelectedCursor);

			// ... and its transition
			mapSelectedTransition = new FadeTransition();
			mapSelectedTransition.setDuration(Duration.millis(1200));
			mapSelectedTransition.setFromValue(2);
			mapSelectedTransition.setToValue(0.5);
			mapSelectedTransition.setCycleCount(Integer.MAX_VALUE);
			mapSelectedTransition.setAutoReverse(true);
			mapSelectedTransition.setNode(mapSelectedCursor);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// unit detail pane
		unitPane = new VBox();
		unitPane.getStyleClass().addAll("detail-pane", "detail-pane--unit");
		unitPane.setLayoutX(24);
		unitPane.setVisible(false);
		window.getChildren().add(unitPane);
	}


	/**
	 * Wipe and render the entire sprite layer.
	 */
	private void renderAllSprites() {
		clearAllSprites();
		for (int[] space : getDrawTraversal()) {
			Tile tile = model.getTileAt(space[0], space[1]);
			if (tile == null) continue;

			City city = tile.getOwnerCity();
			Unit unit = tile.getUnit();

			if (city != null) renderCity(city);
			if (unit != null) renderUnit(unit);
		}
	}


	/**
	 * Render a single city to the map.
	 *
	 * @param city The city to render. Position will be derived from the City's stored coords
	 */
	private void renderCity(City city) {
		int[] coords = gridToIso(city.getX(), city.getY());
		try {
			Image cityImage = new Image(new FileInputStream("src/assets/sprites/city.png"));
			ImageView cityImageView = new ImageView(cityImage);
			cityImageView.setFitWidth(CITY_SIZE);
			cityImageView.setFitHeight(CITY_SIZE);
			cityImageView.setMouseTransparent(true);
			cityImageView.setX(coords[0] + SCROLL_GUTTER + ((TILE_SIZE - CITY_SIZE) / 2.0));
			cityImageView.setY(coords[1] + SCROLL_GUTTER - 34.0);  // Magic Number, for now
			mapElementContainer.getChildren().add(cityImageView);
			spriteImages.add(cityImageView);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Render a single unit to the map.
	 *
	 * @param unit The unit to render. Position will be derived from the Unit's stored coords
	 */
	private void renderUnit(Unit unit) {
		int[] coords = gridToIso(unit.getX(), unit.getY());
		String spriteImage = "src/assets/sprites/settler.png";
		if (unit instanceof Scout) spriteImage = "src/assets/sprites/scout.png";
		if (unit instanceof Warrior) spriteImage = "src/assets/sprites/warrior.png";
		try {
			Image unitImage = new Image(new FileInputStream(spriteImage));
			ImageView unitImageView = new ImageView(unitImage);
			unitImageView.setFitWidth(SPRITE_SIZE);
			unitImageView.setFitHeight(SPRITE_SIZE);
			unitImageView.setMouseTransparent(true);
			unitImageView.setX(coords[0] + SCROLL_GUTTER + ((TILE_SIZE - SPRITE_SIZE) / 2.0 ));
			unitImageView.setY(coords[1] + SCROLL_GUTTER - (SPRITE_SIZE / 4.0));
			mapElementContainer.getChildren().add(unitImageView);
			spriteImages.add(unitImageView);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		renderSpriteHPBar(unit.getHP(), unit.getMaxHP(), coords[0], coords[1]);
	}


	/**
	 * Place an inline HP bar above a certain map square
	 *
	 * @param cur The unit or city's current hp
	 * @param max The unit or city's max hp
	 * @param x The left iso coord of the space to render on
	 * @param y The top iso coord of the space to render on
	 */
	private void renderSpriteHPBar(double cur, double max, int x, int y) {
		GridPane hpBar = createHPBar(cur, max);
		hpBar.setPrefWidth(TILE_SIZE / 2.0);
		hpBar.setLayoutX(x + SCROLL_GUTTER + TILE_SIZE / 4.0);
		hpBar.setLayoutY(y + SCROLL_GUTTER + (TILE_SIZE * ISO_FACTOR) / 1.35);
		mapElementContainer.getChildren().add(hpBar);
		hpBars.add(hpBar);
	}


	/**
	 * Clear all currently rendered sprites.
	 */
	private void clearAllSprites() {
		mapElementContainer.getChildren().removeAll(spriteImages);
		mapElementContainer.getChildren().removeAll(hpBars);
		spriteImages.clear();
		hpBars.clear();
	}


	/**
	 * Center the map on a particular board index.
	 *
	 * @param x The x index of the board grid to center on
	 * @param y The y index of the board grid to center on
	 */
	private void focusMap(int x, int y) {
		int[] coords = gridToIso(x, y);

		// ScrollPane scroll values are percentages (0 through 1), not raw pixel values
		mapScrollContainer.setHvalue((coords[0] + TILE_SIZE / 2.0) / (double) isoBoardWidth);
		mapScrollContainer.setVvalue((coords[1] + TILE_SIZE * ISO_FACTOR / 2.0) / (double) isoBoardHeight);
	}


	/**
	 * Handle an arbitrary click on the map at any time.
	 *
	 * @param ev The event object generated from the click
	 */
	private void handleMapClick(MouseEvent ev) {
		// filter mouseup events that happen after releasing a drag
		// https://stackoverflow.com/questions/26590010/how-can-i-stop-javafx-parent-node-getting-click-after-drag-between-children
		if (!ev.isStillSincePress()) return;

		int[] space = isoToGrid(ev.getX(), ev.getY());
		Tile tile = controller.getTileAt(space[0], space[1]);

		// reject clicks in the negative space left by the iso view
		if (tile == null) return;

		// if a unit is already selected, this click is either move/attack (if in range) or
		// deselect (if out of range)
		if (selectedUnit != null) {
			deselectUnit();

		// otherwise, select the unit under the click if there is one
		} else {
			selectUnit(tile.getUnit());
		}
	}


	/**
	 * Give visual indication of hover within the map.
	 *
	 * @param ev The MouseEvent generated by a hover on the canvas
	 */
	private void handleMapHover(MouseEvent ev) {
		mapHoverCursor.setVisible(false);

		// "snap" to a grid space by getting its grid coord and re-translating to iso coords
		int[] space = isoToGrid(ev.getX(), ev.getY());

		// reject events in the negative space left by the iso view
		if (space[0] < 0 || space[0] >= model.getSize() ||space[1] < 0 || space[1] >= model.getSize()) {
			return;
		}

		int[] coords = gridToIso(space[0], space[1]);

		mapHoverCursor.setX(coords[0] + SCROLL_GUTTER);
		mapHoverCursor.setY(coords[1] + SCROLL_GUTTER);
		mapHoverCursor.setVisible(true);
	}


	/**
	 * Select a unit. This involves marking said unit as selected, centering the map on it, and
	 * building and showing a detail pane that displays the unit's properties.
	 *
	 * @param unit The Unit to select
	 */
	private void selectUnit(Unit unit) {
		if (unit == null) return;

		Tile tile = controller.getTileAt(unit.getX(), unit.getY());

		selectedUnit = unit;

		unitPane.getChildren().clear();

		// pane info: label
		Text name = new Text(unit.getLabel());
		name.getStyleClass().add("detail-pane__name");

		// pane info: HP
		String hpDisp = "positive";
		if (unit.getHP() < unit.getMaxHP() * 2/3) hpDisp = "neutral";
		if (unit.getHP() < unit.getMaxHP() * 1/3) hpDisp = "negative";
		TextFlow hpFlow = createLabeledFigure("HP", (int) unit.getHP(), (int) unit.getMaxHP(), hpDisp);
		GridPane hpBar = createHPBar(unit.getHP(), unit.getMaxHP());

		// pane info: attack
		String attackDisp = "";
		if (tile.getAttackModifier() > 1) attackDisp = "positive";
		if (tile.getAttackModifier() < 1) attackDisp = "negative";
		TextFlow attackFlow = createLabeledFigure(
				"attack",
				(int) (unit.getAttackValue() * tile.getAttackModifier()),
				(tile.getAttackModifier() != 1 ? (int) unit.getAttackValue() : -1),
				attackDisp
		);
		attackFlow.getStyleClass().add("detail-pane__space-above");
		TextFlow attackHelp = new TextFlow();
		attackHelp.getStyleClass().add("detail-pane__help");
		if (tile.getAttackModifier() != 1) {
			Text attackMod = new Text((tile.getAttackModifier() > 1 ? "+" : "-") + " attack ");
			attackMod.getStyleClass().add(tile.getAttackModifier() > 1 ? "positive" : "negative");
			Text attackWhy = new Text("in " + tile.getTerrainType().name().toLowerCase() + "s");
			attackHelp.getChildren().addAll(attackMod, attackWhy);
		}

		// pane info: movement
		TextFlow moveFlow = createLabeledFigure("moves remaining", unit.getMovement(), -1, "");
		moveFlow.getStyleClass().add("detail-pane__space-above");

		// populate and show pane
		unitPane.getChildren().addAll(name, hpFlow, hpBar, attackFlow, attackHelp, moveFlow);
		unitPane.setVisible(true);
		// javafx doesn't calculate this vbox's height correctly, so magic number for now
		unitPane.setLayoutY((WINDOW_HEIGHT - 230) / 2.0);

		// add selection indicator
		int[] coords = gridToIso(unit.getX(), unit.getY());
		mapSelectedCursor.setX(coords[0] + SCROLL_GUTTER);
		mapSelectedCursor.setY(coords[1] + SCROLL_GUTTER);
		mapSelectedCursor.setVisible(true);
		mapSelectedTransition.play();

		// center map on unit
		focusMap(unit.getX(), unit.getY());
	}


	/**
	 * Assemble a TextFlow with a common layout for figures and their associated labels.
	 *
	 * @param label The label text to render
	 * @param figure The primary figure value
	 * @param max If the figure has a max value, pass it here. Otherwise, pass a negative number
	 * @param disposition The disposition class to render ("positive", "neutral", or "negative") or
	 *                    an empty string if not applicable
	 * @return The assembled TextFlow object containing the data inside new Text nodes
	 */
	private TextFlow createLabeledFigure(String label, int figure, int max, String disposition) {
		TextFlow result = new TextFlow();
		result.getStyleClass().add("detail-pane__figure-group");

		Text figureNode = new Text("" + figure);
		figureNode.getStyleClass().add("detail-pane__figure");
		if (disposition.length() > 0) figureNode.getStyleClass().add(disposition);
		result.getChildren().add(figureNode);

		if (max >= 0) {
			Text dividerNode = new Text(" / ");
			dividerNode.getStyleClass().add("detail-pane__divider");
			result.getChildren().add(dividerNode);

			Text maxNode = new Text("" + max);
			maxNode.getStyleClass().add("detail-pane__figure");
			result.getChildren().add(maxNode);
		}

		Text labelNode = new Text("  " + label);
		labelNode.getStyleClass().add("detail-pane__label");
		result.getChildren().add(labelNode);

		return result;
	}


	/**
	 * Create a standardized structure representing an HP bar.
	 *
	 * @param cur The current HP
	 * @param max The max HP
	 * @return A structure of Nodes that represent an HP bar
	 */
	private GridPane createHPBar(double cur, double max) {
		GridPane result = new GridPane();
		result.getStyleClass().add("hp-bar");

		Pane curNode = new Pane();
		curNode.getStyleClass().add("hp-bar__remainder");
		result.add(curNode, 0, 0);

		ColumnConstraints constraint = new ColumnConstraints();
		constraint.setPercentWidth(cur / max * 100);
		result.getColumnConstraints().add(constraint);

		return result;
	}


	/**
	 * Deselect the currently selected unit. This also removes the unit's detail pane.
	 */
	private void deselectUnit() {
		selectedUnit = null;
		unitPane.setVisible(false);
		unitPane.getChildren().clear();
		mapSelectedCursor.setVisible(false);
		mapSelectedTransition.pause();
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


	/**
	 * Translate absolute pixel coordinates in the isometric display to grid spaces.
	 *
	 * <p>Note that returned indices may be outside the real bounds of the grid due to the natural
	 * negative space that any isometric view has in the corners. Since this method doesn't
	 * necessarily return "safe" indices, make sure to check.
	 *
	 * @param x The horizontal pixel offset of the coordinate from the left edge of the canvas
	 * @param y The vertical pixel offset of the coordinate from the top edge of the canvas
	 * @return A two-element int array containing the x and y indices of the space in the grid
	 */
	private int[] isoToGrid(double x, double y) {
		int[] result = new int[2];

		// derived from algebra-ing the gridToIso() calculations:
		//
		// x = tileX * TILE_SIZE / 2 - tileY * TILE_SIZE / 2;
		// y = tileX * TILE_SIZE / 2 * ISO_FACTOR + tileY * TILE_SIZE / 2 * ISO_FACTOR;
		//
		// then translating based on the board size
		double w = TILE_SIZE / 2.0;
		double h = TILE_SIZE / 2.0 * ISO_FACTOR;
		result[0] = (int) ((x / w + y / h) / 2 - model.getSize() / 2);
		result[1] = (int) ((y / h - x / w) / 2 + model.getSize() / 2);

		return result;
	}


	/**
	 * Get a tile image for a terrain type.
	 *
	 * <p>Since there can be many tile choices for a given terrain type, this method chooses one
	 * randomly. Don't expect the same image for the same terrain type each time.
	 *
	 * @param terrainType The terrain type to get a file for
	 * @return An Image object containing the image data for a tile image matching the terrain type
	 */
	private Image getTileImage(Tile.terrainTypes terrainType) {
		try {
			if (terrainType == Tile.terrainTypes.HILL) {
				return new Image(new FileInputStream(
						"src/assets/tiles/hill-" + getRandInt(1, 5) + ".png"
				));
			} else if (terrainType == Tile.terrainTypes.SWAMP) {
				return new Image(new FileInputStream(
						"src/assets/tiles/swamp-" + getRandInt(1, 5) + ".png"
				));
			} else if (terrainType == Tile.terrainTypes.WATER) {
				return new Image(new FileInputStream(
						"src/assets/tiles/water-" + getRandInt(1, 5) + ".png"
				));
			} else if (terrainType == Tile.terrainTypes.MOUNTAIN) {
				return new Image(new FileInputStream(
						"src/assets/tiles/mountain-" + getRandInt(1, 5) + ".png"
				));
			} else {
				return new Image(new FileInputStream(
						"src/assets/tiles/field-" + getRandInt(1, 5) + ".png"
				));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}


	/**
	 * Generate a random integer in a given range.
	 *
	 * @param min The minimum possible result (inclusive)
	 * @param max The maximum possible result (inclusive)
	 * @return A random integer between the two bounds, inclusive
	 */
	private int getRandInt(int min, int max) {
		return (int) (Math.random() * (max - min + 1) + min);
	}

}
