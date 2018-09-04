#!/usr/bin/python
#HelloWorld.py
from Gralog import *
g = Graph("directed");
# g2 = Graph("directed");
#end boiler plate

# for x in range(10):
# 	v = g.addVertex();
# 	v.setCoordinates((x,None));
# 	g.pause();
# 	v.setCoordinates((None,x));

# gString = g.getGraph("GTGF");
# g2.setGraph("GTGF",gString);

for x in range(100):
	v = g.addVertex();
	# v.setCoordinates((x,x));


g.pause();
first = g.getAllVertices()[0];
gPrint("first: " + str(first));

for u in g.getAllVertices()[1:]:
	e = g.addEdge(u,first);
	g.setEdgeContour(e,"dashed");

g.pause();
for v in g.getIncomingNeighbours(first):
	v.setColor(colorRGB=(v.getId(),v.getId(),v.getId()));