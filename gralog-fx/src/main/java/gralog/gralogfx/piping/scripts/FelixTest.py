#!/usr/bin/python
#HelloWorld.py
from Gralog import *
g = Graph("directed");
#end boiler plate

v = g.addVertex();
v2 = g.addVertex();
e = g.addEdge(v2,v);
eid = e.getId();
e2 = g.addEdge(v2,v,eid);
gPrint("e1 id: " + str(e) + " and e2 id: " + str(e2));
g.deleteAllEdges((v,v2));
g.pause();
if g.existsEdge((v2,v)):
	gPrint("there's an edge by krikey!");
g.deleteAllEdges((v2,v));
