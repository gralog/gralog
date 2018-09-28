#!/usr/bin/python

import sys
import math
import xml.etree.cElementTree as ET
import networkx as nx
import igraph as ig
import Gralog as Gralog

n = 0
### import graph ## GRALOG ###
g           = Gralog.Graph(None)
gralog_xml  = g.getGraph("xml")
grlgML_file = open("tmp.graphml","w")
grlgML_file.write(gralog_xml)
grlgML_file.close()
graph       = "tmp.graphml"
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

### Compute new coords + write into graphml-file ## ET ###
nx_layouts  = [nx.circular_layout(g_nx,10), nx.shell_layout(g_nx,None,7,None,2),
                nx.spring_layout(g_nx,None,None, None,50,1e-4,None,7),
                nx.kamada_kawai_layout(g_nx,None,None,'weight',10,None,2),
                nx.spectral_layout(g_nx,'weight',20,None,2)]
nx_lay      = nx_layouts[n]
nx_doc      = ET.parse(graph)
nx_root     = nx_doc.getroot()
for nl in range(len(nx_lay)):
    nx_node = nx_root.find('graph').findall('node')[nl].attrib
    nx_node['x'] = str(round(nx_lay[nx_node['id']][0], 5))
    nx_node['y'] = str(round(nx_lay[nx_node['id']][1], 5))
g.message("sending Message..")
g.message(ET.tostringlist(nx_doc.getroot(), encoding="utf8", method="xml"))
g.message("..Message send")
#g.message("sending Graph..")
#g.setGraph("xml",ET.tostringlist(nx_doc.getroot(), encoding="utf8", method="xml"))
#g.message("..Graph send")

ig_layouts  = ["circle","star","grid","graphopt","fruchterman_reingold","kamada_kawai",
                "mds","lgl","reingold_tilford","reingold_tilford_circular"]#,"sugiyama"]
ig_lay      = g_ig.layout(ig_layouts[n])
ig_lay.scale(len(ig_layouts)/2)
ig_doc      = ET.parse(graph)
ig_root     = ig_doc.getroot()
for il in range(len(ig_lay)):
    ig_node = ig_root.find('graph').findall('node')[il].attrib
    ig_node['x'] = str(round(ig_lay[il][0], 5))
    ig_node['y'] = str(round(ig_lay[il][1], 5))
#g.setGraph("xml",ET.tostring(ig_root, encoding="utf8"))

g.message("New Coordinates")
for v in g.getAllVertices():
    g.message("Knoten: " + v)
    g.message(ig_lay.coords[v.getId()])
    v.setCoordinates((ig_lay.coords[v.getId()][0],ig_lay.coords[v.getId()][1]))
