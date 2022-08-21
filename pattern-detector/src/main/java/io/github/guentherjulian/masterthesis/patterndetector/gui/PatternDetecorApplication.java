package io.github.guentherjulian.masterthesis.patterndetector.gui;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PatternDetecorApplication extends Application {

	@Override
	public void start(Stage stage) throws IOException {
		URL urlFxmlResource = getClass().getClassLoader().getResource("pattern-detector.fxml");
		Parent root = FXMLLoader.load(urlFxmlResource);

		Scene scene = new Scene(root);

		stage.setTitle("Pattern Detector");
		stage.setScene(scene);
		stage.show();
	}
}