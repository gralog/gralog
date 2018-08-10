#!/usr/bin/python
import sys
## TODO: CORRECT, UNIVERSAL PATH!!
sys.path.append('/home/michelle/gralog/gralog/gralog-fx/src/main/java/gralog/gralogfx/piping')
import math
import xml.etree.cElementTree as ET
import networkx as nx
import igraph as ig
from Lib import *
n = 0
print "layout_pythons"
### import graph ## GRALOG ###
g      = Graph(None)
gralog_xml  = g.getGraph("xml")
### import graph to ## IGRAPH + NX ###
g_ig   = ig.Graph.Read_GraphML(gralog_xml)
g_nx = nx.read_graphml(gralog_xml)
doc = ET.parse(gralog_xml)
nodes = doc.getroot().find('graph').findall('node')
x, y = [float(nodes[i].attrib['x']) for i in range(len(nodes))], [float(nodes[i].attrib['y']) for i in range(len(nodes))]
center = (int(sum(x)/len(x)), int(sum(y)/len(y)))
#print center
### DONE ## Write new coords into graphml-file ## ET ###
nx_layouts = [nx.circular_layout(g_nx,10), nx.shell_layout(g_nx,None,7,None,2), nx.spring_layout(g_nx,None,None, None,50,1e-4,None,7), nx.kamada_kawai_layout(g_nx,None,None,'weight',10,None,2), nx.spectral_layout(g_nx,'weight',20,None,2)]
nx_lay = nx_layouts[n]
nx_doc = ET.parse(gralog_xml)
nx_root = nx_doc.getroot()
for nl in range(len(nx_lay)):
    nx_node = nx_root.find('graph').findall('node')[nl].attrib
    nx_node['x'] = str(round(nx_lay[nx_node['id']][0], 5))
    nx_node['y'] = str(round(nx_lay[nx_node['id']][1], 5))
#ET.tostring(nx_root, encoding="utf8").sendGraph("xml")
ig_layouts = ["circle","star","grid","graphopt","fruchterman_reingold","kamada_kawai",
            "mds","lgl","reingold_tilford","reingold_tilford_circular"]#,"sugiyama"]
ig_lay = g_ig.layout(ig_layouts[n])
ig_lay.scale(len(ig_layouts))
#ig_lay.translate(center)
ig_doc = ET.parse(gralog_xml)
ig_root = ig_doc.getroot()
for il in range(len(ig_lay)):
    ig_node = ig_root.find('graph').findall('node')[il].attrib
    ig_node['x'] = str(round(ig_lay[il][0], 5))
    ig_node['y'] = str(round(ig_lay[il][1], 5))
g.setGraph("xml",ET.tostring(ig_root, encoding="utf8"))
