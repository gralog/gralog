/* This file is part of Gralog, Copyright (c) 2016-2017 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.rendering;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class GralogColor {

    public enum Color {
        WHITE   (0xFFFFFF),
        BLACK   (0x000000),
        BLUE    (0x0000FF),
        GREEN   (0x00FF00),
        RED     (0xFF0000),
        GRAY    (0x808080),
        YELLOW  (0xFFFF00),
        CYAN    (0x00FFFF),
        MAGENTA (0xFF00FF),
        SILVER  (0xC0C0C0),
        MAROON	(0x800000),
        OLIVE	(0x808000),
        DARK_GREEN (0x008000),
        PURPLE	(0x800080),
        TEAL	(0x008080),
        NAVY    (0x000080),
        ORANGE  (0xFF4500);

        int value;

        Color(int c) {this.value = c;}
    }

    public int getValue(Color c){
        return c.value;
    }

    public static boolean isColor(String s){ // checks if s is a color from enum Color
        for (Color c : Color.values())
            if (c.name().equalsIgnoreCase(s))
                return true;
        return false;
    }

    public final short r;
    public final short g;
    public final short b;

    public GralogColor(String hex){
        this(Integer.parseInt(hex,16));
    }

    public GralogColor(int red, int green, int blue) {
        this.r = (short) (red & 0xFF);
        this.g = (short) (green & 0xFF);
        this.b = (short) (blue & 0xFF);

    }

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
    public GralogColor(GralogColor c){
        this.r = c.r;
        this.g = c.g;
        this.b = c.b;
    }
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.r;
        hash = 97 * hash + this.g;
        hash = 97 * hash + this.b;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final GralogColor other = (GralogColor) obj;
        return r == other.r && g == other.g && b == other.b;
    }

    @Override
    public String toString() {
        String s = this.toHtmlString();
        return s.substring(1,s.length());
    }

    public String toHtmlString() {
        final String hex = "0123456789ABCDEF";
        return "#" + hex.charAt(r >> 4 & 0x0F) + hex.charAt(r & 0x0F)
            + hex.charAt(g >> 4 & 0x0F) + hex.charAt(g & 0x0F)
            + hex.charAt(b >> 4 & 0x0F) + hex.charAt(b & 0x0F);
    }

    public static GralogColor parseColor(String htmlString) {
        int colorCode = 0;
        int i = 0;
        if (htmlString.charAt(i) == '#')
            i++;

        for (; i < htmlString.length(); i++) {
            int temp = 0;
            char ci = htmlString.charAt(i);
            if ('0' <= ci && ci <= '9')
                temp = ci - '0';
            else if ('a' <= ci && ci <= 'f')
                temp = (ci - 'a') + 10;
            else if ('A' <= ci && ci <= 'F')
                temp = (ci - 'A') + 10;

            colorCode = (colorCode << 4) | temp;
        }

        return new GralogColor(colorCode);
    }
    public static GralogColor parseColorAlpha(String htmlString) {
        int colorCode = 0;
        int i = 0;
        if (htmlString.charAt(i) == '#')
            i++;

        for (; i < htmlString.length() - 2; i++) {
            int temp = 0;
            char ci = htmlString.charAt(i);
            if ('0' <= ci && ci <= '9')
                temp = ci - '0';
            else if ('a' <= ci && ci <= 'f')
                temp = (ci - 'a') + 10;
            else if ('A' <= ci && ci <= 'F')
                temp = (ci - 'A') + 10;

            colorCode = (colorCode << 4) | temp;
        }

        return new GralogColor(colorCode);
    }

    public String name(){
        for (Color c : Color.values()){
            if (c.value == (
                    ((r&0x0FF)<<16)|((g&0x0FF)<<8)|(b&0x0FF)
                            )
               ){
                return c.name();
            }
        }
    return Integer.toHexString(r)+ Integer.toHexString(g) + Integer.toHexString(b);
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
