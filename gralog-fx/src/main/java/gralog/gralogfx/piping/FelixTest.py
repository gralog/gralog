#!/usr/bin/python

import sys
from Lib import *
from random import randint





g = Graph(None);#type \in buechi, directed, etc. or None

# h = Graph("undirected");

# g1 = Graph("directed");

string = g.requestString();
vertices = [];
edges = [];
seen = [];
g.track("seen",seen);
for x in range(2):
	v=g.addVertex()
	vertices.append(v);
	v.setLabel(string);





for x in vertices:
	for y in vertices:
		if x != y:
			edges.append(x.connect(y));

for x in range(0):
	e = g.requestEdge();
	e.setColor("blue");
	s = e.getSource();
	t = e.getTarget();
	s.setColor("blue");
	t.setColor("orange");
	g.pause();
	e.delete();
	# t.delete();
	# s.delete();

loops = g.requestInteger();
for x in range(loops):
	v = g.addVertex();
	v.setLabel(str(x));
	

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







