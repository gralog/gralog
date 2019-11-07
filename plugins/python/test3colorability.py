#!/usr/bin/env python

import sys
import math
import pycosat
from Gralog import *

# The formula for a given graph G is
#
# every vertex v has exactly one colour: R_v, B_v or G_v
#
#        \bigwedge_{v\in V(G)} R_v \lor B_v \lor G_v     # at least one colour
# \land  \bigwedge_{v\in V(G)} R_v \limp \neg B_v \land \neg G_v  # if R_v then not one of the others
# \land  \bigwedge_{v\in V(G)} B_v \limp \neg R_v \land \neg G_v  # if B_v then not one of the others
# \land  \bigwedge_{v\in V(G)} G_v \limp \neg R_v \land \neg B_v  # if G_v then not one of the others
#
# where R_v \limp \neg B_v \land \neg G_v \equiv (\neg R_v \lor \neg B_v) \land (\neg R_v \lor \neg G_v)
# and
# for every edge (u,v), not R_u or not R_v
#                   and not B_u or not B_v
#                   and not G_u or not G_v:
# \land \bigwedge_{(u,v)\in E(G)} \neg R_u or \neg R_v
# \land \bigwedge_{(u,v)\in E(G)} \neg B_u or \neg B_v
# \land \bigwedge_{(u,v)\in E(G)} \neg G_u or \neg G_v


# We encode variables as follows:
#
# R_v -> cantor_pair(0,v.id+1)
# B_v -> cantor_pair(1,v.id+1)
# G_v -> cantor_pair(2,v.id+1)
#
# where cantor_pair(x,y) = 1/2 * (x+y) * (x+y+1) + y
# is the Cantor pairing function (a bijection). The inverse is 
# inv_cantor_pair(z).

def cantor_pair(x,y):
    return( int(((x+y) * (x+y+1))/2.0 + y))

def inv_cantor_pair(z):
    if not isinstance(z,int):
        return 0
    w = int(math.floor((math.sqrt(1 + 8*z)-1)/2.0))
    t = int((w*(w+1))/2.0)
    y = z - t
    x = w - y
    return([x,y])

# For easy reading, wrap cantor_pair and inv_cantor_pair.
# Add +1 to v.id to avoid 0 as a result of cantor_pair.

def R(v):
    return(cantor_pair(1,v.getId()+1))

def B(v):
    return(cantor_pair(2,v.getId()+1))

def G(v):
    return(cantor_pair(3,v.getId()+1))

def graph2cnf(g):

    cnf = []
    
    for v in g.getVertices():

        # at least one colour
        cnf.append([R(v), B(v), G(v)])
#        gPrint("[R(v), B(v), G(v)] = " + str([R(v), B(v), G(v)]))

        # at most one colour
        cnf.append([-R(v), -G(v)])
        cnf.append([-R(v), -B(v)])
        cnf.append([-B(v), -G(v)])
        

    # endpoints of every edge are coloured properly

    for e in g.getEdges():
        u = e.getSource()
        v = e.getTarget()
        cnf.append([-R(u), -R(v)])
        cnf.append([-B(u), -B(v)])
        cnf.append([-G(u), -G(v)])

    return(cnf)

def int_to_color(i):
    if isinstance(i,int):
        if i == 1:
            return "R"
        if i == 2:
            return "B"
        if i == 3:
            return "G"
    return 0        

def human_cnf(cnf):
#    gPrint("Human:")
    cnf_human = []
    for clause in cnf:
#        gPrint("clause: " + str(clause))
        clause_human = []
        for literal in clause:
            letter = ""
#            gPrint("literal: " + str(literal))
            variable = literal
            neg = False
            if literal < 0:
                neg = True
                variable = -literal
#            gPrint("variable: " + str(variable))
            [color,id] = inv_cantor_pair(variable)
            id = int(id)
            id = id - 1
#            gPrint("color:" + str(color) + "; id: " + str(id))
            if (color == 1):
                letter = "R"
            if (color == 2):
                letter = "B"
            if (color == 3):
                letter = "G"
            if neg:
                letter = "-" + letter
#            gPrint("translated literal: " + letter + str(id) )
            clause_human.append(letter + str(id))
#        gPrint("translated clause: " + str(clause_human) )
        cnf_human.append(clause_human)
    return(cnf_human)

###### MAIN #####

g = Graph(None)
vertices = g.getVertices()
id_to_vertex = dict()
for v in vertices:
    id_to_vertex[v.getId()] = v

cnf = graph2cnf(g)

#gPrint("Computed cnf:")
#gPrint(str(cnf))
gPrint("CNF:")
gPrint("["
       + '; '.join([ "[%s]" % ','.join(clause) for clause in human_cnf(cnf)])
       + "]")

gPrint("______")

solution = pycosat.solve(cnf)

#gPrint("Solution: "+ str(solution))



if isinstance(solution,list):   # not UNSAT or UNNKOWN
    gPrint("Solution:")
    for variable in solution:
        if int(variable) > 0:
            [color,vertexId] = inv_cantor_pair(int(variable))
            vertexId = int(vertexId) - 1
            gPrint(int_to_color(color) + str(vertexId))
#            gPrint("variable: " + str(variable) + "; color:" + str(color) + "; id: " + str(vertexId))
            v = id_to_vertex[vertexId]         
            if color == 1:
                v.setColor("Red")
                continue
            if color == 2:
                v.setColor("Blue")
                continue
            if color == 3:
                v.setColor("Green")
                continue
    exit()
if solution == "UNSAT":
    gPrint("There is no 3-colouring of this graph.")
else:
    gPrint("This graph is too complicated, I could not find a 3-colouring or prove that no such 3-colouring exists.")

