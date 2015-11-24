/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.events;

import gralog.structure.*;
import java.util.EventObject;
import java.util.EventListener;

/**
 *
 * @author viktor
 */
public interface StructureListener<V extends Vertex, E extends Edge> extends EventListener {
    public void StructureChanged(StructureEvent<V,E> e);
    public void VertexChanged(VertexEvent<V> e);
    public void EdgeChanged(EdgeEvent<E> e);
}
