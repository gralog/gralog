#!/usr/bin/python

import sys
import math
import xml.etree.cElementTree as ET
import networkx as nx
import igraph as ig
import Gralog as Gralog

### import graph ## GRALOG ###
g           = Gralog.Graph(None)
graph       = "tmp.graphml"
grlgML_file = open(graph,"w")
grlgML_file.write(g.getGraph("xml"))
grlgML_file.close()
g.message("load")

### import graph to ## IGRAPH + NX ###
g_ig    = ig.Graph.Read_GraphML(graph)
g_nx    = nx.read_graphml(graph)

### compute center of point cloud ###
doc = ET.parse(graph)
nodes = doc.getroot().find('graph').findall('node')
x, y = [float(nodes[i].attrib['x']) for i in range(len(nodes))], [float(nodes[i].attrib['y']) for i in range(len(nodes))]
center = (int(sum(x)/len(x)), int(sum(y)/len(y)))
g.message("center: "+str(center))
g.pause()

### Compute new coords + write into graphml-file ## ET ###
nx_layouts  = [nx.circular_layout(g_nx,10), nx.shell_layout(g_nx,None,7,None,2), nx.spring_layout(g_nx,None,None, None,50,1e-4,None,7), nx.kamada_kawai_layout(g_nx,None,None,'weight',10,None,2), nx.spectral_layout(g_nx,'weight',20,None,2)]
ig_layouts  = ["circle","star","grid","graphopt","fruchterman_reingold","kamada_kawai","mds","lgl","reingold_tilford","reingold_tilford_circular"]#,"sugiyama"]

for i in range(len(nx_layouts)):
    nx_lay      = nx_layouts[i]
    g.message(i)
    for v in g.getAllVertices():
        nx_v = nx_lay["n"+str(v.getId()+1)]
        v.setCoordinates((nx_v[0],nx_v[1]))
    g.pause()

for i in range(len(ig_layouts)):
    ig_lay      = g_ig.layout(ig_layouts[i])
    ig_lay.scale(len(ig_layouts)/2)
    g.message(ig_layouts[i])
    for v in g.getAllVertices():
        ig_v = ig_lay.coords[v.getId()]
        v.setCoordinates((ig_v[0],ig_v[1]))
    g.pause()

exit()
