#!/usr/bin/python
import sys
sys.path.append('/home/michelle/gralog/gralog/gralog-fx/src/main/java/gralog/gralogfx/piping')
import math
import random
import matplotlib.pyplot as plt
import xml.etree.cElementTree as ET
import networkx as nx
import igraph as ig
from Lib import *

### import graph ## GRALOG ###
#g      = Graph()
#gralog_xml  = g.getGraph("xml")

### import graph to ## IGRAPH + NX ###
graph = './graphs/a.graphml'
g_ig   = ig.Graph.Read_GraphML(graph)
print g_ig
    ##g_ig   = ig.Graph.Read_GraphML(gralog_xml)
g_nx = nx.read_graphml(graph)
    ##g_nx = nx.read_graphml(gralog_xml)
doc = ET.parse(graph)
print doc.getroot().find('graph').attrib['type']
nodes = doc.getroot().find('graph').findall('node')
x, y = [float(nodes[i].attrib['x']) for i in range(len(nodes))], [float(nodes[i].attrib['y']) for i in range(len(nodes))]
center = (int(sum(x)/len(x)), int(sum(y)/len(y)))
print center
### DONE ## Write new coords into graphml-file ## ET ###
    ##dic = ET.parse(gralog_xml)
    ##root = doc.getroot()
    ##for i in range(len(lay)):
    ##    node = root.find('graph').findall('node')[i].attrib
    ##    node['x'] = str(round(lay[node['id']][0], 5))
    ##    node['y'] = str(round(lay[node['id']][1], 5))
    ##ET.tostring(root, encoding="utf8").sendGraph("xml")
nx_layouts = [nx.circular_layout(g_nx,10), nx.shell_layout(g_nx,None,7,None,2), nx.spring_layout(g_nx,None,None, None,50,1e-4,None,7), nx.kamada_kawai_layout(g_nx,None,None,'weight',10,None,2), nx.spectral_layout(g_nx,'weight',20,None,2)]
for n in range(len(nx_layouts)):
    nx_lay = nx_layouts[n]
    nx_doc = ET.parse(graph)
    nx_root = nx_doc.getroot()
    for nl in range(len(nx_lay)):
        nx_node = nx_root.find('graph').findall('node')[nl].attrib
        nx_node['x'] = str(round(nx_lay[nx_node['id']][0], 5))
        nx_node['y'] = str(round(nx_lay[nx_node['id']][1], 5))
    file = './graphs/nx_out_'+ str(n) +'.graphml'
    ET.ElementTree(nx_root).write(file, xml_declaration=True, encoding="utf-8")

ig_layouts = ["circle","star","grid","graphopt","fruchterman_reingold","kamada_kawai",
            "mds","lgl","reingold_tilford","reingold_tilford_circular"]#,"sugiyama"]
for i in range(len(ig_layouts)):
    ig_lay = g_ig.layout(ig_layouts[i])
    ig_lay.scale(len(ig_layouts))
    #ig_lay.translate(center)
    ig_doc = ET.parse(graph)
    ig_root = ig_doc.getroot()
    for il in range(len(ig_lay)):
        ig_node = ig_root.find('graph').findall('node')[il].attrib
        ig_node['x'] = str(round(ig_lay[il][0], 5))
        ig_node['y'] = str(round(ig_lay[il][1], 5))
    file = './graphs/ig_out_'+ ig_layouts[i] +'.graphml'
    ET.ElementTree(ig_root).write(file, xml_declaration=True, encoding="utf-8")

### draw ###
#test_nx = nx.read_graphml(file)
#nx.draw(g_nx, pos=nx_kk_lay)
#plt.draw()
#plt.show()
test_ig = ig.Graph.Read_GraphML(file)
#ig.plot(g_ig)
#ig.plot(test_ig)
