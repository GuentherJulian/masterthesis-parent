package io.github.guentherjulian.masterthesis.patterndetector.gui.controller;

import java.net.URL;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;

import io.github.guentherjulian.masterthesis.patterndetection.engine.AimPatternDetectionResult;
import io.github.guentherjulian.masterthesis.patterndetection.engine.AimPatternDetectionResultEntry;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

public class PatternDetectorResultController implements Initializable {

	@FXML
	private ListView<AimPatternDetectionResultEntry> listViewResult;

	@FXML
	private Text textNumTemplates;

	@FXML
	private Text textNumSuccessfulParsedTemplates;

	@FXML
	private Text textNumUnsuccessfulParsedTemplates;

	@FXML
	private Text textNumCompilationUnits;

	@FXML
	private Text textNumInstantiationPathMatches;

	@FXML
	private Text textNumComparedFiles;

	@FXML
	private Text textNumFileMatches;

	@FXML
	private Text textProcessingTime;

	@FXML
	private TextArea textAreaMatch;

	private ObservableList<AimPatternDetectionResultEntry> results;
	private AimPatternDetectionResult result;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}

	public void setAimPatternDetectionResult(AimPatternDetectionResult result) {
		this.result = result;
		this.results = FXCollections.observableArrayList(this.result.getResults());
	}

	public void init() {
		this.listViewResult.setItems(this.results);

		this.textNumTemplates.setText(String.valueOf(this.result.getNumParsedTemplates()));
		this.textNumSuccessfulParsedTemplates.setText(String.valueOf(this.result.getNumParseableTemplates()));
		this.textNumUnsuccessfulParsedTemplates.setText(String.valueOf(this.result.getNumUnparseableTemplates()));
		this.textNumCompilationUnits.setText(String.valueOf(this.result.getNumParsedCompilationUnits()));

		this.textNumInstantiationPathMatches.setText(String.valueOf(this.result.getNumInstantiationPathMatches()));
		this.textNumComparedFiles.setText(String.valueOf(this.result.getNumComparedFiles()));
		this.textNumFileMatches.setText(String.valueOf(this.result.getNumFileMatches()));
		this.textProcessingTime.setText(String.format("%.2f ms  /  %.2f s", (result.getProcessingTime() / 1e6),
				(result.getProcessingTime() / 1e9)));

		this.listViewResult.getSelectionModel().selectedItemProperty()
				.addListener((ChangeListener<? super AimPatternDetectionResultEntry>) (
						ObservableValue<? extends AimPatternDetectionResultEntry> ov,
						AimPatternDetectionResultEntry oldValue, AimPatternDetectionResultEntry newValue) -> {

					this.textAreaMatch.clear();
					this.textAreaMatch.appendText("Template path: " + newValue.getTemplatePath() + "\n");
					this.textAreaMatch.appendText("Compilation unit path: " + newValue.getCompilationUnitPath() + "\n");

					if (newValue.isMatch()) {
						this.textAreaMatch.appendText("Is match: Yes\n");
						if (newValue.getPlaceholderSubstitutions() == null
								|| newValue.getPlaceholderSubstitutions().isEmpty()) {
							this.textAreaMatch.appendText("Placeholder substitutions: Empty\n");
						} else {
							this.textAreaMatch.appendText("Placeholder substitutions:\n");
							for (Entry<String, Set<String>> phSubstitution : newValue.getPlaceholderSubstitutions()
									.entrySet()) {
								this.textAreaMatch.appendText(
										phSubstitution.getKey() + " -> " + phSubstitution.getValue().toString() + "\n");
							}
						}

					} else {
						this.textAreaMatch.appendText("Is match: No\n");
						if (newValue.isTemplateUnparseable()) {
							this.textAreaMatch.appendText("Template is not parseable!\n");
						}
					}

					if (newValue.getTreeMatchResult() != null) {
						this.textAreaMatch.appendText(String.format("\nProcessing time: %.2f ms",
								newValue.getTreeMatchResult().getMatchingTime() / 1e6));
					}
				});

		this.listViewResult.setCellFactory(listview -> new ListCell<AimPatternDetectionResultEntry>() {
			@Override
			public void updateItem(AimPatternDetectionResultEntry item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null) {
					setText(null);
					setGraphic(null);
				} else {
					setText(item.toString());
					if (item.isMatch()) {
						setText("Match: " + item.toString());
					}
				}
			}
		});

		this.listViewResult.getSelectionModel().select(this.results.get(0));
	}
}
