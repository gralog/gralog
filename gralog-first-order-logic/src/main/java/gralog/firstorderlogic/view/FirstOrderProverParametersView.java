/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.firstorderlogic.view;

import gralog.firstorderlogic.logic.firstorder.parser.FirstOrderSyntaxChecker;
import gralog.parser.SyntaxChecker;
import gralog.firstorderlogic.algorithm.FirstOrderProver;
import gralog.firstorderlogic.algorithm.FirstOrderProverParameters;
import gralog.firstorderlogic.logic.firstorder.formula.FirstOrderFormula;
import gralog.firstorderlogic.logic.firstorder.parser.FirstOrderParser;
import gralog.gralogfx.views.*;
import gralog.preferences.Preferences;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 *
 */
@ViewDescription(forClass = FirstOrderProverParameters.class)
public class FirstOrderProverParametersView
        extends GridPaneView<FirstOrderProverParameters> {

    public Set<String> getUniqueSearches() {
        String searches = Preferences.getString(FirstOrderProver.class, "searches", "");
        if (searches.isEmpty())
            return new HashSet<>();
        return new HashSet<>(Arrays.asList(searches.split("\n")));
    }

    public Set<String> getUsedVariables(String text) {
        Set<String> usedVariables = new HashSet<>();
        if (text.isEmpty())
            return usedVariables;
        String[] split = text.split("\\s+");
        for (String token : split) {
            int i;
            boolean found = false;
            int end = 1;
            for (i = 0; i < token.length(); i++) {
                if (!Character.isLetterOrDigit(token.charAt(i))) {
                    break;
                }
                else if (Character.isDigit(token.charAt(i)) && !found) {
                    end = i;
                    found = true;
                }
            }
            if (i == token.length()) {
                usedVariables.add(token.substring(0, end));
            }
        }
        return usedVariables;
    }

    @Override
    public void setObject(FirstOrderProverParameters params,
            Consumer<Boolean> submitPossible) {
        this.getChildren().clear();
        if (params == null)
            return;
        this.setVgap(8);
        this.setHgap(10);

        Label label = new Label("Formula:");
        label.setMinWidth(USE_PREF_SIZE);
        setConstraints(label, 0, 0);

        TextField formulaField = new TextField(params.parameter);
        setConstraints(formulaField, 1, 0);
        formulaField.promptTextProperty().set("Please enter a first-order formula");
        formulaField.setPrefWidth(1000);

        TextArea textArea = new TextArea();
        textArea.clear();

        for (String s : getUniqueSearches()) {
            textArea.appendText(s);
            textArea.appendText("\n");
        }

        Text hint = new Text();
        syntaxCheck(formulaField, hint, submitPossible);
        setConstraints(hint, 0, 2, 2, 1);

        params.parameter = formulaField.getText();
        formulaField.textProperty().addListener(e -> {
            syntaxCheck(formulaField, hint, submitPossible);
            params.parameter = formulaField.getText();
        });

        textArea.setEditable(false);
        textArea.setWrapText(false);

        setConstraints(textArea, 1, 1);
        Button load = new Button("load");
        Button save = new Button("save");
        Button delete = new Button("delete");
        Button clear = new Button("clear");
        Button substitute = new Button("Substitute");
        VBox vbox = new VBox(5);
        vbox.getChildren().addAll(load, save, delete, clear, substitute);
        vbox.setMinWidth(USE_PREF_SIZE);
        setConstraints(vbox, 4, 1);

        substitute.setOnAction(e -> {
            if ((textArea.getSelectedText()) != null) {
                String text = textArea.getSelectedText();
                String tfText = formulaField.getText();

                FirstOrderFormula phi = null;
                try {
                    phi = FirstOrderParser.parseString(text);
                    Set<String> usedInFormula = phi.variables();
                    Set<String> usedInField;
                    try {
                        FirstOrderFormula phi2 = FirstOrderParser.parseString(tfText);
                        usedInField = phi2.variables();
                    }
                    catch (Exception ex) {
                        usedInField = getUsedVariables(tfText);
                    }
                    HashMap<String, String> replace = new HashMap<>();
                    String ch = "a";
                    char c = 'a';
                    while (usedInField.contains(ch)) {
                        ch = String.valueOf(++c);
                    }
                    int cnt = 1;
                    for (String s : usedInFormula) {
                        replace.put(s, ch + String.valueOf(cnt++));
                    }
                    String subformula = phi.substitute(replace);
                    formulaField.setText(tfText + "( " + subformula + ")");
                }
                catch (Exception ex) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("Please select a valid formula to substitute");

                    alert.showAndWait();
                }
            }
        });

        /*load.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            fileChooser.getExtensionFilters().addAll(
                    new ExtensionFilter("Text Files", "*.txt"));
            Stage stage = new Stage();
            File f = fileChooser.showOpenDialog(stage);
            if (f != null) {
                textArea.clear();
                Set<String> uniqueSearch;
                try {
                    uniqueSearch = getUniqueSearches(f);
                    for (String s : uniqueSearch) {
                        textArea.appendText(s);
                        textArea.appendText("\n");
                    }
                }
                catch (Exception ex) {
                    Logger.getLogger(FirstOrderProverParametersView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });*/
        save.setOnAction(e -> {
            String text = textArea.getText();
            String fileName = "Formulae" + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + ".txt";
            PrintWriter out;
            try {
                out = new PrintWriter(new BufferedWriter(
                        new FileWriter(fileName, true)));
                out.println(text);
                out.close();
            }
            catch (IOException ex) {
                Logger.getLogger(FirstOrderProverParametersView.class.getName()).log(Level.SEVERE, null, ex);
            }

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("File saved successfully as " + fileName);

            alert.showAndWait();
        });

        delete.setOnAction((ActionEvent e) -> {
            textArea.replaceSelection("");
        });
        clear.setOnAction((ActionEvent e) -> {
            textArea.clear();
        });

        ColumnConstraints textAreaColumn = new ColumnConstraints();
        textAreaColumn.setHgrow(Priority.ALWAYS);
        getColumnConstraints().addAll(new ColumnConstraints(), textAreaColumn, new ColumnConstraints());

        RowConstraints textAreaRow = new RowConstraints();
        textAreaRow.setVgrow(Priority.ALWAYS);
        getRowConstraints().addAll(new RowConstraints(), textAreaRow, new RowConstraints());

        this.getChildren().addAll(label, formulaField, textArea, vbox, hint);
    }

    private void syntaxCheck(TextField valueField, Text hint,
            Consumer<Boolean> submitPossible) {
        FirstOrderSyntaxChecker check = new FirstOrderSyntaxChecker();
        SyntaxChecker.Result syntax = check.check(valueField.getText());
        if (syntax.syntaxCorrect) {
            valueField.setStyle("-fx-text-inner-color: black;");
            submitPossible.accept(true);
        }
        else {
            valueField.setStyle("-fx-text-inner-color: red;");
            submitPossible.accept(false);
        }
        hint.setText(syntax.hint);
    }
}
