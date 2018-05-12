#!/usr/bin/python

import sys
from Lib import *
from random import randint





g = Graph("undirected");#type \in buechi, directed, etc. or None





red = "#ff0000";





numV = 20;

vertices = [];
for x in range(numV):
	vertices.append(g.addVertex());

# for x in vertices:
# 	g.deleteVertex(x);
	# g.pauseUntilSpacePressed();

# g.pauseUntilSpacePressed();
g.getGraph("xml");



# for x in range(numV):
# 	v = g.addVertex();
# 	vertices.append(v);
# 	g.addVertexLabel(v,str(x));


# for x in vertices:
# 	for y in vertices:
# 		if x != y:
# 			g.addEdge(x,y,True);


# g.pauseUntilSpacePressed();

# for x in vertices:
# 	incoming = g.getIncomingNeighbours(x);
# 	for y in incoming:
# 		g.deleteVertex(y);
# 	g.pauseUntilSpacePressed();




