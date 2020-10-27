#!/usr/bin/env python3

###################################
# 2019
#
# Finds a Hamiltonian cycle in the current graph with help of igraph's get_subisomorphisms_vf2 function.
# The latter finds subgraphs isomorhic to a given graph. Here it is called with a cycle of length
# equal to the number of vertices in the given graph. Slow...
#
#Author: Roman Rabinovich roman.rabinovich@tu-berlin.de
###################################

from Gralog import *
import igraph as ig

###### MAIN #####

gPrint("Finding a Hamiltonian cycle with igraph:")

g = Graph(None)
vertices = g.getVertices()

# check simple cases first
if len(vertices) == 0:
    gPrint("The graph is empty, there is a trivial Hamiltonian Cycle.")
    exit(0)

if len(vertices) > len(g.getAllEdges()) + 1:
    gPrint("No Hamiltonian cycle")
    exit(0)

for v in vertices:
    if len(v.getIncidentEdges()) < 2:
        gPrint("No Hamiltonian cycle")
        exit(0)


id_to_vertex = dict()
for v in vertices:
    id_to_vertex[v.getId()] = v


# create a cycle of length g.numVertices
cycle = ig.Graph()

for i in range(len(vertices)):
    cycle.add_vertex()


# get the first vertex

for i in range(len(cycle.vs)-1):
    cycle.add_edge(i, i+1)
cycle.add_edge(len(cycle.vs)-1, 0)

g_ig = g.toIgraph()
list_g = {}
i = 0
for v in vertices:
    list_g[i] = v.getId()
    i += 1
        
isomorphisms = g_ig.get_subisomorphisms_vf2(cycle)

if len(isomorphisms) > 0:
    isomorphism = isomorphisms[0]
else:
    gPrint("No Hamiltonian cycle")
    exit(0)

output = []
for i in isomorphisms[0]:
    output.append(list_g[i])
gPrint("The cycle is represented by a sequence of vertex ids:")
gPrint(str(output))
exit(0)

