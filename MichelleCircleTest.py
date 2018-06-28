#!/usr/bin/python
import sys
import math
import matplotlib.pyplot as plt
import xml.etree.cElementTree as ET
import networkx as nx
import igraph as ig
from Lib import *

### import graph ## GRALOG ###
#g      = Graph()
#gralog_xml  = g.getGraph("xml")

### import graph to ## IGRAPH ###
print "import graph to igraph"
g_ig   = ig.Graph.Read_GraphML("t_wheel.graphml")
    ##g_ig   = ig.Graph.Read_GraphML(gralog_xml)
ig_cyc_lay  = g_ig.layout_circle()
ig_cyc_lay.scale(10)
ig_sta_lay  = g_ig.layout_star()
ig_grd_lay  = g_ig.layout_grid()
ig_opt_lay  = g_ig.layout_graphopt()
#ig_bip_lay  = g_ig.layout_bipartite()
ig_fr_lay  = g_ig.layout_fruchterman_reingold()
ig_kk_lay  = g_ig.layout_kamada_kawai()
ig_mds_lay  = g_ig.layout_mds()
#ig_gfr_lay  = g_ig.layout_grid_fruchterman_reingold()
ig_lgl_lay  = g_ig.layout_lgl()
ig_rt_lay  = g_ig.layout_reingold_tilford()
ig_rtc_lay  = g_ig.layout_reingold_tilford_circular()
ig_sug_lay  = g_ig.layout_sugiyama()

### import graph to ## NX ###
print "import graph to nx"
g_nx = nx.read_graphml('t_wheel.graphml')
    ##g_nx = nx.read_graphml(gralog_xml)
nx_cyc_lay = nx.circular_layout(g_nx,10)
nx_shl_lay = nx.shell_layout(g_nx,None,7,None,2)
nx_spr_lay = nx.spring_layout(g_nx,None,None, None,50,1e-4,None,7)
nx_kk_lay = nx.kamada_kawai_layout(g_nx,None,None,'weight',10,None,2)
nx_spc_lay = nx.spectral_layout(g_nx,'weight',20,None,2)
### DONE ## Write new coords into graphml-file ## ET ###
    ##dic = ET.parse(gralog_xml)
    ##root = doc.getroot()
    ##for i in range(len(lay)):
    ##    node = root.find('graph').findall('node')[i].attrib
    ##    node['x'] = str(round(lay[node['id']][0], 5))
    ##    node['y'] = str(round(lay[node['id']][1], 5))
    ##ET.tostring(root, encoding="utf8").sendGraph("xml")
nx_lay = nx_spc_lay
nx_doc = ET.parse('t_wheel.graphml')
nx_root = nx_doc.getroot()
for i in range(len(nx_lay)):
    nx_node = nx_root.find('graph').findall('node')[i].attrib
    nx_node['x'] = str(round(nx_lay[nx_node['id']][0], 5))
    nx_node['y'] = str(round(nx_lay[nx_node['id']][1], 5))
ET.ElementTree(nx_root).write('MichelleCircleTest_nx_out.graphml', xml_declaration=True, encoding="utf-8")

ig_lay = ig_cyc_lay
ig_doc = ET.parse('t_wheel.graphml')
ig_root = ig_doc.getroot()
for i in range(len(ig_lay)):
    ig_node = ig_root.find('graph').findall('node')[i].attrib
    ig_node['x'] = str(round(ig_lay[i][0], 5))
    ig_node['y'] = str(round(ig_lay[i][1], 5))
ET.ElementTree(ig_root).write('MichelleCircleTest_ig_out.graphml', xml_declaration=True, encoding="utf-8")

### draw ###
test_nx = nx.read_graphml('MichelleCircleTest_nx_out.graphml')
#nx.draw(g_nx, pos=nx_kk_lay)
#plt.draw()
#plt.show()
nx.draw(test_nx, pos=nx_lay)
plt.draw()
plt.show()
test_ig = ig.Graph.Read_GraphML("MichelleCircleTest_ig_out.graphml")
#ig.plot(g_ig)
ig.plot(test_ig)
