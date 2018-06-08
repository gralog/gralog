package gralog.dialog;

import java.util.ArrayList;

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

    protected boolean isListID(String idCandidate){
     String idCandidateUC = idCandidate.toUpperCase();
        return (idCandidateUC.matches("_|[A-Z]([A-Z]|[0-9]|_)*"));
    }

    public DialogParser(){
        dialogAction = NONE;
        dialogState = DONE;
    }

    public String getErrorMsg() {
        return errorMsg;
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
                if (word0.equalsIgnoreCase("filter")){
                    if (inputWords.length == 1){
                        this.dialogState = ASK_WHAT_TO_FILTER;
                        return true;
                    }

                }
                if (word0.equalsIgnoreCase("select")) {

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

            if (dialogState == WAIT_FOR_WHAT_TO_FILTER){
                if (inputWords[0].equalsIgnoreCase("all") && (inputWords.length == 1)){ // filter all
                    this.dialogState = FILTER_ALL_ASK_FILTER_CONDITION;
                    return true;
                }
                if (inputWords[0].equalsIgnoreCase("all") &&
                        inputWords[1].equalsIgnoreCase("vertices")) { // filter all vertices
                    this.dialogState = FILTER_ALL_VERTICES_ASK_FILTER_CONDITION;
                    return true;
                }
                if (inputWords[0].equalsIgnoreCase("all") &&
                        inputWords[1].equalsIgnoreCase("edges")) { // filter all edges
                    this.dialogState = FILTER_ALL_EDGES_ASK_FILTER_CONDITION;
                    return true;
                }
                if (inputWords[0].equalsIgnoreCase("all")) { // all, but not vertices or edegs -> typo
                    this.dialogState = ASK_WHAT_TO_FILTER;
                    return true;
                }
                if (inputWords[0].equalsIgnoreCase("vertices") ||
                        inputWords[0].equalsIgnoreCase("edges")){ // tried to name list a reserved word
                    errorMsg = "Use \"all " + inputWords[0] + "\" to filter all " + inputWords[0] +
                            ". Lists names cannot be reserved words \"vertices\" or \"edges\" (not case sensitive).\n";
                    this.dialogState = ASK_WHAT_TO_FILTER;
                    return true;
                }
                if (isListID(inputWords[0])){
                    // TODO: create new list
                    return true;
                }
                errorMsg = "Expected a list id matching (_|<a-Z>)(_|<a-Z>|<digit>)*.\n";
                this.dialogState = ASK_WHAT_TO_FILTER;
                return true;
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
                this.dialogState = ASK_WHAT_TO_DESELECT;
                return true;
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
                this.dialogState = ASK_WHAT_TO_DESELECT;
                return true;
            }

        } else {// TODO: make an exception
            System.out.println("Something went wrong: Console got an empty string as input.");
        }
        return true;
    }





}