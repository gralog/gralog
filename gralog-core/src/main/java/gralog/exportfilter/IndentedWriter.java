/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.exportfilter;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

/**
 * A stream writer keeping track of indentation. Prefixes strings given to it
 * with the current indentation in form of spaces before writing them to the
 * underlying Writer object.
 */
public class IndentedWriter {

    private final int spacesPerIndent;
    private final String lineFeed;
    private final Writer out;
    private int indent = 0;

    /**
     * @param out             The stream in which to write
     * @param spacesPerIndent The number of spaces per indentation step
     */
    public IndentedWriter(Writer out, int spacesPerIndent) {
        this.spacesPerIndent = spacesPerIndent;
        this.lineFeed = System.getProperty("line.separator");
        this.out = out;
    }

    /**
     * Increases the indentation one step. Subsequent writes will receive more
     * leading spaces.
     */
    public void increaseIndent() {
        indent += spacesPerIndent;
    }

    /**
     * Decreases the indentation one step. Subsequent writes will receive fewer
     * leading spaces. Does nothing if the indent would become negative.
     */
    public void decreaseIndent() {
        if (indent >= 0)
            indent -= spacesPerIndent;
    }

    /**
     * Writes a string with the current indentation.
     *
     * @param s The string to write.
     * @throws IOException Throws if the underlying Writer instance throws.
     */
    public void write(String s) throws IOException {
        out.write(getIndentString() + s);
    }

    /**
     * Writes a string without indentation.
     *
     * @param s The string to write
     * @throws IOException Throws if the underlying Writer instance throws.
     */
    public void writeNoIndent(String s) throws IOException {
        out.write(s);
    }

    /**
     * Writes a string with the current indentation, followed by a newline.
     *
     * @param s The string to write
     * @throws IOException Throws if the underlying Writer instance throws.
     */
    public void writeLine(String s) throws IOException {
        if (s.isEmpty()) // Do not indent empty lines.
            out.write(lineFeed);
        else
            out.write(getIndentString() + s + lineFeed);
    }

    /**
     * Writes a string without indentation, followed by a newline.
     *
     * @param s The string to write
     * @throws IOException Throws if the underlying Writer instance throws.
     */
    public void writeLineNoIndent(String s) throws IOException {
        out.write(s + lineFeed);
    }

    private String getIndentString() {
        char[] buffer = new char[indent];
        Arrays.fill(buffer, 0, indent, ' ');
        return new String(buffer);
    }
}
