package gralog.dialog;

import java.util.ArrayList;


import static gralog.dialog.ActionType.*;
import static gralog.dialog.DialogState.*;
import static gralog.dialog.DialogAction.*;

public class DialogParser {

    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    private ActionType type;
    private DialogAction dialogAction;
    private ArrayList<String> parameters;
    private String errorMsg;
    private DialogState dialogState;

    public DialogParser(){
        dialogAction = NONE;
        dialogState = DONE;
    }

    public ActionType getType() {
        return type;
    }

    public DialogAction getDialogAction() {
        return dialogAction;
    }

    public ArrayList<String> getParameters() {
        return parameters;
    }

    public DialogState getDialogState() {
        return dialogState;
    }

    public boolean parse(DialogState dialogState, String text) {
        if (!text.isEmpty()) {
            String[] inputWords = text.split(" ");
            String word0 = inputWords[0].toUpperCase();

            if (word0.equals("Q") ||
                    word0.equals("EXIT") ||
                    word0.equals("ABORT") ||
                    word0.equals("X") ||
                    word0.equals("CANCEL")
                    ){
                this.dialogState = DONE;
                dialogAction = NONE;
                return true;
            }
            if (dialogState == DONE) { // beginning of a dialog
                if (word0.equalsIgnoreCase("select")) {
                    type = CORE;
                    if (inputWords.length == 1 || !inputWords[1].equalsIgnoreCase("all")) {
                        this.dialogState = ASK_WHAT_TO_SELECT;
                        return true;
                    } // > 1 words
                    if (inputWords.length == 2) {
                        dialogAction = SELECTALL;
                        this.dialogState = DONE;
                        return true;
                    } // trash after correct comand is ignored
                    if (inputWords[2].equalsIgnoreCase("vertices")){
                        dialogAction = SELECT_ALL_VERTICES;
                        this.dialogState = DONE;
                        return true;
                    }
                    if (inputWords[2].equalsIgnoreCase("edges")){
                        dialogAction = SELECT_ALL_EDGES;
                        this.dialogState = DONE;
                        return true;
                    }// text = select all <typo>
                    this.dialogState = ASK_WHAT_TO_SELECT;
                    return true;
                }
                if (word0.equalsIgnoreCase("deselect")) {
                    type = CORE;
                    if (inputWords.length == 1 || !inputWords[1].equalsIgnoreCase("all")) {
                        this.dialogState = ASK_WHAT_TO_DESELECT;
                        return true;
                    } // > 1 words
                    if (inputWords.length == 2) {
                        dialogAction = DESELECTALL;
                        this.dialogState = DONE;
                        return true;
                    } // trash after correct comand is ignored
                    if (inputWords[2].equalsIgnoreCase("vertices")){
                        dialogAction = DESELECT_ALL_VERTICES;
                        this.dialogState = DONE;
                        return true;
                    }
                    if (inputWords[2].equalsIgnoreCase("edges")){
                        dialogAction = DESELECT_ALL_EDGES;
                        this.dialogState = DONE;
                        return true;
                    }// text = select all <typo>
                    this.dialogState = ASK_WHAT_TO_DESELECT;
                    return true;
                }
            }

            if (dialogState == WAIT_FOR_WHAT_TO_SELECT){
                if (!inputWords[0].equalsIgnoreCase("all")){
                    this.dialogState = ASK_WHAT_TO_SELECT;
                    return true;
                }
                if (inputWords.length == 1){
                    dialogAction = SELECTALL;
                    this.dialogState = DONE;
                    return true;
                }
                if (inputWords[1].equalsIgnoreCase("vertices")){
                    dialogAction = SELECT_ALL_VERTICES;
                    this.dialogState = DONE;
                    return true;
                }
                if (inputWords[1].equalsIgnoreCase("edges")){
                    dialogAction = SELECT_ALL_EDGES;
                    this.dialogState = DONE;
                    return true;
                }
            }

            if (dialogState == WAIT_FOR_WHAT_TO_DESELECT){
                if (!inputWords[0].equalsIgnoreCase("all")){
                    this.dialogState = ASK_WHAT_TO_DESELECT;
                    return true;
                }
                if (inputWords.length == 1){
                    dialogAction = DESELECTALL;
                    this.dialogState = DONE;
                    return true;
                }
                if (inputWords[1].equalsIgnoreCase("vertices")){
                    dialogAction = DESELECT_ALL_VERTICES;
                    this.dialogState = DONE;
                    return true;
                }
                if (inputWords[1].equalsIgnoreCase("edges")){
                    dialogAction = DESELECT_ALL_EDGES;
                    this.dialogState = DONE;
                    return true;
                }
            }

        } else {// TODO: make an exception
            System.out.println("Something went wrong: Console got an empty string as input.");
        }
        return true;
    }





}