package gralog.dialog;

public enum DialogAction {
    SELECTALL, SELECT_ALL_VERTICES, SELECT_ALL_EDGES,
    SELECT_LIST, // decide: select list (can be edges, vertices) or select list of vertices/ select list of edges
    DESELECTALL, DESELECT_ALL_VERTICES, DESELECT_ALL_EDGES,
    DESELECT_LIST, // decide: deselect list (can be edges, vertices) or deselect list of vertices/ select list of edges
    ADD_VERTEX, ADD_EDGE, ADD_LIST_VERTICES, ADD_LIST_EDGES,
    CONNECT_LIST_A_TO_LIST_B,
    FILTER,
    NONE
}

/* possible commands
* select/deselect all [vertices | edges] [ignored trash]
*
* filter <what> to <list> [ignored trash]
*
*   where <what> := all vertices | all edges | <list id> // <list id> can be list of vertices or list of edges
*         <to>   := list <identifier> | subgraph <identifier>
*
*
*
* */