#!/usr/bin/python2

import sys
from random import randint
import os
try:
        import networkx as nx
except:
        print("gPrint#-1#" + "netwrokx not installed for " + sys.executable)
        sys.stdout.flush()

try:
        import igraph as ig
except:
        print("gPrint#-1#" + "igraph not installed for " + sys.executable)
import xml.etree.cElementTree as ET
import math

# debugging = False

class Vertex:
        def __init__(self,graph,vid):
                self.sourced = False
                self.id = int(vid)
                self.graph = graph
                self.properties = dict()
                self.properties["id"] = None
                self.properties["label"] = None
                self.properties["color"] = None
                self.properties["strokeColor"] = None
                self.properties["shape"] = None
                self.properties["coordinates"] = None
                self.incomingEdges = []
                self.outgoingEdges = []
                self.incidentEdges = []
                self.wasSourced = False

        def sourceProperties(self,stringFromGralog):
                self.sourced = True
                strings = stringFromGralog.split("#")
                for string in strings:
                        propVal = string.split("=")
                        valueType = ""
                        try:
                                prop = propVal[0]
                                valueType = propVal[1]

                        except:
                                pass
                        try:

                                valueType = valueType.split("|")
                                val = valueType[0]
                                typ = valueType[1]
                                castedValue = self.graph.castValueToType(val,typ)
                                self.properties[prop] = castedValue
                        except:
                                pass

        def getId(self):
                return self.id

        def getLabel(self):
                if not self.wasSourced:
                        self.source()
                return self.properties["label"]

        def setLabel(self,label):
                label = str(label)
                self.properties["label"] = label
                self.graph.setVertexLabel(self.id,label)

        def setCoordinates(self,coordinates):
                co = self.properties["coordinates"]

                x = coordinates[0]
                y = coordinates[1]

                if co == None:
                        co = (None,None)
                if x == None:
                        x = co[0]
                if y == None:
                        y = co[1]
                newCoordinates = (x,y)

                self.properties["coordinates"] = newCoordinates
                self.graph.setVertexCoordinates(self.id,newCoordinates)

        def setFillColor(self,colorHex=-1,colorRGB=-1):
                self.setColor(colorHex,colorRGB)
        def getFillColor(self):
                return self.getColor()
        def getColor(self):
                if not self.wasSourced:
                        self.source()
                return self.properties["color"]
        def setColor(self,colorHex=-1,colorRGB=-1):
                if colorHex != -1:
                        self.properties["fillColor"] = colorHex
                elif colorRGB != -1:
                        self.properties["fillColor"] = colorRGB
                else:
                        return
                self.graph.setVertexFillColor(self.id,colorHex,colorRGB)
        def setStrokeColor(self,colorHex=-1,colorRGB=-1):
                if colorHex != -1:
                        self.properties["strokeColor"] = colorHex
                elif colorRGB != -1:
                        self.properties["strokeColor"] = colorRGB
                else:
                        return
                self.graph.setVertexStrokeColor(self.id,colorHex,colorRGB)
        def getStrokeColor(self):
                if not self.sourced:
                        self.source()
                return self.properties["strokeColor"]
        def setRadius(self,radius):
                self.properties["radius"] = radius
                self.properties["width"] = radius
                self.properties["height"] = radius
                self.graph.setVertexRadius(self.id,radius)
        def setWidth(self,width):
                self.properties["width"] = width
                self.graph.setVertexWidth(self.getId(),width)
        def setHeight(self,height):
                self.properties["height"] = height
                self.graph.setVertexHeight(self.getId(),height)
        def setShape(self,shape):
                self.properties["shape"] = shape
                self.graph.setVertexShape(self.id,shape)
        def setProperty(self,otherProperty,value):
                self.properties[otherProperty] = value
                self.graph.setVertexProperty(self.id,otherProperty,value)
        def getProperty(self,otherProperty):
                if not self.sourced:
                        self.source()
                return self.properties[otherProperty]
        def get(self,prop):
                if not self.sourced:
                        self.source()
                return self.properties[prop]
        def getNeighbours(self):
                return self.graph.getNeighbours(self.id)
        def getOutgoingNeighbours(self):
                return self.graph.getOutgoingNeighbours(self.id)
        def getIncomingNeighbours(self):
                return self.graph.getIncomingNeighbours(self.id)
        def getOutgoingEdges(self):
                return self.graph.getOutgoingEdges(self.id)
        def getIncomingEdges(self):
                return self.graph.getIncomingEdges(self.id)
        def getIncidentEdges(self):
                return self.graph.getIncidentEdges(self.id)
        def delete(self):
                return self.graph.deleteVertex(self)
        def connect(self,v1,edgeId=-1):
                return self.graph.addEdge(self,v1,edgeId)
        def getAllEdgesBetween(self,vertex2):
                return self.graph.getAllEdgesBetween((self.id,vertex2))
        def source(self):
                return self.graph.getVertex(self)
        def __str__(self):
                return str(self.getId())


        #what if i want to get a vertex? should i also get all its neighbours? how about incident edges? This is all v aufw\"andig and leads to the paradigm by which we just store the grpah in python???



class Edge:
        ##private methods
        def __init__(self,graph,eid):
                self.sourced = False
                self.id = int(eid) #if -2, then imported without id like in TGF
                self.graph = graph
                self.properties = dict()
                self.properties["id"] = None
                self.properties["label"] = None
                self.properties["color"] = None
                self.properties["weight"] = None
                self.properties["contour"] = None
                self.properties["source"] = None
                self.properties["target"] = None
                self.wasSourced = False


        def sourceProperties(self,stringFromGralog):
                self.sourced = True
                strings = stringFromGralog.split("#")
                for string in strings:
                        propVal = string.split("=")
                        try:
                                prop = propVal[0]
                                valueType = propVal[1]
                                valueType = valueType.split("|")
                                val = valueType[0]
                                typ = valueType[1]
                                self.properties[prop] = self.graph.castValueToType(val,typ)
                        except:
                                pass

        def setTarget(self,target): ###don't use!!
                self.properties["target"] = target
        def setSource(self,source):
                self.properties["source"] = source

        ##public methods

        def getId(self):
                return self.id
        def setLabel(self,label):
                label = str(label)
                self.properties["label"] = label
                self.graph.setEdgeLabel(self.id,label)
        def getLabel(self):
                if not self.sourced:
                        self.source()
                return self.properties["label"]

        def setColor(self,colorHex=-1,colorRGB=-1):
                if colorHex != -1:
                        self.properties["color"] = colorHex
                elif colorRGB != -1:
                        self.properties["color"] = colorRGB
                else:
                        return
                self.graph.setEdgeColor(self.id,colorHex,colorRGB)
        def getColor(self):
                if not self.sourced:
                        self.source()
                return self.properties["color"]
        def setWeight(self,weight):
                self.properties["weight"] = float(weight)
                self.graph.setEdgeWeight(self.id,weight)
        def getWeight(self):
                if not self.sourced:
                        self.source()
                return self.properties["weight"]

        def setThickness(self,thickness):
                self.properties["thickness"] = float(thickness)
                self.graph.setEdgeThickness(self.id,thickness)
        def getThickness(self):
                if not self.sourced:
                        self.source()
                return self.properties["thickness"]
        def setContour(self,contour):
                self.properties["contour"] = contour
                self.graph.setEdgeContour(self.id,contour)
        def getContour(self):
                if not self.sourced:
                        self.source()
                return self.properties["contour"]

        def getSource(self):
                if not self.sourced:
                        self.source()
                return self.properties["source"]

        def getTarget(self):
                if not self.sourced:
                        self.source()
                return self.properties["target"]
        def setProperty(self,otherProperty,value):
                self.properties[otherProperty] = value
                self.graph.setEdgeProperty(self,otherProperty,value)
        def getProperty(self,otherProperty):
                if not self.sourced:
                        self.source()
                return self.properties[otherProperty]
        def get(self,prop):
                self.source()
                return self.properties[prop]
        def delete(self):
                return self.graph.deleteEdge(self.id)
        def source(self):
                return self.graph.getEdge(self)
        def getAdjacentEdges(self):
                return self.graph.getAdjacentEdges(self.id)


        def __str__(self):
                v = self.getId()
                v_str = str(v)
                source = self.getSource().getId()
                target = self.getTarget().getId()
                return "({:},{:})".format(source, target)


def rgbFormatter(colorRGB):
        r = colorRGB[0]
        g = colorRGB[1]
        b = colorRGB[2]
        s = "rgb"
        s += "(" + str(r).rstrip() + "," + str(g).rstrip() + "," + str(b).rstrip() + ")"
        return s.rstrip()

def hexFormatter(colorHex):
        s = "hex"
        if colorHex[0] == "#":
                colorHex = colorHex[1:]
        s += "("+str(colorHex).rstrip() + ")"
        return s.rstrip()


def vertexId(vertex):
        if isinstance(vertex,Vertex):
                return vertex.getId()
        return vertex

def edgeId(edge):
        if isinstance(edge, Edge):
                return edge.getId()
        return edge



def extractIdFromProperties(stringFromGralog):
#       gPrint("stringFromGralog: " + stringFromGralog)
        strings = stringFromGralog.split(",")
        for string in strings:

                propVal = string.split("=")
                if propVal[0] == "id":
                        return propVal[1]
        return None


def edgeSplitter(edge):
        if type(edge) == tuple and len(edge)==2:#edge as defined by start, end nodes
                return str(vertexId(edge[0])).rstrip()+","+str(vertexId(edge[1])).rstrip()
        if type(edge) == int:# edge is given by id
                return str(edge).rstrip()
        return str(edge.getId()).rstrip()#edge has type Edge

class Graph:
        def __init__(self,format="Undirected Graph"):
                #perform analysis of graph
                self.id_to_vertex = dict()
                self.id_to_edge = dict()
                self.lastIndex = -1
                self.id = -1
                self.variablesToTrack = dict()

                if format == None or format.lower() == "none":
                        #we want a new graph

                        print("useCurrentGraph")
                        sys.stdout.flush()

                        self.lastIndex = -1
                        self.id = sys.stdin.readline()
                        self.getGraph("gtgf")

                else:
                        print(format)
                        sys.stdout.flush()
                        self.id = sys.stdin.readline()


        ####helper functions

        def castValueToType(self,val,typ):

                if typ == "float":
                        return float(val)
                if typ == "int":
                        return int(val)
                if typ == "bool":
                        return bool(val)
                if typ == "string":
                        return str(val)
                if typ == "vertex":
                        return self.getVertexOrNew(val)
                return val

        def getVertexOrNew(self,currId):

                v = currId
                if (isinstance(currId,str)):
                        currId = int(currId)
                if (isinstance(currId,int)):
                        if currId in self.id_to_vertex:
                                v=self.id_to_vertex[currId]
                        else:
                                v=Vertex(self,currId)
                                self.id_to_vertex[currId] = v
                return v


        def getEdgeOrNew(self,currId):

                if type(currId) == tuple:
                        e = self.getEdgeIdByEndpoints(currId)
                        return e

                e = currId

                if not (isinstance(currId, Edge)):
                        try:
                                e=self.id_to_edge[int(currId)]
                        except:
                                e=Edge(self,currId)
                else:
                        gPrint("Error (getEdgeOrNew()): the argument \
                        is neither an edge id nor a pair of vertices.")
                return e

        def termToEdge(self,term):
                endpoints = term.split(",")
                eid = int(endpoints[0])

                e = self.id_to_edge[eid]
                e.sourceProperties(endpoints[0])
                sourceId = int(endpoints[1])
                source = self.getVertexOrNew(sourceId)
                targetId = int(endpoints[2])
                target = self.getVertexOrNew(targetId)

                # ####possibly to remove
                # source.sourceProperties(endpoints[1])
                # target.sourceProperties(endpoints[2])
                # ####fin

                e.setSource(source)
                e.setTarget(target)
                return e

        def representsInt(s):
            try:
                int(s)
                return True
            except ValueError:
                return False

        def edgifyTGFCommand(self,line):
                line = line.strip()
                endpoints = line.split(" ")
                v1String = endpoints[0]
                v1 = self.getVertexOrNew(int(v1String))
                v2String = endpoints[1]
                v2 = self.getVertexOrNew(int(v2String))
                e = self.getEdgeOrNew(-2)
                e.setSource(v1)
                e.setTarget(v2)
                # self.id_to_edge[e.getId()] = e

        def vertexifyTGFCommand(self,line):
                line = line.strip()
                vString = line[0]
                v = self.getVertexOrNew(int(vString))

        def edgifyGTGFCommand(self,line):
                line = line.strip()
                endpoints = line.split(" ")
                v1String = endpoints[0]
                v1 = self.getVertexOrNew(int(v1String))
                v2String = endpoints[1]
                v2 = self.getVertexOrNew(int(v2String))
                eid = int(endpoints[2])
                e = self.getEdgeOrNew(eid)
                e.setSource(v1)
                e.setTarget(v2)
                self.id_to_edge[eid] = e

        def vertexifyGTGFCommand(self,line):
                self.vertexifyTGFCommand(line)

        def getEdgeIdByEndpoints(self, endpoints):
                line = "getEdgeIdByEndpoints#"+str(self.id).rstrip() + "#"
                line = line + edgeSplitter(endpoints)

                print (line.rstrip())
                sys.stdout.flush()

                edgeId = sys.stdin.readline().rstrip()
                return edgeId

        def getVertex(self,vertex):
                line = "getVertex#"+str(self.id).rstrip() + "#"
                line = line + str(vertex).rstrip()

                print (line.rstrip())
                sys.stdout.flush()

                vertexTuple = sys.stdin.readline().rstrip()

                vertex.sourceProperties(vertexTuple)
                return vertex

        def getEdge(self,edge):
                line = "getEdge#"+str(self.id).rstrip() + "#"
                line = line + edgeSplitter(edge)

                print (line.rstrip())
                sys.stdout.flush()

                edgeTuple = sys.stdin.readline().rstrip()
                edge.sourceProperties(edgeTuple)
                return edge


        ####end helper functions


        ####Graph Manipulating Functions

        def addVertex(self,vertexId=-1,pos=(None,None)):
                #return: Vertex object with id
                line = "addVertex#" + str(self.id).rstrip()
                x=-1
                y=-1
                vertexIdSwap = False

                if type(vertexId) == tuple and pos == (None,None):
                        x = vertexId[0]
                        y = vertexId[1]
                        vertexId = -1
                else:
                        x=pos[0]
                        y=pos[1]

                if vertexId != -1:
                        line += "#"+str(vertexId).rstrip()
                if x != None and y != None:
                        line += "#" + str(x).rstrip() + "#" + str(y).rstrip()
                print(line)
                sys.stdout.flush()


                vid = sys.stdin.readline()
                v = Vertex(self,vid)

                self.id_to_vertex[v.getId()] = v

                return v

        def deleteVertex(self, v):
                edges = self.getIncidentEdges(v)
                for e in edges:
                        del self.id_to_edge[e.getId()]
                v = vertexId(v)
                del self.id_to_vertex[v]

                print ("deleteVertex#" +str(self.id).rstrip() + "#" +  str(v))
                sys.stdout.flush()
                # sys.stdin.readline()




        def addEdge(self,sourceVertex, targetVertex, edgeId = -1):
                #return: Edge object with id only
                sourceVertex = vertexId(sourceVertex)
                targetVertex = vertexId(targetVertex)


                idSubString = ""
                if not edgeId==-1:
                        idSubString = "#"+str(edgeId)

                line = "addEdge#"+str(self.id).rstrip() + "#" + str(sourceVertex).rstrip() \
                       + "#" + str(targetVertex).rstrip() +idSubString.rstrip()

                print (line)
                sys.stdout.flush()
                eid = sys.stdin.readline()

                if eid != "\n": # it's possible that the edge cannot be added (e.g., a new selfloop)
                        e = Edge(self,eid)
                        self.id_to_edge[e.getId()] = e
                        return e

                return None


        def existsEdge(self,edge):
                line = "existsEdge#"+str(self.id).rstrip() + "#"
                line = line + edgeSplitter(edge)

                print (line.rstrip())

                sys.stdout.flush()
                thereExistsAnEdge = sys.stdin.readline().rstrip()

                return thereExistsAnEdge.lower() == "true"

        def existsVertex(self,vertex):
                line = "existsVertex#"+str(self.id).rstrip() + "#"
                line = line + str(vertex).rstrip()

                print (line.rstrip())

                sys.stdout.flush()
                thereExistsAVertex = sys.stdin.readline().rstrip()
                return thereExistsAVertex.lower() == "true"


        def deleteEdge(self,edge):

                del self.id_to_edge[edge.getId()]
                line = "deleteEdge#"+str(self.id).rstrip() + "#"
                line = line + edgeSplitter(edge)

                print (line.rstrip())

                sys.stdout.flush()
                # sys.stdin.readline()


        def getAllEdgesBetween(self,vertexPair):

                line = "getAllEdgesBetween#"+str(self.id).rstrip() + "#"
                line = line +edgeSplitter(vertexPair)

                print (line.rstrip())

                sys.stdout.flush()
                endpointList = sys.stdin.readline()

                endpointList = endpointList.split("#")
                edges = []

                for i in range(len(endpointList)):
                        term = endpointList[i].rstrip()
                        term = term[1:-1]
                        e = self.termToEdge(term)
                        if e != None:
                                edges.append(e)

                        # endpointList[i] = (int(endpoints[0]),int(endpoints[1]),int(endpoints[2]))
                return edges

        # creates a random Erdos-Reny graph with n id_to_vertex and edge probability p
        def generateRandomGraph(self, vertexCount, p):
                if not isinstance(vertexCount,int):
                        gPrint("Cannot generate a random graph, wrong parameter: \
                                               vertex number must be an int.")
                if vertexCount < 0:
                        gPrint("Cannot generate a random graph, wrong parameter: \
                                               vertex number cannot be less than 0.")
                if not isinstance(p, float) or p < 0 or p > 1.0:
                        gPrint("Cannot generate a random graph, wrong parameter: \
                                               probability of an edge must be a float in [0,1].")
                if vertexCount == 0:
                        return
                vertices = []
                coordinates = dict()

                for id in range(vertexCount):
                        coordinates[id] = (10*math.cos(2*id*math.pi/vertexCount), 10*math.sin(2*id*math.pi/vertexCount))

                nxgraph = nx.fast_gnp_random_graph(vertexCount, p)
                d = dict()
                id = 0
                for nxV in nxgraph.nodes():
                        d[id] = nxV
                        id += 1

                nxEdges = nxgraph.edges()
                id = 0
                for x in range(vertexCount):
                        vertices.append(self.addVertex(id,coordinates[id]))
                        id += 1
                for x in vertices:
                        for y in vertices:
                                if x.getId() < y.getId():
                                        if (d[x.getId()],d[y.getId()]) in nxEdges:
                                                x.connect(y)



        ####end manilupative functions

        ###setter functions

        ###begin: best for private use!

        def setVertexFillColor(self,vertex,colorHex=-1,colorRGB=-1):

                vertex = vertexId(vertex)
                line = "setVertexFillColor#" + str(self.id).rstrip() + "#" + str(vertex).rstrip() + "#"
                if not (colorHex==-1):
                        line = line + hexFormatter(str(colorHex))
                elif not (colorRGB == -1):
                        try:
                                line = line + rgbFormatter(colorRGB)
                        except:
                                self.sendErrorToGralog("the rgb color: " + str(colorRGB).rstrip() + " is not properly formatted!")
                else:
                        self.sendErrorToGralog("neither Hex nor RGB color specified!")

                print(line.rstrip())
                sys.stdout.flush()
                # sys.stdin.readline()


        def setVertexStrokeColor(self,vertex,colorHex=-1,colorRGB=-1):
                vertex = vertexId(vertex)
                # print("colorhex: " + str(colorHex))
                line = "setVertexStrokeColor#"+str(self.id).rstrip() + "#" + str(vertex).rstrip() + "#"
                if not (colorHex==-1):
                        line = line + hexFormatter(str(colorHex))
                elif not (colorRGB == -1) and len(colorRGB) == 3:
                        line = line + rgbFormatter(colorRGB)
                print (line.rstrip())
                sys.stdout.flush()
                # sys.stdin.readline()

        def setVertexCoordinates(self,vertex,coordinates):

                line = "setVertexCoordinates#" + str(self.id).rstrip()+"#" + str(vertexId(vertex)).rstrip()
                x=-1
                y=-1


                x=coordinates[0]
                y=coordinates[1]
                if x == None:
                        x="empty"
                if y == None:
                        y = "empty"

                line += "#" + str(x).rstrip() + "#" + str(y).rstrip()
                print(line)
                sys.stdout.flush()


        def setEdgeContour(self, edge,contour):
                line = line = "setEdgeContour#"+str(self.id).rstrip() + "#"
                line = line + edgeSplitter(edge)
                line = line +"#" + str(contour).rstrip()
                print(line)
                sys.stdout.flush()


        def setEdgeColor(self,edge,colorHex=-1,colorRGB=-1):
                

                line = "setEdgeColor#"+str(self.id).rstrip() + "#"
                line = line + edgeSplitter(edge)
                line = line + "#"
                if not (colorHex==-1):
                        line = line + hexFormatter(colorHex)
                elif not (colorRGB == -1) and len(colorRGB) == 3:
                        line = line + rgbFormatter(colorRGB)
                print(line.rstrip())
                sys.stdout.flush()
                # sys.stdin.readline()




        def setVertexRadius(self,vertex,newRadius):
                self.setVertexDimension(vertex,newRadius,"radius")

        def setVertexHeight(self,vertex,newHeight):

                self.setVertexDimension(vertex,newHeight,"height")

        def setVertexWidth(self,vertex,newWidth):
                self.setVertexDimension(vertex,newWidth,"width")

        def setVertexDimension(self,vertex,newDimension,dimension):
                vertex = vertexId(vertex)

                line = "setVertexDimension#"+str(self.id).rstrip() + "#" + str(vertex).rstrip() + "#" + str(newDimension).rstrip()+"#" +dimension.rstrip()
                print(line.rstrip())

                sys.stdout.flush()
                # sys.stdin.readline()

        def setVertexShape(self,vertex,shape):
                vertex = vertexId(vertex)

                line = "setVertexShape#"+str(self.id).rstrip() + "#" + str(vertex).rstrip() + "#" + str(shape).rstrip()
                print(line.rstrip())

                sys.stdout.flush()
                # sys.stdin.readline()


        def setEdgeWeight(self,edge,weight):

                self.setEdgeProperty(edge,"weight",weight)

        def setEdgeThickness(self,edge,thickness):

                self.setEdgeProperty(edge,"thickness",thickness)

        def setEdgeProperty(self,edge,propertyName,value):
                line = "setEdgeProperty#"+str(self.id).rstrip() + "#"
                line = line + edgeSplitter(edge)

                line = line + "#" + propertyName.rstrip().lower() +  "#" + str(value).rstrip().lower()

                print(line.rstrip())
                sys.stdout.flush()

        def setVertexProperty(self,vertex,propertyName,value):
                line = "setVertexProperty#"+str(self.id).rstrip() + "#"
                line = line + str(vertexId(vertex)).rstrip()

                line = line + "#" + propertyName.rstrip().lower() +  "#" + str(value).rstrip().lower()

                print(line.rstrip())
                sys.stdout.flush()

        def setEdgeLabel(self,edge, label):
                line = "setEdgeLabel#"+str(self.id).rstrip() + "#"
                line = line + edgeSplitter(edge)

                line = line +"#" + label
                print(line.rstrip())

                sys.stdout.flush()
                # sys.stdin.readline()

        def setVertexLabel(self,vertex, label):
                vertex = vertexId(vertex)
                line = "setVertexLabel#"+str(self.id).rstrip() + "#" + str(vertex).rstrip() + "#" + label
                print(line.rstrip())

                sys.stdout.flush()
                # sys.stdin.readline()


        ####end: best for private use!


        def setGraph(self,graphFormat,graphString = "hello_world"):
                graphFormat = graphFormat.lower()
                line = "setGraph#"+str(self.id).rstrip() + "#" + graphFormat.rstrip()+"#"

                if graphFormat == "gtgf" or graphFormat == "tgf":
                        line += "$$\n"
                line += graphString
                if graphFormat == "gtgf" or graphFormat == "tgf":
                        line += "$\n"

                print(line)
                sys.stdout.flush()
                ##TODO: implement this

        ####end setter functions


        #####getter functions


        def toIgraph(self):
                grlgML_file = open("tmp.graphml","w")
                grlgML_file.write(self.toXml())
                grlgML_file.close()
                g_ig = ig.Graph.Read_GraphML("tmp.graphml")
                os.remove("tmp.graphml")
                return g_ig

        def toNx(self):
                grlgML_file = open("tmp.graphml","w")
                grlgML_file.write(self.toXml())
                grlgML_file.close()
                g_nx = nx.read_graphml("tmp.graphml")
                os.remove("tmp.graphml")
                return g_nx

        def toElementTree(self):
                grlgML_file = open("tmp.graphml","w")
                grlgML_file.write(self.toXml())
                grlgML_file.close()
                g_ET = ET.parse("tmp.graphml")
                os.remove("tmp.graphml")
                return g_ET

        def toXml(self):
                return self.getGraph("xml")

        def getGraph(self,graphFormat):
                ##warning!! importaing as pure TGF will search edge id's will
                ## be lost. This will result in errors on the Gralog side.


                line = "getGraph#"+str(self.id).rstrip() + "#" + graphFormat.rstrip()
                print(line.rstrip())
                i =0

                sys.stdout.flush()

                line = sys.stdin.readline()
                graphString = ""
                if graphFormat.lower() == "tgf" or graphFormat.lower() == "gtgf":
                        tgf = graphFormat.lower() == "tgf"

                        multiline = False
                        first = False
                        if line[0] == line[1] == '$':
                                # line = line[2:]
                                multiline = True
                                if tgf:
                                        first = True
                        line = sys.stdin.readline()

                        hashtagSeen = False
                        if not multiline:
                                return graphString
                        while line[0] != '$':
                                # gPrint("line: " + line +" and line[0]: " + line[0] + " and line[0]!='$': " + str(line[0] != '$'))
                                graphString += line
                                if line[0] == '#':

                                        hashtagSeen = True
                                else:
                                        if not first:
                                                if hashtagSeen:
                                                        if tgf:
                                                                self.edgifyTGFCommand(line)
                                                        else:
                                                                self.edgifyGTGFCommand(line)
                                                else:
                                                        if tgf:
                                                                self.vertexifyTGFCommand(line)
                                                        else:
                                                                self.vertexifyGTGFCommand(line)
                                line = sys.stdin.readline()
                                i += 1
                                first = False

                        return graphString
                if graphFormat.lower() == "xml":
                        return line


        def getAllVertices(self):
                #return: list of Vertex objects with id

                line = "getAllVertices#"+str(self.id).rstrip()
                print(line.rstrip())

                sys.stdout.flush()
                vertexIdStringList = (sys.stdin.readline()).split("#")
                vertexList = []
                for vertexIdString in vertexIdStringList:
                        if representsInt(vertexIdString):
                                v = self.getVertexOrNew(vertexIdString)

                                vertexList.append(v)
                return vertexList

        def getVertices(self):
                return(self.getAllVertices())

        def getAllEdges(self):
                #return: list of fully sourced Edge objects with fully sourced endpoint Vertices

                line = "getAllEdges#"+str(self.id).rstrip()
                print(line.rstrip())

                sys.stdout.flush()
                endpointList = sys.stdin.readline()

                endpointList = endpointList.split("#")
                edges = []
                if len(endpointList) == 1 and endpointList[-1] == "\n":
                        endpointList=[]
                for i in range(len(endpointList)):
                        term = endpointList[i].rstrip()
                        term = term[1:-1]
                        e = self.termToEdge(term)
                        if e != None:
                                edges.append(e)

                        # endpointList[i] = (int(endpoints[0]),int(endpoints[1]),int(endpoints[2]))
                return edges

        def getEdges(self):
                return(self.getAllEdges())
        
        ###start: best for private use!

        def getNeighbours(self,vertex):
                #return: list of Vertex objects with id
                vertex = vertexId(vertex)

                line = "getNeighbours#"+str(self.id).rstrip() + "#" + str(vertex).rstrip()
                print(line.rstrip())

                sys.stdout.flush()
                neighbourIdStringList = (sys.stdin.readline()).split("#")
                neighboursList = []
                for neighbourIdString in neighbourIdStringList:
                        if representsInt(neighbourIdString):
                                v = self.getVertexOrNew(neighbourIdString)

                                neighboursList.append(v)
                return neighboursList

        def getOutgoingNeighbours(self,vertex):
                #return: list of Vertex objects with id
                vertex = vertexId(vertex)

                line = "getOutgoingNeighbours#"+str(self.id).rstrip() + "#" + str(vertex).rstrip()
                print(line.rstrip())

                sys.stdout.flush()
                outgoingNeighbourIdStringList = (sys.stdin.readline()).split("#")
                outgoingNeighboursList = []
                for outgoingNeighbourIdString in outgoingNeighbourIdStringList:
                        if representsInt(outgoingNeighbourIdString):
                                v = self.getVertexOrNew(outgoingNeighbourIdString)
                                outgoingNeighboursList.append(v)
                return outgoingNeighboursList


        def getIncomingNeighbours(self,vertex):
                #return: list of Vertex objects with id
                vertex = vertexId(vertex)

                line = "getIncomingNeighbours#"+str(self.id).rstrip() + "#" + str(vertex).rstrip()
                print(line.rstrip())

                sys.stdout.flush()
                incomingNeighbourIdStringList = (sys.stdin.readline()).split("#")
                incomingNeighboursList = []
                for incomingNeighbourIdString in incomingNeighbourIdStringList:
                        if representsInt(incomingNeighbourIdString):
                                v = self.getVertexOrNew(incomingNeighbourIdString)
                                incomingNeighboursList.append(v)
                return incomingNeighboursList




        def getIncidentEdges(self,vertex):
                #return: list of Edge objects with id's only
                vertex = vertexId(vertex)


                line = "getIncidentEdges#"+str(self.id).rstrip() + "#" + str(vertex).rstrip()
                print(line.rstrip())

                sys.stdout.flush()
                endpointList = sys.stdin.readline()

                endpointList = endpointList.split("#")
                edges = []

                for i in range(len(endpointList)):
                        term = endpointList[i].rstrip()
                        term = term[1:-1]
                        e = self.termToEdge(term)
                        if e != None:
                                edges.append(e)

                return edges

        def getAdjacentEdges(self,edge):
                #return: list of Edge objects with id's only
                line = "getAdjacentEdges#"+str(self.id).rstrip() + "#"
                line = line + edgeSplitter(edge)

                print(line.rstrip())

                sys.stdout.flush()
                endpointList = sys.stdin.readline()

                endpointList = endpointList.split("#")
                edges = []

                for i in range(len(endpointList)):
                        term = endpointList[i].rstrip()
                        term = term[1:-1]
                        e = self.termToEdge(term)
                        if e != None:
                                edges.append(e)

                return edges
        def getOutgoingEdges(self,vertex):
                #return: list of Edge objects with id's only
                vertex = vertexId(vertex)

                line = "getOutgoingEdges#"+str(self.id).rstrip() + "#" + str(vertex).rstrip()
                print(line.rstrip())

                sys.stdout.flush()
                endpointList = sys.stdin.readline()

                endpointList = endpointList.split("#")
                edges = []

                for i in range(len(endpointList)):
                        term = endpointList[i].rstrip()
                        term = term[1:-1]
                        e = self.termToEdge(term)
                        if e != None:
                                edges.append(e)

                return edges


        def getIncomingEdges(self,vertex):
                #return: list of Edge objects with id's only
                vertex = vertexId(vertex)
                line = "getIncomingEdges#"+str(self.id).rstrip() + "#" + str(vertex).rstrip()
                print(line.rstrip())

                sys.stdout.flush()
                endpointList = sys.stdin.readline()

                endpointList = endpointList.split("#")
                edges = []

                for i in range(len(endpointList)):
                        term = endpointList[i].rstrip()
                        term = term[1:-1]
                        e = self.termToEdge(term)
                        if e != None:
                                edges.append(e)

                return edges



        def getEdgeWeight(self,edge):
                return self.getEdgeProperty(edge,"weight")

        def getEdgeLabel(self,edge):
                return self.getEdgeProperty(edge,"label")



        def getEdgeProperty(self,edge,prop):
                #internally: fill edge property dictionary
                #return: String representing queried property

                line = "getEdgeProperty#"+str(self.id).rstrip() + "#"
                line = line + edgeSplitter(edge)
                line = line + "#" + prop.rstrip().lower()

                print(line.rstrip())
                sys.stdout.flush()

                edgeTuple = sys.stdin.readline().rstrip()
                edge.sourceProperties(edgeTuple)
                return edge.getProperty(prop)



        def getVertexProperty(self,vertex,prop):
                #internally: fill edge property dictionary
                #return: String representing queried property

                vid = vertexId(vertex)

                line = "getVertexProperty#"+str(self.id).rstrip() + "#"
                line = line + vid
                line = line + "#" + prop.rstrip().lower()

                print(line.rstrip())
                sys.stdout.flush()

                vertexTuple = sys.stdin.readline().rstrip()
                vertex.sourceProperties(vertexTuple)
                return vertex.getProperty(prop)

        ###end: best use privately!


        def requestVertex(self):
                line = "requestVertex#"+str(self.id).rstrip()
                print(line.rstrip())
                sys.stdout.flush()

                vid = sys.stdin.readline().rstrip()
                vertex = self.getVertexOrNew(vid)
                return vertex

        def requestRandomVertex(self):
                line = "requestRandomVertex#"+str(self.id).rstrip()
                print(line.rstrip())
                sys.stdout.flush()

                vid = sys.stdin.readline().rstrip()
                vertex = self.getVertexOrNew(vid)
                return vertex

        def requestEdge(self):
                line = "requestEdge#"+str(self.id).rstrip()
                print(line.rstrip())
                sys.stdout.flush()

                vid = sys.stdin.readline().rstrip()
                edge = self.getEdgeOrNew(vid)
                return edge

        def requestRandomEdge(self):
                line = "requestRandomEdge#"+str(self.id).rstrip()
                print(line.rstrip())
                sys.stdout.flush()

                eid = sys.stdin.readline().rstrip()
                edge = self.getEdgeOrNew(eid)
                return edge

        def requestInteger(self):
                line = "requestInteger#"+str(self.id).rstrip()
                print(line.rstrip())
                sys.stdout.flush()

                i = sys.stdin.readline().rstrip()
                return int(i)

        def requestFloat(self):
                line = "requestFloat#"+str(self.id).rstrip()
                print(line.rstrip())
                sys.stdout.flush()

                d = sys.stdin.readline().rstrip()
                return float(d)

        def requestString(self):
                line = "requestString#"+str(self.id).rstrip()
                print(line.rstrip())
                sys.stdout.flush()

                st = sys.stdin.readline().rstrip()
                return str(st)

        # def getEdgesByPropertyValue(self,prop,val):
        #       #return: list of Edge objects with id only
        #       line = "getEdgesByPropertyValue#"+str(self.id).rstrip() + "#" + str(prop).rstrip() + "#" + str(val).rstrip()

        #       print(line.rstrip())
        #       sys.stdout.flush()

        #       endpointList = sys.stdin.readline()

        #       endpointList = endpointList.split("#")
        #       id_to_edge = []
        #       for i in range(len(endpointList)):
        #               term = endpointList[i].rstrip()
        #               term = term[1:-1]

        #               e = self.termToEdge(term)
        #               id_to_edge.append(e)
        #       return id_to_edge


        ####end getter functions


        ####runtime changer functions


        def pauseUntilSpacePressed(self,*args):
                # print("pauseUntilSpacePressed")
                line = "pauseUntilSpacePressed"
                rank = None
                try:
                        rank = int(args[0])
                except:
                        pass
                if len(args) > 0 and rank != None:
                        rank = int(args[0])
                        args = args[1:]

                argString = ""

                if rank != None:
                        argString += "#"+str(rank).rstrip()

                for key in sorted(self.variablesToTrack.keys()):
                        term = "#("+str(key).rstrip()+"="+str(self.variablesToTrack[key]).rstrip()+")"
                        # print ("boi term : " + term)
                        argString = argString + term.rstrip()
                # line = "pauseUntilSpacePressed (hello,world)"

                for x in args:
                        if len(x) != 2:
                                argString = "#(syntax=pauseUntilSpacePressed((key,val)))"
                                break
                        if (type(x) == list):
                                for each in x:
                                        term = "#("+"arrayyyy"+str(each[0])+"="+str(each[1])+")"
                                        argString = argString + term
                        else:
                                term = "#("+str(x[0])+"="+str(x[1])+")"
                                argString = argString + term.rstrip()


                line = line + argString
                print(line)
                sys.stdout.flush()
                toSkip = sys.stdin.readline()
                # print ("we just got " + str(toSkip).rstrip() + " back from the pause func!")

        def track(self,name,var):
                #ideally, something like this:
                self.variablesToTrack[name] = var #if this is a pointer, it will work
                # if it is an int or str, or some other non-reference type, it will not

        def unTrack(self,name):
                del self.variablesToTrack[name]

        def sendMessage(self,toSend):
                print(toSend)
                sys.stdout.flush()

        def message(self, message):
                print("message#"+str(self.id).rstrip() + "#"+str(message).rstrip())
                sys.stdout.flush()

        def sendErrorToGralog(self,toSend):
                print("error#"+str(self.id).rstrip() + "#"+str(toSend).rstrip())
                sys.stdout.flush()
                exit()

        def mistakeLine(self):
                print("wubbadubdub 3 men in a tub")
                sys.stdout.flush()
                sys.stdin.readline()

        def pause(self,*args):
                self.pauseUntilSpacePressed(*args)


        #####end runtime changer functions


        def __str__(self):
                vertices = [str(v) for v in self.id_to_vertex]
                vertices.sort()
                edges = [str(e) for e in self.getEdges()]
                edges.sort()
                return "VERTICES: " + " ".join(vertices) + "\nEDGES: " + " ".join(edges)

def gPrint(message):
        if not message: # empty: print nothing except the new line (hacked with \t; <space> doesn't work)
                print("gPrint#-1#" + "\t")
                sys.stdout.flush()
        else:
                message = str(message)
                lines = message.split('\n')
                for line in lines:
                        print("gPrint#-1#" + line)
                        sys.stdout.flush()

def representsInt(s):
    try:
        int(s)
        return True
    except ValueError:
        return False
