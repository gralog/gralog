#!/usr/bin/python
#HelloWorld.py
from Lib import *
#A simple Gralog program which creates a vertex that says "Hello, world"

g = Graph("directed"); #uses the current graph that is open
v = g.addVertex();
v.setRadius(12);
g.pause();
v.setHeight(2.2);
v.setColor("cyan");
g.pause();
v.setWidth(1);
v2 = g.addVertex();
e = g.addEdge(v,v2);
e.setContour("dotted");
e.setWeight(20);
e.setColor(colorHex="71717b");
v.setLabel("Hello, world!")
e2=g.addDirectedEdge(v2,v);
g.pause();
g.deleteAllEdges((v,v2));
