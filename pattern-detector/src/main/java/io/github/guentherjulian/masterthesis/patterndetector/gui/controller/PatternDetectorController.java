package io.github.guentherjulian.masterthesis.patterndetector.gui.controller;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import io.github.guentherjulian.masterthesis.patterndetection.engine.AimPatternDetectionResult;
import io.github.guentherjulian.masterthesis.patterndetection.engine.AimPatternDetectionResultEntry;
import io.github.guentherjulian.masterthesis.patterndetector.detection.Detector;
import io.github.guentherjulian.masterthesis.patterndetector.detection.configuration.MetaLanguage;
import io.github.guentherjulian.masterthesis.patterndetector.detection.configuration.ObjectLanguage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
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
	TextField textfieldMetalanguagePrefix;

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
		});

		textfieldMetalanguagePrefix.setText(MetaLanguage.getMetalanguagePrefixes().get(comboBoxMetaLang.getValue()));

		btnCancel.setOnMouseClicked(event -> Platform.exit());

		btnSelectPathTemplate.setOnMouseClicked(event -> selectDirectory(event, textfieldTemplatePath));
		btnSelectPathCompilationUnit.setOnMouseClicked(event -> selectDirectory(event, textfieldCompilationUnitPath));
		btnSelectTemplateGrammarPath.setOnMouseClicked(event -> selectFile(event, textfieldTemplateGrammarPath));
		btnDetect.setOnMouseClicked(event -> detect(event));

		this.textfieldTemplatePath.setText(
				"C:\\devonfw\\workspaces\\main\\Masterthesis\\masterthesis-parent\\aim-pattern-detection\\src\\test\\resources\\completePatternDetectionTest\\templates");
		this.textfieldCompilationUnitPath.setText(
				"C:\\devonfw\\workspaces\\main\\Masterthesis\\masterthesis-parent\\aim-pattern-detection\\src\\test\\resources\\completePatternDetectionTest\\applicationCode");
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
			Path compilationUnitPath = Paths.get(this.textfieldCompilationUnitPath.getText());
			Path templateGrammarPath = Paths.get(this.textfieldTemplateGrammarPath.getText());

			String objectLanguage = this.comboBoxObjectLang.getValue();
			String metaLanguage = this.comboBoxMetaLang.getValue();
			String metaLanguagePrefix = this.textfieldMetalanguagePrefix.getText();
			Detector detector = new Detector(templatesPath, compilationUnitPath, templateGrammarPath, objectLanguage,
					metaLanguage, metaLanguagePrefix);

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

	private void showResults(AimPatternDetectionResult result) {
		Stage stage = new Stage();
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(getStage());
		stage.setResizable(false);

		Label labelParsedTemplates = new Label("Number of parsed templates: " + result.getNumParsedTemplates());
		Label labelParsedCompilationUnits = new Label(
				"Number of parsed compilation units: " + result.getNumParsedCompilationUnits());
		Label labelComparedFiles = new Label("Number of compared files: " + result.getNumComparedFiles());
		Label labelProcessingTime = new Label("Processing time: " + result.getProcessingTime() + " ns, "
				+ (result.getProcessingTime() / 1e6) + " ms");
		VBox vboxLabel = new VBox(labelParsedTemplates, labelParsedCompilationUnits, labelComparedFiles,
				labelProcessingTime);
		vboxLabel.setPadding(new Insets(10, 0, 10, 10));

		Accordion accordion = new Accordion();
		for (AimPatternDetectionResultEntry aimPatternDetectionResultEntry : result.getResults()) {
			String templateFileName = aimPatternDetectionResultEntry.getTemplatePath().getFileName().toString();
			String compilationUnitFileName = aimPatternDetectionResultEntry.getCompilationUnitPath().getFileName()
					.toString();

			VBox vBoxResultEntry = new VBox();
			vBoxResultEntry.getChildren()
					.add(new Label("Template path: " + aimPatternDetectionResultEntry.getTemplatePath()));
			vBoxResultEntry.getChildren().add(
					new Label("Compilation unit path: " + aimPatternDetectionResultEntry.getCompilationUnitPath()));
			vBoxResultEntry.getChildren()
					.add(new Label("Is match: " + (aimPatternDetectionResultEntry.isMatch() ? "Yes" : "No")));
			if (aimPatternDetectionResultEntry.isMatch()) {
				Map<String, Set<String>> placeholderSubstitutions = aimPatternDetectionResultEntry
						.getPlaceholderSubstitutions();
				vBoxResultEntry.getChildren()
						.add(new Label("Placeholder substitutions: " + placeholderSubstitutions.toString()));
			}
			TitledPane pane = new TitledPane(templateFileName + " <-> " + compilationUnitFileName, vBoxResultEntry);
			accordion.getPanes().add(pane);
		}

		VBox vBox = new VBox(vboxLabel, accordion);
		vBox.setPrefWidth(640);
		vBox.setPrefHeight(400);
		ScrollPane scrollPane = new ScrollPane(vBox);

		Scene scene = new Scene(scrollPane);
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
