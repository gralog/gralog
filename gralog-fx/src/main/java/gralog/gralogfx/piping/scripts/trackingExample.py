#!/usr/bin/python
#TrackingExample.py
from Gralog import *
g = Graph("directed");

g2 = Graph("directed");

for x in range(5):
	v = g.addVertex(x,(x,x));
	v.setLabel(x);


gString = g.getGraph("xml");

g2.setGraph("xml",gString);
# gPrint(gString);