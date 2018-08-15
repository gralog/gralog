#!/usr/bin/python

import sys
from Gralog import *
from random import randint





g = Graph(None);#type \in buechi, directed, etc. or None

# h = Graph("undirected");
g.setGraph("xml");


v = [];
for x in range(4):
	v.append(g.addVertex());

for x in v:
	for y in v:
		if x < y:
			g.addEdge(x,y);

vertices = g.getAllVertices();
for vertex in vertices:
	gPrint("vertex: " + str(vertex));

gGraph = g.getGraph("gtgf");

gPrint("just saved graph!");
g.pause();
for x in v:
	for y in v:
		if x < y:
			g.deleteEdge((x,y));
			g.pause();


g.setGraph("gtgf",gGraph);



	

# for x in edges:
# 	if x.getId() < 10:
# 		x.setColor("red");

# for x in range(1):
# 	v = g.requestVertex();
# 	v.setColor("red");
# 	g.pause();
# 	v.delete();
# 	g.pause();


# 	seen.append(v.getId());
	
# 	g.pause();








# first = 








