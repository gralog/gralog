#!/usr/bin/python

import sys
from Lib import *
from random import randint





g = Graph(None);#type \in buechi, directed, etc. or None

# h = Graph("undirected");

# g1 = Graph("directed");



numV = 100;

vertices = [];

gregarity = [0];


g.track("my id",str(g.id));

# g.track("g vertices",vertices);



	# g.pauseUntilSpacePressed();
	# print("hello world",str(g.getOutgoingEdges(v1)));


# for x in ids:
# 	c = g.getEdgeColor((v1,v2),x);

# 	g.setEdgeContour((v1,v2),"dashed",x);
# 	g.setEdgeColor((v1,v2),colorRGB=(12,234,21));
# 	# print("we're getting back : " + c);
# 	g.pauseUntilSpacePressed(("color",c));

ids = [];
for x in range(2):
	ids.append(g.addVertex());

edges = {}

for x in ids:
	for y in ids:
		if x != y:
			for z in range(4):
				g.addEdge(x,y,False);

# 		# g.setEdgeColor((x,y),colorRGB=(255,0,0),edgeId=eid);
# # for x in range(4):
# # 	g.deleteEdge((v1,v2),ids[x]);
# g.pauseUntilSpacePressed();
# for x in ids:
# 	for y in ids:
# 		if x != y:
# 			for z in range(4):
# 				g.deleteEdge((x,y),z);

# for x in vertices:
# 	for y in vertices:
# 		g.addEdge(x,y);
		
# 		# g.pauseUntilSpacePressed(2);
# 	# g.pauseUntilSpacePressed(1);

# g.pauseUntilSpacePressed();

# for x in vertices:
# 	for y in vertices:
# 		g.setEdgeContour((x,y),"dashed");
		
# 	# g.pauseUntilSpacePressed(5);


# g.pauseUntilSpacePressed();

# for x in vertices:
# 	g.deleteVertex(x);







