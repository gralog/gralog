/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.firstorderlogic.algorithm;

import gralog.firstorderlogic.formula.FirstOrderFormula;
import gralog.firstorderlogic.parser.FirstOrderParser;

import gralog.algorithm.*;

import gralog.structure.*;
import gralog.progresshandler.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import gralog.finitegame.structure.*;
import gralog.firstorderlogic.parser.FirstOrderSyntaxChecker;
import gralog.preferences.Preferences;
import gralog.rendering.Vector2D;

/**
 *
 */
@AlgorithmDescription(
    name = "Model Checking Game for First Order Logic",
    text = "",
    url = "https://en.wikipedia.org/wiki/Game_semantics#Classical_logic"
)
public class ModelCheckingGameFOLogic extends Algorithm {

    @Override
    public AlgorithmParameters getParameters(Structure s, Highlights highlights) {
        return new StringAlgorithmParameter(
            "Formula",
            Preferences.getString(this.getClass(), "formula", "!x. ?y. E(y,x) ∨ E(x,z)"),
            new FirstOrderSyntaxChecker(),
            FirstOrderSyntaxChecker.explanation());
    }

    /* public void getUniqueGamePositions(FiniteGame game) {
       Set<FiniteGamePosition> gp=game.getVertices();
       for(FiniteGamePosition v: (Set<FiniteGamePosition>)gp) {
           for(FiniteGamePosition w: gp) {
               if(v!=w) {
                   if(v.Label.equals(w.Label)) {
                       Set<Edge> incidentEdges=w.getIncidentEdges();
                       for(Edge e: incidentEdges) {
                           if(e.getSource()!=w) {
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
        Preferences.setString(this.getClass(), "formula", sp.parameter);

        FirstOrderFormula phi = FirstOrderParser.parseString(sp.parameter);

        Collection<Vertex> V = s.getVertices();
        int i = 0;
        for (Vertex v : V) {
            v.label = String.valueOf(v.id) + ":" + v.label;
            i++;
        }

        HashMap<String, Vertex> varassign = new HashMap<>();
        FiniteGame gp = new FiniteGame();
        Vector2D ob = new Vector2D(5.0, 5.0);

        phi.constructGameGraph(s, varassign, gp, ob);

        return gp;
    }
}
