package nl.tudelft.rvh;

import nl.tudelft.rvh.chapter1.BufferClosed;
import nl.tudelft.rvh.chapter1.BufferOpen;
import nl.tudelft.rvh.chapter2.CacheCumulative;
import nl.tudelft.rvh.chapter2.CacheNonCumulative;
import nl.tudelft.rvh.scala.ScalaExampleTab;
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
		tabPane.getTabs().addAll(new ExampleTab(),
				new BufferClosed(), new BufferOpen(),
				new CacheCumulative(), new CacheNonCumulative(),
				new ScalaExampleTab());

		stage.setScene(new Scene(tabPane, 800, 600));
		stage.setTitle("Feedback Control Systems");
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}
