#!/usr/bin/env python3

############################################
# Copyright (c) 2012 Ganesh Gopalakrishnan ganesh@cs.utah.edu
#
# Check if the given graph has a Hamiltonian cycle.
#
# Author: Ganesh Gopalakrishnan ganesh@cs.utah.edu
############################################
#
# Edited by Roman Rabinovich in 2019 to use it with Gralog
#
############################################

from z3 import *
from Gralog import *

def gencon(gr):
    """
        Input a graph as an adjacency list, e.g. {0:[1,2], 1:[2], 2:[1,0]}.
        Produces solver to check if the given graph has
        a Hamiltonian cycle. Query the solver using s.check() and if sat,
        then s.model() spells out the cycle. Two example graphs from
        http://en.wikipedia.org/wiki/Hamiltonian_path are tested.
        =======================================================
        
        Explanation:
        
        Generate a list of Int vars. Constrain the first Int var ("Node 0") to be 0.
        Pick a node i, and attempt to number all nodes reachable from i to have a
        number one higher (mod L) than assigned to node i (use an Or constraint).
        
        =======================================================
        """
    L = len(gr)
    cv = [Int(i) for i in range(L)]
    s = Solver()
    s.add(cv[0]==0)
    for i in range(L):
        s.add(Or([cv[j]==(cv[i]+1)%L for j in gr[i]]))
    return s


# get the graph
g = Graph(None)

# convert the graph to a dict as needed by gencon()
edges = g.getEdges()
gr = {}
for vertex in g.getVertices():
    gr[vertex.getId()] = []
    for neighb in vertex.getNeighbours():
        gr[vertex.getId()].append(neighb.getId())
#gPrint("The graph: " + str(gr))

# make a z3 instance
z3_instance = gencon(gr)

# specify the timeout
gPrint("Specify a timeout (in seconds, 0: no timeout):")
i = g.requestInteger()
i *= 1000
if i > 0:
    z3_instance.set("timeout", i)

# compute the result
result = z3_instance.check()


# print the result in Gralog
if result == sat:
    result_str = ""
    for d in z3_instance.model().decls():
        result_str += str(z3_instance.model()[d]) + ", "
    gPrint("There is a Hamiltonian cycle: " + result_str[:-2])
else:
    if result == unsat:
        gPrint("There is no Hamiltonian cycle.")
    else:
        gPrint("I could not solve the instance.")
