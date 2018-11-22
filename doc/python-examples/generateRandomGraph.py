#!/usr/bin/python

import sys
from Gralog import *


g = Graph()

g.generateRandomGraph(50,0.09)

gPrint(str(len(g.getVertices())))
