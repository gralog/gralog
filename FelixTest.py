#!/usr/bin/python

import sys
from Lib import *
from random import randint





g = Graph(None);#type \in buechi, directed, etc. or None

# h = Graph("undirected");

# g1 = Graph("directed");

vertices = [];
for x in range(2):
	vertices.append(g.addVertex());
edges = [];
for v in vertices:
	for u in vertices:
		edges.append(g.addEdge(u,v));
g.pauseUntilSpacePressed();
for v in vertices:
	for u in vertices:
		g.deleteAllEdges((u,v));
		g.pauseUntilSpacePressed();


# first = 








