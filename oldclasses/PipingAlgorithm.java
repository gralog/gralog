
/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
/**
 *
 * @author meike
 * @also felix
 */
package gralog.algorithm;



import gralog.structure.*;
import gralog.algorithm.*;
import gralog.progresshandler.*;

import java.util.Set;

import java.io.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
// import javafx.scene.control.HBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

@AlgorithmDescription(
    name = "Piping-plugin for enabling programming of plugins in python",
    text = "text",
    url = "https://url.com"
)

public class PipingAlgorithm extends Algorithm {

    @Override
    public AlgorithmParameters getParameters(Structure s, Highlights highlights) {
        return null;
    }

    public Object run(Structure structure, AlgorithmParameters params,
        Set<Object> selection, ProgressHandler onprogress) throws
        Exception {
        String[] commands
        	= {"gralog-layout/python/layout_python.py", structure.xmlToString()};
        System.out.println("Gralog says: starting.");
        String output = this.exec(commands);
        System.out.println("Finished exec.");

        Platform.runLater(
            () -> {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("result after exec\n:" + output);
                alert.showAndWait();
            }
        );
        return null;
    }

    private String exec(String[] execStr) {
        Platform.runLater(
            () -> {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("executing:" + execStr[0]);
                alert.showAndWait();
            }
        );
        String result = "";
        try {
            String line;
            Process p = Runtime.getRuntime().exec(execStr);
            BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            PrintStream out = new PrintStream(p.getOutputStream());
            out.println("Take this!");
            
            out.println("x");
            out.flush();
            while ((line = bri.readLine()) != null) {
                result = result + line + ".\n";
            }
            bri.close();
            while ((line = bre.readLine()) != null) {
                System.out.println(line);
            }
            bre.close();
            p.waitFor();
            System.out.println("Done.");
        } catch (Exception err) {
            err.printStackTrace();
        }
        return result;
    }
}