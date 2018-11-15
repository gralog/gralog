/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package gralog;

import gralog.preferences.Preferences;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Tracks a persistent list of recent queries.
 */
public final class RecentQueries {

    public static final int MAX_RECENT_QUERIES = 20;

    private RecentQueries() {
    }

    private static List<String> respectCapacity(List<String> queries) {
        if (queries.size() > MAX_RECENT_QUERIES)
            return queries.subList(0, queries.size() - 1);
        return queries;
    }

    /**
     * Returns a list of the most recent queries by loading it from the
     * preferences. Use the scope to choose the class to whom this query list
     * corresponds. Returns at most UIConstants.MAX_RECENT_QUERIES many queries.
     *
     * @param scope A class identifying the scope.
     * @return A list of recent queries.
     */
    public static List<String> get(Class scope) {
        String queries = Preferences.getString(scope, "queries", "");
        if (queries.isEmpty())
            return new LinkedList<>();
        else
            return respectCapacity(new LinkedList<>(Arrays.asList(queries.split("\n"))));
    }

    /**
     * Returns the most recent queries joined by "\n" by loading the list from
     * the preferences. Use the scope to choose the class to whom this query
     * list corresponds. Returns at most UIConstants.MAX_RECENT_QUERIES many
     * queries.
     *
     * @param scope A class identifying the scope.
     * @return A list of recent queries.
     */
    public static String getString(Class scope) {
        return String.join("\n", RecentQueries.get(scope));
    }

    /**
     * Adds a query to the front of the list of the most recent queries. If the
     * query already exists, it will be removed first. If the list of the recent
     * queries grows beyond UIConstants.MAX_RECENT_QUERIES, the list will be
     * truncated to this size.
     *
     * @param scope A class identifying the scope.
     * @param query The query to add to the most recent queries.
     */
    public static void add(Class scope, String query) {
        List<String> queries = RecentQueries.get(scope);

        queries.remove(query);
        queries.add(0, query);
        queries = respectCapacity(queries);

        Preferences.setString(scope, "queries", String.join("\n", queries));
    }
}
