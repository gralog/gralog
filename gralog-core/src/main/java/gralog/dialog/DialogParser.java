package gralog.dialog;

import gralog.rendering.GralogColor;

import java.util.ArrayList;

import static gralog.dialog.ActionType.FX;
import static gralog.dialog.DialogState.*;
import static gralog.dialog.DialogAction.*;
import static gralog.dialog.DialogState.DESELECT_ALL;
import static gralog.dialog.DialogState.FILTER;
import static gralog.dialog.DialogState.SELECT_ALL;

public class DialogParser {

    public static final String ANSI_RED = "\u001B[31m"; // for debugging
    public static final String ANSI_RESET = "\u001B[0m";

    private ActionType actionType;
    private DialogAction dialogAction;
    private ArrayList<String> parameters;
    private String errorMsg;
    private DialogState dialogState;
    private static String[] forbiddenIds = {"ALL", "SELECT", "FILTER", "WHERE", "ST", "SUCH", "THAT",
            "TO", "VERTICES", "EDGES", "DELETE", "FILL", "COLOR", "STROKE", "THICKNESS", "WIDTH", "HEIGHT",
            "SIZE", "ID", "SHAPE", "WEIGHT", "TYPE", "EDGETYPE", "DEGREE", "INDEGREE", "OUTDEGREE", "BUTTERFLY",
            "HAS", "SELFLOOP", "DIRECTED",
            "DELETE", "UNION", "INTERSECTION", "DIFFERENCE", "SYMMETRIC", "COMPLEMENT",
            "CONNECT",
            "ADD", "REMOVE", "DELETE", "CONTRACT", "EDGE",
            "GENERATE", "WHEEL", "GRID", "CLIQUE", "CYCLE", "PATH", "TORUS", "COMPLETE", "TREE", "CYLINDRICAL",
            "NO", "CONDITION", "LABEL", "X", "Q", "EXIT", "ABORT", "CANCEL"};

    private boolean hasValueForm(String s){
        // color or number or "PLAIN" or "DOTTED" or "DASHED"
        return (isColorValue(s) || s.matches("-?\\d*((.|,)\\d+)?"));
    }

    private boolean hasIdForm(String idCandidate){
        String idCandidateUC = idCandidate.toUpperCase();
        for (String s : forbiddenIds) if(s.equals(idCandidateUC)) return false;
        return (idCandidateUC.matches("(_|[A-Z])([A-Z]|[0-9]|_)*"));
    }

    private boolean isColorValue(String colorValueCandidate){
        return (GralogColor.isColor(colorValueCandidate));
    }

    public DialogParser(){
        dialogAction = NONE;
        this.dialogState = DONE;
        parameters = new ArrayList<String>();
        errorMsg = "";
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String s){ errorMsg = s; }

    public ActionType getType() {
        return actionType;
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
        this.dialogState = dialogState;
        System.out.println(ANSI_RED + "parse: got string: " + text + ANSI_RESET); //debugging
        if (text.isEmpty()) {// TODO: make an exception
            System.out.println("Something went wrong: Console got an empty string as input.\n");
            return false;
        }
        String[] inputWords = text.toUpperCase().split(" ");
        if (inputWords[0].equals("Q") ||
                inputWords[0].equals("EXIT") ||
                inputWords[0].equals("ABORT") ||
                inputWords[0].equals("X") ||
                inputWords[0].equals("CANCEL")
                ) {
            this.dialogState = DONE;
            dialogAction = NONE;
            parameters.clear();
            return true;
        }

        int i = 0;
        while (i <= inputWords.length) {
            System.out.println(ANSI_RED + "DialogParser: i=" + i +
                    "; dialogState=" + this.dialogState +
                    "; inputWords.length=" + inputWords.length + ANSI_RESET);
            if (this.dialogState == DONE) {
                if (i == inputWords.length)
                    return true;
                switch (inputWords[i]) {
                    case "H":
                    case "HELP":
                    case "?":
                        // this.dialogState = DONE;
                        dialogAction = HELP;
                        parameters.clear();
                        return true;
                    case "SELECT":
                        this.dialogState = SELECT;
                        i++;
                        break;
                    case "DESELECT":
                        this.dialogState = DESELECT;
                        i++;
                        break;
                    case "FILTER":
                        this.dialogState = FILTER;
                        i++;
                        break;
                    default:
                        errorMsg = "Parse error. Please, try again.\n";
                        return true;
                }
            }
            if (this.dialogState == SELECT) {
                if (i == inputWords.length) {
                    errorMsg = "What to select?  Format: (all [vertices|edges]) | <list id>\n";
                    return true;
                }
                if (hasIdForm(inputWords[i])) {
                    if (i == inputWords.length - 1) { // last word
                        this.dialogState = DONE;
                        actionType = FX;
                        dialogAction = SELECT_LIST;
                        parameters.add(inputWords[i]);
                        return true;
                    } else {           // could not parse: select <id> <trash>
                        // dont change dialogState == SELECT
                        errorMsg = "What to select? Format: (all [vertices|edges]) | <list id>\n";
                        return true;
                    }
                }
                if (inputWords[i].equals("ALL")) {
                    this.dialogState = SELECT_ALL;
                    i++;
                    continue;
                }
                errorMsg = "What to select? Format: (all [vertices|edges]) | <list id>\n";
                return true;
            }
            if (this.dialogState == DESELECT) {
                if (i == inputWords.length) {
                    errorMsg = "What to deselect?  Format: (all [vertices|edges]) | <list id>\n";
                    return true;
                }
                if (hasIdForm(inputWords[i])) {
                    if (i == inputWords.length - 1) { // last word
                        this.dialogState = DONE;
                        actionType = FX;
                        dialogAction = DESELECT_LIST;
                        parameters.add(inputWords[i]);
                        return true;
                    } else {           // could not parse: deselect <id> <trash>
                        // dont change dialogState == DESELECT
                        errorMsg = "What to deselect? Format: (all [vertices|edges]) | <list id>\n";
                        return true;
                    }
                }
                if (inputWords[i].equals("ALL")) {
                    this.dialogState = DESELECT_ALL;
                    i++;
                    continue;
                }
                errorMsg = "What to deselect? Format: (all [vertices|edges]) | <list id>\n";
                return true;
            }
            if (this.dialogState == DialogState.SELECT_ALL) {
                System.out.println("DialogParser.parse: dialogState=" + DialogState.SELECT_ALL);
                if (i == inputWords.length) {
                    this.dialogState = DONE;
                    dialogAction = DialogAction.SELECT_ALL;
                    parameters.clear();
                    return true;
                }
                if (inputWords[i].equals("VERTICES")) {
                    this.dialogState = DONE;
                    dialogAction = SELECT_ALL_VERTICES;
                    parameters.clear();
                    return true;
                }
                if (inputWords[i].equals("EDGES")) {
                    this.dialogState = DONE;
                    dialogAction = SELECT_ALL_EDGES;
                    parameters.clear();
                    return true;
                }
                errorMsg = "Select all what: edges or vertices? (Or abort.)\n";
                return true;

            }
            if (this.dialogState == DESELECT_ALL) {
                if (i == inputWords.length) {
                    this.dialogState = DONE;
                    dialogAction = DialogAction.DESELECT_ALL;
                    parameters.clear();
                    return true;
                }
                if (inputWords[i].equals("VERTICES")) {
                    this.dialogState = DONE;
                    dialogAction = DESELECT_ALL_VERTICES;
                    parameters.clear();
                    return true;
                }
                if (inputWords[i].equals("EDGES")) {
                    this.dialogState = DONE;
                    dialogAction = DESELECT_ALL_EDGES;
                    parameters.clear();
                    return true;
                }
                errorMsg = "Deselect all what: edges or vertices?\n";
                return true;
            }
            if (this.dialogState == FILTER) {
                if (i == inputWords.length) {
                    errorMsg = "What to filter? Format: <what> where|st|(such that)  <parameters> to <list>\n";
                    return true;
                }
                if (inputWords[i].equals("ALL")) {
                    this.dialogState = FILTER_ALL;
                    i++;
                    continue;
                }
                if (hasIdForm(inputWords[i])) {
                    this.dialogState = FILTER_WHAT;
                    parameters.add(inputWords[i]);
                    i++;
                    continue;
                }
                errorMsg = "What to filter? Format: <what> where|st|(such that)  <parameters> to <list>\n";
                return true;
            }
            if (this.dialogState == FILTER_ALL) {
                if (i == inputWords.length) {
                    errorMsg = "Vertices or edges?\n";
                    return true;
                }
                if (inputWords[i].equals("VERTICES")) {
                    this.dialogState = FILTER_WHAT;
                    parameters.add("vertices");
                    i++;
                    continue;
                }
                if (inputWords[i].equals("EDGES")) {
                    this.dialogState = FILTER_WHAT;
                    parameters.add("edges");
                    i++;
                    continue;
                }
                errorMsg = "Vertices or edges?\n";
                return true;
            }
            if (this.dialogState == FILTER_WHAT) {
                if (i == inputWords.length) {
                    errorMsg = "Specify filter conditions. Start with \"where\". Enter \"help\" for help.\n";
                    return true;
                }
                if (inputWords[i].equals("WHERE") || inputWords[i].equals("ST")) {
                    this.dialogState = FILTER_WHAT_WHERE;
                    i++;
                    continue;
                }
                if (inputWords[i].equals("such")) {
                    this.dialogState = FILTER_WHAT_SUCH;
                    i++;
                    continue;
                }
                errorMsg = "Specify filter conditions. Start with \"where\". Enter \"help\" for help.\n";
                return true;
            }
            if (this.dialogState == FILTER_WHAT_SUCH) {
                if (i == inputWords.length) {
                    errorMsg = "You probably mean \"such that\". Enter \"that\" or abort.\n";
                    return true;
                }
                if (inputWords[i].equals("THAT")) {
                    this.dialogState = FILTER_WHAT_WHERE;
                    i++;
                    continue;
                }
                errorMsg = "You probably mean \"such that\". Enter \"that\" or abort.\n";
                return true;
            }
            if (this.dialogState == FILTER_WHAT_WHERE) {
                if (i == inputWords.length) {
                    errorMsg = "Specify filter conditions. Enter \"help\" for help.\n";
                    return true;
                }
                if (inputWords[i].equals("NO")) {
                    this.dialogState = FILTER_WHAT_WHERE_NO;
                    parameters.add("nocodition");
                    i++;
                    continue;
                }
                if (inputWords[i].equals("FILL")) {
                    this.dialogState = FILTER_WHAT_WHERE_FILL;
                    parameters.add("fill");
                    i++;
                    continue;
                }
                if (inputWords[i].equals("STROKE")) {
                    this.dialogState = FILTER_WHAT_WHERE_STROKE;
                    parameters.add("stroke");
                    i++;
                    continue;
                }
                if (inputWords[i].equals("COLOR")) {
                    this.dialogState = FILTER_WHAT_WHERE_PARAM;
                    parameters.add("color");
                    i++;
                    continue;
                }
                if (inputWords[i].equals("THICKNESS")) {
                    this.dialogState = FILTER_WHAT_WHERE_PARAM;
                    parameters.add("thickness");
                    i++;
                    continue;
                }
                if (inputWords[i].equals("WIDTH")) {
                    this.dialogState = FILTER_WHAT_WHERE_PARAM;
                    parameters.add("width");
                    i++;
                    continue;
                }
                if (inputWords[i].equals("HEIGHT")) {
                    this.dialogState = FILTER_WHAT_WHERE_PARAM;
                    parameters.add("height");
                    i++;
                    continue;
                }
                if (inputWords[i].equals("SIZE")) {
                    this.dialogState = FILTER_WHAT_WHERE_PARAM;
                    parameters.add("size");
                    i++;
                    continue;
                }
                if (inputWords[i].equals("ID")) {
                    this.dialogState = FILTER_WHAT_WHERE_PARAM;
                    parameters.add("id");
                    i++;
                    continue;
                }
                if (inputWords[i].equals("SHAPE")) {
                    this.dialogState = FILTER_WHAT_WHERE_PARAM;
                    parameters.add("shape");
                    i++;
                    continue;
                }
                if (inputWords[i].equals("WEIGHT")) {
                    this.dialogState = FILTER_WHAT_WHERE_PARAM;
                    parameters.add("weight");
                    i++;
                    continue;
                }
                if (inputWords[i].equals("TYPE")) {
                    this.dialogState = FILTER_WHAT_WHERE_PARAM;
                    parameters.add("type");
                    i++;
                    continue;
                }
                if (inputWords[i].equals("EDGE")) {
                    this.dialogState = FILTER_WHAT_WHERE_EDGE;
                    parameters.add("edge");
                    i++;
                    continue;
                }
                if (inputWords[i].equals("EDGETYPE")) {
                    this.dialogState = FILTER_WHAT_WHERE_PARAM;
                    parameters.add("edgetype");
                    i++;
                    continue;
                }
                if (inputWords[i].equals("DEGREE")) {
                    this.dialogState = FILTER_WHAT_WHERE_PARAM;
                    parameters.add("degree");
                    i++;
                    continue;
                }
                if (inputWords[i].equals("INDEGREE")) {
                    this.dialogState = FILTER_WHAT_WHERE_PARAM;
                    parameters.add("indegree");
                    i++;
                    continue;
                }
                if (inputWords[i].equals("OUTDEGREE")) {
                    this.dialogState = FILTER_WHAT_WHERE_PARAM;
                    parameters.add("outdegree");
                    i++;
                    continue;
                }
                if (inputWords[i].equals("HAS")) {
                    this.dialogState = FILTER_WHAT_WHERE_HAS;
                    i++;
                    continue;
                }
                if (inputWords[i].equals("DIRECTED")) {
                    this.dialogState = FILTER_WHAT_WHERE_COND;
                    parameters.add("directed");
                    i++;
                    continue;
                }
                errorMsg = "Mistyped? Add conditions. Enter \"help\" for help.\n";
                return true;
            }
            if (this.dialogState == FILTER_WHAT_WHERE_NO) {
                if (i == inputWords.length) {
                    this.dialogState = FILTER_WHAT_WHERE_COND;
                    errorMsg = "Specify more conditions or where to save the result. Start with \"to\", then give a list id.\n";
                    return true;
                }
                if (inputWords[i].equals("CONDITION")) {
                    this.dialogState = FILTER_WHAT_WHERE_COND;
                    ;
                    i++;
                    continue;
                }
                if (inputWords[i].equals("TO")) {
                    this.dialogState = FILTER_WHAT_WHERE_COND_TO;
                    i++;
                    continue;
                }
                this.dialogState = FILTER_WHAT_WHERE_COND;
                errorMsg = "Specify more conditions or where to save the result. Start with \"to\", then give a list id.\n";
                return true;

            }
            if (this.dialogState == FILTER_WHAT_WHERE_FILL) {
                if (i == inputWords.length) {
                    this.dialogState = FILTER_WHAT_WHERE_PARAM;
                    errorMsg = "Specify the fill color or abort.\n";
                    return true;
                }
                if (inputWords[i].equals("COLOR")) {
                    this.dialogState = FILTER_WHAT_WHERE_PARAM;
                    i++;
                    continue;
                }
                if (isColorValue(inputWords[i])) {
                    this.dialogState = FILTER_WHAT_WHERE_COND;
                    parameters.add(inputWords[i]);
                    i++;
                    continue;
                }
                errorMsg = "Specify a color or abort.\n";
                return true;
            }
            if (this.dialogState == FILTER_WHAT_WHERE_STROKE) {
                if (i == inputWords.length) {
                    this.dialogState = FILTER_WHAT_WHERE_PARAM;
                    errorMsg = "Specify the stroke color or abort.\n";
                    return true;
                }
                if (inputWords[i].equals("COLOR")) {
                    this.dialogState = FILTER_WHAT_WHERE_PARAM;
                    i++;
                    continue;
                }
                if (isColorValue(inputWords[i])) {
                    this.dialogState = FILTER_WHAT_WHERE_COND;
                    parameters.add(inputWords[i]);
                    i++;
                    continue;
                }
                errorMsg = "Specify a color or abort.\n";
                return true;
            }
            if (this.dialogState == FILTER_WHAT_WHERE_EDGE) {
                if (i == inputWords.length) {
                    this.dialogState = FILTER_WHAT_WHERE_PARAM;
                    errorMsg = "Specify the edge type or abort.\n";
                    return true;
                }
                if (inputWords[i].equals("TYPE")) {
                    this.dialogState = FILTER_WHAT_WHERE_PARAM;
                    i++;
                    continue;
                }
                if (isColorValue(inputWords[i])) {
                    this.dialogState = FILTER_WHAT_WHERE_COND;
                    parameters.add(inputWords[i]);
                    i++;
                    continue;
                }
                errorMsg = "Specify an edge type or abort.\n";
                return true;

            }
            if (this.dialogState == FILTER_WHAT_WHERE_PARAM){
                if (i == inputWords.length){
                    errorMsg = "Specify the value or abort.\n";
                    return true;
                }
                if (hasValueForm(inputWords[i])){
                    this.dialogState = FILTER_WHAT_WHERE_COND;
                    parameters.add(inputWords[i]);
                    i++;
                    continue;
                }
                errorMsg = "Specify a value or abort.\n";
                return true;
            }
            if (this.dialogState == FILTER_WHAT_WHERE_HAS) {
                if (i == inputWords.length) {
                    errorMsg = "Has what? Specify property or abort.\n";
                    return true;
                }
                if (inputWords[i].equals("SELFLOOP")) {
                    this.dialogState = FILTER_WHAT_WHERE_COND;
                    parameters.add("selfloop");
                    i++;
                    continue;
                }
                if (inputWords[i].equals("LABEL")) {
                    this.dialogState = FILTER_WHAT_WHERE_COND;
                    parameters.add("label");
                    i++;
                    continue;
                }
                errorMsg = "Possible properties to have are: selfloop, label. Specify property or abort.\n";
                return true;
            }
            if (this.dialogState == FILTER_WHAT_WHERE_COND) {
                if (i == inputWords.length) {
                    errorMsg = "Specify where to save the result. Start with \"to\", then give a list id.\n";
                    return true;
                }
                if (inputWords[i].equals("TO")) {
                    this.dialogState = FILTER_WHAT_WHERE_COND_TO;
                    i++;
                    continue;
                }
                // epsilon transition to FILTER_WHAT_WHERE
                this.dialogState = FILTER_WHAT_WHERE;
                continue;
            }
            if (this.dialogState == FILTER_WHAT_WHERE_COND_TO) {
                if (i == inputWords.length) {
                    errorMsg = "Specify where to save the result or abort.\n";
                    return true;
                }
                if (hasIdForm(inputWords[i])) {
                    this.dialogState = DONE;
                    parameters.add(inputWords[i]);
                    dialogAction = DialogAction.FILTER;
                    return true;
                }
                errorMsg = "Specify where to save the result. Format: (_|[a-Z])(_|[a-Z]|[0-9])*.\n";
                return true;
            }
            errorMsg = "Something went wrong, I could not parse your command. Please, try to write the command in one line.\n";
            return true;
        }
        return true;
    }
}