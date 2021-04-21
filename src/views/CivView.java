package views;

import controllers.CivController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.CivModel;

import java.util.Observable;
import java.util.Observer;

/**
 * A GUI, eventually.
 *
 * @author Connie Sun, Ryan Smith, Luke Hankins, Tim Gavlick
 */
@SuppressWarnings("deprecation")
public class CivView extends Application implements Observer {

	private CivController controller;
	private CivModel model;


	@Override
	public void start(Stage stage) {
		this.model = new CivModel();
		this.controller = new CivController(model);

		VBox window = new VBox();

		buildUI(window);

		model.addObserver(this);

		// render the application window
		Scene scene = new Scene(window, 100, 100);
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
	private void buildUI(VBox window) {
		// todo
	}

}
