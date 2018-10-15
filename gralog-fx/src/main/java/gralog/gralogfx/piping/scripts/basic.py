#!/usr/bin/python

import sys
from Gralog import *
import igraph as ig


#g = Graph();#type \in buechi, directed, etc. or None

dir = Graph("directed")
#buech = Graph("buechi")
for i in range(10):
    dir.addVertex((0, i))
dir.message('LALALA')

vert = dir.getAllVertices()

for v_i in range(len(vert)-1):
    dir.setVertexFillColor(vert[v_i],"blue")
    dir.pause()
    dir.setVertexFillColor(vert[v_i],"red")
    dir.pause()
    dir.setVertexFillColor(vert[v_i],"green")
    dir.pause()
    dir.setVertexFillColor(vert[v_i],"white")
    dir.pause()
    dir.setVertexFillColor(vert[v_i],"black")
    dir.pause()
    dir.setVertexShape(vert[v_i],'diamond')
    dir.pause()
exit()
