package gralog.dialog;

import gralog.algorithm.StringAlgorithmParameter;
import gralog.generator.Cycle;
import gralog.structure.Highlights;
import gralog.structure.Structure;
import gralog.structure.Vertex;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
    }

    @Test
    public void testFilter(){

        parameters.add("ALL");
        parameters.add("VERTICES");
        parameters.add("WHERE");
        parameters.add("FILL");
        parameters.add("WHITE");
        parameters.add("TO");
        parameters.add("whiteList");
        dialog.filter(parameters, c20, highlights);

    }

}
