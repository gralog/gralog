#!/usr/bin/python
#HelloWorld.py
from Gralog import *
g = Graph("undirected");
#end boiler plate

v = g.addVertex();
v2 = g.addVertex();
g.addEdge(v2,v2);

g2 = Graph("directed");
gString = g.getGraph("GTGF");
g2.setGraph("GTGF",gString);
g2.addVertex();