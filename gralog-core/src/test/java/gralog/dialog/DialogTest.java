package gralog.dialog;

import gralog.algorithm.StringAlgorithmParameter;
import gralog.generator.Cycle;
import gralog.rendering.GralogColor;
import gralog.structure.Highlights;
import gralog.structure.Structure;
import gralog.structure.Vertex;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DialogTest {

    private Dialog dialog = new Dialog();
    ArrayList<String> parameters = new ArrayList<String>();
    Structure c20 = (new Cycle()).generate(new StringAlgorithmParameter("", "20"));
    Highlights highlights = new Highlights();

    public DialogTest(){
        ArrayList<Vertex> initialList = new ArrayList<Vertex>();
        for (int i = 0; i < 10; i++)
            initialList.add(c20.getVertexById(i));
        highlights.selectAll(initialList);
        assertEquals(20,c20.getVertices().size());
        assertEquals(20,c20.getEdges().size());
    }

    @Test
    public void testSort(){

        // sort LESS15 ID ASC
        parameters.add("SORT");;
        parameters.add("LESS15");
        parameters.add("ID");
        parameters.add("ASC");
        dialog.sort(parameters);
        assertEquals(0, dialog.getVertexListS().get("LESS15").get(0).id);
        assertEquals(15, dialog.getVertexListS().get("LESS15").size());
        parameters.clear();

        // sort LESS15 ID DESC
        parameters.add("LESS15");
        parameters.add("ID");
        parameters.add("DESC");
        dialog.sort(parameters);
        assertEquals(14, dialog.getVertexListS().get("LESS15").get(0).id);
        assertEquals(15, dialog.getVertexListS().get("LESS15").size());
        parameters.clear();

    }

    @Test
    public void testFilter(){

        // TODO: input has more than needed

        // filter all vertices fill white WHITELIST
        parameters.add("ALL");
        parameters.add("VERTICES");
        parameters.add("FILL");
        parameters.add("WHITE");
        parameters.add("WHITELIST");
        dialog.filter(parameters, c20, highlights);
        assertEquals(GralogColor.WHITE, dialog.getVertexListS().get("WHITELIST").get(0).fillColor);
        assertEquals(20, dialog.getVertexListS().get("WHITELIST").size());
        parameters.clear();

        // filter all vertices ID < 15 LESS15
        parameters.add("ALL");
        parameters.add("VERTICES");
        parameters.add("ID");
        parameters.add("<");
        parameters.add("15");
        parameters.add("LESS15");
        dialog.filter(parameters, c20, highlights);
        assertEquals(15, dialog.getVertexListS().get("LESS15").size());
        parameters.clear();


        // filter all vertices ID < 2 ID > 1 LESS15
        parameters.add("ALL");
        parameters.add("VERTICES");
        parameters.add("ID");
        parameters.add("<");
        parameters.add("2");
        parameters.add("ID");
        parameters.add(">");
        parameters.add("0");
        parameters.add("VL1");
        dialog.filter(parameters, c20, highlights);
        assertEquals(1, dialog.getVertexListS().get("VL1").get(0).id);
        assertEquals(1, dialog.getVertexListS().get("VL1").size());
        parameters.clear();


    }


}
