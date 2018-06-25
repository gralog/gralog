#!/usr/bin/python
import sys
import matplotlib.pyplot as plt
import xml.etree.cElementTree as ET
import networkx as nx
import igraph as ig
from Lib import *

### import graph ## GRALOG ###
#g      = Graph()
#gralog_xml  = g.getGraph("xml")

### import graph to ## IGRAPH ###
g_ig   = ig.Graph.Read_GraphML("t_wheel.graphml")
    ##g_ig   = ig.Graph.Read_GraphML(gralog_xml)
ig_cyc_lay  = g_ig.layout_circle()

### import graph to ## NX ###
g_nx = nx.read_graphml('t_wheel.graphml')
    ##g_nx = nx.read_graphml(gralog_xml)
nx_cyc_lay = nx.circular_layout(g_nx,10)
print nx_cyc_lay
nx_shl_lay = nx.shell_layout(g_nx,[[0],[1,2,3]],10)
print nx_shl_lay
nx_spr_lay = nx.spring_layout(g_nx)
print nx_spr_lay
nx_spc_lay = nx.spectral_layout(g_nx)
print nx_spc_lay
### DONE ## Write new coords into graphml-file ## ET ###
    ##dic = ET.parse(gralog_xml)
    ##root = doc.getroot()
    ##for i in range(len(lay)):
    ##    node = root.find('graph').findall('node')[i].attrib
    ##    node['x'] = str(round(lay[node['id']][0], 5))
    ##    node['y'] = str(round(lay[node['id']][1], 5))
    ##ET.tostring(root, encoding="utf8").sendGraph("xml")
nx_doc = ET.parse('t_wheel.graphml')
nx_root = nx_doc.getroot()
for i in range(len(nx_shl_lay)):
    nx_node = nx_root.find('graph').findall('node')[i].attrib
    nx_node['x'] = str(round(nx_shl_lay[i][0], 5))
    nx_node['y'] = str(round(nx_shl_lay[i][1], 5))

#    nx_node['x'] = str(round(nx_shl_lay[nx_node['id']][0], 5))
#    nx_node['y'] = str(round(nx_shl_lay[nx_node['id']][1], 5))
ET.ElementTree(nx_root).write('MichelleCircleTest_nx_out.graphml', xml_declaration=True, encoding="utf-8")

ig_doc = ET.parse('t_wheel.graphml')
ig_root = ig_doc.getroot()
for i in range(len(ig_cyc_lay)):
    ig_node = ig_root.find('graph').findall('node')[i].attrib
    ig_node['x'] = str(round(ig_cyc_lay[i][0], 5))
    ig_node['y'] = str(round(ig_cyc_lay[i][1], 5))
ET.ElementTree(ig_root).write('MichelleCircleTest_ig_out.graphml', xml_declaration=True, encoding="utf-8")

### draw ###
test_nx = nx.read_graphml('MichelleCircleTest_nx_out.graphml')
#nx.draw(g_nx, pos=nx_lay)
#plt.draw()
#plt.show()
#nx.draw(test_nx, pos=nx_lay)
#plt.draw()
#plt.show()
#test_ig = ig.Graph.Read_GraphML("MichelleCircleTest_ig_out.graphml")
#ig.plot(g_ig)
#ig.plot(test_ig)
