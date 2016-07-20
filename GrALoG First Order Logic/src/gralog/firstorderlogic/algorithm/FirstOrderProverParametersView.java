/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.algorithm;

import static com.oracle.jrockit.jfr.ContentType.Timestamp;
import gralog.firstorderlogic.importfilter.LoadFromFile;
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
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 *
 * @author Hv
 */
@ViewDescription(forClass=FirstOrderProverParameters.class)
public class FirstOrderProverParametersView extends GridPaneView{

    public Set<String> getUniqueSearches(File f) throws Exception{
        Set<String> result=new HashSet<>();
        
        if(f.exists()){
            BufferedReader input = new BufferedReader(new FileReader(f));
            String tmp=null;
            while((tmp=input.readLine())!=null){
                result.add(tmp);
            }
            
        }
        return result;
    }
    
    @Override
    public void Update() {
        this.getChildren().clear();
        if(displayObject != null){
            FirstOrderProverParameters p =(FirstOrderProverParameters)displayObject;
            this.setVgap(8);
            this.setHgap(10);
            this.add(new Label("Formulae"), 0, 0);
            File file=new File("PreviousSearch.txt");
            String str="";
           
            if (file.exists()){
            
            try {
                
                BufferedReader input = new BufferedReader(new FileReader(file));
                str=input.readLine();
            } catch (Exception ex) {
                    str= "ERROR" + ex.toString();
                }
            
            }
            else str="";
            TextField tf=new TextField(str);
            this.add(tf, 1,0);
            TextArea textArea = new TextArea();
            textArea.clear();
           
            Set<String> uniqueSearches=null;
            try {
                uniqueSearches = getUniqueSearches(new File("CorrectSearches.txt") );
            } catch (Exception ex) {
                uniqueSearches.add(ex.toString());
            }
            
            for(String s : uniqueSearches){
                textArea.appendText(s);
                textArea.appendText("\n");
            }
            
                p.formulae=tf.getText();
            tf.textProperty().addListener((observable, oldValue, newValue) -> {
                  p.formulae=tf.getText();
                  
            });
        
           
           
            
            textArea.setEditable(false);
            textArea.setWrapText(false);

           textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            
            this.add(textArea,1,2);
            Button load=new Button("load");
            Button save=new Button("save");
            Button delete =new Button("delete");
            Button clear=new Button("clear");
            this.add(load, 0, 3);
            this.add(save, 1, 3);
            this.add(delete,2,3);
            this.add(clear, 3, 3);
            
            
            load.setOnAction(new EventHandler<ActionEvent>() {
             @Override
              public void handle(ActionEvent e) {
                  FileChooser fileChooser = new FileChooser();
                  fileChooser.setTitle("Open Resource File");
                  fileChooser.getExtensionFilters().addAll(
                    new ExtensionFilter("Text Files", "*.txt") );
                  Stage stage=new Stage();
                  File file= fileChooser.showOpenDialog(stage);  
                  if(file!=null){
                      textArea.clear();
                      Set<String> uniqueSearch;
                      try {
                          uniqueSearch = getUniqueSearches(file);
                          for(String s : uniqueSearch){
                              textArea.appendText(s);
                              textArea.appendText("\n");
                          }
                      } catch (Exception ex) {
                          Logger.getLogger(FirstOrderProverParametersView.class.getName()).log(Level.SEVERE, null, ex);
                      }
                      
                  }
              }
            
            });
            
              save.setOnAction(new EventHandler<ActionEvent>() {
             @Override
              public void handle(ActionEvent e) {
                  String text=textArea.getText();
                  String fileName="Formulae"+ new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + ".txt";
                  PrintWriter out;    
                 try {    
                     out = new PrintWriter(new BufferedWriter(
                             new FileWriter( fileName ,true)));
                     out.println(text);  
                     out.close();
                 } catch (IOException ex) {
                     Logger.getLogger(FirstOrderProverParametersView.class.getName()).log(Level.SEVERE, null, ex);
                 }
                 
                 Alert alert = new Alert(AlertType.INFORMATION);
                 alert.setTitle("Information Dialog");
                 alert.setHeaderText(null);
                 alert.setContentText("File saved successfully as " + fileName);

alert.showAndWait();
                 
            
              }
            
            });
            
             
              
              
           delete.setOnAction(new EventHandler<ActionEvent>() {
             @Override
              public void handle(ActionEvent e) {
                  textArea.replaceSelection("");
              }
            
            });
             clear.setOnAction(new EventHandler<ActionEvent>() {
             @Override
              public void handle(ActionEvent e) {
                  textArea.clear();
              }
            
            });
              
              
        }
    }
}

    

