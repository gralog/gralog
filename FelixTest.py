#!/usr/bin/python

import sys
from Lib import *
from random import randint





g = Graph("undirected");#type \in buechi, directed, etc. or None

# g1 = Graph("directed");



red = "#ff0000";





numV = 2;

vertices = [];
vertices1 = [];

gregarity = [0];



g.track("g vertices",vertices);


for x in range(numV):
	vertices.append(g.addVertex());





# for x in vertices:
# 	g.deleteVertex(x);
	# g.pauseUntilSpacePressed();

# g.pauseUntilSpacePressed();
# g.getGraph("tgf");





# for x in range(numV):
# 	v = g.addVertex();
# 	vertices.append(v);
# 	g.setVertexLabel(v,str(x));



for x in vertices:
	for y in vertices:
		if x != y:
			g.addEdge(x,y,True);


edges = g.getAllEdges();
g.pauseUntilSpacePressed();

for x in edges:
	# print("trying with : " + str(x));
	g.setEdgeLabel(x,"schwupps" + str(x));
	g.setEdgeContour(x,"dashed");


g.pauseUntilSpacePressed();
for x in edges:
	g.setEdgeContour(x,"dotted");

g.pauseUntilSpacePressed()

for x in edges:
	g.setEdgeContour(x,"plain");
	g.setEdgeColor(x,"lolol");


# for x in g.getAllVertices():
# 	g.setVertexLabel(x,str(x));
# 	# g.pauseUntilSpacePressed();
# g.pauseUntilSpacePressed();

# for x in vertices:
# 	g.deleteVertex(x);
# 	g.pauseUntilSpacePressed();




