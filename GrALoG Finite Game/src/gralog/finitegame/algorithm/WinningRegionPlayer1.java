/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.finitegame.algorithm;

import gralog.finitegame.structure.*;

import java.util.HashMap;
import java.util.HashSet;

import gralog.algorithm.*;
import gralog.progresshandler.ProgressHandler;
import gralog.structure.*;
import java.util.Set;

/**
 *
 * @author viktor
 */
@AlgorithmDescription(
  name="Player 1 Winning Region",
  text="Finds the winning-region of player 1",
  url=""
)
public class WinningRegionPlayer1 extends WinningRegionPlayer0
{
    @Override
    public Object Run(FiniteGame game, AlgorithmParameters ap, Set<Object> selection, ProgressHandler onprogress) throws Exception
    {
        HashMap<FiniteGamePosition, Integer> winningRegions = WinningRegions(game);
        HashSet<Vertex> result = new HashSet<>();
        for(Vertex v : game.getVertices())
            if(winningRegions.containsKey((FiniteGamePosition)v))
                if(winningRegions.get((FiniteGamePosition)v) == 1)
                    result.add(v);
        return result;
    }
}
