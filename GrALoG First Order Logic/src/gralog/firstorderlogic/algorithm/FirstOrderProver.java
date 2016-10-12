/*
 * This file is part of GrALoG FX, Copyright (c) 2016 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later.
 */
package gralog.firstorderlogic.algorithm;

import gralog.firstorderlogic.logic.firstorder.formula.FirstOrderFormula;
import gralog.firstorderlogic.logic.firstorder.parser.FirstOrderParser;

import gralog.algorithm.*;
import gralog.firstorderlogic.prover.TreeDecomposition.*;
import gralog.structure.*;
import gralog.progresshandler.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.util.HashMap;
import java.util.Set;

/**
 *
 */
@AlgorithmDescription(
        name = "First Order Logic Prover",
        text = "",
        url = "First_Order_Prover_Manual.pdf"
)

public class FirstOrderProver extends Algorithm {

    @Override
    public AlgorithmParameters getParameters(Structure s) {
        return new FirstOrderProverParameters();
    }

    public Object run(Structure s, AlgorithmParameters p, Set<Object> selection,
            ProgressHandler onprogress) throws Exception {
        FirstOrderProverParameters sp = (FirstOrderProverParameters) (p);
        Set<Vertex> V = s.getVertices();
        int i = 0;
        for (Vertex v : V) {
            v.label = String.valueOf(i);
            i++;
        }
        onprogress.onProgress(s);
        if (V.isEmpty()) {
            return "Please input a graph";
        }
        try (PrintWriter out = new PrintWriter(new BufferedWriter(
                new FileWriter("PreviousSearch.txt", false)))) {
            out.println(sp.formulae);
        }
        FirstOrderParser parser = new FirstOrderParser();
        FirstOrderFormula phi;
        try {
            phi = parser.parseString(sp.formulae);
        }
        catch (Exception ex) {
            return ex.getMessage();
        }
        try (PrintWriter out = new PrintWriter(new BufferedWriter(
                new FileWriter("CorrectSearches.txt", true)))) {
            out.println(sp.formulae);
        }

        HashMap<String, Vertex> varassign = new HashMap<>();

        //FOQueryResult result=new FOQueryResult();
        Bag rootBag = phi.evaluateProver(s, varassign, onprogress);

        rootBag.caption = phi.toString();

        return rootBag;
    }
}
