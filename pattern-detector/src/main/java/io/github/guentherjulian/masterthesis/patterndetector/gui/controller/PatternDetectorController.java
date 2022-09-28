package io.github.guentherjulian.masterthesis.patterndetector.gui.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import io.github.guentherjulian.masterthesis.patterndetection.engine.AimPatternDetectionResult;
import io.github.guentherjulian.masterthesis.patterndetector.detection.Detector;
import io.github.guentherjulian.masterthesis.patterndetector.detection.configuration.MetaLanguage;
import io.github.guentherjulian.masterthesis.patterndetector.detection.configuration.ObjectLanguage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PatternDetectorController implements Initializable {

	private Stage stage;

	@FXML
	VBox vboxMain;

	@FXML
	TextField textfieldTemplatePath;

	@FXML
	TextField textfieldTemplatesRootPath;

	@FXML
	TextField textfieldCompilationUnitPath;

	@FXML
	TextField textfieldTemplateGrammarPath;

	@FXML
	Button btnSelectPathTemplate;

	@FXML
	Button btnSelectPathTemplatesRoot;

	@FXML
	Button btnSelectPathCompilationUnit;

	@FXML
	Button btnSelectTemplateGrammarPath;

	@FXML
	ComboBox<String> comboBoxObjectLang;

	@FXML
	ComboBox<String> comboBoxMetaLang;

	@FXML
	TextField textfieldMetalanguagePrefix;

	@FXML
	TextField textfieldMetalanguageFileExtension;

	@FXML
	CheckBox checkBoxPathMatching;

	@FXML
	CheckBox checkBoxTemplatePreprocessing;

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
		String[] objectLanguages = ObjectLanguage.getSupportedObjectLanguages();
		comboBoxObjectLang.getItems().addAll(objectLanguages);
		comboBoxObjectLang.setValue(objectLanguages[0]);

		String[] metaLanguages = MetaLanguage.getSupportedMetaLanguages();
		comboBoxMetaLang.getItems().addAll(metaLanguages);
		comboBoxMetaLang.setValue(metaLanguages[0]);
		comboBoxMetaLang.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			textfieldMetalanguagePrefix.setText(MetaLanguage.getMetalanguagePrefixes().get(newValue));
			textfieldMetalanguageFileExtension.setText(MetaLanguage.getMetalanguageFileExtensions().get(newValue));
		});

		textfieldMetalanguagePrefix.setText(MetaLanguage.getMetalanguagePrefixes().get(comboBoxMetaLang.getValue()));
		textfieldMetalanguageFileExtension
				.setText(MetaLanguage.getMetalanguageFileExtensions().get(comboBoxMetaLang.getValue()));

		btnCancel.setOnMouseClicked(event -> Platform.exit());

		btnSelectPathTemplate.setOnMouseClicked(event -> selectDirectory(event, textfieldTemplatePath));
		btnSelectPathTemplatesRoot.setOnMouseClicked(event -> selectDirectory(event, textfieldTemplatesRootPath));
		btnSelectPathCompilationUnit.setOnMouseClicked(event -> selectDirectory(event, textfieldCompilationUnitPath));
		btnSelectTemplateGrammarPath.setOnMouseClicked(event -> selectFile(event, textfieldTemplateGrammarPath));
		btnDetect.setOnMouseClicked(event -> detect(event));

		this.textfieldTemplatePath.setText(
				"C:\\devonfw\\workspaces\\main\\GuentherJulian\\cobigen\\cobigen-templates\\crud-java-server-app\\src\\main\\resources\\templates");
		this.textfieldTemplatesRootPath.setText(
				"C:\\devonfw\\workspaces\\main\\GuentherJulian\\cobigen\\cobigen-templates\\crud-java-server-app\\src\\main\\resources");
		this.textfieldCompilationUnitPath.setText("C:\\devonfw\\workspaces\\main\\devonfw\\jump-the-queue\\java\\jtqj");
		this.textfieldTemplateGrammarPath.setText(
				"C:\\devonfw\\workspaces\\main\\Masterthesis\\masterthesis-parent\\aim-pattern-detection\\src\\test\\resources\\grammars\\java8FreemarkerTemplate\\Java8FreemarkerTemplate.g4");
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
			Path templatesRootPath = Paths.get(this.textfieldTemplatesRootPath.getText());
			Path compilationUnitPath = Paths.get(this.textfieldCompilationUnitPath.getText());
			Path templateGrammarPath = Paths.get(this.textfieldTemplateGrammarPath.getText());

			String objectLanguage = this.comboBoxObjectLang.getValue();
			String metaLanguage = this.comboBoxMetaLang.getValue();
			String metaLanguagePrefix = this.textfieldMetalanguagePrefix.getText();
			String metaLanguageFileExtension = this.textfieldMetalanguageFileExtension.getText();

			Detector detector = new Detector(templatesPath, templatesRootPath, compilationUnitPath, templateGrammarPath,
					objectLanguage, metaLanguage, metaLanguagePrefix, metaLanguageFileExtension);

			if (!this.checkBoxPathMatching.isSelected()) {
				detector.setInstantiationPathMatching(false);
			}
			if (!this.checkBoxTemplatePreprocessing.isSelected()) {
				detector.setTemplatePreprocessing(false);
			}

			AimPatternDetectionResult result = null;
			try {
				result = detector.detect();
				if (result != null) {
					showResults(result);
				}
			} catch (Exception e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Error");
				alert.setContentText("Error while detection: " + e.getMessage());
				alert.show();
			}

		}
		return null;
	}

	private boolean validateInputs() {
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
			Alert alert = new Alert(AlertType.ERROR);
			alert.setHeaderText("Validation error");
			alert.setContentText(alertMessage);
			alert.show();
			return false;
		}

		return true;
	}

	private void showResults(AimPatternDetectionResult result) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("pattern-detector-result.fxml"));
		Parent root = fxmlLoader.load();

		PatternDetectorResultController controller = fxmlLoader.getController();
		controller.setAimPatternDetectionResult(result);
		controller.init();

		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());

		Stage stage = new Stage();
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(getStage());
		stage.setResizable(false);
		stage.setTitle("Results");

		stage.setScene(scene);
		stage.show();
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
