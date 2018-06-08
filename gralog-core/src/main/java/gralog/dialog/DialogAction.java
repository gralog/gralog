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
* select/deselect (all [vertices | edges] [ignored trash]) | <list id>
*
* filter <what> where|st|(such that)  <parameters> to <list> [ignored trash]
*
*   where <what> := (all vertices) | (all edges) | <list id>      // <list id> can be list of vertices or list of edges
*         <parameterS> := <parameterS> <parameterS> | <parameter>=<value> | <bool_parameter>
*         <parameter> := fill|(fill color) | stroke|(stroke color) | color | thickness | width | height | size | id | shape
*                                   | weight | type | (edge type)|edgeType
*                                   | degree | indegree | outdegree | butterfly?
*         <bool_parameter> := has selfloop | directed
*         <to>   := list <identifier> | subgraph <identifier>
*
*   if <list id> already exists, new elements are added to the list
*
* delete <list id>
* union <list id> <list id> to <list id>
* intersection <list id> <list id> to <list id>
* difference <list id> <list id> to <list id>
* symmetric difference <list id> <list id> to <list id>
* complement <list id> to <list id>
* connect <list id> <list id> <formula> to <list id>
*     formula := <arithmetic expression with at most one variable i> =|>|>=|<|<= <arithmetic expression with one variable i>
*     <arithmetic expression with one variable i> must return a natural number and be built from
*     natural constants, i, +, *, /, %, ceil(.), floor(.), .^., root(.,.), exp(.,.)
* add <obj id> to <list id>
* remove <obj id> to <list id>
* contract <list id>
* */