/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.events;

import gralog.structure.*;
import java.util.EventObject;

/**
 *
 * @author viktor
 */
public class VertexEvent<V extends Vertex> extends EventObject {
    public VertexEvent(V source) {
        super(source);
    }
}