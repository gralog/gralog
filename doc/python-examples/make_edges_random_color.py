#!/usr/bin/env python3

from random import seed, random
from Gralog import *
import math

###### MAIN #####

g = Graph(None)
edges = g.getEdges()
seed(2)
for e in edges:
    color = int((random()*100000000))% 16777215
    e.setColor('#' + str(color))

