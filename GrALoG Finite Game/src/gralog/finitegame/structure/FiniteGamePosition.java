
package gralog.finitegame.structure;

import gralog.structure.*;
import gralog.plugins.XmlName;
import gralog.rendering.GralogColor;
import gralog.rendering.GralogGraphicsContext;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@XmlName(name="finitegameposition")
public class FiniteGamePosition extends Vertex
{
    public Boolean Player1Position = true;
    
    @Override
    public Element ToXml(Document doc, String id) throws Exception
    {
        Element vnode = super.ToXml(doc, id);
        vnode.setAttribute("player1position", Player1Position ? "true" : "false");
        return vnode;
    }
    
    @Override
    public String FromXml(Element vnode)
    {
        String id = super.FromXml(vnode);
        if(vnode.hasAttribute("player1position"))
            Player1Position = (vnode.getAttribute("player1position").equals("true"));
        return id;
    }
 
    @Override
    public void Render(GralogGraphicsContext gc, Set<Object> highlights)
    {
        if(Player1Position)
        {
            super.Render(gc, highlights);
        }
        else
        {
            if(highlights != null && highlights.contains(this))
                gc.FillRectangle(Coordinates.get(0)-Radius-0.07, // outer
                                 Coordinates.get(1)-Radius-0.07,
                                 Coordinates.get(0)+Radius+0.07,
                                 Coordinates.get(1)+Radius+0.07,
                                 GralogColor.red);
            
            gc.FillRectangle(Coordinates.get(0)-Radius, // outer
                             Coordinates.get(1)-Radius,
                             Coordinates.get(0)+Radius,
                             Coordinates.get(1)+Radius,
                             StrokeColor);
            gc.FillRectangle(Coordinates.get(0)-Radius+StrokeWidth, // inner
                             Coordinates.get(1)-Radius+StrokeWidth,
                             Coordinates.get(0)+Radius-StrokeWidth,
                             Coordinates.get(1)+Radius-StrokeWidth,
                             FillColor);
            gc.PutText(Coordinates.get(0), Coordinates.get(1), Label,
                       TextHeight, FillColor.inverse());
        }
    }
    
    @Override
    public boolean ContainsCoordinate(Double x, Double y)
    {
        return (Coordinates.get(0)-Radius <= x)
            && (x <= Coordinates.get(0)+Radius)
            && (Coordinates.get(1)-Radius <= y)
            && (y <= Coordinates.get(1)+Radius);
    }
}
