/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.algorithm;

import gralog.structure.*;
import gralog.rendering.*;
import gralog.progresshandler.*;

import java.util.Vector;
import java.util.Set;

/**
 *
 * @author viktor
 */
@AlgorithmDescription(
  name="Spring Embedder",
  text="Assigns coordinates to the vertices using the spring-embedding techinque",
  url="https://en.wikipedia.org/wiki/Force-directed_graph_drawing"
)
public class SpringEmbedder extends Algorithm {
    
    @Override
    public AlgorithmParameters GetParameters(Structure s) {
        return new SpringEmbedderParameters();
    }

    
    protected Vector<Double> dimension_limits = new Vector<Double>();

    
    public SpringEmbedder()
    {
        dimension_limits.add(30d);
        dimension_limits.add(20d);
    }
    
    
    public VectorND Coulomb(VectorND u, VectorND v, SpringEmbedderParameters p)
    {
        // coulomb's law:
        //                 u-v    c_repel
        // f_repel(u,v) = ----- * -------
        //                |u-v|   |u-v|²


        Double distance = (u.Minus(v)).Length() / p.unstressed_spring_length;
        if(distance < 0.00001) // to avoid (near) infinite force
            return u.Normalized().Multiply(p.unstressed_spring_length);
        VectorND e = (u.Minus(v)).Normalized().Multiply(p.unstressed_spring_length);
        
        Double factor = p.c_repel / (distance*distance);
        return e.Multiply(factor);
    }

    VectorND Hooke(VectorND u, VectorND v, SpringEmbedderParameters p)
    {
        // hooke's law:
        // attraction force of a spring is proportional to distance

        Double distance = u.Minus(v).Length();
        if(distance <  p.unstressed_spring_length)
            return u.Multiply(0d);
        distance = (distance / p.unstressed_spring_length) - 1d;

        VectorND e = (u.Minus(v)).Normalized().Multiply(p.unstressed_spring_length);
        // e is normalized vector between the two points

        return e.Multiply(-p.c_spring * distance);
    }


    public Object Run(Structure s, AlgorithmParameters ap, ProgressHandler onprogress) throws Exception {
        SpringEmbedderParameters p = (SpringEmbedderParameters)ap;
        Vector<VectorND> tractions = new Vector<VectorND>();
        int dimensions = dimension_limits.size();

        p.unstressed_spring_length = dimension_limits.elementAt(0);
        for(int i = 1; i < dimensions; i++)
            p.unstressed_spring_length *= dimension_limits.elementAt(i);
        p.unstressed_spring_length /= s.getVertices().size();
        p.unstressed_spring_length = Math.pow(p.unstressed_spring_length, 1.0f/dimensions) / (dimensions * 2);

        // init
        Set<Vertex> vertices = s.getVertices();
        for(Vertex a : vertices)
        {
            VectorND coordinates = new VectorND();
            for(int i = 0; i < dimensions; i++)
                coordinates.add(Math.random() * dimension_limits.elementAt(i));
            a.Coordinates = coordinates.toVector();
            // should make sure that no two vertices have the same position
            
            tractions.add(new VectorND());
        }

        Double max_movement = 0d;
        int iteration = 0;
        int nextincrease = 50;
        do
        {
            // compute movement
            max_movement = 0d;
            int i = 0;
            for(Vertex a : vertices)
            {
                VectorND traction = tractions.elementAt(i).Multiply(p.friction);

                // compute forces on a by the other vertices
                VectorND aCoordinates = new VectorND(a.Coordinates);
                for(Vertex b : vertices)
                {
                    if(b==a)
                        continue;

                    // force on a by vertex b
                    VectorND bCoordinates = new VectorND(b.Coordinates);
                    traction = traction.Plus(Coulomb(aCoordinates, bCoordinates, p));
                    if(s.Adjacent(a,b))
                        traction = traction.Plus(Hooke(aCoordinates, bCoordinates, p));
                }

                tractions.setElementAt(traction, i);
                i++;
            }


            // execute movement
            i = 0;
            for(Vertex a : vertices)
            {
                Vector<Double> OldCoordinates = a.Coordinates;
                Vector<Double> NewCoordinates = new Vector<Double>(dimensions);
                for(int j = 0; j < dimensions; j++)
                    NewCoordinates.add(
                            Math.max(0.0d,
                                    Math.min(dimension_limits.elementAt(j),
                                             OldCoordinates.elementAt(j) + p.delta * tractions.elementAt(i).toVector().elementAt(j)   )));
                a.Coordinates = NewCoordinates;

                // for the loop-condition
                Double current_movement = 0.0d;
                for(int j = 0; j < dimensions; j++)
                    current_movement += (OldCoordinates.elementAt(j) - NewCoordinates.elementAt(j)) * (OldCoordinates.elementAt(j) - NewCoordinates.elementAt(j));
                if(Math.sqrt(current_movement) > max_movement)
                    max_movement = Math.sqrt(current_movement);
                
                i++;
            }


            if(++iteration > nextincrease)
            {
                p.movement_threshold += 0.01d;
                nextincrease *= 4;
            }
            if(iteration > p.max_iterations)
                break;
            
            if(onprogress != null)
                onprogress.OnProgress(s);
        }
        while(max_movement > p.movement_threshold * p.unstressed_spring_length);

        return null;
    }
}
