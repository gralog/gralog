package gralog.gralogfx.dialog;

import gralog.algorithm.CycleParameters;
import gralog.algorithm.GridParameters;
import gralog.algorithm.StringAlgorithmParameter;
import gralog.algorithm.StringAlgorithmParametersList;
import gralog.dialog.Dialog;
import gralog.dialog.DialogAction;
import gralog.dialog.DialogParser;
import gralog.dialog.DialogState;
import gralog.generator.Cycle;
import gralog.generator.CylindricalGrid;
import gralog.generator.Grid;
import gralog.generator.Wheel;
import gralog.gralogfx.StructurePane;
import gralog.plugins.PluginManager;
import gralog.structure.Structure;
import javafx.scene.image.Image;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gralog.dialog.DialogAction.*;
import static gralog.dialog.DialogState.DONE;
import static gralog.dialog.DialogState.SELECT;
import static org.junit.Assert.*;

public class DialogConsoleParserTest {

    private Structure anEdge;
    Structure c20;
    Structure cylGrid;
    Structure grid5x5;
    Structure wheel5;

    private DialogParser dialogParser;
    private StructurePane currentStructure;


    public DialogConsoleParserTest() {
        dialogParser = new DialogParser();

//        try {
//            PluginManager.initialize();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
        //anEdge = getGraph("anEdge.graphml");

        // generate some graphs

        c20 = (new Cycle()).generate(
                new CycleParameters(Arrays.asList("20","undirected")));
        List<String> parameters = Arrays.asList("6","5");
        cylGrid = (new CylindricalGrid()).generate(new GridParameters(parameters));
        grid5x5 = (new Grid()).generate(new GridParameters(parameters));
        wheel5 = (new Wheel()).generate(new StringAlgorithmParameter("", "5"));
    }

    private void resetParserFields() {
        dialogParser.clearParameters();
        dialogParser.setDialogState(DONE);
        dialogParser.setDialogAction(NONE);

    }



    @Test
    public void testParser() {

        resetParserFields();
        dialogParser.parse("Select all");
        assertTrue(dialogParser.getParameters().isEmpty());
        assertEquals(DONE,dialogParser.getDialogState());
        assertEquals(SELECT_ALL,dialogParser.getDialogAction());
        assertEquals("",dialogParser.getErrorMsg());

        resetParserFields();
        dialogParser.parse("Slect all");
        assertTrue(dialogParser.getParameters().isEmpty());
        assertEquals(DONE,dialogParser.getDialogState());
        assertEquals(NONE,dialogParser.getDialogAction());
        assertEquals("Parse error. Please, try again. (Quit: Q)",dialogParser.getErrorMsg());

        resetParserFields();
        dialogParser.parse("Slect");
        assertTrue(dialogParser.getParameters().isEmpty());
        assertEquals(DONE,dialogParser.getDialogState());
        assertEquals(NONE,dialogParser.getDialogAction());
        assertEquals("Parse error. Please, try again. (Quit: Q)",dialogParser.getErrorMsg());

        resetParserFields();
        dialogParser.parse("Select all vertices");
        assertTrue(dialogParser.getParameters().isEmpty());
        assertEquals(DONE,dialogParser.getDialogState());
        assertEquals(DialogAction.SELECT_ALL_VERTICES,dialogParser.getDialogAction());
        assertEquals("",dialogParser.getErrorMsg());

        resetParserFields();
        dialogParser.parse("SELECT all ABC");
        assertTrue(dialogParser.getParameters().isEmpty());
        assertEquals(DialogState.SELECT_ALL,dialogParser.getDialogState());
        assertEquals(NONE,dialogParser.getDialogAction());
        assertEquals("Edges or vertices? (accepted: SELECT ALL) (Quit: Q)",dialogParser.getErrorMsg());

        resetParserFields();
        dialogParser.parse("SELECT all vertices");
        assertTrue(dialogParser.getParameters().isEmpty());
        assertEquals(DialogState.DONE,dialogParser.getDialogState());
        assertEquals(DialogAction.SELECT_ALL_VERTICES,dialogParser.getDialogAction());
        assertEquals("",dialogParser.getErrorMsg());

        resetParserFields();
        dialogParser.parse("SELECT all vertices abc");
        assertTrue(dialogParser.getParameters().isEmpty());
        assertEquals(DialogState.DONE,dialogParser.getDialogState());
        assertEquals(DialogAction.SELECT_ALL_VERTICES,dialogParser.getDialogAction());
        assertEquals("",dialogParser.getErrorMsg());

        resetParserFields();
        dialogParser.parse("select");
        assertTrue(dialogParser.getParameters().isEmpty());
        assertEquals(DialogState.SELECT,dialogParser.getDialogState());
        assertEquals(DialogAction.NONE,dialogParser.getDialogAction());
        assertEquals("What to select? Format: (all [vertices|edges]) | <list id> (accepted: SELECT) (Quit: Q)",dialogParser.getErrorMsg());

        resetParserFields();
        dialogParser.parse("select abc");
        assertEquals("ABC",dialogParser.getParameters().get(0));
        assertEquals(1,dialogParser.getParameters().size());
        assertEquals(DialogState.DONE,dialogParser.getDialogState());
        assertEquals(DialogAction.SELECT_LIST,dialogParser.getDialogAction());
        assertEquals("",dialogParser.getErrorMsg());

        resetParserFields();
        dialogParser.parse("select abc trash");
        assertTrue(dialogParser.getParameters().isEmpty());
        assertEquals(DialogState.SELECT,dialogParser.getDialogState());
        assertEquals(DialogAction.NONE,dialogParser.getDialogAction());
        assertEquals("What to select? Format: (all [vertices|edges]) | <list id> (accepted: SELECT) (Quit: Q)",dialogParser.getErrorMsg());

// the same with deselect
        resetParserFields();
        dialogParser.parse("deselect all");
        assertTrue(dialogParser.getParameters().isEmpty());
        assertEquals(DONE,dialogParser.getDialogState());
        assertEquals(DESELECT_ALL,dialogParser.getDialogAction());
        assertEquals("",dialogParser.getErrorMsg());

        resetParserFields();
        dialogParser.parse("Slect all");
        assertTrue(dialogParser.getParameters().isEmpty());
        assertEquals(DONE,dialogParser.getDialogState());
        assertEquals(NONE,dialogParser.getDialogAction());
        assertEquals("Parse error. Please, try again. (Quit: Q)",dialogParser.getErrorMsg());

        resetParserFields();
        dialogParser.parse("Slect");
        assertTrue(dialogParser.getParameters().isEmpty());
        assertEquals(DONE,dialogParser.getDialogState());
        assertEquals(NONE,dialogParser.getDialogAction());
        assertEquals("Parse error. Please, try again. (Quit: Q)",dialogParser.getErrorMsg());

        resetParserFields();
        dialogParser.parse("deselect all vertices");
        assertTrue(dialogParser.getParameters().isEmpty());
        assertEquals(DONE,dialogParser.getDialogState());
        assertEquals(DialogAction.DESELECT_ALL_VERTICES,dialogParser.getDialogAction());
        assertEquals("",dialogParser.getErrorMsg());

        resetParserFields();
        dialogParser.parse("deselect all ABC");
        assertTrue(dialogParser.getParameters().isEmpty());
        assertEquals(DialogState.DESELECT_ALL,dialogParser.getDialogState());
        assertEquals(NONE,dialogParser.getDialogAction());
        assertEquals("Edges or vertices? (accepted: DESELECT ALL) (Quit: Q)",dialogParser.getErrorMsg());

        resetParserFields();
        dialogParser.parse("deselect all vertices");
        assertTrue(dialogParser.getParameters().isEmpty());
        assertEquals(DialogState.DONE,dialogParser.getDialogState());
        assertEquals(DialogAction.DESELECT_ALL_VERTICES,dialogParser.getDialogAction());
        assertEquals("",dialogParser.getErrorMsg());

        resetParserFields();
        dialogParser.parse("deselect all vertices abc");
        assertTrue(dialogParser.getParameters().isEmpty());
        assertEquals(DialogState.DONE,dialogParser.getDialogState());
        assertEquals(DialogAction.DESELECT_ALL_VERTICES,dialogParser.getDialogAction());
        assertEquals("",dialogParser.getErrorMsg());

        resetParserFields();
        dialogParser.parse("deselect");
        assertTrue(dialogParser.getParameters().isEmpty());
        assertEquals(DialogState.DESELECT,dialogParser.getDialogState());
        assertEquals(DialogAction.NONE,dialogParser.getDialogAction());
        assertEquals("What to deselect?  Format: (all [vertices|edges]) | <list id> (accepted: DESELECT) (Quit: Q)",dialogParser.getErrorMsg());

        resetParserFields();
        dialogParser.parse("deselect abc");
        assertEquals("ABC",dialogParser.getParameters().get(0));
        assertEquals(1,dialogParser.getParameters().size());
        assertEquals(DialogState.DONE,dialogParser.getDialogState());
        assertEquals(DialogAction.DESELECT_LIST,dialogParser.getDialogAction());
        assertEquals("",dialogParser.getErrorMsg());

        resetParserFields();
        dialogParser.parse("deselect abc trash");
        assertTrue(dialogParser.getParameters().isEmpty());
        assertEquals(DialogState.DESELECT,dialogParser.getDialogState());
        assertEquals(DialogAction.NONE,dialogParser.getDialogAction());
        assertEquals("What to deselect? Format: (all [vertices|edges]) | <list id> (accepted: DESELECT) (Quit: Q)",dialogParser.getErrorMsg());
        dialogParser.setDialogState(DONE);

        dialogParser.parse("connect L1 L2 =i+1");
        assertEquals(DialogState.DONE, dialogParser.getDialogState());

    }

    @Test
    public void testFilter() {
        resetParserFields();
        dialogParser.parse("filter");
        assertTrue(dialogParser.getParameters().isEmpty());
        assertEquals(DialogState.FILTER,dialogParser.getDialogState());
        assertEquals(DialogAction.NONE,dialogParser.getDialogAction());
        assertEquals("What to filter? Format: <what> where|st|(such that) <parameters> to <list> (accepted: FILTER) (Quit: Q)",dialogParser.getErrorMsg());

        resetParserFields();
        dialogParser.parse("filter all");
        assertFalse(dialogParser.getParameters().isEmpty());
        assertEquals("ALL", dialogParser.getParameters().get(0));
        assertEquals(DialogState.FILTER_ALL,dialogParser.getDialogState());
        assertEquals(DialogAction.NONE,dialogParser.getDialogAction());
        assertEquals("Vertices or edges? (accepted: FILTER ALL) (Quit: Q)",dialogParser.getErrorMsg());

        // TODO: write more filter tests


    }


    private Structure getGraph(String fileName) {
        try {
            return Structure.loadFromStream(getClass().getClassLoader().getResourceAsStream(fileName));
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
