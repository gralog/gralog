/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.algorithm;

import gralog.progresshandler.ProgressHandler;
import gralog.rendering.Vector2D;
import gralog.structure.Edge;
import gralog.structure.Highlights;
import gralog.structure.Structure;
import gralog.structure.Vertex;
import javafx.util.Pair;

import java.util.*;

/**
 *
 */
@AlgorithmDescription(
        name = "Spring Embedder",
        text = "Assigns coordinates to the vertices using the spring-embedding techinque",
        url = "https://en.wikipedia.org/wiki/Force-directed_graph_drawing"
)
public class SpringEmbedder extends Algorithm {

    protected Vector2D dimensionLimits;

    public SpringEmbedder() {
        dimensionLimits = new Vector2D(30d, 20d);
    }

    @Override
    public AlgorithmParameters getParameters(Structure s, Highlights highlights) {
        return new SpringEmbedderParameters();
    }

    public Vector2D coulomb(Vector2D u, Vector2D v, SpringEmbedderParameters p) {
        // coulomb's law:
        //                 u-v    c_repel
        // f_repel(u,v) = ----- * -------
        //                |u-v|   |u-v|²

        double distance = u.minus(v).length() / p.unstressedSpringLength;
        if (distance < 0.00001) // to avoid (near) infinite force
            return u.normalized().multiply(p.unstressedSpringLength);
        Vector2D e = (u.minus(v)).normalized().multiply(p.unstressedSpringLength);

        Double factor = p.cRepel / (distance * distance);
        return e.multiply(factor);
    }

    Vector2D hooke(Vector2D u, Vector2D v, SpringEmbedderParameters p) {
        // hooke's law:
        // attraction force of a spring is proportional to distance

        Double distance = u.minus(v).length();
        if (distance < p.unstressedSpringLength)
            return u.multiply(0d);
        distance = (distance / p.unstressedSpringLength) - 1d;

        Vector2D e = (u.minus(v)).normalized().multiply(p.unstressedSpringLength);
        // e is normalized vector between the two points

        return e.multiply(-p.cSpring * distance);
    }

    public Object run(Structure s, AlgorithmParameters ap, Set<Object> selection,
                      ProgressHandler onprogress) throws Exception {
        SpringEmbedderParameters p = (SpringEmbedderParameters) ap;
        ArrayList<Vector2D> tractions = new ArrayList<>();

        double maxDim = s.getVertices().size() * p.unstressedSpringLength;
        dimensionLimits = new Vector2D(maxDim, maxDim);

        /*
        p.unstressed_spring_length = dimension_limits.elementAt(0);
        for(int i = 1; i < dimensions; i++)
            p.unstressed_spring_length *= dimension_limits.elementAt(i);
        p.unstressed_spring_length /= s.getVertices().size();
        p.unstressed_spring_length = Math.pow(p.unstressed_spring_length, 1.0f/dimensions) / (dimensions * 2);
         */
        // init
        Collection<Vertex> vertices = s.getVertices();
        // To make sure that no two vertices have the same position
        HashSet<Integer> savedCoords = new HashSet<>();
        for (Vertex a : vertices) {
            Vector2D coordinates = new Vector2D(
                    Math.random() * dimensionLimits.getX(),
                    Math.random() * dimensionLimits.getY()
            );
            while (savedCoords.contains(coordinates.hashCode()))
                coordinates = new Vector2D(
                        Math.random() * dimensionLimits.getX(),
                        Math.random() * dimensionLimits.getY()
                );
            savedCoords.add(coordinates.hashCode());
            a.setCoordinates(coordinates);


            tractions.add(new Vector2D(0d, 0d));
        }

        // remove intermediate points (they cause problems and why would you keep them?)
        Set<Edge> edges = s.getEdges();
        for (Edge e : edges)
            e.intermediatePoints.clear();

        double maxMovement;
        int iteration = 0;
        int nextincrease = 50;
        do {
            // compute movement
            maxMovement = 0d;
            int i = 0;
            for (Vertex a : vertices) {
                Vector2D traction = tractions.get(i).multiply(p.friction);

                // compute forces on a by the other vertices
                for (Vertex b : vertices) {
                    if (b == a)
                        continue;

                    // force on a by vertex b
                    traction = traction.plus(coulomb(a.getCoordinates(), b.getCoordinates(), p));
                    if (s.adjacent(a, b))
                        traction = traction.plus(hooke(a.getCoordinates(), b.getCoordinates(), p));
                }

                tractions.set(i, traction);
                ++i;
            }

            // execute movement
            i = 0;
            for (Vertex a : vertices) {
                Vector2D oldCoordinates = a.getCoordinates();
                Vector2D newCoordinates = new Vector2D(
                        Math.max(0.0d,
                                Math.min(dimensionLimits.getX(), oldCoordinates.getX()) + p.delta * tractions.get(i).getX()),
                        Math.max(0.0d,
                                Math.min(dimensionLimits.getY(), oldCoordinates.getY()) + p.delta * tractions.get(i).getY())
                );
                a.setCoordinates(newCoordinates);

                // for the loop condition
                double currentMovement
                        = (oldCoordinates.getX() - newCoordinates.getX()) * (oldCoordinates.getX() - newCoordinates.getX())
                        + (oldCoordinates.getY() - newCoordinates.getY()) * (oldCoordinates.getY() - newCoordinates.getY());
                if (Math.sqrt(currentMovement) > maxMovement)
                    maxMovement = Math.sqrt(currentMovement);

                ++i;
            }

            if (++iteration > nextincrease) {
                p.movementThreshold += 0.01d;
                nextincrease *= 4;
            }
            if (iteration > p.maxIterations)
                break;

            if (onprogress != null)
                onprogress.onProgress(s);
        } while (maxMovement > p.movementThreshold * p.unstressedSpringLength);

        return null;
    }
}
