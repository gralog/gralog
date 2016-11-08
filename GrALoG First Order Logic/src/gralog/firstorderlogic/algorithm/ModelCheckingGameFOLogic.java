/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.firstorderlogic.algorithm;

import gralog.firstorderlogic.logic.firstorder.formula.FirstOrderFormula;
import gralog.firstorderlogic.logic.firstorder.parser.FirstOrderParser;

import gralog.algorithm.*;

import gralog.structure.*;
import gralog.progresshandler.*;
import java.util.HashMap;
import java.util.Set;

import gralog.finitegame.structure.*;
import gralog.firstorderlogic.view.FirstOrderSyntaxChecker;
import gralog.properties.Properties;
import gralog.rendering.Vector2D;

/**
 *
 */
@AlgorithmDescription(
        name = "Model Checking Game for first Order Logic",
        text = "",
        url = "https://en.wikipedia.org/wiki/Game_semantics#Classical_logic"
)
public class ModelCheckingGameFOLogic extends Algorithm {

    @Override
    public AlgorithmParameters getParameters(Structure s) {
        return new StringAlgorithmParameter(
                "Formula",
                Properties.getString(this.getClass(), "formula", "!x. ?y. E(y,x) ∨ E(x,z)"),
                new FirstOrderSyntaxChecker(),
                FirstOrderSyntaxChecker.explanation());
    }

    /* public void getUniqueGamePositions(FiniteGame game){
       Set<FiniteGamePosition> gp=game.getVertices();
       for(FiniteGamePosition v: (Set<FiniteGamePosition>)gp){
           for(FiniteGamePosition w: gp){
               if(v!=w){
                   if(v.Label.equals(w.Label)){
                       Set<Edge> connectedEdges=w.getConnectedEdges();
                       for(Edge e: connectedEdges){
                           if(e.getSource()!=w){
                               Vertex temp=e.getSource();
                               game.AddEdge(game.CreateEdge(temp,v) );
                           }
                       }
                   }
               }
           }
       }
   }
     */
    public Object run(Structure s, AlgorithmParameters p, Set<Object> selection,
            ProgressHandler onprogress) throws Exception {
        StringAlgorithmParameter sp = (StringAlgorithmParameter) (p);
        Properties.setString(this.getClass(), "formula", sp.parameter);

        FirstOrderParser parser = new FirstOrderParser();
        FirstOrderFormula phi = parser.parseString(sp.parameter);

        Set<Vertex> V = s.getVertices();
        int i = 0;
        for (Vertex v : V) {
            v.label = String.valueOf(i);
            i++;
        }
        HashMap<String, Vertex> varassign = new HashMap<>();
        FiniteGame gp = new FiniteGame();
        Vector2D ob = new Vector2D(5.0, 5.0);

        phi.constructGameGraph(s, varassign, gp, ob);

        return gp;
    }
}
