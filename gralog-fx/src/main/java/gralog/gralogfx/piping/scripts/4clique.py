#!/usr/bin/python

import sys
from Gralog import *





g = Graph("undirected");
vertices = [];
for x in range(g.requestInteger()):
	vertices.append(g.addVertex());

for v in vertices:
	for w in vertices:
		if v < w:
			v.connect(w);

