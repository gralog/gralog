package gralog.dialog;

import java.util.ArrayList;


import static gralog.dialog.ActionType.*;
import static gralog.dialog.DialogState.*;

public class DialogParser {
    private ActionType type;
    private DialogAction dialogAction;
    private ArrayList<String> parameters;
    private String errorMsg;
    private DialogState dialogState;


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
            if (dialogState == DONE) {
                if (inputWords[0].equalsIgnoreCase("select")) {
                    type = CORE;
                    if (inputWords.length == 1) {
                        this.dialogState = ASK_WHAT_TO_SELECT;
                        return true;
                    } // > 1 words
                    if (inputWords[1].equalsIgnoreCase("all")) {
                        if (inputWords.length == 2) {
                            dialogAction = DialogAction.SELECTALL;
                            this.dialogState = DONE;
                            return true;
                        }
                        if (inputWords.length >= 3){ // trash after correct comand is ignored
                            if (inputWords[2].equalsIgnoreCase("vertices")){
                                dialogAction = DialogAction.SELECT_ALL_VERTICES;
                                this.dialogState = DONE;
                            }
                            else if (inputWords[2].equalsIgnoreCase("edges")){
                                dialogAction = DialogAction.SELECT_ALL_EDGES;
                                this.dialogState = DONE;
                            }
                            else{ // text = select all <typo>
                                this.dialogState = ASK_WHAT_TO_SELECT;
                                return true;
                            }

                        }
                    } else { // because after "select" should be "all"
                        errorMsg = "Wrong format: " + inputWords[0] + " " + inputWords[1] +
                                ". Did you mean: " + inputWords[0] + " " + "all|all vertices|all edges?";
                        this.dialogState = DONE;
                        return false;
                    }
                }
            }
        } else {// TODO: make an exception
            System.out.println("Something went wrong: Console got an empty string as input.");
        }
        return true;
    }





}