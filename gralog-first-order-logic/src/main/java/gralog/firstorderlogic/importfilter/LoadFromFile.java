/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog.firstorderlogic.importfilter;

import gralog.structure.*;
import gralog.importfilter.*;
import java.io.*;

@ImportFilterDescription(
    name = "first order logic formulae",
    text = "load formulae from a file",
    url = "",
    fileExtension = "txt"
)
/**
 *
 */
public class LoadFromFile extends ImportFilter {

    private static int count = 1;

    @Override
    public Structure importGraph(InputStream stream,
        ImportFilterParameters params) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        String line;
        try (PrintWriter out = new PrintWriter(new BufferedWriter(
            new FileWriter("Formulae" + count + ".txt", true)))) {
            count++;
            while ((line = reader.readLine()) != null) {
                out.println(line);

            }
        }
        return new DirectedGraph();
    }
}
