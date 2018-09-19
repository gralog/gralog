#!/usr/bin/python
#ef.py
from Gralog import *
from bfs import populateBFS
g = Graph("directed");
g.generateRandomGraph(15);

v1 = g.requestVertex();
v2 = g.requestVertex();

populateBFS(g,v1);

curr = v2.getProperty("prev");
toDelete = [];
while curr != v1:
	toDelete.append(curr);
	curr = curr.getProperty("prev");
v1.connect(v2);
for v in toDelete:
	v.delete();



