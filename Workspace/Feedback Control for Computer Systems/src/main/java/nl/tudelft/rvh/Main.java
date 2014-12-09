package nl.tudelft.rvh;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.stage.Stage;
import nl.tudelft.rvh.chapter1.BufferClosed;
import nl.tudelft.rvh.chapter1.BufferOpen;
import nl.tudelft.rvh.chapter2.CacheCumulative;
import nl.tudelft.rvh.chapter2.CacheNonCumulative;

public class Main extends Application {

	@Override
	public void start(Stage stage) {
		TabPane tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		tabPane.getTabs().addAll(new CacheCumulative(), new CacheNonCumulative(),	// chapter 2
				new BufferClosed(), new BufferOpen());	// chapter 1

		stage.setScene(new Scene(tabPane, 800, 600));
		stage.setTitle("Feedback Control Systems");
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}
