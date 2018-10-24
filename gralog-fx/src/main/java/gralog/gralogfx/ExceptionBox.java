/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.gralogfx;

import java.io.PrintWriter;
import java.io.StringWriter;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.control.CheckBox;

/**
 *
 */
public class ExceptionBox extends Alert {

    public ExceptionBox() {
        super(AlertType.ERROR);
        this.setTitle("Exception Caught");
        this.setHeaderText(null);
    }

    public void showAndWait(Exception ex) {
        this.setContentText(ex.getMessage());

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("Stack-Trace:");
        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(false);

        CheckBox dontShowThisMessageAgain = new CheckBox("Don't show this message again");

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);
        expContent.add(dontShowThisMessageAgain,0,2);

        // Set expandable Exception into the dialog pane.
        this.getDialogPane().setExpandableContent(expContent);
        this.showAndWait();
    }

    public void showAndWait(String msg) {

        this.setContentText(msg);

        // Create expandable Exception.
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        String exceptionText = sw.toString();

        Label label = new Label("Stack-Trace:");
        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(false);

        CheckBox dontShowThisMessageAgain = new CheckBox("Don't show this message again");

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);
        expContent.add(dontShowThisMessageAgain,0,2);

        // Set expandable Exception into the dialog pane.
        this.getDialogPane().setExpandableContent(expContent);
        this.showAndWait();
    }
}
