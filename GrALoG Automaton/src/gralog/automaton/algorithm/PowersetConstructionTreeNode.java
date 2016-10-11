/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.automaton.algorithm;

import java.util.Set;

import gralog.automaton.*;
import gralog.structure.*;
import gralog.rendering.Vector2D;

/**
 *
 */
public class PowersetConstructionTreeNode {

    PowersetConstructionTreeNode left;
    PowersetConstructionTreeNode right;
    State Content;

    public PowersetConstructionTreeNode(PowersetConstructionTreeNode left,
            PowersetConstructionTreeNode right, State Content) {
        this.left = left;
        this.right = right;
        this.Content = Content;
    }

    public State getContent() {
        return Content;
    }

    public void setContent(State Content) {
        this.Content = Content;
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
        Set<Vertex> allStates = sourceAutomaton.getVertices();
        PowersetConstructionTreeNode run = this;

        for (Vertex v : allStates) {
            if (statesSubset.contains(v)) {
                if (run.getRight() == null)
                    run.setRight(new PowersetConstructionTreeNode(null, null, null));
                run = run.getRight();
            }
            else {
                if (run.getLeft() == null)
                    run.setLeft(new PowersetConstructionTreeNode(null, null, null));
                run = run.getLeft();
            }
        }

        if (run.getContent() == null) {
            State temp = resultAutomaton.createVertex();

            temp.coordinates = new Vector2D(0d, 0d);
            for (State v : statesSubset)
                temp.coordinates = temp.coordinates.plus(v.coordinates);

            if (statesSubset.size() > 0)
                temp.coordinates = temp.coordinates.multiply(1d / statesSubset.size());

            resultAutomaton.addVertex(temp);
            run.setContent(temp);
        }

        return run.getContent();
    }
}
