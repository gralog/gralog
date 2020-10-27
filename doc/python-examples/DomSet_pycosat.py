#!/usr/bin/env python


import pycosat
from Gralog import *
from itertools import islice, combinations

def D(v):
    return v.getId()+1

def graph2cnf(g, k):

    cnf = []

    # for every vertex v: v is a dominator or one of its neighbours
    for v in g.getVertices():
        potential_dominators = []
        for w in v.getNeighbours():
            potential_dominators.append(D(w))
        potential_dominators.append(D(v))
        cnf.append(potential_dominators)

    # every set of k+1 vertices has a non-dominator
    for vertex_list in combinations(g.getVertices(), k+1):
        cnf.append([-D(v) for v in vertex_list])

        
    return(cnf)  


###### MAIN #####

g = Graph(None)
vertices = g.getVertices()
id_to_vertex = dict()
for v in vertices:
    id_to_vertex[v.getId()] = v

gPrint("What size of a domianting set are you looking for?")
k = g.requestInteger()
    
cnf = graph2cnf(g, k)

#gPrint("Computed cnf:")
#gPrint(str(cnf))
#gPrint("______")

solution = pycosat.solve(cnf)
#gPrint("Solution: "+ str(solution))



if solution == "UNSAT":
    gPrint("There is no dominating set of size " + str(k) + ".")
    exit(0)
if solution == "UNNKOWN":
    gPrint("This graph is too complicated, I could not find a dominating set of size "
           + str(k)
           + " or prove that no such exists.")
    exit(0)

# solution == "SAT"
for lit in solution:
    if lit > 0:
        v = id_to_vertex[lit - 1]
        v.setColor("Blue")
        
gPrint(str([lit-1 for lit in solution if lit > 0]))

