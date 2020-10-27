/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

package gralog.dialog;

import gralog.rendering.GralogColor;
import gralog.rendering.shapes.RenderingShape;
import gralog.structure.Edge;

import java.util.ArrayList;

import static gralog.dialog.ActionType.FX;

import static gralog.dialog.DialogAction.HELP;
import static gralog.dialog.DialogAction.NONE;
import static gralog.dialog.DialogAction.FIND_GRAPH_ELEMENT;
import static gralog.dialog.DialogAction.SELECT_LIST;
import static gralog.dialog.DialogAction.DESELECT_LIST;
import static gralog.dialog.DialogAction.SELECT_ALL_VERTICES;
import static gralog.dialog.DialogAction.DESELECT_ALL_VERTICES;
import static gralog.dialog.DialogAction.SELECT_ALL_EDGES;
import static gralog.dialog.DialogAction.DESELECT_ALL_EDGES;
import static gralog.dialog.DialogState.*;


public class DialogParser {

    public static final String ANSI_GREEN = "\u001B[32m"; // for debugging
    public static final String ANSI_RESET = "\u001B[0m";
    private static String[] forbiddenIds = {"ALL", "SELECT", "SELECTED", "FILTER", "WHERE", "ST", "SUCH", "THAT",
        "TO", "VERTICES", "EDGES", "DELETE", "REMOVE", "FILL", "COLOR", "STROKE", "THICKNESS", "WIDTH", "HEIGHT",
        "SIZE", "ID", "SHAPE", "WEIGHT", "TYPE", "EDGETYPE", "DEGREE", "INDEGREE", "OUTDEGREE", "BUTTERFLY",
        "HAS", "HASNT", "SELFLOOP", "DIRECTED",
        "UNION", "INTERSECTION", "DIFFERENCE", "SYMMETRIC", "COMPLEMENT",
        "CONNECT",
        "ADD", "CONTRACT", "EDGE",
        "GENERATE", "WHEEL", "GRID", "CLIQUE", "CYCLE", "PATH", "TORUS", "COMPLETE", "TREE", "CYLINDRICAL",
        "NO", "CONDITION", "LABEL", "X", "Q", "EXIT", "ABORT", "CANCEL",
        "SORT", "LEFTRIGHT", "RIGHTLEFT", "TOPDOWN", "BOTTOMUP",
        "CONNECT",
        "PRINTALL"}; // TODO: add missing
    private int i; // running variable
    private ActionType actionType;
    private DialogAction dialogAction;
    private ArrayList<String> parameters;
    private String errorMsg;
    private DialogState dialogState;

    public DialogParser() {
        dialogAction = NONE;
        this.dialogState = DONE;
        parameters = new ArrayList<String>();
        errorMsg = "";
    }

    private static boolean isShape(String s) {
        for (PossibleShapes ps : PossibleShapes.values())
            if (ps.name().equalsIgnoreCase(s))
                return true;
        return false;
    }


    /*      CONSTRUCTOR      SETA     GETA        */

    public void addParameter(String parameter) {
        parameters.add(parameter);
    }

    public void clearParameters() {
        this.parameters.clear();
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String s) {
        errorMsg = s;
    }

    public ActionType getType() {
        return actionType;
    }

    public DialogAction getDialogAction() {
        return dialogAction;
    }

    public void setDialogAction(DialogAction dialogAction) {
        this.dialogAction = dialogAction;
    } // maybe make dialogState public

    public ArrayList<String> getParameters() {
        return parameters;
    }

    public DialogState getDialogState() {
        return dialogState;
    }

    public void setDialogState(DialogState dialogState) {
        this.dialogState = dialogState;
    } // maybe make dialogState public

    /*   CHECKING FORM    */

    private boolean hasValueForm(String s) {
        // color or number or "PLAIN" or "DOTTED" or "DASHED"
        return (isColorValue(s) || s.matches("-?\\d*((\\.|,)\\d+)?")); // ... or is digit
    }

    private boolean isInt(String s) {
        return s.matches("\\d+");
    }

    private boolean isFloat(String s) {
        return s.matches("-?\\d*((\\.|,)\\d+)?");
    }

    private boolean hasIdForm(String idCandidate) {
        String idCandidateUC = idCandidate.toUpperCase();
        for (String s : forbiddenIds)
            if (s.equals(idCandidateUC))
                return false;
        return (idCandidateUC.matches("(_|[A-Z])([A-Z]|[0-9]|_)*"));
    }

    private boolean isColorValue(String colorValueCandidate) {
        return (GralogColor.isColor(colorValueCandidate));
    }

    /*         TRANSITIONS         */

    private void transition(DialogState dialogState) {
        this.dialogState = dialogState;
        i++;
    }
    private void transition(DialogState dialogState, String addedParameter) {
        this.dialogState = dialogState;
        parameters.add(addedParameter);
        i++;
    }

    private void transition(DialogState dialogState, DialogAction dialogAction, String addedParameter) {
        this.dialogState = dialogState;
        parameters.add(addedParameter);
        this.dialogAction = dialogAction;
        i++;
    }

    private void transition(DialogState dialogState, DialogAction dialogAction) {
        this.dialogState = dialogState;
        this.dialogAction = dialogAction;
        i++;
    }

    private void transition(DialogState dialogState, DialogAction dialogAction,
                            String addedParameter, String errorMsg) {
        this.dialogState = dialogState;
        parameters.add(addedParameter);
        this.dialogAction = dialogAction;
        this.errorMsg = errorMsg;
        i++;
    }

    private void transitionErr(DialogState dialogState, String errorMsg) {
        this.dialogState = dialogState;
        this.errorMsg = errorMsg;
    }

    /*          PARSE                */


    public void parse(String text) {
        dialogAction = NONE;
        errorMsg = "";
        if (text.isEmpty()) { // TODO: make an exception
            System.err.println("Something went wrong: Console got an empty string as input.");
            return;
        }
        // trim
        // upper case
        // wtite space around <, >, =,
        // delete multiple spaces
        String tmpText = text.trim()
                .replaceAll("<", " < ")
                .replaceAll(">", " > ")
                .replaceAll("=", " =")
                .replaceAll(" +", " ");
        // delete all spaces in the formula after =
        String tmpText2;
        if (tmpText.contains("=")) {
            tmpText2 = tmpText.substring(0, tmpText.indexOf('=') + 1).toUpperCase()
                    + tmpText.substring(tmpText.indexOf('=') + 1).replaceAll(" ", "");

        }
        else {
            tmpText2 = tmpText.toUpperCase();
        }
        String[] inputWords = tmpText2.split(" ");

        // special case: if text contains "label", then the next word should not be capitalised. restore it in this case
        for (int i = 0; i < inputWords.length; i++){
            if (inputWords[i].equals("LABEL")
                    && i < inputWords.length-1){ // not the last word
                String[] againInputWords = text.split(" ");
                inputWords[i+1] = againInputWords[i+1];
            }
        }

        if (inputWords[0].equals("Q")
                || inputWords[0].equals("EXIT")
                || inputWords[0].equals("ABORT")
                || inputWords[0].equals("X")
                || inputWords[0].equals("CANCEL")
        ) {
            this.dialogState = DONE;
            dialogAction = NONE;
            parameters.clear();
            return;
        }

        i = 0;
        while (i <= inputWords.length) {
            if (this.dialogState == DONE) {
                if (i == inputWords.length)
                    return;
                switch (inputWords[i]) {
                    case "H":
                    case "HELP":
                    case "?":
                        // this.dialogState = DONE;
                        dialogAction = HELP;
                        parameters.clear();
                        return;
                    case "SELECT":
                        transition(SELECT);
                        break;
                    case "DESELECT":
                        transition(DESELECT);
                        break;
                    case "FILTER":
                        transition(FILTER);
                        break;
                    case "SORT":
                        transition(DialogState.SORT);
                        break;
                    case "UNION":
                        transition(DialogState.TWOLISTSOP, "UNION");
                        break;
                    case "INTERSECTION":
                        transition(DialogState.TWOLISTSOP, "INTERSECTION");
                        break;
                    case "DIFFERENCE":
                        transition(DialogState.TWOLISTSOP, "DIFFERENCE");
                        break;
                    case "SYMMETRIC":
                        transition(DialogState.TWOLISTSOP, "SYMMETRIC");
                        break;
                    case "DELETE":
                        transition(DialogState.DELETE);
                        break;
                    case "REMOVE":
                        transition(DialogState.REMOVE);
                        break;
                    case "COMPLEMENT":
                        transition(DialogState.COMPLEMENT);
                        break;
                    case "CONNECT":
                        transition(DialogState.CONNECT);
                        break;
                    case "PRINT":
                        transition(DialogState.PRINT);
                        break;
                    case "FIND":
                        transition(DialogState.FIND);
                        break;
                    case "LAYOUT":
                        transition(DialogState.LAYOUT);
                        break;
                    case "SET":
                        transition(DialogState.SET);
                        break;
                    default:
                        errorMsg = "Parse error. Please, try again. (Quit: Q)";
                        return;
                }
            }

            if (this.dialogState == DialogState.SET) {
                if (i == inputWords.length) {
                    errorMsg = "Choose a list id.";
                    return;
                }
                if (hasIdForm(inputWords[i])){
                    transition(DialogState.SET_WHAT, inputWords[i]);
                    continue;
                }
                errorMsg = "Choose a list id.";
                return;

            }

            if (this.dialogState == DialogState.SET_WHAT) {
                if (i == inputWords.length) {
                    errorMsg = "Choose property to set. Format: "
                            + "DIRECTED | UNDIRECTED | COLOR | LABEL | TYPE | EDGETYPE | THICKNESS | WEIGHT | FILL [COLOR] | STROKE [COLOR] | SHAPE | WIDTH | HEIGHT";
                    return;
                }
                if (inputWords[i].equals("DIRECTED")){
                    transition(DONE, DialogAction.SET_DIRECTED);
                    return;
                }
                if (inputWords[i].equals("UNDIRECTED")){
                    transition(DONE, DialogAction.SET_UNDIRECTED);
                    return;
                }

                if (inputWords[i].equals("COLOR")){
                    transition(DialogState.SET_WHAT_COLOR);
                    continue;
                }
                if (inputWords[i].equals("LABEL")){
                    transition(DialogState.SET_WHAT_LABEL);
                    continue;
                }
                if (inputWords[i].equals("TYPE")){
                    transition(DialogState.SET_WHAT_TYPE);
                    continue;
                }
                if (inputWords[i].equals("EDGETYPE")){
                    transition(DialogState.SET_WHAT_EDGETYPE);
                    continue;
                }
                if (inputWords[i].equals("THICKNESS")){
                    transition(DialogState.SET_WHAT_THICKNESS);
                    continue;
                }
                if (inputWords[i].equals("WEIGHT")){
                    transition(DialogState.SET_WHAT_WEIGHT);
                    continue;
                }
                if (inputWords[i].equals("FILL")){
                    transition(DialogState.SET_WHAT_FILL);
                    continue;
                }
                if (inputWords[i].equals("STROKE")){
                    transition(DialogState.SET_WHAT_STROKE);
                    continue;
                }
                if (inputWords[i].equals("SHAPE")){
                    transition(DialogState.SET_WHAT_SHAPE);
                    continue;
                }
                if (inputWords[i].equals("WIDTH")){
                    transition(DialogState.SET_WHAT_WIDTH);
                    continue;
                }
                if (inputWords[i].equals("HEIGHT")){
                    transition(DialogState.SET_WHAT_HEIGHT);
                    continue;
                }

                errorMsg = "Choose property to set. Format: "
                        + "DIRECTED | UNDIRECTED | COLOR | LABEL | TYPE | EDGETYPE | THICKNESS | WEIGHT | FILL [COLOR] | STROKE [COLOR] | SHAPE | WIDTH | HEIGHT";
                return;
            }

            if (this.dialogState == DialogState.SET_WHAT_COLOR) {
                if (i == inputWords.length) {
                    errorMsg = "Choose a color. Available colors:" + GralogColor.availableColors();
                    return;
                }
                if (isColorValue(inputWords[i])) {
                    transition(DONE, DialogAction.SET_COLOR, inputWords[i]);
                    return;
                }
                errorMsg = "Choose a color. Available colors: " + GralogColor.availableColors();
                return;
            }

            if (this.dialogState == DialogState.SET_WHAT_LABEL) {
                if (i == inputWords.length) {
                    errorMsg = "Choose a label.";
                    return;
                }
                transition(DONE, DialogAction.SET_LABEL, inputWords[i]);
                return;
            }
            if (this.dialogState == DialogState.SET_WHAT_TYPE) {
                if (i == inputWords.length) {
                    errorMsg = "Choose a type: PLAIN, DOTTED or DASHED.";
                    return;
                }
                if (inputWords[i].equals("PLAIN") || inputWords[i].equals("DOTTED") || inputWords[i].equals("DASHED")) {
                    transition(DONE, DialogAction.SET_TYPE, inputWords[i]);
                    return;
                }
                errorMsg = "Choose a type: PLAIN, DOTTED or DASHED.";
                return;
            }
            if (this.dialogState == DialogState.SET_WHAT_EDGETYPE) {
                if (i == inputWords.length) {
                    errorMsg = "Choose a type: BEZIER or SHARP.";
                    return;
                }
                if (inputWords[i].equals("BEZIER") || inputWords[i].equals("SHARP")) {
                    transition(DONE, DialogAction.SET_EDGETYPE, inputWords[i]);
                    return;
                }
                errorMsg = "Choose a type: BEZIER or SHARP.";
                return;
            }

            if (this.dialogState == DialogState.SET_WHAT_THICKNESS) {
                if (i == inputWords.length) {
                    errorMsg = "What should the thickness be? (Enter a real number.)";
                    return;
                }
                if (isFloat(inputWords[i])) {
                    transition(DONE, DialogAction.SET_THICKNESS, inputWords[i]);
                    return;
                }
                errorMsg = "What should the thickness be? (Enter a real number.)";
                return;
            }

            if (this.dialogState == DialogState.SET_WHAT_WEIGHT) {
                if (i == inputWords.length) {
                    errorMsg = "What should the weight be? (Enter a real number.)";
                    return;
                }
                if (isFloat(inputWords[i])) {
                    transition(DONE, DialogAction.SET_WEIGHT, inputWords[i]);
                    return;
                }
                errorMsg = "What should the weight be? (Enter a real number.)";
                return;
            }

            if (this.dialogState == DialogState.SET_WHAT_FILL) {
                if (i == inputWords.length) {
                    errorMsg = "Choose a fill color. (E.g., Red, Blue,...)";
                    return;
                }
                if (inputWords[i].equals("COLOR")) {
                    transition(SET_WHAT_FILL); // swallow "COLOR", go to the same state
                    continue;
                }
                if (isColorValue(inputWords[i])){
                    transition(DONE, DialogAction.SET_FILL, inputWords[i]);
                    return;
                }
                errorMsg = "Choose a fill color. (E.g., Red, Blue,...)";
                return;
            }

            if (this.dialogState == DialogState.SET_WHAT_STROKE) {
                if (i == inputWords.length) {
                    errorMsg = "Choose a stroke color. (E.g., Red, Blue,...)";
                    return;
                }
                if (inputWords[i].equals("COLOR")) {
                    transition(SET_WHAT_STROKE); // swallow "COLOR", go to the same state
                    continue;
                }
                if (isColorValue(inputWords[i])){
                    transition(DONE, DialogAction.SET_STROKE, inputWords[i]);
                    return;
                }
                errorMsg = "Choose a fill color. (E.g., Red, Blue,...)";
                return;
            }

            if (this.dialogState == DialogState.SET_WHAT_COLOR){
                if (i == inputWords.length){
                    errorMsg = "Choose a color. (E.g., Red, Blue,...)";
                    return;
                }
                if (isColorValue(inputWords[i])){
                    transition(DONE, DialogAction.SET_COLOR, inputWords[i]);
                    return;
                }
                errorMsg = "Choose a color. (E.g., Red, Blue,...)";
                return;
            }

            if (this.dialogState == DialogState.SET_WHAT_SHAPE) {
                if (i == inputWords.length) {
                    errorMsg = "Choose a shape: CYCLE | ELLIPSE | DIAMOND | RECTANGLE";
                    return;
                }
                if (isShape(inputWords[i])) {
                    transition(DONE, DialogAction.SET_SHAPE, inputWords[i]);
                    return;
                }
                errorMsg = "Choose a shape: CYCLE | ELLIPSE | DIAMOND | RECTANGLE";
                return;
            }

            if (this.dialogState == DialogState.SET_WHAT_WIDTH) {
                if (i == inputWords.length) {
                    errorMsg = "What should be the width? (Enter a real number.)";
                    return;
                }
                if (isFloat(inputWords[i])) {
                    transition(DONE, DialogAction.SET_WIDTH, inputWords[i]);
                    return;
                }
                errorMsg = "What should be the width? (Enter a real number.)";
                return;
            }

            if (this.dialogState == DialogState.SET_WHAT_HEIGHT) {
                if (i == inputWords.length) {
                    errorMsg = "What should be the height? (Enter a real number.)";
                    return;
                }
                if (isFloat(inputWords[i])) {
                    transition(DONE, DialogAction.SET_HEIGHT, inputWords[i]);
                    return;
                }
                errorMsg = "What should be the height? (Enter a real number.)";
                return;
            }

            if (this.dialogState == DialogState.LAYOUT) {
                if (i == inputWords.length) {
                    errorMsg = "Choose a layout. Format: "
                            + "CIRCLE|STAR|GRID|GRAPHOPT|FRUCHTERMAN_REINGOLD|KAMADA_KAWAI|"
                            + "MDS|LGL|REINGOLD_TILFORD|REINGOLD_TILFORD_CIRCULAR.";
                    return;
                }
                if (inputWords[i].equals("CIRCLE")
                        || inputWords[i].equals("STAR")
                        || inputWords[i].equals("GRID")
                        || inputWords[i].equals("GRAPHOPT")
                        || inputWords[i].equals("FRUCHTERMAN_REINGOLD")
                        || inputWords[i].equals("KAMADA_KAWAI")
                        || inputWords[i].equals("MDS")
                        || inputWords[i].equals("LGL")
                        || inputWords[i].equals("REINGOLD_TILFORD_CIRCULAR")
                ) {
                    transition(DONE, DialogAction.LAYOUT, inputWords[i]);
                    return;
                }
                errorMsg = "Choose a layout. Format: CIRCLE|STAR|GRID|GRAPHOPT|FRUCHTERMAN_REINGOLD|KAMADA_KAWAI|MDS|"
                        + "LGL|REINGOLD_TILFORD|REINGOLD_TILFORD_CIRCULAR.";
                return;
            }


            if (this.dialogState == DialogState.FIND) {
                if (i == inputWords.length) {
                    errorMsg = "Say what to find.";
                    return;
                }
                if (hasIdForm(inputWords[i])) {
                    transition(DONE, DialogAction.FIND_LIST, inputWords[i]);
                    return;
                }
                if (inputWords[i].equals("VERTEX")) {
                    transition(DialogState.FIND_VERTEX);
                    continue;
                }
                if (inputWords[i].equals("EDGE")) {
                    transition(DialogState.FIND_EDGE);
                    continue;
                }
                if (isInt(inputWords[i])) {
                    transition(DONE, FIND_GRAPH_ELEMENT, inputWords[i]);
                    return;
                }
                errorMsg = "Say what to find.";
                return;
            }
            if (this.dialogState == DialogState.FIND_VERTEX) {
                if (i == inputWords.length) {
                    errorMsg = "Specify vertex id.";
                    return;
                }
                if (isInt(inputWords[i])) {
                    transition(DONE, DialogAction.FIND_VERTEX, inputWords[i]);
                    return;
                }
                errorMsg = "Could not parse: " + inputWords[i] + ".";
                return;
            }
            if (this.dialogState == DialogState.FIND_EDGE) {
                if (i == inputWords.length) {
                    errorMsg = "Specify edge id.";
                    return;
                }
                if (isInt(inputWords[i])) {
                    transition(DONE, DialogAction.FIND_EDGE, inputWords[i]);
                    return;
                }
                errorMsg = "Could not parse: " + inputWords[i] + ".";
                return;
            }
            if (this.dialogState == DialogState.CONNECT) {
                if (i == inputWords.length) {
                    errorMsg = "Specify what to connect. Format: CONNECT <list> <list> =<formula>|BICLIQUE|MATCHING or "
                            + "CONNECT <list> =<formula>|PATH|CYCLE|CLIQUE";
                    return;
                }
                if (hasIdForm(inputWords[i])) {
                    transition(CONNECT_WHAT, inputWords[i]);
                    continue;
                } else {
                    errorMsg = "Specify what to connect. Format: CONNECT <list> <list> =<formula>|BICLIQUE|MATCHING or "
                            + "CONNECT <list> =<formula>|PATH|CYCLE|CLIQUE";
                    return;
                }
            }
            if (this.dialogState == DialogState.CONNECT_WHAT) {
                if (i == inputWords.length) {
                    transition(DONE, DialogAction.CONNECT_CLIQUE);
                    return;
                }
                if (hasIdForm(inputWords[i])) {
                    transition(CONNECT_WHAT_WHAT, inputWords[i]);
                    continue;
                }
                if (inputWords[i].substring(0, 1).equals("=")) { // first character is =, a formula
                    transition(DONE, DialogAction.CONNECT_FORMULA, inputWords[i].substring(1)); // everything but =
                    return;
                }
                if (inputWords[i].equals("PATH")) {
                    transition(DONE, DialogAction.CONNECT_PATH);
                    return;
                }
                if (inputWords[i].equals("CYCLE")) {
                    transition(DONE, DialogAction.CONNECT_CYCLE);
                    return;
                }
                if (inputWords[i].equals("CLIQUE")) {
                    transition(DONE, DialogAction.CONNECT_CLIQUE);
                    return;
                }
                if (inputWords[i].equals("TCLOSURE")) {
                    transition(DONE, DialogAction.CONNECT_TCLOSURE);
                    return;
                }

                if (inputWords[i].equals("SELFLOOP")) {
                    transition(DONE, DialogAction.CONNECT_SELFLOOP);
                    return;
                }
                if (inputWords[i].substring(0, 1).equals("=")) { // first character is =, a formula
                    transition(DONE, DialogAction.CONNECT_FORMULA, inputWords[i].substring(1)); // everything but =
                    return;
                }

                errorMsg = "Could not parse " + inputWords[i] + ".";
                return;
            }
            if (this.dialogState == CONNECT_WHAT_WHAT) {
                if (i == inputWords.length) {
                    errorMsg = "Specify how to connect the lists. Format: BICLIQUE|MATCHING|=<formula>.";
                    return;
                }
                if (inputWords[i].equals("BICLIQUE")) {
                    transition(DONE, DialogAction.CONNECT_BICLIQUE);
                    return;
                }
                if (inputWords[i].equals("MATCHING")) {
                    transition(DONE, DialogAction.CONNECT_MATCHING);
                    return;
                }
                if (inputWords[i].substring(0, 1).equals("=")) { // first character is =, a formula
                    transition(DONE, DialogAction.CONNECT_2_LISTS_FORMULA, inputWords[i].substring(1)); // everyth but =
                    return;
                }
                errorMsg = "Specify how to connect the lists. Format: BICLIQUE|MATCHING|=<formula>.";
                return;
            }


            /*   PRINT  */

            if (this.dialogState == DialogState.PRINT) {
                if (i == inputWords.length) {
                    transition(DONE, DialogAction.PRINT, "PRINTALL");
                    return;
                }
                if (hasIdForm(inputWords[i])) {
                    transition(DONE, DialogAction.PRINT, inputWords[i]);
                    return;
                } else {
                    errorMsg = inputWords[i] + " cannot be a list name.";
                    return;
                }
            }

            /* ONE SET OPERATIONS  */

            if (this.dialogState == DialogState.DELETE) {
                if (i == inputWords.length) {
                    errorMsg = "Specify an exsisting list to be deleted. (Quit: Q).";
                    return;
                }
                if (hasIdForm(inputWords[i])) {
                    transition(DONE, DialogAction.DELETE, inputWords[i]);
                    return;
                } else {
                    errorMsg = "Specify an exsisting list to be deleted. (Quit: Q).";
                    return;
                }
            }
            if (this.dialogState == DialogState.REMOVE) {
                if (i == inputWords.length) {
                    errorMsg = "Specify an exsisting list to remove elements from. (Quit: Q).";
                    return;
                }
                if (hasIdForm(inputWords[i])) {
                    transition(DONE, DialogAction.REMOVE, inputWords[i]);
                    return;
                } else {
                    errorMsg = "Specify an exsisting list to remove elements from. (Quit: Q).";
                    return;
                }
            }

            if (this.dialogState == DialogState.COMPLEMENT) {
                if (i == inputWords.length) {
                    errorMsg = "Specify source and target lists. (Quit: Q)";
                    return;
                }
                if (hasIdForm(inputWords[i])) {
                    transition(COMPLEMENT_WHAT, inputWords[i]);
                    continue;
                } else {
                    errorMsg = "I could not parse the source name " + inputWords[i] + ". Specify source and target " +
                            "lists. (Quit: Q)";
                    return;
                }
            }
            if (this.dialogState == COMPLEMENT_WHAT) {
                if (i == inputWords.length) {
                    errorMsg = "Specify the target list. (Quit: Q)";
                    return;
                }
                if (inputWords[i].equals("TO")) { // swallow "TO"
                    transition(COMPLEMENT_WHAT);
                    continue;
                }
                if (hasIdForm(inputWords[i])) {
                    transition(DONE, DialogAction.COMPLEMENT, inputWords[i]);
                    return;
                } else {
                    errorMsg = inputWords[i] + "cannot be a list name. Specify target list. (Quit: Q)";
                    return;
                }
            }

            /*     TWO SETS OPERATIONS*/

            if (this.dialogState == DialogState.TWOLISTSOP) {
                if (i == inputWords.length) {
                    errorMsg = "Choose two existing lists. (Quit: Q)";
                    return;
                }
                if (hasIdForm(inputWords[i])) {
                    transition(TWOLISTSOP_WHAT, inputWords[i]);
                    continue;
                } else {
                    errorMsg = "Choose two existing lists. (Quit: Q)";
                    return;
                }
            }
            if (this.dialogState == DialogState.TWOLISTSOP_WHAT) {
                if (i == inputWords.length) {
                    errorMsg = "Specify the second list. (Quit: Q)";
                    return;
                }
                if (hasIdForm(inputWords[i])) {
                    transition(TWOLISTSOP_WHAT_WHAT, inputWords[i]);
                    continue;
                } else {
                    errorMsg = inputWords[i] + " is not a list name. (Quit: Q)";
                    return;
                }
            }
            if (this.dialogState == DialogState.TWOLISTSOP_WHAT_WHAT) {
                if (i == inputWords.length) {
                    errorMsg = "Specify the target list. (Quit: Q)";
                    return;
                }
                if (hasIdForm(inputWords[i])) {
                    transition(DONE, DialogAction.TWO_LISTS_OP, inputWords[i]);
                    return;
                }
                errorMsg = inputWords[i] + " cannot be a list name. Specify the target list. (Quit: Q)";
                // todo: help for reserved words, format of identifiers (with examples)
                return;
            }

            /*    SORT    */

            if (this.dialogState == DialogState.SORT) {
                if (i == inputWords.length) {
                    errorMsg = "What to sort? Format: <list> [LEFTRIGHT|RIGHTLEFT|TOPDOWN|BOTTOMUP|ID "
                            + "[ASC|DESC]|LABEL [ASC|DESC]] (Quit: Q)";
                    return;
                }
                if (hasIdForm(inputWords[i])) {
                    transition(SORT_WHAT, inputWords[i]);
                    continue;
                } else {
                    errorMsg = "What to sort? Format: <list> [LEFTRIGHT|RIGHTLEFT|TOPDOWN|BOTTOMUP|ID [ASC|DESC]|LABEL "
                            + "[ASC|DESC]] (Quit: Q)";
                    return;
                }
            }
            if (this.dialogState == SORT_WHAT) {
                if (i == inputWords.length) {
                    errorMsg = "Please choose how to sort: [LEFTRIGHT|RIGHTLEFT|TOPDOWN|BOTTOMUP|ID [ASC|DESC]|LABEL "
                            + "[ASC|DESC]] (Quit: Q)";
                    return;
                }
                if (inputWords[i].equals("LEFTRIGHT")) {
                    transition(DONE, DialogAction.SORT, "LEFTRIGHT");
                    return;
                }
                if (inputWords[i].equals("RIGHTLEFT")) {
                    transition(DONE, DialogAction.SORT, "RIGHTLEFT");
                    return;
                }
                if (inputWords[i].equals("TOPDOWN")) {
                    transition(DONE, DialogAction.SORT, "TOPDOWN");
                    return;
                }
                if (inputWords[i].equals("BOTTOMUP")) {
                    transition(DONE, DialogAction.SORT, "BOTTOMUP");
                    return;
                }
                if (inputWords[i].equals("ID")) {
                    transition(SORT_WHAT_ID, "ID");
                    continue;
                }
                if (inputWords[i].equals("LABEL")) {
                    transition(SORT_WHAT_ID, "LABEL"); // SORT_WHAT_LABEL would be the same
                    continue;
                }
                errorMsg = "Please choose how to sort: [LEFTRIGHT|RIGHTLEFT|TOPDOWN|BOTTOMUP" +
                        "|ID [ASC|DESC]|LABEL [ASC|DESC]] (Quit: Q)";
                return;

            }
            if (this.dialogState == SORT_WHAT_ID) {
                if (i == inputWords.length) {
                    errorMsg = "Ascending (ASC) or descending (DESC)?  (Quit: Q)";
                    return;
                }
                if (inputWords[i].equals("ASC")) {
                    transition(DONE, DialogAction.SORT, "ASC");
                    return;
                }
                if (inputWords[i].equals("DESC")) {
                    transition(DONE, DialogAction.SORT, "DESC");
                    return;
                }
                errorMsg = "Ascending (ASC) or descending (DESC)?  (Quit: Q)";
                return;
            }

            /*     SELECT  DESELECT     */

            if (this.dialogState == SELECT) {
                if (i == inputWords.length) {
                    errorMsg = "What to select? Format: (all [vertices|edges]) "
                            + "| <list id> (accepted: SELECT) (Quit: Q)";
                    return;
                }
                if (hasIdForm(inputWords[i])) {
                    if (i == inputWords.length - 1) { // last word
                        transition(DONE, SELECT_LIST, inputWords[i]);
                        actionType = FX;
                        return;
                    } else {           // could not parse: select <id> <trash>
                        // dont change dialogState == SELECT
                        errorMsg = "What to select? Format: (all [vertices|edges]) "
                                + "| <list id> (accepted: SELECT) (Quit: Q)";
                        return;
                    }
                }
                if (inputWords[i].equals("ALL")) {
                    transition(SELECT_ALL);
                    continue;
                }
                errorMsg = "What to select? Format: (all [vertices|edges]) | <list id> (accepted: SELECT) (Quit: Q)";
                return;
            }
            if (this.dialogState == DESELECT) {
                if (i == inputWords.length) {
                    errorMsg = "What to deselect?  Format: (all [vertices|edges]) "
                            + "| <list id> (accepted: DESELECT) (Quit: Q)";
                    return;
                }
                if (hasIdForm(inputWords[i])) {
                    if (i == inputWords.length - 1) { // last word
                        transition(DONE, DESELECT_LIST, inputWords[i]);
                        actionType = FX;
                        return;
                    } else {           // could not parse: deselect <id> <trash>
                        // dont change dialogState == DESELECT
                        errorMsg = "What to deselect? Format: (all [vertices|edges]) "
                                + "| <list id> (accepted: DESELECT) (Quit: Q)";
                        return;
                    }
                }
                if (inputWords[i].equals("ALL")) {
                    transition(DESELECT_ALL);
                    continue;
                }
                errorMsg = "What to deselect? Format: (all [vertices|edges]) "
                        + "| <list id> (accepted: DESELECT) (Quit: Q)";
                return;
            }
            if (this.dialogState == DialogState.SELECT_ALL) {
                if (i == inputWords.length) {
                    transition(DONE, DialogAction.SELECT_ALL);
                    parameters.clear();
                    return;
                }
                if (inputWords[i].equals("VERTICES")) {
                    transition(DONE, DialogAction.SELECT_ALL_VERTICES);
                    parameters.clear();
                    return;
                }
                if (inputWords[i].equals("EDGES")) {
                    transition(DONE, DialogAction.SELECT_ALL_EDGES);
                    parameters.clear();
                    return;
                }
                errorMsg = "Edges or vertices? (accepted: SELECT ALL) (Quit: Q)";
                return;

            }
            if (this.dialogState == DESELECT_ALL) {
                if (i == inputWords.length) {
                    transition(DONE, DialogAction.DESELECT_ALL);
                    parameters.clear();
                    return;
                }
                if (inputWords[i].equals("VERTICES")) {
                    transition(DONE, DialogAction.DESELECT_ALL_VERTICES);
                    parameters.clear();
                    return;
                }
                if (inputWords[i].equals("EDGES")) {
                    transition(DONE, DialogAction.DESELECT_ALL_EDGES);
                    parameters.clear();
                    return;
                }
                errorMsg = "Edges or vertices? (accepted: DESELECT ALL) (Quit: Q)";
                return;
            }


            /*        FILTER        */

            if (this.dialogState == FILTER) {
                if (i == inputWords.length) {
                    errorMsg = "What to filter? Format: <what> where|st|(such that) "
                            + "<parameters> to <list> (accepted: FILTER) (Quit: Q)";
                    return;
                }
                if (inputWords[i].equals("ALL")) {
                    transition(FILTER_ALL, "ALL");
                    continue;
                }
                if (inputWords[i].equals("SELECTED")) {
                    transition(FILTER_SELECTED, "SELECTED");
                    // filter all ... behaves as filter selected ...
                    continue;
                }
                if (hasIdForm(inputWords[i])) {
                    transition(FILTER_WHAT, inputWords[i]);
                    continue;
                } else {
                    errorMsg = "What to filter? Format: <what> where|st|(such that) "
                            + " <parameters> to <list> (accepted: FILTER) (Quit: Q)";
                    return;
                }
            }
            if (this.dialogState == FILTER_SELECTED) {
                if (i == inputWords.length) {
                    errorMsg = "Vertices or edges? (Quit: Q)";
                    return;
                }
                if (inputWords[i].equals("VERTICES")) {
                    transition(FILTER_WHAT, "VERTICES");
                    continue;
                }
                if (inputWords[i].equals("EDGES")) {
                    transition(FILTER_WHAT, "EDGES");
                    continue;
                }
                errorMsg = "Vertices or edges? (accepted: FILTER SELECTED)  (Quit: Q)";
                return;

            }
            if (this.dialogState == FILTER_ALL) {
                if (i == inputWords.length) {
                    errorMsg = "Vertices or edges? (accepted: FILTER ALL) (Quit: Q)";
                    return;
                }
                if (inputWords[i].equals("VERTICES")) {
                    transition(FILTER_WHAT, "VERTICES");
                    continue;
                }
                if (inputWords[i].equals("EDGES")) {
                    transition(FILTER_WHAT, "EDGES");
                    continue;
                }
                if (inputWords[i].equals("SELECTED")) {
                    parameters.clear(); // replace parameters=[["ALL"]] by parameters=[["SELECTED"]]
                    transition(FILTER_SELECTED, "SELECTED");
                    continue;
                }
                errorMsg = "Vertices or edges? (accepted: FILTER ALL) (Quit: Q)";
                return;
            }
            if (this.dialogState == FILTER_WHAT) {
                if (i == inputWords.length) {
                    errorMsg = "Specify filter conditions. Start with \"where\". Enter \"help\" for help. "
                            + "(accepted: FILTER <OBJECT>) (Quit: Q)";
                    return;
                }
                if (inputWords[i].equals("WHERE") || inputWords[i].equals("ST")) {
                    transition(FILTER_WHAT_WHERE);
                    continue;
                }
                if (inputWords[i].equals("SUCH")) {
                    transition(FILTER_WHAT_SUCH);
                    continue;
                }
                if (inputWords[i].equals("TO")) {
                    transition(FILTER_WHAT_WHERE_COND_TO, "NOCONDITION");
                    continue;
                }
                errorMsg = "Specify filter conditions. Start with \"where\". Enter \"help\" for help. "
                        + "(accepted: FILTER <OBJECT>) (Quit: Q)";
                return;
            }
            if (this.dialogState == FILTER_WHAT_SUCH) {
                if (i == inputWords.length) {
                    errorMsg = "You probably mean \"such that\". Enter \"that\" or abort. "
                            + "(accepted: FILTER <OBJECT> WHERE/SUCH THAT) (Quit: Q)";
                    return;
                }
                if (inputWords[i].equals("THAT")) {
                    transition(FILTER_WHAT_WHERE);
                    continue;
                }
                errorMsg = "You probably mean \"such that\". Enter \"that\" or abort. "
                        + "(accepted: FILTER <OBJECT> WHERE/SUCH THAT) (Quit: Q)";
                return;
            }
            if (this.dialogState == FILTER_WHAT_WHERE) {
                if (i == inputWords.length) {
                    errorMsg = "Specify filter conditions. Enter \"help\" for help. "
                            + "(accepted: FILTER <OBJECT> WHERE/SUCH THAT) (Quit: Q)";
                    return;
                }
                if (inputWords[i].equals("NO")) {
                    transition(FILTER_WHAT_WHERE_NO, "NOCONDITION");
                    continue;
                }
                if (inputWords[i].equals("LABEL")) {
                    transition(FILTER_WHAT_WHERE_LABEL);
                    continue;
                }
                if (inputWords[i].equals("FILL")) {
                    transition(FILTER_WHAT_WHERE_FILL, "FILL");
                    continue;
                }
                if (inputWords[i].equals("STROKE")) {
                    transition(FILTER_WHAT_WHERE_STROKE, "STROKE");
                    continue;
                }
                if (inputWords[i].equals("COLOR")) {
                    transition(FILTER_WHAT_WHERE_COLOR, "COLOR");
                    continue;
                }
                if (inputWords[i].equals("THICKNESS")) {
                    transition(FILTER_WHAT_WHERE_FLOATPARAM, "THICKNESS");
                    continue;
                }
                if (inputWords[i].equals("WIDTH")) {
                    transition(FILTER_WHAT_WHERE_FLOATPARAM, "WIDTH");
                    continue;
                }
                if (inputWords[i].equals("HEIGHT")) {
                    transition(FILTER_WHAT_WHERE_FLOATPARAM, "HEIGHT");
                    continue;
                }
                if (inputWords[i].equals("SIZE")) {
                    transition(FILTER_WHAT_WHERE_FLOATPARAM, "SIZE");
                    continue;
                }
                if (inputWords[i].equals("ID")) {
                    transition(FILTER_WHAT_WHERE_INTPARAM, "ID");
                    continue;
                }
                if (inputWords[i].equals("SHAPE")) {
                    transition(FILTER_WHAT_WHERE_SHAPE, "SHAPE");
                    continue;
                }
                if (inputWords[i].equals("WEIGHT")) {
                    transition(FILTER_WHAT_WHERE_FLOATPARAM, "WEIGHT");
                    continue;
                }
                if (inputWords[i].equals("TYPE")) {
                    transition(FILTER_WHAT_WHERE_PARAM, "TYPE");
                    continue;
                }
                if (inputWords[i].equals("EDGE")) {
                    transition(FILTER_WHAT_WHERE_EDGE, "EDGE");
                    continue;
                }
                if (inputWords[i].equals("EDGETYPE")) {
                    transition(FILTER_WHAT_WHERE_EDGETYPE, "EDGETYPE");
                    continue;
                }
                if (inputWords[i].equals("DEGREE")) {
                    transition(FILTER_WHAT_WHERE_INTPARAM, "DEGREE");
                    continue;
                }
                if (inputWords[i].equals("INDEGREE")) {
                    transition(FILTER_WHAT_WHERE_INTPARAM, "INDEGREE");
                    continue;
                }
                if (inputWords[i].equals("OUTDEGREE")) {
                    transition(FILTER_WHAT_WHERE_INTPARAM, "OUTDEGREE");
                    continue;
                }
                if (inputWords[i].equals("HAS")) {
                    transition(FILTER_WHAT_WHERE_HAS);
                    continue;
                }
                if (inputWords[i].equals("HASNT")) {
                    transition(FILTER_WHAT_WHERE_HASNT);
                    continue;
                }
                if (inputWords[i].equals("DIRECTED")) {
                    transition(FILTER_WHAT_WHERE_PARAM, "DIRECTED");
                    continue;
                }
                errorMsg = "Mistyped? Add conditions. Enter \"help\" for help. "
                        + "(accepted: FILTER <OBJECT> WHERE/SUCH THAT) (Quit: Q)";
                return;
            }

            if (this.dialogState == FILTER_WHAT_WHERE_NO) {
                if (i == inputWords.length) {
                    this.dialogState = FILTER_WHAT_WHERE_COND;
                    errorMsg = "Specify more conditions or where to save the result. "
                            + "Start with \"to\", then give a list id. (accepted: FILTER "
                            + "<OBJECT> WHERE/SUCH THAT NO) (Quit: Q)";
                    return;
                }
                if (inputWords[i].equals("CONDITION")) {
                    transition(FILTER_WHAT_WHERE_COND);
                    continue;
                }
                if (inputWords[i].equals("TO")) {
                    transition(FILTER_WHAT_WHERE_COND_TO);
                    continue;
                }
                this.dialogState = FILTER_WHAT_WHERE_COND;
                errorMsg = "Specify more conditions or where to save the result. Start with \"to\", "
                        + "then give a list id. (accepted: FILTER <OBJECT> WHERE/SUCH THAT NO ) (Quit: Q)";
                return;

            }
            if (this.dialogState == FILTER_WHAT_WHERE_LABEL) {
                if (i == inputWords.length) {
                    this.dialogState = FILTER_WHAT_WHERE_PARAM;
                    errorMsg = "Specify what the label should contain or that it should be empty: "
                            + "CONTAINS <SMTH> or EMPTY. (Quit: Q)";
                    return;
                }
                if (inputWords[i].equals("CONTAINS")) {
                    transition(FILTER_WHAT_WHERE_LABEL_CONTAINS, "LABELCONTAINS");
                    continue;
                }
                if (inputWords[i].equals("EMPTY")) {
                    transition(FILTER_WHAT_WHERE_COND, "LABELEMPTY");
                    continue;
                }
                errorMsg = "Specify what the label should contain or that it should be empty: "
                        + "CONTAINS <SMTH> or EMPTY. (Quit: Q)";
                return;
            }
            if (this.dialogState == dialogState.FILTER_WHAT_WHERE_LABEL_CONTAINS) {
                if (i == inputWords.length) {
                    errorMsg = "Specify what labels should contain (no quotes).";
                    return;
                }
                transition(FILTER_WHAT_WHERE_COND, inputWords[i]);
                continue;
            }
            if (this.dialogState == FILTER_WHAT_WHERE_FILL) {
                if (i == inputWords.length) {
                    this.dialogState = FILTER_WHAT_WHERE_COLOR;
                    errorMsg = "Specify the fill color or abort. (accepted: FILTER <OBJECT> "
                            + "WHERE/SUCH THAT FILL) (Quit: Q)";
                    return;
                }
                if (inputWords[i].equals("COLOR")) {
                    transition(FILTER_WHAT_WHERE_COLOR);
                    continue;
                }
                if (isColorValue(inputWords[i])) {
                    transition(FILTER_WHAT_WHERE_COND, inputWords[i]);
                    continue;
                }
                errorMsg = "Specify a color or abort. (accepted: FILTER <OBJECT> WHERE/SUCH THAT FILL) (Quit: Q)";
                return;
            }
            if (this.dialogState == FILTER_WHAT_WHERE_STROKE) {
                if (i == inputWords.length) {
                    transitionErr(FILTER_WHAT_WHERE_PARAM, "Specify the stroke color. "
                            + "(accepted: FILTER <OBJECT> WHERE/SUCH THAT STROKE/COLOR) (Quit: Q)");
                    return;
                }
                if (inputWords[i].equals("COLOR")) {
                    transition(FILTER_WHAT_WHERE_COLOR);
                    continue;
                }
                if (isColorValue(inputWords[i])) {
                    transition(FILTER_WHAT_WHERE_COND, inputWords[i]);
                    continue;
                }
                errorMsg = "Specify a color or abort. "
                        + "(accepted: FILTER <OBJECT> WHERE/SUCH THAT STROKE/COLOR) (Quit: Q)";
                return;
            }
            if (this.dialogState == FILTER_WHAT_WHERE_EDGE) {
                if (i == inputWords.length) {
                    transitionErr(FILTER_WHAT_WHERE_PARAM, "Specify the edge type or abort. "
                            + "(accepted: FILTER <OBJECT> WHERE/SUCH THAT EDGE) (Quit: Q)");
                    return;
                }
                if (inputWords[i].equals("TYPE")) {
                    transition(FILTER_WHAT_WHERE_PARAM);
                    continue;
                }
                if (isColorValue(inputWords[i])) {
                    transitionErr(FILTER_WHAT_WHERE_COND, inputWords[i]);
                    continue;
                }
                errorMsg = "Specify an edge type. (accepted: FILTER <OBJECT> WHERE/SUCH THAT EDGE) (Quit: Q)";
                return;

            }
            if (this.dialogState == FILTER_WHAT_WHERE_PARAM) {
                if (i == inputWords.length) {
                    errorMsg = "Specify a value. (accepted: FILTER <OBJECT> WHERE/SUCH THAT <PROPERTY>) (Quit: Q)";
                    return;
                }
                if (hasValueForm(inputWords[i])) {
                    transition(FILTER_WHAT_WHERE_COND, inputWords[i]);
                    continue;
                }
                errorMsg = "Specify a value. (accepted: FILTER <OBJECT> WHERE/SUCH THAT PROPERTY) (Quit: Q)";
                return;
            }
            if (this.dialogState == FILTER_WHAT_WHERE_EDGETYPE) {
                if (i == inputWords.length) {
                    errorMsg = "Specify an edge type. (accepted: FILTER <OBJECT> WHERE/SUCH THAT <PROPERTY>) (Quit: Q)";
                    return;
                }
                if (Edge.isEdgeType(inputWords[i])) {
                    transition(FILTER_WHAT_WHERE_COND, inputWords[i]);
                    continue;
                }
                errorMsg = "Specify an edge type. (accepted: FILTER <OBJECT> WHERE/SUCH THAT PROPERTY) (Quit: Q)";
                return;
            }

            if (this.dialogState == FILTER_WHAT_WHERE_SHAPE) {
                if (i == inputWords.length) {
                    errorMsg = "Specify a shape. (accepted: FILTER <OBJECT> WHERE/SUCH THAT <PROPERTY>) (Quit: Q)";
                    return; // TODO: Specify a shape: ...
                }
                if (RenderingShape.isShape(inputWords[i])) {
                    transition(FILTER_WHAT_WHERE_COND, inputWords[i]);
                    continue;
                }
                errorMsg = "Specify a shape. (accepted: FILTER <OBJECT> WHERE/SUCH THAT PROPERTY) (Quit: Q)";
                return; // TODO: Specify a shape: ...
            }
            if (this.dialogState == FILTER_WHAT_WHERE_COLOR) {
                if (i == inputWords.length) {
                    errorMsg = "Specify a color. (accepted: FILTER <OBJECT> WHERE/SUCH THAT <PROPERTY>) (Quit: Q)";
                    return;
                }
                if (isColorValue(inputWords[i])) {
                    transition(FILTER_WHAT_WHERE_COND, inputWords[i]);
                    continue;
                }
                errorMsg = "Specify a color. (accepted: FILTER <OBJECT> WHERE/SUCH THAT PROPERTY) (Quit: Q)";
                return;
            }

            if (this.dialogState == FILTER_WHAT_WHERE_INTPARAM) {
                if (i == inputWords.length) {
                    errorMsg = "Specify a value or say < or >. (accepted: FILTER <OBJECT> WHERE/SUCH "
                            + "THAT <NUMERICAL PROPERTY>) (Quit: Q)";
                    return;
                }
                if (isInt(inputWords[i])) {
                    transition(FILTER_WHAT_WHERE_COND, inputWords[i]);
                    continue;
                }
                if (inputWords[i].equals("<")) {
                    transition(FILTER_WHAT_WHERE_PARAM, "<");
                    continue;
                }
                if (inputWords[i].equals(">")) {
                    transition(FILTER_WHAT_WHERE_PARAM, ">");
                    continue;
                }
                errorMsg = "Specify a value or say < or >. (accepted: FILTER <OBJECT> WHERE/SUCH "
                        + "THAT <NUMERICAL PROPERTY>) (Quit: Q)";
                return;
            }
            if (this.dialogState == FILTER_WHAT_WHERE_FLOATPARAM) {
                if (i == inputWords.length) {
                    errorMsg = "Specify a value or say < or >. (accepted: FILTER <OBJECT> "
                            + "WHERE/SUCH THAT <NUMERICAL PROPERTY>) (Quit: Q)";
                    return;
                }
                if (isFloat(inputWords[i])) {
                    transition(FILTER_WHAT_WHERE_COND, inputWords[i]);
                    continue;
                }
                if (inputWords[i].equals("<")) {
                    transition(FILTER_WHAT_WHERE_PARAM, "<");
                    continue;
                }
                if (inputWords[i].equals(">")) {
                    transition(FILTER_WHAT_WHERE_PARAM, ">");
                    continue;
                }
                errorMsg = "Specify a value or say < or >. (accepted: FILTER <OBJECT> "
                        + "WHERE/SUCH THAT <NUMERICAL PROPERTY>) (Quit: Q)";
                return;
            }

            if (this.dialogState == FILTER_WHAT_WHERE_HAS) {
                if (i == inputWords.length) {
                    errorMsg = "Has what: selfloop or label? (accepted: FILTER <OBJECT> WHERE/SUCH THAT HAS) (Quit: Q)";
                    return;
                }
                if (inputWords[i].equals("SELFLOOP")) {
                    transition(FILTER_WHAT_WHERE_COND, "SELFLOOP");
                    continue;
                }
                if (inputWords[i].equals("LABEL")) {
                    transition(FILTER_WHAT_WHERE_COND, "LABEL");
                    continue;
                }
                errorMsg = "Possible properties to have are: selfloop, label. (accepted: "
                        + "FILTER <OBJECT> WHERE/SUCH THAT HAS)  (Quit: Q)";
                return;
            }
            if (this.dialogState == FILTER_WHAT_WHERE_HASNT) {
                if (i == inputWords.length) {
                    errorMsg = "Hasn\'t what: selfloop or value?  (Quit: Q)";
                    return;
                }
                if (inputWords[i].equals("SELFLOOP")) {
                    transition(FILTER_WHAT_WHERE_COND, "NOSELFLOOP");
                    continue;
                }
                if (inputWords[i].equals("LABEL")) {
                    transition(FILTER_WHAT_WHERE_COND, "NOLABEL");
                    continue;
                }
                errorMsg = "Possible properties to have are: selfloop, label. (accepted: "
                        + "FILTER <OBJECT> WHERE/SUCH THAT HASNT) (Quit: Q)";
                return;
            }

            if (this.dialogState == FILTER_WHAT_WHERE_COND) {
                if (i == inputWords.length) {
                    errorMsg = "Specify further conditions or where to save the result (start with \"to\", "
                            + "then give a list id). (accepted: FILTER <OBJECT> WHERE/SUCH THAT <PROPERTY> <VALUE>) "
                            + "(Quit: Q)";
                    return;
                }
                if (inputWords[i].equals("TO")) {
                    transition(FILTER_WHAT_WHERE_COND_TO);
                    continue;
                }
                // epsilon transition to FILTER_WHAT_WHERE
                this.dialogState = FILTER_WHAT_WHERE;
                continue;
            }
            if (this.dialogState == FILTER_WHAT_WHERE_COND_TO) {
                if (i == inputWords.length) {
                    errorMsg = "Specify where to save the result. (accepted: FILTER <OBJECT> WHERE/SUCH THAT "
                            + "<PROPERTY> <VALUE> TO) (Quit: Q)";
                    return;
                }
                if (hasIdForm(inputWords[i])) {
                    transition(DONE, DialogAction.FILTER, inputWords[i]);
                    return;
                } else {
                    errorMsg = "Specify where to save the result. Format: (_|[a-Z])(_|[a-Z]|[0-9])*. "
                            + "(accepted: FILTER <OBJECT> WHERE/SUCH THAT <PROPERTY> <VALUE> TO) (Quit: Q)";
                    return;
                }
            }
            errorMsg = "Something went wrong, I could not parse your command. Please, "
                    +  "try to write the command in one line.";
            return;
        }
        return;
    }


    /*          PARSE                */


    private enum PossibleShapes {
        // TODO: SQUARE,
        CYCLE,
        ELLIPSE,
        RECTANGLE,
        DIAMOND
    }
}
