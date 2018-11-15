/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

package gralog.rendering.shapes;


/**
 * Describes a rectangle, contains two end points
 */
public class SizeBox {

    public Double width;
    public Double height;

    public Double angle;

    public SizeBox(Double width, Double height) {
        this.width = width;
        this.height = height;
    }
}
