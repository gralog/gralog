#!/usr/bin/python

import sys
from Lib import *
from random import randint





g = Graph("undirected");#type \in buechi, directed, etc. or None


# vertices1 = [];
# vertices2 = [];


red = "#ff0000";

# for x in range(5):
# 	vertices1.append(g.addVertex());

# for x in vertices1:
# 	for y in vertices1:
# 		if (not (x == y)):
# 			g.addEdge(x,y,directed=True);


# g.pauseUntilSpacePressed();

# for x in vertices1:
# 	g.setVertexFillColor(x,red);
# 	# g.setVertexStrokeColor(x,"#ff0000");

# g.pauseUntilSpacePressed();

# for x in vertices1:
# 	for y in vertices1:
# 		if (not(x==y)):
# 			g.setEdgeColor(x,y);
# 			g.pauseUntilSpacePressed();
	

# v = g.addVertex();
# g.setVertexStrokeColor(v,red);
# g.setVertexFillColor(v,"purple");
# g.mistakeLine();




numV = 2;

vertices = [];


for x in range(numV):
	v = g.addVertex();
	vertices.append(v);
	g.addVertexLabel(v,str(x));


for x in vertices:
	for y in vertices:
		if x != y:
			g.addEdge(x,y,True);


g.pauseUntilSpacePressed();

for x in vertices:
	incoming = g.getIncomingNeighbours(x);
	for y in incoming:
		g.deleteVertex(y);
	g.pauseUntilSpacePressed();




