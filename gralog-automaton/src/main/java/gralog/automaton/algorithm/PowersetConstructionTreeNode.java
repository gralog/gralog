/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.automaton.algorithm;

import gralog.automaton.Automaton;
import gralog.automaton.State;
import gralog.structure.Vertex;

import java.util.Collection;
import java.util.Set;

/**
 *
 */
public class PowersetConstructionTreeNode {

    PowersetConstructionTreeNode left;
    PowersetConstructionTreeNode right;
    State content;

    public PowersetConstructionTreeNode(PowersetConstructionTreeNode l,
        PowersetConstructionTreeNode r, State c) {
        this.left = l;
        this.right = r;
        this.content = c;
    }

    public State getContent() {
        return content;
    }

    public void setContent(State c) {
        this.content = c;
    }

    public PowersetConstructionTreeNode getLeft() {
        return left;
    }

    public void setLeft(PowersetConstructionTreeNode left) {
        this.left = left;
    }

    public PowersetConstructionTreeNode getRight() {
        return right;
    }

    public void setRight(PowersetConstructionTreeNode right) {
        this.right = right;
    }

    public State getContentForSet(Automaton sourceAutomaton,
        Automaton resultAutomaton, Set<State> statesSubset) {
        Collection<State> allStates = sourceAutomaton.getVertices();
        PowersetConstructionTreeNode run = this;

        for (Vertex v : allStates) {
            if (statesSubset.contains(v)) {
                if (run.getRight() == null) {
                    run.setRight(new PowersetConstructionTreeNode(null, null, null));
                }
                run = run.getRight();
            } else {
                if (run.getLeft() == null) {
                    run.setLeft(new PowersetConstructionTreeNode(null, null, null));
                }
                run = run.getLeft();
            }
        }

        if (run.getContent() == null) {
            State temp = resultAutomaton.createVertex();

            temp.setCoordinates(0d, 0d);
            for (State v : statesSubset) {
                temp.setCoordinates(temp.getCoordinates().plus(v.getCoordinates()));
            }

            if (statesSubset.size() > 0) {
                temp.setCoordinates(temp.getCoordinates().multiply(1d / statesSubset.size()));
            }

            resultAutomaton.addVertex(temp);
            run.setContent(temp);
        }

        return run.getContent();
    }
}
