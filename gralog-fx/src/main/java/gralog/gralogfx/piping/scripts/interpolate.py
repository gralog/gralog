#!/usr/bin/python
#interpolate.py
from Gralog import *
from bfs import populateBFS
g = Graph("directed");
v1 = g.addVertex((0,0));
v2 = g.addVertex((20,20));

g.pause();
prev = v1;
for x in range(1,10):
	v = g.addVertex((2*x,2*x));
	prev.connect(v);
	prev = v;
prev.connect(v2);





