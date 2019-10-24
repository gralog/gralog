/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.automaton;

import gralog.plugins.XmlName;
import gralog.preferences.Configuration;
import gralog.rendering.GralogColor;
import gralog.rendering.GralogGraphicsContext;
import gralog.structure.Highlights;
import gralog.structure.Vertex;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 */
@XmlName(name = "state")
public class State extends Vertex {

    public Boolean startState = false;
    public Boolean finalState = false;

    public Double initialMarkAngle = 0d; // degrees
    public Double initialMarkLength = 0.7d; // cm
    public Double initialMarkWidth = 2.54 / 96; // cm
    public Double initialMarkHeadAngle = 40d; // degrees
    public Double initialMarkHeadLength = 0.4d; // cm
    public GralogColor initialMarkColor = GralogColor.BLACK;


    public State() {
        super();
    }

    public State(Configuration config) {
        super(config);
    }

    /**
     * Initializes lots of variables from a given configuration.
     * @param config
     */
    @Override
    protected void initWithConfig(Configuration config) {
        super.initWithConfig(config);

        //TODO: additional initializations from config have to be made
    }
    @Override
    public Element toXml(Document doc, String id) throws Exception {
        Element vnode = super.toXml(doc, id);
        vnode.setAttribute("initial", startState ? "true" : "false");
        vnode.setAttribute("final", finalState ? "true" : "false");
        vnode.setAttribute("initialmarkangle", initialMarkAngle.toString());
        vnode.setAttribute("initialmarklength", initialMarkLength.toString());
        vnode.setAttribute("initialmarkwidth", initialMarkWidth.toString());
        vnode.setAttribute("initialmarkheadangle", initialMarkHeadAngle.toString());
        vnode.setAttribute("initialmarkheadlength", initialMarkHeadLength.toString());
        vnode.setAttribute("initialmarkcolor", initialMarkColor.toHtmlString());
        return vnode;
    }

    @Override
    public String fromXml(Element vnode) {
        String id = super.fromXml(vnode);
        startState = (vnode.getAttribute("initial").equals("true"));
        finalState = (vnode.getAttribute("final").equals("true"));
        if (vnode.hasAttribute("initialmarkangle")) {
            initialMarkAngle = Double.parseDouble(vnode.getAttribute("initialmarkangle"));
        }
        if (vnode.hasAttribute("initialmarklength")) {
            initialMarkLength = Double.parseDouble(vnode.getAttribute("initialmarklength"));
        }
        if (vnode.hasAttribute("initialmarkwidth")) {
            initialMarkWidth = Double.parseDouble(vnode.getAttribute("initialmarkwidth"));
        }
        if (vnode.hasAttribute("initialmarkheadangle")) {
            initialMarkHeadAngle = Double.parseDouble(vnode.getAttribute("initialmarkheadangle"));
        }
        if (vnode.hasAttribute("initialmarkheadlength")) {
            initialMarkHeadLength = Double.parseDouble(vnode.getAttribute("initialmarkheadlength"));
        }
        if (vnode.hasAttribute("initialmarkcolor")) {
            initialMarkColor = GralogColor.parseColor(vnode.getAttribute("initialmarkcolor"));
        }

        return id;
    }

    @Override
    public void render(GralogGraphicsContext gc, Highlights highlights) {
        super.render(gc, highlights);

        //TODO: make it look good

        if (this.finalState) {
            gc.circle(getCoordinates(), radius - 3 * strokeWidth, strokeColor);
            gc.circle(getCoordinates(), radius - 4 * strokeWidth, fillColor);
        }
        /*
        if (startState) {
            Vector2D center = coordinates;
            Vector2D intersectionOffset = new Vector2D(
                -radius * Math.cos(initialMarkAngle / 180 * Math.PI),
                -radius * Math.sin(initialMarkAngle / 180 * Math.PI));
            Vector2D intersection = center.plus(intersectionOffset);
            Vector2D headStart = intersection.plus(intersectionOffset.normalized().multiply(initialMarkLength));

            gc.arrow(headStart, intersection,
                initialMarkHeadAngle, initialMarkHeadLength,
                initialMarkColor, initialMarkWidth);
        }
        */
    }
}
