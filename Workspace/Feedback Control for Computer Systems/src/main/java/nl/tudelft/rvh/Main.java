package nl.tudelft.rvh;

import nl.tudelft.rvh.chapter1.Chapter1ClosedSimulation;
import nl.tudelft.rvh.chapter1.Chapter1OpenSimulation;
import nl.tudelft.rvh.chapter2.Chapter2CumulativeSimulation;
import nl.tudelft.rvh.chapter2.Chapter2NonCumulativeSimulation;
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
				new Chapter1ClosedSimulation(), new Chapter1OpenSimulation(),
				new Chapter2CumulativeSimulation(), new Chapter2NonCumulativeSimulation(),
				new ScalaExampleTab());

		stage.setScene(new Scene(tabPane, 800, 600));
		stage.setTitle("Feedback Control Systems");
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}
