#!/usr/bin/env python3

from time import time
from z3 import *
from Gralog import *


def gencon(g, k):

    ids = [v.getId() for v in g.getVertices()]

    D = [Bool('D%i' % i) for i in ids ]
    s = Solver()
    # the sum of D[i]'s set on True is at most k
    s.add(PbLe([(Bool('D%i' % i),1) for i in ids], k))

    # for every vertex v: v is a dominator or one of its neighbours
    for v in g.getVertices():
        potential_dominators = []
        s.add(Or(D[v.getId()], Or([D[w.getId()] for w in v.getNeighbours() ])))

    return s


###### MAIN #####

# get the graph
g = Graph(None)

id_to_vertex = dict()
for v in g.getVertices():
    id_to_vertex[v.getId()] = v


gPrint("{:<24}\t{:^20}\t{:<20}".format("Size of a dominating set", "Result", "Computation time (sec.)"))
result_list = []
for i in range(len(g.getVertices()), 1, -1):
    z3_instance = gencon(g, i)
    start_time = time()
    result = z3_instance.check()
    result_time = time() - start_time
    if result == sat:
        m = z3_instance.model()
        result_list = [int(str(var)[1:]) for var in m if m[var]]
        result_print = "yes"
        min_size = i
    else:
        if result == unknown:
            gPrint("This graph is too complicated, I could not find any dominating set.")
            exit(0)
        else: # result == unsat
            result_print = "no"
    gPrint("{:^30}{:>30}{:20.2f}".format(i, result_print, result_time))
    if result == unsat:
        break

result_list.sort()
gPrint("")
gPrint("The smallest dominating set is of size " + str(min_size) + ": " + str(result_list))
for id in result_list:
    id_to_vertex[id].setColor("Blue")
