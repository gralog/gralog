/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.algorithm;

import gralog.firstorderlogic.logic.firstorder.formula.FirstOrderFormula;
import gralog.firstorderlogic.logic.firstorder.parser.FirstOrderParser;
import gralog.gralogfx.views.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 *
 */
@ViewDescription(forClass = FirstOrderProverParameters.class)
public class FirstOrderProverParametersView extends GridPaneView {

    public Set<String> getUniqueSearches(File f) throws Exception {
        Set<String> result = new HashSet<>();

        if (f.exists()) {
            BufferedReader input = new BufferedReader(new FileReader(f));
            String tmp;
            while ((tmp = input.readLine()) != null) {
                result.add(tmp);
            }

        }
        return result;
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
    public void update() {
        this.getChildren().clear();
        if (displayObject != null) {
            FirstOrderProverParameters p = (FirstOrderProverParameters) displayObject;
            this.setVgap(8);
            this.setHgap(10);
            Label label = new Label("Formula");
            setConstraints(label, 0, 0);
            File file = new File("PreviousSearch.txt");
            String str = "";

            if (file.exists()) {

                try {

                    BufferedReader input = new BufferedReader(new FileReader(file));
                    str = input.readLine();
                }
                catch (Exception ex) {
                    str = "ERROR" + ex.toString();
                }
            }
            else {
                str = "";
            }
            TextField tf = new TextField(str);
            setConstraints(tf, 1, 0);
            TextArea textArea = new TextArea();
            textArea.clear();

            Set<String> uniqueSearches = null;
            try {
                uniqueSearches = getUniqueSearches(new File("CorrectSearches.txt"));
            }
            catch (Exception ex) {
                uniqueSearches.add(ex.toString());
            }

            for (String s : uniqueSearches) {
                textArea.appendText(s);
                textArea.appendText("\n");
            }

            p.formulae = tf.getText();
            tf.textProperty().addListener((observable, oldValue, newValue) -> {
                p.formulae = tf.getText();

            });

            textArea.setEditable(false);
            textArea.setWrapText(false);

            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);

            setConstraints(textArea, 1, 1);
            Button load = new Button("load");
            Button save = new Button("save");
            Button delete = new Button("delete");
            Button clear = new Button("clear");
            Button substitute = new Button("Substitute");
            VBox vbox = new VBox(5);
            vbox.getChildren().addAll(load, save, delete, clear, substitute);
            setConstraints(vbox, 4, 1);

            substitute.setOnAction(e -> {
                if ((textArea.getSelectedText()) != null) {
                    String text = textArea.getSelectedText();
                    String tfText = tf.getText();

                    FirstOrderParser parser = new FirstOrderParser();

                    FirstOrderFormula phi = null;
                    try {
                        phi = parser.parseString(text);
                        Set<String> usedInFormula = phi.variables();
                        Set<String> usedInField;
                        try {
                            FirstOrderFormula phi2 = parser.parseString(tfText);
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
                        tf.setText(tfText + "( " + subformula + ")");

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

            load.setOnAction(e -> {
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
            });

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
            this.getChildren().addAll(label, tf, textArea, vbox);

            setMaxSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
        }
    }
}
