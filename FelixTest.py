#!/usr/bin/python

import sys
from Lib import *
from random import randint





g = Graph(None);#type \in buechi, directed, etc. or None

# h = Graph("undirected");

# g1 = Graph("directed");


vertices = [];
edges = [];
for x in range(2):
	vertices.append(g.addVertex());

for x in vertices:
	x.setColor("purple");
	x.setStrokeColor("green");

for x in vertices:
	for y in vertices:
		e=g.addEdge(x,y);
		edges.append(e);
		e.setColor("brown");



# first = 








