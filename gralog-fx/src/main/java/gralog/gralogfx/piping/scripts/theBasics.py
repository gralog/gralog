#!/usr/bin/python
#theBasics.py
from Gralog import *
g = Graph("undirected");

for x in range(10):
	v = g.addVertex();
	v.setColor("blue");
	g.pause();
