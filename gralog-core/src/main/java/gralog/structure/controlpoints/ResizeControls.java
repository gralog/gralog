/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

package gralog.structure.controlpoints;

import gralog.rendering.GralogColor;
import gralog.rendering.GralogGraphicsContext;
import gralog.rendering.Vector2D;
import gralog.structure.IMovable;
import gralog.structure.Vertex;

import java.io.Serializable;

public class ResizeControls implements IMovable, Serializable {
    public static final double CLICK_RADIUS = 0.1;
    public boolean active;

    public Vertex v;
    RControl[] cs = new RControl[4];
    public ResizeControls(Vertex v) {
        this.v = v;
    }

    public void setCoordinates() {
        Vector2D center = v.getCoordinates();
        double width = v.shape.sizeBox.width;
        double height = v.shape.sizeBox.height;
        cs[0] = new RControl(0, this, center.getX() - width / 2, center.getY() - height / 2);
        cs[1] = new RControl(1, this, center.getX() + width / 2, center.getY() - height / 2);
        cs[2] = new RControl(2, this, center.getX() + width / 2, center.getY() + height / 2);
        cs[3] = new RControl(3, this, center.getX() - width / 2, center.getY() + height / 2);
    }

    @Override
    public void move(Vector2D vec) {
        for (int i = 0; i < 4; i++)
            cs[i].position = cs[i].position.plus(vec);
    }

    private void updateVertexShape() {
        v.shape.setWidth(Math.abs(cs[0].position.getX() - cs[2].position.getX()));
        v.shape.setHeight(Math.abs(cs[0].position.getY() - cs[2].position.getY()));
        v.setCoordinates((cs[1].position.getX() + cs[0].position.getX()) / 2,
                (cs[2].position.getY() + cs[1].position.getY()) / 2);
    }

    public IMovable findObject(double x, double y) {


        for (int i = 0; i < 4; i++) {
            Vector2D vec = cs[i].position;
            Vector2D trgt = new Vector2D(x, y);
            if (vec.minus(trgt).length() < CLICK_RADIUS) {
                return cs[i];
            }
        }
        return null;
    }


    public void render(GralogGraphicsContext gc) {
        Vector2D pos = cs[0].position;
        Vector2D shape = (new Vector2D(v.shape.sizeBox.width, v.shape.sizeBox.height));

        for (int i = 0; i < 4; i++) {
            gc.line(cs[i].position.getX(), cs[i].position.getY(),
                    cs[(i + 1) % 4].position.getX(), cs[(i + 1) % 4].position.getY(),
                    GralogColor.BLACK, 0.01, GralogGraphicsContext.LineType.DASHED);
        }
        double ovl = 0.1;
        for (int i = 0; i < 4; i++) {
            pos = cs[i].position;
            gc.fillOval(pos.getX(), pos.getY(), ovl, ovl);
        }
    }

    public static class RControl implements IMovable {

        public ResizeControls parent;
        public Vector2D position;
        public int id;

        public RControl(int id, ResizeControls parent, double x, double y) {
            this.id = id;
            this.parent = parent;
            position = new Vector2D(x, y);
        }

        @Override
        public void move(Vector2D vec) {
            position = position.plus(vec);

            int prev = (4 + id - 1) % 4;
            int next = (4 + id + 1) % 4;
            if (id % 2 == 1) {
                parent.cs[next].setX(position.getX());
                parent.cs[prev].setY(position.getY());
            } else {
                parent.cs[prev].setX(position.getX());
                parent.cs[next].setY(position.getY());
            }
            parent.updateVertexShape();
        }

        public Vector2D parentCenter() {
            return parent.v.getCoordinates();
        }

        public RControl getNextSibling() {
            return parent.cs[(4 + id + 1) % 4];
        }

        public RControl getPreviousSibling() {
            return parent.cs[(4 + id - 1) % 4];
        }

        public RControl getDiagonalSibling() {
            return parent.cs[(4 + id + 2) % 4];
        }

        public void setX(double x) {
            position = new Vector2D(x, position.getY());
        }

        public void setY(double y) {
            position = new Vector2D(position.getX(), y);
        }
    }

}
