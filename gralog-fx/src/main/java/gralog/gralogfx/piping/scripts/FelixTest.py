#!/usr/bin/python
#HelloWorld.py
from Gralog import *
g = Graph("directed");
import math;
# g2 = Graph("directed");
#end boiler plate

# for x in range(10):
# 	v = g.addVertex();
# 	v.setCoordinates((x,None));
# 	g.pause();
# 	v.setCoordinates((None,x));

# gString = g.getGraph("GTGF");
# g2.setGraph("GTGF",gString);

r = 255
g.addVertex(0,(0,0));
for x in range(1,r):
	v = g.addVertex(x);
	cosVal = 20*math.cos(math.pi*2*x/r);
	sinVal = 20*math.sin(math.pi*2*x/r);
	g.track(x,(cosVal,sinVal));
	v.setCoordinates((cosVal,sinVal));
	# v.setCoordinates((x,x));


g.pause();
first = g.getAllVertices()[0];


for u in g.getAllVertices()[1:]:
	e = g.addEdge(first,u);
	e.getLabel();
	g.setEdgeContour(e,"dashed");

g.pause(("hello","world"));
for v in g.getOutgoingNeighbours(first):
	v.setColor(colorRGB=(0,0,v.getId()));
	v.setLabel(v.getId());

