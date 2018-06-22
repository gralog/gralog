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

### import graph ## IGRAPH ###
#g_ig   = ig.Graph.Read_GraphML("t_adirectedgraph.graphml")
    ##g_ig   = ig.Graph.Read_GraphML(gralog_xml)
#g_lay  = g_ig.layout_circle()
#ig.plot(g_ig,layout=g_lay)

### import graph ## NX ###
g_nx = nx.read_graphml('t_adirectedgraph.graphml')
    ##g_nx = nx.read_graphml(gralog_xml)
lay = nx.circular_layout(g_nx)

### Write new coords into graphml-file ## ET ###
doc = ET.parse('t_adirectedgraph.graphml')
root = doc.getroot()
print(root)
for i in range(len(lay)):
    node = doc.graphml.graph.node[i]
    print(node['x'], node['y'], round(lay[node['id']][0], 5))
    node['x'] = round(lay[node['id']][0], 5)
    node['y'] = round(lay[node['id']][1], 5)
    print(node['x'], node['y'])
output = open('MichelleCircleTest_out.graphml','w+')
output.write(untangle.unparse(doc))

### NOT WORKING ## Write new coords into graphml-file ## XMLTODICT ## NOT WORKING ###
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
test_nx = nx.read_graphml('MichelleCircleTest_out.graphml')
nx.draw(g_nx)
plt.draw()
plt.show()
nx.draw(test_nx)
plt.draw()
plt.show()
