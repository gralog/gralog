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


gPrint("What size of a domianting set are you looking for?")
k = g.requestInteger()


# make a z3 instance
z3_instance = gencon(g, k)

# specify the timeout
gPrint("Specify a timeout (in seconds, 0: no timeout):")
i = g.requestInteger()
i *= 1000
if i > 0:
    z3_instance.set("timeout", i)

start_time = time()
# compute the result
result = z3_instance.check()
gPrint("Computation time " + str(time() - start_time))

# print the result in Gralog
if result == sat:
    m = z3_instance.model()
    # convert z3 variables to strings, delete the leading 'D' and convert them to int
    result_list = [int(str(var)[1:]) for var in m if m[var]]
    result_list.sort()
    gPrint("There is a dominating set of size " + str(k) + ": " + str(result_list))
    for id in result_list:
        id_to_vertex[id].setColor("Blue")
else:
    if result == unsat:
        gPrint("There is no dominating set of size " + str(k) + ".")
    else:
        gPrint("This graph is too complicated, I could not find a dominating set of size "
           + str(k)
           + " or prove that no such exists.")

        

# for lit in solution:
#     if lit > 0:
#         v = id_to_vertex[lit - 1]
#         v.setColor("Blue")
        
# gPrint(str([lit-1 for lit in solution if lit > 0]))

