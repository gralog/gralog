/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.gralogfx;

import java.util.Map;
import java.util.HashMap;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.Dimension;

import org.jgraph.JGraph;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.ext.JGraphModelAdapter;

import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javax.swing.SwingUtilities;

/**
 *
 * @author viktor
 */
public class JGraphPane extends SwingNode {

    
    private ListenableGraph graph = null;
    private JGraphModelAdapter m_jgAdapter = null;
    private JGraph jgraph = null;
    
    
    public JGraphPane() {
        
        super();
        
        graph = new ListenableDirectedGraph( DefaultEdge.class );
        m_jgAdapter = new JGraphModelAdapter( graph );
        jgraph = new JGraph( m_jgAdapter );
        
        Dimension size = new Dimension(800,600);
        jgraph.setPreferredSize( size );
        jgraph.setBackground( new Color(0xFAFBFF) );
       
        SwingUtilities.invokeLater(() -> {
            
            this.setContent(jgraph);
            this.resize(size.width, size.height);
            
        });
    }
    
    
    public void putVertex(Object v, double x, double y) {
        
        graph.addVertex(v);
        
        DefaultGraphCell cell = m_jgAdapter.getVertexCell( v );
        Map              attr = cell.getAttributes(  );
        Rectangle2D      b    = GraphConstants.getBounds( attr );
        
        double w = b.getWidth();
        double h = b.getHeight();
        GraphConstants.setBounds( attr, new Rectangle.Double( x-w/2, y-h/2, w, h) );

        Map cellAttr = new HashMap(  );
        cellAttr.put( cell, attr );
        
        m_jgAdapter.edit( cellAttr, null, null, null); // fire event(?)
    }
    
    
    public void addEdge(Object src, Object target) {
        graph.addEdge(src,target);
    }
    
    
}
