#!/usr/bin/python
#HelloWorld.py
from Lib import *
#A simple Gralog program which creates a vertex that says "Hello, world"

g = Graph(None); #uses the current graph that is open
# v = []
# for x in range(3):
# 	v.append(g.addVertex());
# for x in v:
# 	for y in v:
# 		if x <y:
# 			g.addEdge(x,y);
# g.getGraph("xml");
# g.pause();
g.setGraph("xml","graph");
v = g.requestVertex();
n = g.requestInteger();
for x in range(n):
	z = g.addVertex();
	z.setColor("purple");
