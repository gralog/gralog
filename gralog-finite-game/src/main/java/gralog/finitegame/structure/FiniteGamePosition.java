/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.finitegame.structure;

import gralog.preferences.Configuration;
import gralog.structure.*;
import gralog.plugins.XmlName;
import gralog.rendering.GralogColor;
import gralog.rendering.GralogGraphicsContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@XmlName(name = "finitegameposition")
public class FiniteGamePosition extends Vertex {

    public Boolean player1Position = true;

    public FiniteGamePosition(){
        super();
    }

    public FiniteGamePosition(Configuration config){
        super(config);
    }

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
    public void render(GralogGraphicsContext gc, Highlights highlights) {
        if (player1Position) {
            super.render(gc, highlights);
        } else {
            if (highlights.isSelected(this))
                gc.fillRectangle(coordinates.getX() - radius - 0.07, // outer
                    coordinates.getY() - radius - 0.07,
                    coordinates.getX() + radius + 0.07,
                    coordinates.getY() + radius + 0.07,
                    GralogColor.RED);

            gc.fillRectangle(coordinates.getX() - radius, // outer
                coordinates.getY() - radius,
                coordinates.getX() + radius,
                coordinates.getY() + radius,
                strokeColor);
            gc.fillRectangle(coordinates.getX() - radius + strokeWidth, // inner
                coordinates.getY() - radius + strokeWidth,
                coordinates.getX() + radius - strokeWidth,
                coordinates.getY() + radius - strokeWidth,
                fillColor);
            gc.putText(coordinates, label,
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
