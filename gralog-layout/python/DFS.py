#!/usr/bin/python

import sys
sys.path.append('gralog-fx/src/main/java/gralog/gralogfx/piping')
from Lib import *

def breitensuche(g, start, suche, v):
    # adj ist die Adjazenzliste {knoten: [kanten]}
    # start ist der Index des Knoten, in dem die Suche beginnt
    # suche ist der gesuchte Knoten
    queue = []
    queue.insert(0,start)
    besucht = []
    while len(besucht) != len(v):
        aktiverKnoten = queue.pop(-1)
        aktiverKnoten.setFillColor("blue")
        g.pause()
        besucht.append(aktiverKnoten)
        adj = g.getNeighbours(aktiverKnoten)
        for andererKnoten in adj:
            if andererKnoten in besucht:
                continue
            if andererKnoten == suche:
                # Knoten gefunden
                return True
            queue.insert(0,andererKnoten)
    return False

graph = Graph(None)
v = graph.getAllVertices()
for ver in v:
    ver.setFillColor("white")
breitensuche(graph, v[0],v[-1], v)
