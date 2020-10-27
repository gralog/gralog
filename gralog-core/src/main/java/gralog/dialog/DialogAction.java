/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

package gralog.dialog;

public enum DialogAction {
    SELECT_ALL,
    SELECT_ALL_VERTICES,
    SELECT_ALL_EDGES,
    SELECT_LIST, // decide: select list (can be edges, vertices) or select list of vertices/ select list of edges
    DESELECT_ALL,
    DESELECT_ALL_VERTICES,
    DESELECT_ALL_EDGES,
    DESELECT_LIST, // decide: deselect list (can be edges, vertices) or deselect list of vertices/ select list of edges
    ADD_VERTEX,
    ADD_EDGE,
    ADD_LIST_VERTICES,
    ADD_LIST_EDGES,
    CONNECT_LIST_A_TO_LIST_B,
    FILTER,
    TWO_LISTS_OP,
    DELETE,
    REMOVE,
    COMPLEMENT,
    PRINT,
    FIND_LIST,
    FIND_VERTEX,
    FIND_EDGE,
    FIND_GRAPH_ELEMENT,
    LAYOUT,
    SET_DIRECTED,
    SET_UNDIRECTED,
    SET_COLOR, SET_LABEL, SET_TYPE, SET_EDGETYPE, SET_THICKNESS, SET_WEIGHT, SET_FILL, SET_STROKE, SET_SHAPE, SET_WIDTH, SET_HEIGHT,
    CONNECT_CLIQUE,
    CONNECT_PATH,
    CONNECT_CYCLE,
    CONNECT_BICLIQUE,
    CONNECT_MATCHING,
    CONNECT_TCLOSURE,
    CONNECT_FORMULA,
    CONNECT_2_LISTS_FORMULA,
    CONNECT_SELFLOOP,
    HELP,
    NONE,
    SORT }

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
 * remove|delete <obj id> from <list id>
 * contract edge <edge list id>
 * contract edges <edge id>
 * contract subgraph <subgraph id>
 * contract subgraph <vertex list id>
 * butterfly contract <edge id>
 * butterfly contract <edge list id>
 * generarte <graph> [<generateParameterS>]
 *   <graph> := grid <n,m>| wheel <n> | cycle <n> | path <n> | clique <n> | torus <n,m> | complete tree <deg,depth>
 *                        | cylindrical grid <n,m> | petersen | haus vom nikolaus | bull | butterfly | duerer |
 *                        | erdos-renyi <n,p> | complete bipartite <n,m> | matching <n> | cube | 3-dim cube |
 *                        | parallelepiped <n,m,k> | star <n>
 *
 * subdivide edge <edge id>
 * subdivide <edge list id>
 * */
