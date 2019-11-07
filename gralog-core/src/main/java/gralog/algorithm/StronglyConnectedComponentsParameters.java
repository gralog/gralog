package gralog.algorithm;

import gralog.structure.Vertex;

import java.util.ArrayList;

public class StronglyConnectedComponentsParameters extends ImplicitParameters{
    public ArrayList<Vertex> vertices;
    public StronglyConnectedComponentsParameters(ArrayList<Vertex> vertices){
        this.vertices = vertices;

    }
}
