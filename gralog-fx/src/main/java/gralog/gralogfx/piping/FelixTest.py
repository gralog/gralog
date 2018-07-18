#!/usr/bin/python

import sys
from Lib import *
from random import randint





g = Graph(None);#type \in buechi, directed, etc. or None

# h = Graph("undirected");

# g1 = Graph("directed");


vertices = [];
edges = [];
seen = [];
g.track("seen",seen);
for x in range(40):
	vertices.append(g.addVertex());


for x in vertices:
	for y in vertices:
		if x != y:
			edges.append(x.connect(y));

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








