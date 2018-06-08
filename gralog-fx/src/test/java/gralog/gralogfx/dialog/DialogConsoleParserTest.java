package gralog.gralogfx.dialog;

import gralog.dialog.DialogParser;
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
        anEdge = getGraph("anEdge.graphml");
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
