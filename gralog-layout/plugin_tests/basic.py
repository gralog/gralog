#!/usr/bin/python

import sys
sys.path.append('/home/michelle/gralog/gralog/gralog-fx/src/main/java/gralog/gralogfx/piping')
from Lib import *
import igraph as ig


#g = Graph();#type \in buechi, directed, etc. or None

dir = Graph("directed")
#buech = Graph("buechi")
for i in range(10):
    dir.addVertex(0, i)
dir.message('LALALA')

vert = dir.getAllVertices()
for v in vert:
    v.setLabel(str(v.id))

for v_i in range(len(vert)-1):
    dir.setVertexFillColor(vert[v_i],"red")
    dir.pause()
    dir.setVertexShape(vert[v_i],'diamond')
exit()
