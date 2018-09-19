#!/usr/bin/python
#bfs.py
from Gralog import *
g = Graph("undirected");

gPrint("A graph with a lot of vertices")
for x in range(10):
	g.track(x,x);
vertices = [g.addVertex()];
g.track("vertices",vertices);
g.track("hello","world");
g.pause();
