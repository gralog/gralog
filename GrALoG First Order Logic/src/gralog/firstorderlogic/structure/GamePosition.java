/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.firstorderlogic.structure;

import gralog.structure.*;
import gralog.plugins.XmlName;
import gralog.rendering.GralogGraphicsContext;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/**
 *
 * @author Hv
 */
@XmlName(name="GamePosition")
public class GamePosition extends Vertex{
    public Boolean Player1Position =true;
    
    @Override
    public Element ToXml(Document doc,String id) throws Exception{
        Element vnode = super.ToXml(doc,id);
        vnode.setAttribute("player1position",Player1Position ? "true" : "false");
        return vnode;
    }
    
    @Override 
    public String FromXml(Element vnode){
        String id=super.FromXml(vnode);
        if(vnode.hasAttribute("player0position"))
            Player1Position=(vnode.getAttribute("player0position").equals("true") );
        return id;
        
    }
     @Override
    public void Render(GralogGraphicsContext gc,Set<Object> highlights){
        if(Player1Position){
            super.Render(gc, highlights);
        }
        else{
            gc.FillRectangle(Coordinates.get(0)-Radius,Coordinates.get(1)-Radius,Coordinates.get(0)+Radius,Coordinates.get(1)+Radius,StrokeColor);
            gc.FillRectangle(Coordinates.get(0)-Radius+StrokeWidth,Coordinates.get(1)-Radius+StrokeWidth,Coordinates.get(0)+Radius-StrokeWidth,Coordinates.get(1)+Radius-StrokeWidth,FillColor);
            gc.PutText(Coordinates.get(0),Coordinates.get(1),Label,TextHeight,FillColor.inverse());
        }
    }
   
}
