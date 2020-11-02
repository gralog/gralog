#!/usr/bin/env python3

import sys
import math
from z3 import *
from Gralog import *


def graph2z3(g):

    s = Solver()

    global R
    R = {}
    global G
    G = {}
    global B
    B = {}

    for v in g.getVertices():
        v = v.getId()
        R[v] = Bool('R_%i' % v)
        G[v] = Bool('G_%i' % v)
        B[v] = Bool('B_%i' % v)

    for v in g.getVertices():
        v = v.getId()
        s.add(Or(R[v], G[v], B[v]))

        s.add(Or(Not(R[v]), Not(G[v])))
        s.add(Or(Not(R[v]), Not(B[v])))
        s.add(Or(Not(B[v]), Not(G[v])))

    for e in g.getEdges():
        u = e.getSource().getId()
        v = e.getTarget().getId()
        if u > v:
            continue
        s.add(Or(Not(R[u]), Not(R[v])))
        s.add(Or(Not(G[u]), Not(G[v])))
        s.add(Or(Not(B[u]), Not(B[v])))
    
    return(s)



###### MAIN #####

g = Graph(None)
vertices = g.getVertices()
id_to_vertex = dict()
for v in vertices:
    id_to_vertex[v.getId()] = v

s = graph2z3(g)

result = s.check()

sat     = CheckSatResult(Z3_L_TRUE)
unsat   = CheckSatResult(Z3_L_FALSE)
unknown = CheckSatResult(Z3_L_UNDEF)

if result == sat:
    m = s.model()
elif result == unsat:
    gPrint("Not 3-colourable")
    exit()
else:
    gPrint("The graph is too complicated, I could not solve the instance.")
    exit()

for v in g.getVertices():
    if is_true(m[R[v.getId()]]):
        v.setColor("Red")
        continue
    if is_true(m[G[v.getId()]]):
        v.setColor("Green")
        continue
    if is_true(m[B[v.getId()]]):
        v.setColor("Blue")
        continue
    gPrint("Error: vertex " + str(v) + " has no colour, terminating.")
    exit(1)
