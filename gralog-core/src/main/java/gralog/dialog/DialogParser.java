package gralog.dialog;

import gralog.rendering.GralogColor;
import gralog.rendering.shapes.RenderingShape;
import gralog.structure.Edge;

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

    private int i; // running variable
    private ActionType actionType;
    private DialogAction dialogAction;
    private ArrayList<String> parameters;
    private String errorMsg;
    private DialogState dialogState;
    private static String[] forbiddenIds = {"ALL", "SELECT", "SELECTED", "FILTER", "WHERE", "ST", "SUCH", "THAT",
            "TO", "VERTICES", "EDGES", "DELETE", "FILL", "COLOR", "STROKE", "THICKNESS", "WIDTH", "HEIGHT",
            "SIZE", "ID", "SHAPE", "WEIGHT", "TYPE", "EDGETYPE", "DEGREE", "INDEGREE", "OUTDEGREE", "BUTTERFLY",
            "HAS", "HASNT", "SELFLOOP", "DIRECTED",
            "DELETE", "UNION", "INTERSECTION", "DIFFERENCE", "SYMMETRIC", "COMPLEMENT",
            "CONNECT",
            "ADD", "REMOVE", "DELETE", "CONTRACT", "EDGE",
            "GENERATE", "WHEEL", "GRID", "CLIQUE", "CYCLE", "PATH", "TORUS", "COMPLETE", "TREE", "CYLINDRICAL",
            "NO", "CONDITION", "LABEL", "X", "Q", "EXIT", "ABORT", "CANCEL",
            "SORT", "LEFTTORIGHT", "RIGHTTOLEFT", "TOPDOWN", "BOTTOMUP"};

    private enum PossibleShapes {
        // TODO SQUARE,
        // TODO CYCLE,
        ELLIPSE,
        RECTANGLE,
        DIAMOND
    }

    private static boolean isShape(String s){
        for (PossibleShapes ps : PossibleShapes.values())
            if (ps.name().equalsIgnoreCase(s))
                return true;
        return false;
    }


    /*      CONSTRUCTOR      SETA     GETA        */

    public DialogParser(){
        dialogAction = NONE;
        this.dialogState = DONE;
        parameters = new ArrayList<String>();
        errorMsg = "";
    }

    public void addParameter(String parameter){parameters.add(parameter);}

    public void setDialogState(DialogState dialogState) {this.dialogState = dialogState;} // maybe make dialogState public
    public void setDialogAction(DialogAction dialogAction) {this.dialogAction = dialogAction;} // maybe make dialogState public
    public void clearParameters() {this.parameters.clear();}
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


    /*   CHECKING FORM    */

    private boolean hasValueForm(String s){
        // color or number or "PLAIN" or "DOTTED" or "DASHED"
        return (isColorValue(s) || s.matches("-?\\d*((\\.|,)\\d+)?")); // ... or is digit
    }

    private boolean isInt(String s){
        return s.matches("\\d+");
    }

    private boolean isFloat(String s){
        return s.matches("-?\\d*((\\.|,)\\d+)?");
    }

    private boolean hasIdForm(String idCandidate){
        String idCandidateUC = idCandidate.toUpperCase();
        for (String s : forbiddenIds) if(s.equals(idCandidateUC)) return false;
        return (idCandidateUC.matches("(_|[A-Z])([A-Z]|[0-9]|_)*"));
    }

    private boolean isColorValue(String colorValueCandidate){
        return (GralogColor.isColor(colorValueCandidate));
    }

    /*         TRANSITIONS         */

    private void transition(DialogState dialogState){
        this.dialogState = dialogState;
        i++;
        System.out.println("Changed to dialogState = [" + dialogState + "], err = " + errorMsg);
    }
    private void transition(DialogState dialogState, String addedParameter){
        this.dialogState = dialogState;
        parameters.add(addedParameter);
        i++;
        System.out.println("Changed to dialogState = [" + dialogState + "], err = " + errorMsg);
    }
    private void transition(DialogState dialogState, DialogAction dialogAction,String addedParameter){
        this.dialogState = dialogState;
        parameters.add(addedParameter);
        this.dialogAction = dialogAction;
        i++;
        System.out.println("Changed to dialogState = [" + dialogState + "], err = " + errorMsg);
    }
    private void transition(DialogState dialogState, DialogAction dialogAction){
        this.dialogState = dialogState;
        this.dialogAction = dialogAction;
        i++;
        System.out.println("Changed to dialogState = [" + dialogState + "], err = " + errorMsg);
    }
    private void transition(DialogState dialogState, DialogAction dialogAction,String addedParameter, String errorMsg){
        this.dialogState = dialogState;
        parameters.add(addedParameter);
        this.dialogAction = dialogAction;
        this.errorMsg = errorMsg;
        i++;
        System.out.println("Changed to dialogState = [" + dialogState + "], err = " + errorMsg);
    }
    private void transitionErr(DialogState dialogState, String errorMsg){
        this.dialogState = dialogState;
        this.errorMsg = errorMsg;
        System.out.println("Changed to dialogState = [" + dialogState + "]");
    }


    /*          PARSE                */


    public boolean parse(String text) {
        System.out.println(ANSI_RED + "parse: got string: " + text + ANSI_RESET); //debugging
        dialogAction = NONE;
        errorMsg = "";
        if (text.isEmpty()) {// TODO: make an exception
            System.out.println("Something went wrong: Console got an empty string as input.\n");
            return false;
        }
        String[] inputWords = text.trim().toUpperCase().split(" ");
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

        i = 0;
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
                        transition(DialogState.TWOLISTSOP,"UNION");
                        break;
                    case "INTERSECTION":
                        transition(DialogState.TWOLISTSOP,"INTERSECTION");
                        break;
                    case "DIFFERENCE":
                        transition(DialogState.TWOLISTSOP,"DIFFERENCE");
                        break;
                    case "SYMMETRIC":
                        transition(DialogState.TWOLISTSOP,"SYMMETRIC");
                        break;
                    case "DELETE":
                        transition(DialogState.DELETE);
                        break;
                    case "COMPLEMENT":
                        transition(DialogState.COMPLEMENT);
                    default:
                        errorMsg = "Parse error. Please, try again. (Abort: A)\n";
                        return true;
                }
            }

            /* ONE SET OPERATIONS  */

            if (this.dialogState == DialogState.DELETE){
                if (i == inputWords.length){
                    errorMsg = "Specify an exsisting list to be deleted. (Abort: A).\n";
                    return true;
                }
                if (hasIdForm(inputWords[i])){
                    transition(DONE,DialogAction.DELETE,inputWords[i]);
                    return true;
                }
                errorMsg = "Specify an exsisting list to be deleted. (Abort: A).\n";
                return true;
            }

            if (this.dialogState == DialogState.COMPLEMENT){
                if (i == inputWords.length){
                    errorMsg = "Specify source and target lists. (Abort: A)\n";
                    return true;
                }
                if (hasIdForm(inputWords[i])){
                    transition(COMPLEMENT_WHAT,inputWords[i]);
                    continue;
                }
                errorMsg = "I could not parse the source name " + inputWords[i] + ". Specify source and target lists. (Abort: A)\n";
                return true;
            }
            if (this.dialogState == COMPLEMENT_WHAT){
                if (i == inputWords.length){
                    errorMsg = "Specify the target list. (Abort: A)\n";
                    return true;
                }
                if (hasIdForm(inputWords[i])){
                    transition(DONE,DialogAction.COMPLEMENT,inputWords[i]);
                    return true;
                }
                errorMsg = "I could not parse the target name " + inputWords[i] + ". Specify target list. (Abort: A)\n";
                return true;
            }

            /*     TWO SETS OPERATIONS*/

            if (this.dialogState == DialogState.TWOLISTSOP){
                if (i == inputWords.length){
                    errorMsg = "Choose two existing lists. (Abort: A)\n";
                    return true;
                }
                if (hasIdForm(inputWords[i])){
                    transition(TWOLISTSOP_WHAT,inputWords[i]);
                    continue;
                }
                errorMsg = "Choose two existing lists. (Abort: A)\n";
                return true;
            }
            if (this.dialogState == DialogState.TWOLISTSOP_WHAT){
                if (i == inputWords.length){
                    errorMsg = "Specify the second list. (Abort: A)\n";
                    return true;
                }
                if (hasIdForm(inputWords[i])){
                    transition(TWOLISTSOP_WHAT_WHAT,inputWords[i]);
                    return true;
                }
                errorMsg = "Specify the second list. (Abort: A)\n";
                return true;
            }
            if (this.dialogState == DialogState.TWOLISTSOP_WHAT_WHAT){
                if (i == inputWords.length){
                    errorMsg = "Specify the target list. (Abort: A)\n";
                    return true;
                }
                if (hasIdForm(inputWords[i])){
                    transition(DONE,DialogAction.TWO_LISTS_OP,inputWords[i]);
                    return true;
                }
                errorMsg = "Specify the target list. (Abort: A)\n";
                return true;
            }

            /*    SORT    */

            if (this.dialogState == DialogState.SORT){
                if (i == inputWords.length){
                    errorMsg = "What to sort? Format: <list> [LEFTRIGHT|RIGHTLEFT|TOPDOWN|BOTTOMUP|ID [ASC|DESC]|LABEL [ASC|DESC]] (Abort: A)";
                    return true;
                }
                if (hasIdForm(inputWords[i])){
                    transition(SORT_WHAT,inputWords[i]);
                    continue;
                }
                else{
                    errorMsg = "What to sort? Format: <list> [LEFTRIGHT|RIGHTLEFT|TOPDOWN|BOTTOMUP|ID [ASC|DESC]|LABEL [ASC|DESC]] (Abort: A)";
                    return true;
                }
            }
            if (this.dialogState == SORT_WHAT){
                if (i == inputWords.length){
                    errorMsg = "Please choose how to sort: [LEFTRIGHT|RIGHTLEFT|TOPDOWN|BOTTOMUP|ID [ASC|DESC]|LABEL [ASC|DESC]] (Abort: A)";
                }
                if (inputWords[i].equals("LEFTRIGHT")){
                    transition(DONE,"LEFTRIGHT");
                    return true;
                }
                if (inputWords[i].equals("RIGHTLEFT")){
                    transition(DONE,DialogAction.SORT,"RIGHTLEFT");
                    return true;
                }
                if (inputWords[i].equals("TOPDOWN")){
                    transition(DONE,DialogAction.SORT,"TOPDOWN");
                    return true;
                }
                if (inputWords[i].equals("BOTTOMUP")){
                    transition(DONE,DialogAction.SORT,"BOTTOMUP");
                    return true;
                }
                if (inputWords[i].equals("ID")){
                    transition(SORT_WHAT_ID,"ID");
                    return true;
                }
                if (inputWords[i].equals("LABEL")){
                    transition(SORT_WHAT_ID,"LABEL"); // SORT_WHAT_LABEL would be the same
                    return true;
                }
                errorMsg = "Please choose how to sort: [LEFTRIGHT|RIGHTLEFT|TOPDOWN|BOTTOMUP|ID [ASC|DESC]|LABEL [ASC|DESC]] (Abort: A)";
                return true;

            }
            if (this.dialogState == SORT_WHAT_ID){
                if (i == inputWords.length){
                    errorMsg = "Ascending (ASC) or descending (DESC)?  (Abort: A)";
                    return true;
                }
                if (inputWords[i].equals("ASC")){
                    transition(DONE,DialogAction.SORT,"ASC");
                    return true;
                }
                if (inputWords[i].equals("DESC")){
                    transition(DONE,DialogAction.SORT,"DESC");
                    return true;
                }
                errorMsg = "Ascending (ASC) or descending (DESC)?  (Abort: A)";
                return true;
            }

            /*     SELECT  DESELECT     */

            if (this.dialogState == SELECT) {
                if (i == inputWords.length) {
                    errorMsg = "What to select?  Format: (all [vertices|edges]) | <list id> (accepted: SELECT) (Abort: A)\n";
                    return true;
                }
                if (hasIdForm(inputWords[i])) {
                    if (i == inputWords.length - 1) { // last word
                        transition(DONE, SELECT_LIST,inputWords[i]);
                        actionType = FX;
                        return true;
                    } else {           // could not parse: select <id> <trash>
                        // dont change dialogState == SELECT
                        errorMsg = "What to select? Format: (all [vertices|edges]) | <list id> (accepted: SELECT) (Abort: A)\n";
                        return true;
                    }
                }
                if (inputWords[i].equals("ALL")) {
                    transition(SELECT_ALL);
                    continue;
                }
                errorMsg = "What to select? Format: (all [vertices|edges]) | <list id> (accepted: SELECT) (Abort: A)\n";
                return true;
            }
            if (this.dialogState == DESELECT) {
                if (i == inputWords.length) {
                    errorMsg = "What to deselect?  Format: (all [vertices|edges]) | <list id> (accepted: DESELECT) (Abort: A)\n";
                    return true;
                }
                if (hasIdForm(inputWords[i])) {
                    if (i == inputWords.length - 1) { // last word
                        transition(DONE,DESELECT_LIST,inputWords[i]);
                        actionType = FX;
                        return true;
                    } else {           // could not parse: deselect <id> <trash>
                        // dont change dialogState == DESELECT
                        errorMsg = "What to deselect? Format: (all [vertices|edges]) | <list id> (accepted: DESELECT) (Abort: A)\n";
                        return true;
                    }
                }
                if (inputWords[i].equals("ALL")) {
                    transition(DESELECT_ALL);
                    continue;
                }
                errorMsg = "What to deselect? Format: (all [vertices|edges]) | <list id> (accepted: DESELECT) (Abort: A)\n";
                return true;
            }
            if (this.dialogState == DialogState.SELECT_ALL) {
                if (i == inputWords.length) {
                    transition(DONE,DialogAction.SELECT_ALL);
                    parameters.clear();
                    return true;
                }
                if (inputWords[i].equals("VERTICES")) {
                    transition(DONE,SELECT_ALL_VERTICES);
                    parameters.clear();
                    return true;
                }
                if (inputWords[i].equals("EDGES")) {
                    transition(DONE,SELECT_ALL_EDGES);
                    parameters.clear();
                    return true;
                }
                errorMsg = "Edges or vertices? (accepted: SELECT ALL) (Abort: A)\n";
                return true;

            }
            if (this.dialogState == DESELECT_ALL) {
                if (i == inputWords.length) {
                    transition(DONE,DialogAction.DESELECT_ALL);
                    parameters.clear();
                    return true;
                }
                if (inputWords[i].equals("VERTICES")) {
                    transition(DONE,DESELECT_ALL_VERTICES);
                    parameters.clear();
                    return true;
                }
                if (inputWords[i].equals("EDGES")) {
                    transition(DONE,DESELECT_ALL_EDGES);
                    parameters.clear();
                    return true;
                }
                errorMsg = "Edges or vertices? (accepted: DESELECT ALL) (Abort: A)\n";
                return true;
            }


            /*        FILTER        */

            if (this.dialogState == FILTER) {
                if (i == inputWords.length) {
                    errorMsg = "What to filter? Format: <what> where|st|(such that)  <parameters> to <list> (accepted: FILTER) (Abort: A)\n";
                    return true;
                }
                if (inputWords[i].equals("ALL")) {
                    transition(FILTER_ALL,"ALL");
                    continue;
                }
                if (inputWords[i].equals("SELECTED")){
                    transition(FILTER_SELECTED,"SELECTED"); // filter all ... behaves as filter selected ...
                    continue;
                }
                if (hasIdForm(inputWords[i])) {
                    transition(FILTER_WHAT,inputWords[i]);
                    continue;
                }
                errorMsg = "What to filter? Format: <what> where|st|(such that)  <parameters> to <list> (accepted: FILTER) (Abort: A)\n";
                return true;
            }
            if (this.dialogState == FILTER_SELECTED){
                if (i == inputWords.length){
                    errorMsg = "Vertices or edges? (Abort: A)\n";
                    return true;
                }
                if (inputWords[i].equals("VERTICES")){
                    transition(FILTER_WHAT,"VERTICES");
                    continue;
                }
                if (inputWords[i].equals("EDGES")){
                    transition(FILTER_WHAT,"EDGES");
                    continue;
                }
                errorMsg = "Vertices or edges? (accepted: FILTER SELECTED)  (Abort: A)\n";
                return true;

            }
            if (this.dialogState == FILTER_ALL) {
                if (i == inputWords.length) {
                    errorMsg = "Vertices or edges? (accepted: FILTER ALL) (Abort: A)\n";
                    return true;
                }
                if (inputWords[i].equals("VERTICES")){
                    transition(FILTER_WHAT,"VERTICES");
                    continue;
                }
                if (inputWords[i].equals("EDGES")){
                    transition(FILTER_WHAT,"EDGES");
                    continue;
                }
                if (inputWords[i].equals("SELECTED")){
                    parameters.clear(); // replace parameters=[["ALL"]] by parameters=[["SELECTED"]]
                    transition(FILTER_SELECTED,"SELECTED");
                    continue;
                }
                errorMsg = "Vertices or edges? (accepted: FILTER ALL) (Abort: A)\n";
                return true;
            }
            if (this.dialogState == FILTER_WHAT) {
                if (i == inputWords.length) {
                    errorMsg = "Specify filter conditions. Start with \"where\". Enter \"help\" for help. (accepted: FILTER <OBJECT>) (Abort: A)\n";
                    return true;
                }
                if (inputWords[i].equals("WHERE") || inputWords[i].equals("ST")) {
                    transition(FILTER_WHAT_WHERE);
                    continue;
                }
                if (inputWords[i].equals("such")) {
                    transition(FILTER_WHAT_SUCH);
                    continue;
                }
                errorMsg = "Specify filter conditions. Start with \"where\". Enter \"help\" for help. (accepted: FILTER <OBJECT>) (Abort: A)\n";
                return true;
            }
            if (this.dialogState == FILTER_WHAT_SUCH) {
                if (i == inputWords.length) {
                    errorMsg = "You probably mean \"such that\". Enter \"that\" or abort. (accepted: FILTER <OBJECT> WHERE/SUCH THAT) (Abort: A)\n";
                    return true;
                }
                if (inputWords[i].equals("THAT")) {
                    transition(FILTER_WHAT_WHERE);
                    continue;
                }
                errorMsg = "You probably mean \"such that\". Enter \"that\" or abort. (accepted: FILTER <OBJECT> WHERE/SUCH THAT) (Abort: A)\n";
                return true;
            }
            if (this.dialogState == FILTER_WHAT_WHERE) {
                if (i == inputWords.length) {
                    errorMsg = "Specify filter conditions. Enter \"help\" for help. (accepted: FILTER <OBJECT> WHERE/SUCH THAT) (Abort: A)\n";
                    return true;
                }
                if (inputWords[i].equals("NO")){
                    transition(FILTER_WHAT_WHERE_NO,"NOCONDITION");
                    continue;
                }
                if (inputWords[i].equals("LABEL")){
                    transition(FILTER_WHAT_WHERE_LABEL);
                    continue;
                }
                if (inputWords[i].equals("FILL")){
                    transition(FILTER_WHAT_WHERE_FILL,"FILL");
                    continue;
                }
                if (inputWords[i].equals("STROKE")){
                    transition(FILTER_WHAT_WHERE_STROKE,"STROKE");
                    continue;
                }
                if (inputWords[i].equals("COLOR")){
                    transition(FILTER_WHAT_WHERE_PARAM,"COLOR");
                    continue;
                }
                if (inputWords[i].equals("THICKNESS")){
                    transition(FILTER_WHAT_WHERE_FLOATPARAM,"THICKNESS");
                    continue;
                }
                if (inputWords[i].equals("WIDTH")){
                    transition(FILTER_WHAT_WHERE_FLOATPARAM,"WIDTH");
                    continue;
                }
                if (inputWords[i].equals("HEIGHT")){
                    transition(FILTER_WHAT_WHERE_FLOATPARAM, "HEIGHT");
                    continue;
                }
                if (inputWords[i].equals("SIZE")){
                    transition(FILTER_WHAT_WHERE_FLOATPARAM, "SIZE");
                    continue;
                }
                if (inputWords[i].equals("ID")){
                    transition(FILTER_WHAT_WHERE_INTPARAM,"ID");
                    continue;
                }
                if (inputWords[i].equals("SHAPE")){
                    transition(FILTER_WHAT_WHERE_SHAPE,"SHAPE");
                    continue;
                }
                if (inputWords[i].equals("WEIGHT")){
                    transition(FILTER_WHAT_WHERE_FLOATPARAM,"WEIGHT");
                    continue;
                }
                if (inputWords[i].equals("TYPE")){
                    transition(FILTER_WHAT_WHERE_PARAM,"TYPE");
                    continue;
                }
                if (inputWords[i].equals("EDGE")){
                    transition(FILTER_WHAT_WHERE_EDGE, "EDGE");
                    continue;
                }
                if (inputWords[i].equals("EDGETYPE")){
                    transition(FILTER_WHAT_WHERE_EDGETYPE,"EDGETYPE");
                    continue;
                }
                if (inputWords[i].equals("DEGREE")){
                    transition(FILTER_WHAT_WHERE_INTPARAM,"DEGREE");
                    continue;
                }
                if (inputWords[i].equals("INDEGREE")){
                    transition(FILTER_WHAT_WHERE_INTPARAM,"INDEGREE");
                    continue;
                }
                if (inputWords[i].equals("OUTDEGREE")){
                    transition(FILTER_WHAT_WHERE_INTPARAM,"OUTDEGREE");
                    continue;
                }
                if (inputWords[i].equals("HAS")){
                    transition(FILTER_WHAT_WHERE_HAS);
                    continue;
                }
                if (inputWords[i].equals("HASNT")){
                    transition(FILTER_WHAT_WHERE_HASNT);
                    continue;
                }
                if (inputWords[i].equals("DIRECTED")){
                    transition(FILTER_WHAT_WHERE_PARAM,"DIRECTED");
                    continue;
                }
                errorMsg = "Mistyped? Add conditions. Enter \"help\" for help. (accepted: FILTER <OBJECT> WHERE/SUCH THAT) (Abort: A)\n";
                return true;
            }

            if (this.dialogState == FILTER_WHAT_WHERE_NO) {
                if (i == inputWords.length) {
                    this.dialogState = FILTER_WHAT_WHERE_COND;
                    errorMsg = "Specify more conditions or where to save the result. Start with \"to\", then give a list id. (accepted: FILTER <OBJECT> WHERE/SUCH THAT NO) (Abort: A)\n";
                    return true;
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
                errorMsg = "Specify more conditions or where to save the result. Start with \"to\", then give a list id. (accepted: FILTER <OBJECT> WHERE/SUCH THAT NO ) (Abort: A)\n";
                return true;

            }
            if (this.dialogState == FILTER_WHAT_WHERE_LABEL) {
                if (i == inputWords.length) {
                    this.dialogState = FILTER_WHAT_WHERE_PARAM;
                    errorMsg = "Specify what the label should contain or that it should be empty: CONTAINS <SMTH> or EMPTY. (Abort: A)\n";
                    return true;
                }
                if (inputWords[i].equals("CONTAINS")){
                    transition(FILTER_WHAT_WHERE_LABEL_CONTAINS, "LABELCONTAINS");
                    continue;
                }
                if (inputWords[i].equals("EMPTY")){
                    transition(FILTER_WHAT_WHERE_COND,"LABELEMPTY");
                    continue;
                }
                errorMsg = "Specify what the label should contain or that it should be empty: CONTAINS <SMTH> or EMPTY. (Abort: A)\n";
                return true;
            }
            if (this.dialogState == dialogState.FILTER_WHAT_WHERE_LABEL_CONTAINS){
                if (i == inputWords.length){
                    errorMsg = "Specify what labels should contain (no quotes).\n";
                    return true;
                }
                transition(FILTER_WHAT_WHERE_COND,inputWords[i]);
                continue;
            }
            if (this.dialogState == FILTER_WHAT_WHERE_FILL) {
                if (i == inputWords.length) {
                    this.dialogState = FILTER_WHAT_WHERE_COLOR;
                    errorMsg = "Specify the fill color or abort. (accepted: FILTER <OBJECT> WHERE/SUCH THAT FILL) (Abort: A)\n";
                    return true;
                }
                if (inputWords[i].equals("COLOR")) {
                    transition(FILTER_WHAT_WHERE_COLOR);
                    continue;
                }
                if (isColorValue(inputWords[i])) {
                    transition(FILTER_WHAT_WHERE_COND,inputWords[i]);
                    continue;
                }
                errorMsg = "Specify a color or abort. (accepted: FILTER <OBJECT> WHERE/SUCH THAT FILL) (Abort: A)\n";
                return true;
            }
            if (this.dialogState == FILTER_WHAT_WHERE_STROKE) {
                if (i == inputWords.length) {
                    transitionErr(FILTER_WHAT_WHERE_PARAM,"Specify the stroke color. (accepted: FILTER <OBJECT> WHERE/SUCH THAT STROKE/COLOR) (Abort: A)\n");
                    return true;
                }
                if (inputWords[i].equals("COLOR")) {
                    transition(FILTER_WHAT_WHERE_COLOR);
                    continue;
                }
                if (isColorValue(inputWords[i])) {
                    transition(FILTER_WHAT_WHERE_COND,inputWords[i]);
                    continue;
                }
                errorMsg = "Specify a color or abort. (accepted: FILTER <OBJECT> WHERE/SUCH THAT STROKE/COLOR) (Abort: A)\n";
                return true;
            }
            if (this.dialogState == FILTER_WHAT_WHERE_EDGE) {
                if (i == inputWords.length) {
                    transitionErr(FILTER_WHAT_WHERE_PARAM, "Specify the edge type or abort. (accepted: FILTER <OBJECT> WHERE/SUCH THAT EDGE) (Abort: A)\n");
                    return true;
                }
                if (inputWords[i].equals("TYPE")) {
                    transition(FILTER_WHAT_WHERE_PARAM);
                    continue;
                }
                if (isColorValue(inputWords[i])) {
                    transitionErr(FILTER_WHAT_WHERE_COND, inputWords[i]);
                    continue;
                }
                errorMsg = "Specify an edge type. (accepted: FILTER <OBJECT> WHERE/SUCH THAT EDGE) (Abort: A)\n";
                return true;

            }
            if (this.dialogState == FILTER_WHAT_WHERE_PARAM){
                if (i == inputWords.length){
                    errorMsg = "Specify a value. (accepted: FILTER <OBJECT> WHERE/SUCH THAT <PROPERTY>) (Abort: A)\n";
                    return true;
                }
                if (hasValueForm(inputWords[i])){
                    transition(FILTER_WHAT_WHERE_COND,inputWords[i]);
                    continue;
                }
                errorMsg = "Specify a value. (accepted: FILTER <OBJECT> WHERE/SUCH THAT PROPERTY) (Abort: A)\n";
                return true;
            }
            if (this.dialogState == FILTER_WHAT_WHERE_EDGETYPE){
                if (i == inputWords.length){
                    errorMsg = "Specify an edge type. (accepted: FILTER <OBJECT> WHERE/SUCH THAT <PROPERTY>) (Abort: A)\n";
                    return true;
                }
                if (Edge.isEdgeType(inputWords[i])){
                    transition(FILTER_WHAT_WHERE_COND,inputWords[i]);
                    continue;
                }
                errorMsg = "Specify an edge type. (accepted: FILTER <OBJECT> WHERE/SUCH THAT PROPERTY) (Abort: A)\n";
                return true;
            }

            if (this.dialogState == FILTER_WHAT_WHERE_SHAPE){
                if (i == inputWords.length){
                    errorMsg = "Specify a shape. (accepted: FILTER <OBJECT> WHERE/SUCH THAT <PROPERTY>) (Abort: A)\n";
                    return true; // TODO: Specify a shape: ...
                }
                if (RenderingShape.isShape(inputWords[i])){
                    transition(FILTER_WHAT_WHERE_COND,inputWords[i]);
                    continue;
                }
                errorMsg = "Specify a shape. (accepted: FILTER <OBJECT> WHERE/SUCH THAT PROPERTY) (Abort: A)\n";
                return true; // TODO: Specify a shape: ...
            }
            if (this.dialogState == FILTER_WHAT_WHERE_COLOR){
                if (i == inputWords.length){
                    errorMsg = "Specify a color. (accepted: FILTER <OBJECT> WHERE/SUCH THAT <PROPERTY>) (Abort: A)\n";
                    return true;
                }
                if (isColorValue(inputWords[i])){
                    transition(FILTER_WHAT_WHERE_COND,inputWords[i]);
                    continue;
                }
                errorMsg = "Specify a color. (accepted: FILTER <OBJECT> WHERE/SUCH THAT PROPERTY) (Abort: A)\n";
                return true;
            }

            if (this.dialogState == FILTER_WHAT_WHERE_INTPARAM){
                if (i == inputWords.length){
                    errorMsg = "Specify a value or say < or >. (accepted: FILTER <OBJECT> WHERE/SUCH THAT <NUMERICAL PROPERTY>) (Abort: A)\n";
                    return true;
                }
                if (isInt(inputWords[i])){
                    transition(FILTER_WHAT_WHERE_COND,inputWords[i]);
                    continue;
                }
                if (inputWords[i].equals("<")){
                    transition(FILTER_WHAT_WHERE_PARAM,"<");
                    continue;
                }
                if (inputWords[i].equals(">")){
                    transition(FILTER_WHAT_WHERE_PARAM,">");
                    continue;
                }
                errorMsg = "Specify a value or say < or >. (accepted: FILTER <OBJECT> WHERE/SUCH THAT <NUMERICAL PROPERTY>) (Abort: A)\n";
                return true;
            }
            if (this.dialogState == FILTER_WHAT_WHERE_FLOATPARAM){
                if (i == inputWords.length){
                    errorMsg = "Specify a value or say < or >. (accepted: FILTER <OBJECT> WHERE/SUCH THAT <NUMERICAL PROPERTY>) (Abort: A)\n";
                    return true;
                }
                if (isFloat(inputWords[i])){
                    transition(FILTER_WHAT_WHERE_COND,inputWords[i]);
                    continue;
                }
                if (inputWords[i].equals("<")){
                    transition(FILTER_WHAT_WHERE_PARAM,"<");
                    continue;
                }
                if (inputWords[i].equals(">")){
                    transition(FILTER_WHAT_WHERE_PARAM,">");
                    continue;
                }
                errorMsg = "Specify a value or say < or >. (accepted: FILTER <OBJECT> WHERE/SUCH THAT <NUMERICAL PROPERTY>) (Abort: A)\n";
                return true;
            }

            if (this.dialogState == FILTER_WHAT_WHERE_HAS) {
                if (i == inputWords.length) {
                    errorMsg = "Has what: selfloop or label? (accepted: FILTER <OBJECT> WHERE/SUCH THAT HAS) (Abort: A)\n";
                    return true;
                }
                if (inputWords[i].equals("SELFLOOP")) {
                    transition(FILTER_WHAT_WHERE_COND, "SELFLOOP");
                    continue;
                }
                if (inputWords[i].equals("LABEL")) {
                    transition(FILTER_WHAT_WHERE_COND, "LABEL");
                    continue;
                }
                errorMsg = "Possible properties to have are: selfloop, label. (accepted: FILTER <OBJECT> WHERE/SUCH THAT HAS)  (Abort: A)\n";
                return true;
            }
            if (this.dialogState == FILTER_WHAT_WHERE_HASNT) {
                if (i == inputWords.length) {
                    errorMsg = "Hasn\'t what: selfloop or value?  (Abort: A)\n";
                    return true;
                }
                if (inputWords[i].equals("SELFLOOP")) {
                    transition(FILTER_WHAT_WHERE_COND, "NOSELFLOOP");
                    continue;
                }
                if (inputWords[i].equals("LABEL")) {
                    transition(FILTER_WHAT_WHERE_COND, "NOLABEL");
                    continue;
                }
                errorMsg = "Possible properties to have are: selfloop, label. (accepted: FILTER <OBJECT> WHERE/SUCH THAT HASNT) (Abort: A)\n";
                return true;
            }

            if (this.dialogState == FILTER_WHAT_WHERE_COND) {
                if (i == inputWords.length) {
                    errorMsg = "Specify further conditions or where to save the result (start with \"to\", then give a list id). (accepted: FILTER <OBJECT> WHERE/SUCH THAT <PROPERTY> <VALUE>) (Abort: A)\n";
                    return true;
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
                    errorMsg = "Specify where to save the result. (accepted: FILTER <OBJECT> WHERE/SUCH THAT <PROPERTY> <VALUE> TO) (Abort: A)\n";
                    return true;
                }
                if (hasIdForm(inputWords[i])) {
                    transition(DONE,DialogAction.FILTER,inputWords[i]);
                    return true;
                }
                errorMsg = "Specify where to save the result. Format: (_|[a-Z])(_|[a-Z]|[0-9])*. (accepted: FILTER <OBJECT> WHERE/SUCH THAT <PROPERTY> <VALUE> TO) (Abort: A)\n";
                return true;
            }
            errorMsg = "Something went wrong, I could not parse your command. Please, try to write the command in one line.\n";
            return true;
        }
        return true;
    }
}