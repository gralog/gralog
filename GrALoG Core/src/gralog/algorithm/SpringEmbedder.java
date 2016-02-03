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
        return new GenericAlgorithmParameter<String>("");
    }
    
    
    Double c_repel                  =     3.0d;
    Double c_spring                 =     1.0d;
    Double friction                 =     0.5d; // lower value = more friction (0 = no preservation of momentum)
    Double unstressed_spring_length =     3.0d;
    Double delta                    =     0.04d;
    Double movement_threshold       =     0.01d; // percentage of unstressed_spring_length
    int max_iterations              = 10000;

    
    protected Vector<Double> dimension_limits = new Vector<Double>();

    
    public SpringEmbedder()
    {
        dimension_limits.add(30d);
        dimension_limits.add(20d);
    }
    
    
    public VectorND Coulomb(VectorND u, VectorND v)
    {
        // coulomb's law:
        //                 u-v    c_repel
        // f_repel(u,v) = ----- * -------
        //                |u-v|   |u-v|²


        Double distance = (u.Minus(v)).Length() / unstressed_spring_length;
        if(distance < 0.00001) // to avoid (near) infinite force
            return u.Normalized().Multiply(unstressed_spring_length);
        VectorND e = (u.Minus(v)).Normalized().Multiply(unstressed_spring_length);
        
        Double factor = c_repel / (distance*distance);
        return e.Multiply(factor);
    }

    VectorND Hooke(VectorND u, VectorND v)
    {
        // hooke's law:
        // attraction force of a spring is proportional to distance

        Double distance = u.Minus(v).Length();
        if(distance <  unstressed_spring_length)
            return u.Multiply(0d);
        distance = (distance / unstressed_spring_length) - 1d;

        VectorND e = (u.Minus(v)).Normalized().Multiply(unstressed_spring_length);
        // e is normalized vector between the two points

        return e.Multiply(-c_spring * distance);
    }


    public Object Run(Structure s, AlgorithmParameters p, ProgressHandler onprogress) throws Exception {
        Vector<VectorND> tractions = new Vector<VectorND>();
        int dimensions = dimension_limits.size();

        unstressed_spring_length = dimension_limits.elementAt(0);
        for(int i = 1; i < dimensions; i++)
            unstressed_spring_length *= dimension_limits.elementAt(i);
        unstressed_spring_length /= s.getVertices().size();
        unstressed_spring_length = Math.pow(unstressed_spring_length, 1.0f/dimensions) / (dimensions * 2);

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
                VectorND traction = tractions.elementAt(i).Multiply(friction);

                // compute forces on a by the other vertices
                VectorND aCoordinates = new VectorND(a.Coordinates);
                for(Vertex b : vertices)
                {
                    if(b==a)
                        continue;

                    // force on a by vertex b
                    VectorND bCoordinates = new VectorND(b.Coordinates);
                    traction = traction.Plus(Coulomb(aCoordinates, bCoordinates));
                    if(s.Adjacent(a,b))
                        traction = traction.Plus(Hooke(aCoordinates, bCoordinates));
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
                                             OldCoordinates.elementAt(j) + delta * tractions.elementAt(i).toVector().elementAt(j)   )));
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
                movement_threshold += 0.01d;
                nextincrease *= 4;
            }
            if(iteration > max_iterations)
                break;
            
            if(onprogress != null)
                onprogress.OnProgress(s);
        }
        while(max_movement > movement_threshold * unstressed_spring_length);

        return null;
    }
}
