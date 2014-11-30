package nl.tudelft.rvh;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage stage) {
		TabPane tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		tabPane.getTabs().add(new ExampleTab());
		
		stage.setScene(new Scene(tabPane, 800, 600));
		stage.setTitle("Feedback Control Systems");
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}
