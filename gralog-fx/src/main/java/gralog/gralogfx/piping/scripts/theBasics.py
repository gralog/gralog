#!/usr/bin/python
#theBasics.py
from Gralog import *
g = Graph("undirected");


# for x in range(5):
# 	v=g.addVertex((x*3,x*7));
# 	v.setLabel("vertex number " + str(x));
# 	v.setRadius(x+3);
# 	v.setColor(str(x*40));
# g.pause();
# v = g.getAllVertices()[0];
# v.setShape("rectangle");
# for x in g.getAllVertices():
# 	for y in g.getAllVertices():
# 		x.connect(y);
# g.track("vertices",g.getAllVertices());

# g.pause();

vertices = [];
for x in range(500):
	v = g.addVertex();
	vertices.append(v);
	v.setColor("blue");

for x in vertices:
	for y in vertices:
		if x.getId() < y.getId():
			x.connect(y);

