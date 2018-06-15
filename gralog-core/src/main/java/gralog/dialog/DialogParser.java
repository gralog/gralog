package gralog.dialog;

import gralog.rendering.GralogColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static gralog.dialog.ActionType.FX;
import static gralog.dialog.DialogState.*;
import static gralog.dialog.DialogAction.*;
import static gralog.dialog.DialogState.DESELECT_ALL;
import static gralog.dialog.DialogState.FILTER;
import static gralog.dialog.DialogState.SELECT_ALL;

public class DialogParser {

    public static final String ANSI_RED = "\u001B[31m";
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
            "NO", "CONDITION", "LABEL"};


    private boolean hasIdForm(String idCandidate){
        String idCandidateUC = idCandidate.toUpperCase();
        for (String s : forbiddenIds) if(s.equals(idCandidateUC)) return false;
        return (idCandidateUC.matches("_|[A-Z]([A-Z]|[0-9]|_)*"));
    }

    private boolean isColorValue(String colorValueCandidate){
        return (GralogColor.isColor(colorValueCandidate));
    }

    public DialogParser(){
        dialogAction = NONE;
        dialogState = DONE;
        parameters = new ArrayList<String>();
    }

    public String getErrorMsg() {
        return errorMsg;
    }

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
        if (!text.isEmpty()) {
            String[] inputWords = text.toUpperCase().split(" ");
            if (inputWords[0].equals("Q") ||
                    inputWords[0].equals("EXIT") ||
                    inputWords[0].equals("ABORT") ||
                    inputWords[0].equals("X") ||
                    inputWords[0].equals("CANCEL")
                    ){
                this.dialogState = DONE;
                dialogAction = NONE;
                parameters.clear();
                return true;
            }

            int i = 0;
            while (i <= inputWords.length){
                if (dialogState == DONE){
                    switch (inputWords[i]){
                        case "H": case "HELP": case "?": dialogState = DONE;
                                                         dialogAction = HELP;
                                                         parameters.clear();
                                                         return true;
                        case "SELECT": dialogState = SELECT; i++; break;
                        case "DESELECT": dialogState = DESELECT; i++; break;
                        case "FILTER": dialogState = FILTER; i++; break;
                    }
                    continue;
                }
                if (dialogState == SELECT){
                    if (i == inputWords.length){
                        errorMsg = "What to select?  Format: (all [vertices|edges]) | <list id>\n";
                        return true;
                    }
                    if (hasIdForm(inputWords[i]))                        {
                        if (i == inputWords.length-1) { // last word
                            dialogState = DONE;
                            actionType = FX;
                            dialogAction = SELECT_LIST;
                            parameters.add(inputWords[i]);
                            return true;
                        }
                        else{           // could not parse: select <id> <trash>
                            // dont change dialogState == SELECT
                            errorMsg = "What to select? Format: (all [vertices|edges]) | <list id>\n";
                            return true;
                        }
                    }
                    if (inputWords[i].equals("all")){
                        dialogState = SELECT_ALL;
                        i++;
                        continue;
                    }
                    errorMsg = "What to select? Format: (all [vertices|edges]) | <list id>\n";
                    return true;
                }
                if (dialogState == DESELECT){
                    if (i == inputWords.length){
                        errorMsg = "What to deselect?  Format: (all [vertices|edges]) | <list id>\n";
                        return true;
                    }
                    if (hasIdForm(inputWords[i]))                        {
                        if (i == inputWords.length-1) { // last word
                            actionType = FX;
                            dialogAction = DESELECT_LIST;
                            parameters.add(inputWords[i]);
                            return true;
                        }
                        else{           // could not parse: select <id> <trash>
                            // dont change dialogState == SELECT
                            errorMsg = "What to deselect? Format: (all [vertices|edges]) | <list id>\n";
                            return true;
                        }
                    }
                    if (inputWords[i].equals("all")){
                        dialogState = DESELECT_ALL;
                        i++;
                        continue;
                    }
                    errorMsg = "What to deselect? Format: (all [vertices|edges]) | <list id>\n";
                    return true;
                }
                if (dialogState == DialogState.SELECT_ALL){
                    if (i == inputWords.length){
                        dialogState = DONE;
                        dialogAction = DialogAction.SELECT_ALL;
                        parameters.clear();
                        return true;
                    }
                    if (inputWords[i].equals("VERTICES")){
                        dialogState = DONE;
                        dialogAction = SELECT_ALL_VERTICES;
                        parameters.clear();
                        return true;
                    }
                }
                if (dialogState == DESELECT_ALL){
                    if (i == inputWords.length){
                        dialogState = DONE;
                        dialogAction = DialogAction.DESELECT_ALL;
                        parameters.clear();
                        return true;
                    }
                    if (inputWords[i].equals("VERTICES")){
                        dialogState = DONE;
                        dialogAction = DESELECT_ALL_VERTICES;
                        parameters.clear();
                        return true;
                    }
                }
                if (dialogState == FILTER){
                    if (i == inputWords.length){
                        errorMsg = "What to filter? Format: <what> where|st|(such that)  <parameters> to <list>\n";
                        return true;
                    }
                    if (inputWords[i].equals("ALL")){
                        dialogState = FILTER_ALL;
                        i++;
                        continue;
                    }
                    if (hasIdForm(inputWords[i])){
                        dialogState = FILTER_WHAT;
                        parameters.add(inputWords[i]);
                        i++;
                        continue;
                    }
                    errorMsg = "What to filter? Format: <what> where|st|(such that)  <parameters> to <list>\n";
                    return true;
                }
                if (dialogState == FILTER_ALL){
                    if (i == inputWords.length){
                        errorMsg = "All vertices or all edges?";
                        return true;
                    }
                    if (inputWords[i].equals("VERTICES")){
                        dialogState = FILTER_WHAT;
                        parameters.add("VERTICES");
                        i++;
                        continue;
                    }
                    if (inputWords[i].equals("EDGES")){
                        dialogState = FILTER_WHAT;
                        parameters.add("EDGES");
                        i++;
                        continue;
                    }
                    errorMsg = "All vertices or all edges?";
                    return true;
                }
                if (dialogState == FILTER_WHAT){
                    if (i == inputWords.length){
                        errorMsg = "Specify filter conditions. Start with \"where\". Enter \"help\" for help.\n";
                        return true;
                    }
                    if (inputWords[i].equals("WHERE")|| inputWords[i].equals("ST")){
                        dialogState = FILTER_WHAT_WHERE;
                        i++;
                        continue;
                    }
                    if (inputWords[i].equals("such")){
                        dialogState = FILTER_WHAT_SUCH;
                        i++;
                        continue;
                    }
                }
                if (dialogState == FILTER_WHAT_SUCH){
                    if (i == inputWords.length){
                        errorMsg = "You probably mean \"such that\". Enter \"that\" or abort.\n";
                        return true;
                    }
                    if (inputWords[i].equals("THAT")){
                        dialogState = FILTER_WHAT_WHERE;
                        i++;
                        continue;
                    }
                    errorMsg = "You probably mean \"such that\". Enter \"that\" or abort.\n";
                    return true;
                }
                if (dialogState == FILTER_WHAT_WHERE){
                    if (i == inputWords.length){
                        errorMsg = "Specify filter conditions. Enter \"help\" for help.";
                        return true;
                    }
                    if (inputWords[i].equals("NO")){
                        dialogState = FILTER_WHAT_WHERE_NO;
                        parameters.add("NOCONDITON");
                        i++;
                        continue;
                    }
                    if (inputWords[i].equals("FILL")){
                        dialogState = FILTER_WHAT_WHERE_FILL;
                        parameters.add("FILL");
                        i++;
                        continue;
                    }
                    if (inputWords[i].equals("STROKE")){
                        dialogState = FILTER_WHAT_WHERE_STROKE;
                        parameters.add("STROKE");
                        i++;
                        continue;
                    }
                    if (inputWords[i].equals("COLOR")){
                        dialogState = FILTER_WHAT_WHERE_PARAM;
                        parameters.add("COLOR");
                        i++;
                        continue;
                    }
                    if (inputWords[i].equals("THICKNESS")){
                        dialogState = FILTER_WHAT_WHERE_PARAM;
                        parameters.add("THICKNESS");
                        i++;
                        continue;
                    }
                    if (inputWords[i].equals("WIDTH")){
                        dialogState = FILTER_WHAT_WHERE_PARAM;
                        parameters.add("WIDTH");
                        i++;
                        continue;
                    }
                    if (inputWords[i].equals("HEIGHT")){
                        dialogState = FILTER_WHAT_WHERE_PARAM;
                        parameters.add("HEIGHT");
                        i++;
                        continue;
                    }
                    if (inputWords[i].equals("SIZE")){
                        dialogState = FILTER_WHAT_WHERE_PARAM;
                        parameters.add("SIZE");
                        i++;
                        continue;
                    }
                    if (inputWords[i].equals("ID")){
                        dialogState = FILTER_WHAT_WHERE_PARAM;
                        parameters.add("ID");
                        i++;
                        continue;
                    }
                    if (inputWords[i].equals("SHAPE")){
                        dialogState = FILTER_WHAT_WHERE_PARAM;
                        parameters.add("SHAPE");
                        i++;
                        continue;
                    }
                    if (inputWords[i].equals("WEIGHT")){
                        dialogState = FILTER_WHAT_WHERE_PARAM;
                        parameters.add("WEIGHT");
                        i++;
                        continue;
                    }
                    if (inputWords[i].equals("TYPE")){
                        dialogState = FILTER_WHAT_WHERE_PARAM;
                        parameters.add("TYPE");
                        i++;
                        continue;
                    }
                    if (inputWords[i].equals("EDGE")){
                        dialogState = FILTER_WHAT_WHERE_EDGE;
                        parameters.add("EDGE");
                        i++;
                        continue;
                    }
                    if (inputWords[i].equals("EDGETYPE")){
                        dialogState = FILTER_WHAT_WHERE_PARAM;
                        parameters.add("EDGETYPE");
                        i++;
                        continue;
                    }
                    if (inputWords[i].equals("DEGREE")){
                        dialogState = FILTER_WHAT_WHERE_PARAM;
                        parameters.add("DEGREE");
                        i++;
                        continue;
                    }
                    if (inputWords[i].equals("INDEGREE")){
                        dialogState = FILTER_WHAT_WHERE_PARAM;
                        parameters.add("INDEGREE");
                        i++;
                        continue;
                    }
                    if (inputWords[i].equals("OUTDEGREE")){
                        dialogState = FILTER_WHAT_WHERE_PARAM;
                        parameters.add("OUTDEGREE");
                        i++;
                        continue;
                    }
                    if (inputWords[i].equals("HAS")){
                        dialogState = FILTER_WHAT_WHERE_HAS;
                        i++;
                        continue;
                    }
                    if (inputWords[i].equals("DIRECTED")){
                        dialogState = FILTER_WHAT_WHERE_PARAM;
                        parameters.add("DIRECTED");
                        i++;
                        continue;
                    }
                    errorMsg = "Mistyped? Add conditions. Enter \"help\" for help.\n";
                    return true;
                }
                if (dialogState == FILTER_WHAT_WHERE_NO){
                    if (i == inputWords.length){
                        dialogState = FILTER_WHAT_WHERE_COND;
                        errorMsg = "Specify where to save the result. Start with \"to\", then give a list id.\n";
                        return true;
                    }
                    if (inputWords[i].equals("CONDITION")){
                        dialogState = FILTER_WHAT_WHERE_COND;;
                        i++;
                        continue;
                    }
                    else if (inputWords[i].equals("TO")){
                        dialogState = FILTER_WHAT_WHERE_COND_TO;
                        i++;
                        continue;
                    }
                    else{
                        dialogState = FILTER_WHAT_WHERE_COND;
                        errorMsg = "Specify where to save the result. Start with \"to\", then give a list id.\n";
                        return true;
                    }
                }
                if (dialogState == FILTER_WHAT_WHERE_FILL){
                    if (i == inputWords.length){
                        dialogState = FILTER_WHAT_WHERE_PARAM;
                        errorMsg = "Specify the fill color or abort.\n";
                        return true;
                    }
                    if (inputWords[i].equals("COLOR")){
                        dialogState = FILTER_WHAT_WHERE_PARAM;
                        i++;
                        continue;
                    }
                    if (isColorValue(inputWords[i])){
                        dialogState = FILTER_WHAT_WHERE_COND;
                        parameters.add(inputWords[i]);
                        i++;
                        continue;
                    }
                }
                if (dialogState == FILTER_WHAT_WHERE_STROKE){
                    if (i == inputWords.length){
                        dialogState = FILTER_WHAT_WHERE_PARAM;
                        errorMsg = "Specify the stroke color or abort.\n";
                        return true;
                    }
                    if (inputWords[i].equals("COLOR")){
                        dialogState = FILTER_WHAT_WHERE_PARAM;
                        i++;
                        continue;
                    }
                    if (isColorValue(inputWords[i])){
                        dialogState = FILTER_WHAT_WHERE_COND;
                        parameters.add(inputWords[i]);
                        i++;
                        continue;
                    }
                }
                if (dialogState == FILTER_WHAT_WHERE_EDGE){
                    if (i == inputWords.length){
                        dialogState = FILTER_WHAT_WHERE_PARAM;
                        errorMsg = "Specify the edge type or abort.\n";
                        return true;
                    }
                    if (inputWords[i].equals("TYPE")){
                        dialogState = FILTER_WHAT_WHERE_PARAM;
                        i++;
                        continue;
                    }
                    if (isColorValue(inputWords[i])){
                        dialogState = FILTER_WHAT_WHERE_COND;
                        parameters.add(inputWords[i]);
                        i++;
                        continue;
                    }
                }
                if (dialogState == FILTER_WHAT_WHERE_HAS){
                    if (i == inputWords.length){
                        errorMsg = "Has what? Specify property or abort.\n";
                        return true;
                    }
                    if (inputWords[i].equals("SELFLOOP")){
                        dialogState = FILTER_WHAT_WHERE_COND;
                        parameters.add("selfloop");
                        i++;
                        continue;
                    }
                    if (inputWords[i].equals("LABEL")){
                        dialogState = FILTER_WHAT_WHERE_COND;
                        parameters.add("label");
                        i++;
                        continue;
                    }
                    errorMsg = "Possible properties to have are: selfloop, label. Specify property or abort.\n";
                    return true;
                }
                if (dialogState == FILTER_WHAT_WHERE_COND){
                    if (i == inputWords.length){
                        errorMsg = "Specify where to save the result. Start with \"to\", then give a list id.\n";
                        return true;
                    }
                    if (inputWords[i].equals("TO")){
                        dialogState = FILTER_WHAT_WHERE_COND_TO;
                        i++;
                        continue;
                    }
                    errorMsg = "Specify where to save the result. Start with \"to\", then give a list id.\n";
                    return true;
                }
                if (dialogState == FILTER_WHAT_WHERE_COND_TO){
                    if (i==inputWords.length) {
                        errorMsg = "Specify where to save the result or abort.\n";
                        return true;
                    }
                    if (hasIdForm(inputWords[i])){
                        dialogState = DONE;
                        parameters.add(inputWords[i]);
                        dialogAction = DialogAction.FILTER;
                        return true;
                    }
                    errorMsg = "Specify where to save the result. Format: (_|[a-Z])(_|[a-Z]|[0-9])*.\n";
                    return true;
                }

            }
        } else {// TODO: make an exception
            System.out.println("Something went wrong: Console got an empty string as input.");
        }
        return true;
    }





}