/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.finitegame.algorithm;

import gralog.finitegame.structure.*;

import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import gralog.algorithm.*;
import gralog.progresshandler.ProgressHandler;
import gralog.structure.*;

/**
 *
 */
@AlgorithmDescription(
        name = "Player 0 Winning Region",
        text = "Finds the winning-region of player 0",
        url = ""
)
public class WinningRegionPlayer0 extends Algorithm {

    protected HashMap<FiniteGamePosition, Integer> WinningRegions(
            FiniteGame game) {
        HashMap<FiniteGamePosition, Integer> result = new HashMap<>();
        Set<Vertex> V = game.getVertices();
        Set<FiniteGamePosition> LastIteration = new HashSet<>();
        Set<FiniteGamePosition> CurrentIteration = new HashSet<>();

        // collect terminal positions
        for (Vertex v : V) {
            if (!(v instanceof FiniteGamePosition))
                continue;
            FiniteGamePosition p = (FiniteGamePosition) v;

            boolean isTerminal = true;
            for (Edge e : p.getConnectedEdges())
                if (p == e.getSource()) {
                    isTerminal = false;
                    break;
                }

            if (isTerminal) {
                result.put(p, p.player1Position ? 0 : 1);
                LastIteration.add(p);
            }
        }

        // 
        while (LastIteration.size() > 0) {
            for (FiniteGamePosition p : LastIteration) {
                for (Edge e : p.getConnectedEdges()) {
                    FiniteGamePosition s = (FiniteGamePosition) e.getSource();
                    if (s == p) // only examine incoming edges
                        continue;
                    if (result.containsKey(e.getSource())) // source already found
                        continue;

                    if ((s.player1Position && result.get(p) == 1) // player can move into his winning region
                        || ((!s.player1Position) && result.get(p) == 0)) {
                        result.put(s, s.player1Position ? 1 : 0);
                        CurrentIteration.add(s);
                    }
                    else {
                        boolean allOutgoingEdgesGoToOppositeWinningRegion = true;
                        for (Edge o : s.getConnectedEdges()) {
                            if (o.getSource() != s) // not outgoing
                                continue;
                            if (result.containsKey((FiniteGamePosition) o.getTarget())) {
                                if ((result.get((FiniteGamePosition) o.getTarget()) == 0 && !s.player1Position)
                                    || (result.get((FiniteGamePosition) o.getTarget()) == 1 && s.player1Position)) {
                                    allOutgoingEdgesGoToOppositeWinningRegion = false;
                                    break;
                                }
                            }
                            else {
                                allOutgoingEdgesGoToOppositeWinningRegion = false;
                                break;
                            }
                        }

                        if (allOutgoingEdgesGoToOppositeWinningRegion) {
                            result.put(s, s.player1Position ? 0 : 1);
                            CurrentIteration.add(s);
                        }
                    }
                }
            }

            Set<FiniteGamePosition> temp = CurrentIteration;
            CurrentIteration = LastIteration;
            LastIteration = temp;
            CurrentIteration.clear();
        }

        return result;
    }

    public Object run(FiniteGame game, AlgorithmParameters ap,
            Set<Object> selection, ProgressHandler onprogress) throws Exception {
        HashMap<FiniteGamePosition, Integer> winningRegions = WinningRegions(game);
        HashSet<Vertex> result = new HashSet<>();
        for (Vertex v : game.getVertices())
            if (winningRegions.containsKey((FiniteGamePosition) v))
                if (winningRegions.get((FiniteGamePosition) v) == 0)
                    result.add(v);
        return result;
    }
}
