#!/usr/bin/python
#HelloWorld.py
from Gralog import *
g = Graph("directed");
g2 = Graph("directed");
#end boiler plate

for x in range(10):
	g.addVertex();

gString = g.getGraph("GTGF");
g2.setGraph("GTGF",gString);
