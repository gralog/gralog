/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.automaton;

import gralog.plugins.XmlName;
import gralog.rendering.*;
import gralog.structure.*;

import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author viktor
 */
@XmlName(name="state")
public class State extends Vertex {
    
    public Boolean StartState = false;
    public Boolean FinalState = false;
    
    public Double InitialMarkAngle = 0d; // degrees
    public Double InitialMarkLength = 0.7d; // cm
    public Double InitialMarkWidth = 2.54/96; // cm
    public Double InitialMarkHeadAngle = 40d; // degrees
    public Double InitialMarkHeadLength = 0.4d; // cm
    public GralogColor InitialMarkColor = GralogColor.black;
    
    @Override
    public Element ToXml(Document doc, String id) throws Exception {
        Element vnode = super.ToXml(doc, id);
        vnode.setAttribute("initial", StartState ? "true" : "false");
        vnode.setAttribute("final", FinalState ? "true" : "false");
        vnode.setAttribute("initialmarkangle", InitialMarkAngle.toString());
        vnode.setAttribute("initialmarklength", InitialMarkLength.toString());
        vnode.setAttribute("initialmarkwidth", InitialMarkWidth.toString());
        vnode.setAttribute("initialmarkheadangle", InitialMarkHeadAngle.toString());
        vnode.setAttribute("initialmarkheadlength", InitialMarkHeadLength.toString());
        vnode.setAttribute("initialmarkcolor", InitialMarkColor.toHtmlString());
        return vnode;
    }
    
    @Override
    public String FromXml(Element vnode) {
        String id = super.FromXml(vnode);
        StartState = (vnode.getAttribute("initial").equals("true"));
        FinalState = (vnode.getAttribute("final").equals("true"));
        if(vnode.hasAttribute("initialmarkangle"))
            InitialMarkAngle = Double.parseDouble(vnode.getAttribute("initialmarkangle"));
        if(vnode.hasAttribute("initialmarklength"))
            InitialMarkLength = Double.parseDouble(vnode.getAttribute("initialmarklength"));
        if(vnode.hasAttribute("initialmarkwidth"))
            InitialMarkWidth = Double.parseDouble(vnode.getAttribute("initialmarkwidth"));
        if(vnode.hasAttribute("initialmarkheadangle"))
            InitialMarkHeadAngle = Double.parseDouble(vnode.getAttribute("initialmarkheadangle"));
        if(vnode.hasAttribute("initialmarkheadlength"))
            InitialMarkHeadLength = Double.parseDouble(vnode.getAttribute("initialmarkheadlength"));
        if(vnode.hasAttribute("initialmarkcolor"))
            InitialMarkColor = GralogColor.parseColor(vnode.getAttribute("initialmarkcolor"));
        
        return id;
    }
    
    @Override
    public void Render(GralogGraphicsContext gc, Set<Object> highlights) {
        
        if(highlights != null && highlights.contains(this))
            gc.Circle(Coordinates.get(0), Coordinates.get(1), Radius+0.07, GralogColor.red);

        gc.Circle(Coordinates.get(0), Coordinates.get(1), Radius, StrokeColor);
        gc.Circle(Coordinates.get(0), Coordinates.get(1), Radius-StrokeWidth, FillColor);
        if(this.FinalState) {
            gc.Circle(Coordinates.get(0), Coordinates.get(1), Radius-3*StrokeWidth, StrokeColor);
            gc.Circle(Coordinates.get(0), Coordinates.get(1), Radius-4*StrokeWidth, FillColor);
        }
        
        if(StartState) {
            Vector2D center = Coordinates;
            Vector2D intersectionOffset = new Vector2D(-Radius*Math.cos(InitialMarkAngle/180*Math.PI),
                                                       -Radius*Math.sin(InitialMarkAngle/180*Math.PI));
            Vector2D intersection = center.Plus(intersectionOffset);
            Vector2D headStart = intersection.Plus(intersectionOffset.Normalized().Multiply(InitialMarkLength));
            
            gc.Arrow(headStart.getX(), headStart.getY(), intersection.getX(), intersection.getY(), InitialMarkHeadAngle, InitialMarkHeadLength, InitialMarkColor, InitialMarkWidth);
        }
        
        gc.PutText(Coordinates.get(0), Coordinates.get(1), Label, TextHeight, FillColor.inverse());
    }
}
