package gralog.finitegame.structure;

import gralog.structure.*;
import gralog.plugins.XmlName;
import gralog.rendering.GralogColor;
import gralog.rendering.GralogGraphicsContext;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@XmlName(name = "finitegameposition")
public class FiniteGamePosition extends Vertex {

    public Boolean player1Position = true;

    @Override
    public Element toXml(Document doc, String id) throws Exception {
        Element vnode = super.toXml(doc, id);
        vnode.setAttribute("player1position", player1Position ? "true" : "false");
        return vnode;
    }

    @Override
    public String fromXml(Element vnode) {
        String id = super.fromXml(vnode);
        if (vnode.hasAttribute("player1position"))
            player1Position = (vnode.getAttribute("player1position").equals("true"));
        return id;
    }

    @Override
    public void render(GralogGraphicsContext gc, Set<Object> highlights) {
        if (player1Position) {
            super.render(gc, highlights);
        }
        else {
            if (highlights != null && highlights.contains(this))
                gc.fillRectangle(coordinates.get(0) - radius - 0.07, // outer
                                 coordinates.get(1) - radius - 0.07,
                                 coordinates.get(0) + radius + 0.07,
                                 coordinates.get(1) + radius + 0.07,
                                 GralogColor.RED);

            gc.fillRectangle(coordinates.get(0) - radius, // outer
                             coordinates.get(1) - radius,
                             coordinates.get(0) + radius,
                             coordinates.get(1) + radius,
                             strokeColor);
            gc.fillRectangle(coordinates.get(0) - radius + strokeWidth, // inner
                             coordinates.get(1) - radius + strokeWidth,
                             coordinates.get(0) + radius - strokeWidth,
                             coordinates.get(1) + radius - strokeWidth,
                             fillColor);
            gc.putText(coordinates.get(0), coordinates.get(1), label,
                       textHeight, fillColor.inverse());
        }
    }

    @Override
    public boolean containsCoordinate(double x, double y) {
        return (coordinates.get(0) - radius <= x)
               && (x <= coordinates.get(0) + radius)
               && (coordinates.get(1) - radius <= y)
               && (y <= coordinates.get(1) + radius);
    }
}
