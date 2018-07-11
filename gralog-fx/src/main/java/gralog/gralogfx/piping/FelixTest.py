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
for x in range(20):
	vertices.append(g.addVertex());


for x in vertices:
	for y in vertices:
		if x != y:
			edges.append(x.connect(y));

for x in edges:
	if x.getId() < 10:
		x.setColor("red");

for x in range(20):
	v = g.requestRandomVertex();
	seen.append(v.getId());
	v.setColor("orange");
	g.pause();








# first = 








