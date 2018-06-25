#!/usr/bin/python
import sys
import collections
import matplotlib.pyplot as plt
import xmltodict
import xml.etree.cElementTree as ET
import networkx as nx
import igraph as ig
from Lib import *

### import graph ## GRALOG ###
#g      = Graph()
#gralog_xml  = g.getGraph("xml")

### import graph to ## IGRAPH ###
g_ig   = ig.Graph.Read_GraphML("t_di.graphml")
    ##g_ig   = ig.Graph.Read_GraphML(gralog_xml)
ig_lay  = g_ig.layout_circle()

### import graph to ## NX ###
g_nx = nx.read_graphml('t_adirectedgraph.graphml')
    ##g_nx = nx.read_graphml(gralog_xml)
nx_lay = nx.circular_layout(g_nx,10)
print(nx_lay)
### DONE ## Write new coords into graphml-file ## ET ###
    ##dic = ET.parse(gralog_xml)
    ##root = doc.getroot()
    ##for i in range(len(lay)):
    ##    node = root.find('graph').findall('node')[i].attrib
    ##    node['x'] = str(round(lay[node['id']][0], 5))
    ##    node['y'] = str(round(lay[node['id']][1], 5))
    ##ET.tostring(root, encoding="utf8").sendGraph("xml")
nx_doc = ET.parse('t_adirectedgraph.graphml')
nx_root = nx_doc.getroot()
for i in range(len(nx_lay)):
    nx_node = nx_root.find('graph').findall('node')[i].attrib
    nx_node['x'] = str(round(nx_lay[nx_node['id']][0], 5))
    nx_node['y'] = str(round(nx_lay[nx_node['id']][1], 5))
ig_doc = ET.parse('t_adirectedgraph.graphml')
ig_root = ig_doc.getroot()
for i in range(len(ig_lay)):
    ig_node = ig_root.find('graph').findall('node')[i].attrib
    ig_node['x'] = str(round(ig_lay[i][0], 5))
    ig_node['y'] = str(round(ig_lay[i][1], 5))
#print ET.tostring(root,encoding="utf8")
ET.ElementTree(nx_root).write('MichelleCircleTest_nx_out.graphml', xml_declaration=True, encoding="utf-8")
ET.ElementTree(ig_root).write('MichelleCircleTest_ig_out.graphml', xml_declaration=True, encoding="utf-8")
### DONE ###

### NOT WORKING ## Write new coords into graphml-file ## XMLTODICT ## NOT WORKING ##
## outdated encoding of edges, nodes ###
    #dic = xmltodict.parse(gralog_xml)
    #for i in range(len(lay)):
    #    node = dic['graphml']['graph']['node'][i]
    #    node['@x'] = lay[node['@id']][0]
    #    node['@y'] = lay[node['@id']][1]
    #return xmltodict.unparse(dic)
#with open('t_adirectedgraph.graphml','rw') as fd:
#    doc = xmltodict.parse(fd.read())
#    for i in range(len(lay)):
#        node = doc['graphml']['graph']['node'][i]
#        node['@x'] = round(lay[node['@id']][0], 5)
#        node['@y'] = round(lay[node['@id']][1], 5)
#        print(node['@x'], node['@y'])
#    output = open('MichelleCircleTest_out.graphml','w+')
#    output.write(xmltodict.unparse(doc))
### NOT WORKING ## XMLTODICT ## NOT WORKING ###

### draw ###
test_nx = nx.read_graphml('MichelleCircleTest_nx_out.graphml')
nx.draw(g_nx, pos=nx_lay)
plt.draw()
#plt.show()
nx.draw(test_nx, pos=nx_lay)
plt.draw()
#plt.show()
#test_ig = ig.Graph.Read_GraphML("MichelleCircleTest_ig_out.graphml")
#ig.plot(g_ig)
#ig.plot(test_ig)
