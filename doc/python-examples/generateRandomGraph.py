#!/usr/bin/python

import sys
from Gralog import *


g = Graph()

g.generateRandomGraph(10,0.5)

gPrint(str(len(g.getVertices())))
