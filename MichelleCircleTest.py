#!/usr/bin/python

import sys
import matplotlib.pyplot as plt

import networkx as nx
import igraph as ig

from Lib import *

### von gralog:
#g      = Graph()
#g_xml  = g.getGraph("xml")

### Verarbeitung in igraph:
#print('create graph..')
#g_ig   = ig.Graph.Read_GraphML("xml_testout_copy.graphml")
#print('..graph created')
#g_lay  = g_ig.layout_circle()
#ig.plot(g_ig,layout=g_lay)

### Verarbeitung in nx:
g_nx = nx.read_graphml("adirectedgraph.graphml")
#g_nx = nx.read_graphml("xml_testout.graphml")
lay = nx.circular_layout(g_nx)
print(lay)
nx.draw(g_nx, pos=nx.circular_layout(g_nx))
plt.draw()
plt.show()
