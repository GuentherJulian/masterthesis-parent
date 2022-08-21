package io.github.guentherjulian.masterthesis.patterndetector.gui.controller;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import io.github.guentherjulian.masterthesis.patterndetector.detection.Detector;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class PatternDetectorController implements Initializable {

	private Stage stage;

	@FXML
	VBox vboxMain;

	@FXML
	TextField textfieldTemplatePath;

	@FXML
	TextField textfieldCompilationUnitPath;

	@FXML
	TextField textfieldTemplateGrammarPath;

	@FXML
	Button btnSelectPathTemplate;

	@FXML
	Button btnSelectPathCompilationUnit;

	@FXML
	Button btnSelectTemplateGrammarPath;

	@FXML
	ComboBox<String> comboBoxObjectLang;

	@FXML
	ComboBox<String> comboBoxMetaLang;

	@FXML
	Button btnCancel;

	@FXML
	Button btnDetect;

	@FXML
	private void onActionMenuItemQuit(ActionEvent event) {
		Platform.exit();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		String[] objectLanguages = new String[] { "Java", "C" };
		comboBoxObjectLang.getItems().addAll(objectLanguages);

		String[] metaLanguages = new String[] { "FreeMarker", "Velocity", "StringTemplate" };
		comboBoxMetaLang.getItems().addAll(metaLanguages);

		btnCancel.setOnMouseClicked(event -> Platform.exit());

		btnSelectPathTemplate.setOnMouseClicked(event -> selectDirectory(event, textfieldTemplatePath));
		btnSelectPathCompilationUnit.setOnMouseClicked(event -> selectDirectory(event, textfieldCompilationUnitPath));
		btnSelectTemplateGrammarPath.setOnMouseClicked(event -> selectFile(event, textfieldTemplateGrammarPath));
		btnDetect.setOnMouseClicked(event -> detect(event));
	}

	private void selectDirectory(MouseEvent event, TextField targetTextField) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(getRootDirectory().toFile());

		File selectedDirectory = directoryChooser.showDialog(this.getStage());
		if (selectedDirectory != null) {
			targetTextField.setText(selectedDirectory.toString());
		}
	}

	private void selectFile(MouseEvent event, TextField targetTextField) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(getRootDirectory().toFile());
		File selectedFile = fileChooser.showOpenDialog(getStage());
		if (selectedFile != null) {
			targetTextField.setText(selectedFile.toString());
		}
	}

	private Object detect(MouseEvent event) {
		if (validateInputs()) {
			Path templatesPath = Paths.get(this.textfieldTemplatePath.getText());
			Path compilationUnitPath = Paths.get(this.textfieldCompilationUnitPath.getText());
			Path templateGrammarPath = Paths.get(this.textfieldTemplateGrammarPath.toString());
			String objectLanguage = this.comboBoxObjectLang.getValue();
			String metaLanguage = this.comboBoxMetaLang.getValue();
			Detector detector = new Detector(templatesPath, compilationUnitPath, templateGrammarPath, objectLanguage,
					metaLanguage);
			detector.detect();

		}
		return null;
	}

	private boolean validateInputs() {

		Alert alert;
		String alertMessage = "";

		if (this.textfieldTemplatePath.getText().isEmpty()) {
			alertMessage = "You must specify a path to the template directory!";

		} else {
			if (!Files.exists(Paths.get(this.textfieldTemplatePath.getText()))) {
				alertMessage = "The template directory does not exist!";
			}
		}

		if (alertMessage.isEmpty()) {
			if (this.textfieldCompilationUnitPath.getText().isEmpty()) {
				alertMessage = "You must specify a path to the directory with the compilation units!";

			} else {
				if (!Files.exists(Paths.get(this.textfieldCompilationUnitPath.getText()))) {
					alertMessage = "The directory with the compilation units does not exist!";
				}
			}

			if (alertMessage.isEmpty()) {
				if (this.textfieldTemplateGrammarPath.getText().isEmpty()) {
					alertMessage = "You must specify a path to the template grammar!";

				} else {
					if (!Files.exists(Paths.get(this.textfieldTemplateGrammarPath.getText()))) {
						alertMessage = "The path to the template grammar does not exist!";
					}
				}

				if (alertMessage.isEmpty()) {
					if (this.comboBoxObjectLang.getValue() == null || this.comboBoxObjectLang.getValue().isEmpty()) {
						alertMessage = "You must select a object language!";
					}

					if (alertMessage.isEmpty()) {
						if (this.comboBoxMetaLang.getValue() == null || this.comboBoxMetaLang.getValue().isEmpty()) {
							alertMessage = "You must select a metalanguage!";
						}
					}
				}
			}
		}

		if (!alertMessage.isEmpty()) {
			alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Validation error");
			alert.setContentText(alertMessage);
			alert.show();
			return false;
		}

		return true;
	}

	private Stage getStage() {
		if (this.stage == null) {
			this.stage = (Stage) vboxMain.getScene().getWindow();
		}
		return this.stage;
	}

	private Path getRootDirectory() {
		Path homeDirectory = new File(System.getProperty("user.home")).toPath();
		Path rootDirectory = homeDirectory.getRoot();
		return rootDirectory;
	}
}
