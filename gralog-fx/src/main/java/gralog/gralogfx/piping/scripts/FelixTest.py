#!/usr/bin/python

import sys
from Lib import *
from random import randint





g = Graph();#type \in buechi, directed, etc. or None

# h = Graph("undirected");

g1 = Graph("undirected");
v = [];
for x in range(1):
	v.append(g.addVertex());

for x in v:
	for y in v:
		if x < y:
			g.addEdge(x,y);

gGraph = g.getGraph("gtgf");


g.setGraph("xml");

	

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








