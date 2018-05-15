#!/usr/bin/python

import sys
from Lib import *
from random import randint





g = Graph("undirected");#type \in buechi, directed, etc. or None





red = "#ff0000";





numV = 10;

vertices = [];

gregarity = [0];
trackingVars = [("gregarity",gregarity)];


g.track("vertices",vertices);

for x in range(numV):
	vertices.append(g.addVertex());
	g.pauseUntilSpacePressed(trackingVars,("hello","worild"));
	gregarity[0] += 1;

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

for x in edges:
	# print("trying with : " + str(x));
	g.setEdgeLabel(x,"schwupps" + str(x));



for x in g.getAllVertices():
	g.setVertexLabel(x,str(x));
	# g.pauseUntilSpacePressed();
# g.pauseUntilSpacePressed();

# for x in vertices:
# 	g.deleteVertex(x);
# 	g.pauseUntilSpacePressed();




