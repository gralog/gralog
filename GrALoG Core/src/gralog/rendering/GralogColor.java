/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gralog.rendering;

/**
 *
 * @author viktor
 */
public class GralogColor {

    public short r;
    public short g;
    public short b;

    public GralogColor(short red, short green, short blue) {
        this.r = (short) (red & 0xFF);
        this.g = (short) (green & 0xFF);
        this.b = (short) (blue & 0xFF);
    }

    public GralogColor(int rgb) {
        this((short) ((rgb >> 16) & 0xFF),
             (short) ((rgb >> 8) & 0xFF),
             (short) (rgb & 0xFF));
    }

    public boolean equals(GralogColor c) {
        return this.r == c.r
               && this.g == c.g
               && this.b == c.b;
    }

    public String toHtmlString() {
        final String hex = "0123456789ABCDEF";
        return "#" + hex.charAt(r >> 4 & 0x0F) + hex.charAt(r & 0x0F)
               + hex.charAt(g >> 4 & 0x0F) + hex.charAt(g & 0x0F)
               + hex.charAt(b >> 4 & 0x0F) + hex.charAt(b & 0x0F);
    }

    public static GralogColor parseColor(String HtmlString) {

        int ColorCode = 0;
        int i = 0;
        if (HtmlString.charAt(i) == '#')
            i++;

        for (; i < HtmlString.length(); i++) {
            int temp = 0;
            char ci = HtmlString.charAt(i);
            if ('0' <= ci && ci <= '9')
                temp = ci - '0';
            else if ('a' <= ci && ci <= 'f')
                temp = (ci - 'a') + 10;
            else if ('A' <= ci && ci <= 'F')
                temp = (ci - 'A') + 10;

            ColorCode = (ColorCode << 4) | temp;
        }

        return new GralogColor(ColorCode);
    }

    public GralogColor inverse() {
        return new GralogColor((short) (255 - r), (short) (255 - g), (short) (255 - b));
    }

    public static final GralogColor BLACK = new GralogColor(0x000000);
    public static final GralogColor RED = new GralogColor(0xFF0000);
    public static final GralogColor GREEN = new GralogColor(0x00FF00);
    public static final GralogColor BLUE = new GralogColor(0x0000FF);
    public static final GralogColor WHITE = new GralogColor(0xFFFFFF);
}
