package gralog.gralogfx.dialog;

import gralog.algorithm.StringAlgorithmParameter;
import gralog.dialog.DialogParser;
import gralog.generator.Cycle;
import gralog.generator.CylindricalGrid;
import gralog.generator.Grid;
import gralog.generator.Wheel;
import gralog.gralogfx.StructurePane;
import gralog.plugins.PluginManager;
import gralog.structure.Structure;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class DialogConsoleParserTest {

    private Structure anEdge;

    public DialogConsoleParserTest(){
        try {
            PluginManager.initialize();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //anEdge = getGraph("anEdge.graphml");

        // generate some graphs

        Structure c20 = (new Cycle()).generate(new StringAlgorithmParameter("", "20"));
        Structure cylGrid = (new CylindricalGrid()).generate(new StringAlgorithmParameter("", "5"));
        Structure grid5x5 = (new Grid()).generate(new StringAlgorithmParameter("", "5"));
        Structure wheel5 = (new Wheel()).generate(new StringAlgorithmParameter("", "5"));
    }

    @Test
    public void testSelect(){
        DialogParser dialogParser = new DialogParser();
        StructurePane currentStructure;
        assertEquals(2,anEdge.getVertices().size());

    }


    private Structure getGraph(String fileName){
        try {
            return Structure.loadFromStream(getClass().getClassLoader().getResourceAsStream(fileName));
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }
}
