#!/usr/bin/python

import sys
from random import randint



class Vertex:
	def __init__(self,graph,vid):
		self.sourced = False;
		self.id = int(vid);
		self.graph = graph;
		self.properties = dict();
		self.properties["id"] = None;
		self.properties["label"] = None;
		self.properties["color"] = None;
		self.properties["strokeColor"] = None;
		self.properties["shape"] = None;
		self.incomingEdges = [];
		self.outgoingEdges = [];
		self.incidentEdges = [];
		self.wasSourced = False;
	def sourceProperties(self,stringFromGralog):
		self.sourced = True;
		self.sourced = True;
		strings = stringFromGralog.split("|");
		for string in strings:
			propVal = string.split("=");
			try:
				prop = propVal[0];
				val = propVal[1];
				self.properties[prop] = val;
			except:
				print("fuck")
				pass;

	def getId(self):
		return self.id;
	def setLabel(self,label):
		self.properties["label"] = label;
		self.graph.setVertexLabel(self.id,label);
	def getLabel(self):
		if not self.wasSourced:
			self.source();
		return self.properties["label"];
	def setFillColor(self,colorHex=-1,colorRGB=-1):
		if not (colorHex == -1):
			self.properties["color"] = colorHex;
			self.graph.setVertexFillColor(self.id,colorHex=colorHex);
		elif not (colorRGB == -1) and len(colorRGB) == 3:
			self.properties["color"] =colorRGB;
			self.graph.setVertexFillColor(self.id,colorRGB=colorRGB);
	def getFillColor(self):
		if not self.wasSourced:
			self.source();
		return self.properties["color"];
	def setStrokeColor(self,colorHex=-1,colorRGB=-1):
		if not (colorHex == -1):
			self.color = colorHex;
			self.graph.setVertexStrokeColor(self.id,colorHex=colorHex);
		elif not (colorRGB == -1) and len(colorRGB) == 3:
			self.color=colorRGB;
			self.graph.setVertexStrokeColor(self.id,colorRGB=colorRGB);
	def getStrokeColor(self):
		if not self.sourced:
			self.source();
		return self.properties["strokeColor"];
	def setColor(self,colorHex=-1,colorRGB=-1):
		self.graph.setVertexFillColor(self.id,colorHex,colorRGB);
	def setRadius(self,radius):
		self.graph.setVertexRadius(self.getId(),radius);
	def setWidth(self,width):
		self.graph.setVertexWidth(self.getId(),width);
	def setHeight(self,height):
		self.graph.setVertexHeight(self.getId(),height);
	def getColor(self):
		if not self.sourced:
			self.source();
		return self.fillColor;
	def setShape(self,shape):
		self.shape = shape;
		self.graph.setVertexShape(self.id,shape);
	def getShape(self):
		if not self.sourced:
			self.source();
		return self.shape;
	def setOtherProperty(self,otherProperty,value):
		self.properties[otherProperty] = value;
		self.graph.setVertexProperty(self.id,otherProperty,value);
	def get(self,prop):
		if not self.sourced:
			self.source();
		return self.properties[prop];
	def getNeighbours(self):
		return self.graph.getNeighbours(self.id);
	def delete(self):
		return self.graph.deleteVertex(self);
	def connect(self,v1,directed = False,edgeId=-1):
		return self.graph.addEdge(self,v1,directed,edgeId);
	def source(self):
		return self.graph.getVertex(self);
	def __str__(self):
		return str(self.getId());

	# def getIncidentEdges:
	# 	return self.incidentEdges;

	#what if i want to get a vertex? should i also get all its neighbours? how about incidenet edges? This is all v aufw\"andig and leads to the paradigm by which we just store the grpah in python???



class Edge:
	def __init__(self,graph,eid):
		self.sourced = False;
		self.id = int(eid);
		self.graph = graph;
		self.properties = dict();
		self.properties["id"] = None;
		self.properties["label"] = None;
		self.properties["color"] = None;
		self.properties["weight"] = None;
		self.properties["contour"] = None;
		self.properties["source"] = None;
		self.properties["target"] = None;
		self.wasSourced = False;

	def sourceProperties(self,stringFromGralog):
		self.sourced = True;
		strings = stringFromGralog.split("|");
		for string in strings:
			propVal = string.split("=");
			try:
				prop = propVal[0];
				val = propVal[1];
				self.properties[prop] = val;
			except:
				pass;
	def getId(self):
		return self.id;
	def setLabel(self,label):
		self.properties["label"] = label;
		self.graph.setEdgeLabel(self.id,label);
	def getLabel(self):
		if not self.sourced:
			self.source();
		return self.properties["label"];

	def setColor(self,colorHex=-1,colorRGB=-1):
		if not (colorHex == -1):
			self.properties["color"] = colorHex;
			self.graph.setEdgeColor(self.id,colorHex=colorHex);
		elif not (colorRGB == -1) and len(colorRGB) == 3:
			self.properties["color"] =colorRGB;
			self.graph.setEdgeColor(self.id,colorRGB=colorRGB);
	def getColor(self):
		if not self.sourced:
			self.source();
		return self.properties["color"];
	def setWeight(self,weight):
		self.weight = int(weight);
		self.graph.setEdgeWeight(self.id,weight);
	def getWeight(self):
		if not self.sourced:
			self.source();
		return self.weight;
	def setContour(self,contour):

		self.graph.setEdgeContour(self.id,contour);
	def getContour(self):
		if not self.sourced:
			self.source();
		return self.contour;
	def setSource(self,source):
		self.properties["source"] = source;
	def getSource(self):
		if not self.sourced:
			self.source();
		return self.properties["source"];
	def setTarget(self,target):
		self.properties["target"] = target;
	def getTarget(self):
		if not self.sourced:
			self.source();
		return self.properties["target"];
	def setOtherProperty(self,otherProperty,value):
		self.properties[otherProperty] = value;
		self.graph.setEdgeProperty(otherProperty,value);
	def get(self,prop):
		self.source();
		return self.properties[prop];
	def delete(self):
		return self.graph.deleteEdge(self);
	def source(self):
		return self.graph.getEdge(self);


	def __str__(self):
		return str(self.getId());


def rgbFormatter(colorRGB):
	r = colorRGB[0];
	g = colorRGB[1];
	b = colorRGB[2];
	s = "rgb";
	s += "(" + str(r).rstrip() + "," + str(g).rstrip() + "," + str(b).rstrip() + ")";
	return s.rstrip();

def hexFormatter(colorHex):
	s = "hex";
	if colorHex[0] == "#":
		colorHex = colorHex[1:];
	s += "("+str(colorHex).rstrip() + ")";
	return s.rstrip();

		
def vertexId(vertex):
	if isinstance(vertex,Vertex):
		return vertex.getId();
	return vertex;

def edgeId(edge):
	if isinstance(edge,Ddge):
		return edge.getId();
	return edge;



def extractIdFromProperties(stringFromGralog):
	strings = stringFromGralog.split("|");
	for string in strings:

		propVal = string.split("=");
		if propVal[0] == "id":
			return propVal[1];
	return None;


def edgeSplitter(edge):
	if type(edge) == tuple and len(edge)==2:#edge as defined by start, end nodes
		return str(vertexId(edge[0])).rstrip()+","+str(vertexId(edge[1])).rstrip();
	return str(edge).rstrip();#edge as defined by id

class Graph:
	def __init__(self,format="Undirected Graph"):
		#perform analysis of graph
		self.vertices = dict();
		self.edges = dict();
		self.lastIndex = -1;
		self.id = -1;
		self.variablesToTrack = dict();
		if format == None or format.lower() == "none":
			#we want a new graph

			print("useCurrentGraph");
			sys.stdout.flush();

			self.lastIndex = -1;
			self.id = sys.stdin.readline();
			
	
		else:
			print(format);
			sys.stdout.flush();
			self.id = sys.stdin.readline();
	

	####helper functions
	def getVertexOrNew(self,currId):

		v = None;
		try:
			v=self.vertices[currid];
		except:
			v=Vertex(self,currId);
		return v;

	def getEdgeOrNew(self,currId):

		e = None;
		try:
			e=self.edges[currid];
		except:
			e=Edge(self,currId);
		return e;

	def termToEdge(self,term):
		endpoints = term.split(",");
		eid = self.extractIdFromProperties(endpoints[0]);
		e = self.edges[eid];
		e.sourceProperties(endpoints[0]);
		sourceId = self.extractIdFromProperties(endpoints[1]);
		source = self.getVertexOrNew(sourceId);
		targetId = self.extractIdFromProperties(endpoints[2]);
		target = self.getVertexOrNew(targetId);

		####possibly to remove
		source.sourceProperties(endpoints[1]);
		target.sourceProperties(endpoints[2]);
		####fin

		e.setSource(source);
		e.setTarget(target);
		return e;


	

	def representsInt(s):
	    try: 
	        int(s)
	        return True
	    except ValueError:
	        return False


	####end helper functions


	####Graph Manipulating Functions

	def addVertex(self,x=None,y=None,vertexId=-1):
		#return: Vertex object with id
		idString = "";
		if vertexId != -1:
			idString = "#"+str(vertexId).rstrip();
		print ("addVertex#" + str(self.id).rstrip() + idString)
		sys.stdout.flush();


		vid = sys.stdin.readline();
		v = Vertex(self,vid);

		self.vertices[v.getId()] = v;
		
		return v;

	def deleteVertex(self,vertex):
		vertex = vertexId(vertex);
		
		print "deleteVertex#" +str(self.id).rstrip() + "#" +  str(vertex);
		sys.stdout.flush();
		# sys.stdin.readline();


	def setGraph(self,format,graphString = None):
		line = "setGraph#"+str(self.id).rstrip() + "#" + format.rstrip()+"#";
		#@Michelle: format entspricht z.B. XML oder TGF oder sowas. du muesst das also gemaess deines Formats entsprechend eingeben
		#kommentiere die folgende Zeile aus und fuege deinen Kram hinzu...
		#line = line + XML_NACH_MICHELLE
		XML_NACH_MICHELLE = "hello_world";
		print line + XML_NACH_MICHELLE;
		sys.stdout.flush();
		##TODO: implement this somehow haha


	def addEdge(self,sourceVertex, targetVertex, directed = False, edgeId = -1):
		#return: Edge object with id only
		sourceVertex = vertexId(sourceVertex);
		targetVertex = vertexId(targetVertex);


		idSubString = "";
		if not edgeId==-1:
			idSubString = "#"+str(edgeId);
			
		line = "addEdge#"+str(self.id).rstrip() + "#" + str(sourceVertex).rstrip() + "#" + str(targetVertex).rstrip() + "#" + str(directed).lower()+idSubString.rstrip();

		print line.rstrip();
	
		sys.stdout.flush();
		eid = sys.stdin.readline();
		
		e = Edge(self,eid);

		self.edges[e.getId()] = e;
		return e;

	def addDirectedEdge(self,sourceVertex, targetVertex, edgeId=-1):
		#return: Edge object with id only
		return self.addEdge(sourceVertex,targetVertex,directed=True,edgeId=edgeId);


	def deleteEdge(self,edge):

		line = "deleteEdge#"+str(self.id).rstrip() + "#";
		line = line + edgeSplitter(edge);

		print line.rstrip();

		sys.stdout.flush();
		# sys.stdin.readline();

	def deleteAllEdges(self,(sourceVertexId,targetVertexId)):

		line = "deleteAllEdges#"+str(self.id).rstrip() + "#" + str(sourceVertexId).rstrip() + "#" + str(targetVertexId).rstrip();

		print line.rstrip();

		sys.stdout.flush();
		# sys.stdin.readline();


	####end manilupative functions

	###setter functions

	def setVertexFillColor(self,vertex,colorHex=-1,colorRGB=-1):
		# print("colorhex: " + str(colorHex));
		vertex = vertexId(vertex);
		line = "setVertexFillColor#" + str(self.id).rstrip() + "#" + str(vertex).rstrip() + "#"
		if not (colorHex==-1):
			line = line + hexFormatter(str(colorHex));
		elif not (colorRGB == -1):
			try:
				line = line + rgbFormatter(colorRGB);
			except:
				self.sendErrorToGralog("the rgb color: " + str(colorRGB).rstrip() + " is not properly formatted!");
		else:
			self.sendErrorToGralog("neither Hex nor RGB color specified!");
			
		print(line.rstrip());
		sys.stdout.flush();
		# sys.stdin.readline();
	

	def setVertexStrokeColor(self,vertex,colorHex=-1,colorRGB=-1):
		vertex = vertexId(vertex);
		# print("colorhex: " + str(colorHex));
		line = "setVertexStrokeColor#"+str(self.id).rstrip() + "#" + str(vertex).rstrip() + "#"
		if not (colorHex==-1):
			line = line + hexFormatter(colorHex);
		elif not (colorRGB == -1) and len(colorRGB) == 3:
			line = line + rgbFormatter(colorRGB);
		print line.rstrip();
		sys.stdout.flush();
		# sys.stdin.readline();

	def setEdgeContour(self, edge,contour):
		
		line = line = "setEdgeContour#"+str(self.id).rstrip() + "#";
		line = line + edgeSplitter(edge);
		line = line +"#" + str(contour).rstrip();
		print(line);
		sys.stdout.flush()


	def setEdgeColor(self,edge,colorHex=-1,colorRGB=-1):
		
		line = "setEdgeColor#"+str(self.id).rstrip() + "#";
		line = line + edgeSplitter(edge);
		line = line + "#";
		if not (colorHex==-1):
			line = line + hexFormatter(colorHex);
		elif not (colorRGB == -1) and len(colorRGB) == 3:
			line = line + rgbFormatter(colorRGB);
		print line.rstrip();
		sys.stdout.flush();
		# sys.stdin.readline();




	def setVertexRadius(self,vertex,newRadius):
		self.setVertexDimension(vertex,newRadius,"radius");

	def setVertexHeight(self,vertex,newHeight):
		self.setVertexDimension(vertex,newHeight,"height");

	def setVertexWidth(self,vertex,newWidth):
		self.setVertexDimension(vertex,newWidth,"width");

	def setVertexDimension(self,vertex,newDimension,dimension):
		vertex = vertexId(vertex);
		
		line = "setVertexDimension#"+str(self.id).rstrip() + "#" + str(vertex).rstrip() + "#" + str(newDimension).rstrip()+"#" +dimension.rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		# sys.stdin.readline();

	def setVertexShape(self,vertex,shape):
		vertex = vertexId(vertex);
			
		line = "setVertexShape#"+str(self.id).rstrip() + "#" + str(vertex).rstrip() + "#" + str(shape).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		# sys.stdin.readline();


	def setEdgeWeight(self,edge,weight):
		self.setEdgeProperty(edge,"weight",weight);

	def setEdgeProperty(self,edge,property,value):
		line = "setEdgeProperty#"+str(self.id).rstrip() + "#"
		line = line + edgeSplitter(edge);

		line = line + "#" + property.rstrip().lower() +  "#" + str(value).rstrip().lower();

		print line.rstrip();
		sys.stdout.flush();

	

	def setEdgeLabel(self,edge, label):
		
		
		line = "setEdgeLabel#"+str(self.id).rstrip() + "#";
		line = line + edgeSplitter(edge);

		line = line +"#" + label;
		print line.rstrip();

		sys.stdout.flush();
		# sys.stdin.readline();

	def setVertexLabel(self,vertex, label):
		vertex = vertexId(vertex);
		line = "setVertexLabel#"+str(self.id).rstrip() + "#" + str(vertex).rstrip() + "#" + label;
		print line.rstrip();

		sys.stdout.flush();
		# sys.stdin.readline();

	####end setter functions


	#####getter functions




	def getGraph(self,format):
		
			
		line = "getGraph#"+str(self.id).rstrip() + "#" + format.rstrip();
		print line.rstrip();
		i =0;
	
		sys.stdout.flush();

		line = sys.stdin.readline();
		graph = "";

		multiline = False;
		if line[0] == line[1] == '$':
			# line = line[2:];
			multiline = True;
			first = True;
			

		while multiline and (line[0] != '$' or first):
			graph += line;
			line = sys.stdin.readline();
			i += 1;
			first = False;
		# nextline = "also getting: " ,sys.stdin.readline();


		return graph;

	
	

	def getAllVertices(self):
		#return: list of Vertex objects with id
		
		line = "getAllVertices#"+str(self.id).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		vertexIdStringList = (sys.stdin.readline()).split("#");
		vertexList = [];
		for vertexIdString in vertexIdStringList:
			v = self.getVertexOrNew(vertexIdString);

			vertexList.append(v);
		return vertexList;


	def getNeighbours(self,vertex):
		#return: list of Vertex objects with id
		vertex = vertexId(vertex);
			
		line = "getNeighbours#"+str(self.id).rstrip() + "#" + str(vertex).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		neighbourIdStringList = (sys.stdin.readline()).split("#");
		neighboursList = [];
		for neighbourIdString in neighbourIdStringList:
			v = getVertexOrNew(neighbourIdString);
			neighboursList.append(v);
		return neighboursList;

	def getOutgoingNeighbours(self,vertex):
		#return: list of Vertex objects with id
		vertex = vertexId(vertex);
			
		line = "getOutgoingNeighbours#"+str(self.id).rstrip() + "#" + str(vertex).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		outgoingNeighbourIdStringList = (sys.stdin.readline()).split("#");
		outgoingNeighboursList = [];
		for outgoingNeighbourIdString in outgoingNeighbourIdStringList:
			v = getVertexOrNew(outgoingNeighbourIdString);
			outgoingNeighboursList.append(v);
		return outgoingNeighboursList;


	def getIncomingNeighbours(self,vertex):
		#return: list of Vertex objects with id
		vertex = vertexId(vertex);
			
		line = "getIncomingNeighbours#"+str(self.id).rstrip() + "#" + str(vertex).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		incomingNeighbourIdStringList = (sys.stdin.readline()).split("#");
		incomingNeighboursList = [];
		for incomingNeighbourIdString in incomingNeighbourIdStringList:
			v = getVertexOrNew(incomingNeighbourIdString);
			incomingNeighboursList.append(v);
		return incomingNeighboursList;

	
	def getAllEdges(self):
		#return: list of fully sourced Edge objects with fully sourced endpoint Vertices
			
		line = "getAllEdges#"+str(self.id).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		endpointList = sys.stdin.readline();

		endpointList = endpointList.split("#");
		edges = [];

		for i in range(len(endpointList)):
			term = endpointList[i].rstrip();
			term = term[1:-1];
			e = self.termToEdge(term);
			
			edges.append(e);

			# endpointList[i] = (int(endpoints[0]),int(endpoints[1]),int(endpoints[2]));
		return edges;

	def getIncidentEdges(self,vertex):
		#return: list of Edge objects with id's only
		vertex = vertexId(vertex);

			
		line = "getIncidentEdges#"+str(self.id).rstrip() + "#" + str(vertex).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		edgeIdList = (sys.stdin.readline()).split("#");
		# print("endpointList: " , endpointList);
		edges = [];
		for i in range(len(edgeIdList)):
			id = edgeIdList[i].rstrip();

			e = self.getEdgeOrNew(id);
			edges.append(e);

		return edges;

	def getAdjacentEdges(self,edge):
		#return: list of Edge objects with id's only
		line = "getAdjacentEdges#"+str(self.id).rstrip() + "#";
		line = line + edgeSplitter(edge);

		print line.rstrip();
	
		sys.stdout.flush();
		edgeIdList = (sys.stdin.readline()).split("#");
		# print("endpointList: " , endpointList);
		edges = [];
		for i in range(len(edgeIdList)):
			id = edgeIdList[i].rstrip();

			e = self.getEdgeOrNew(id);
			edges.append(e);

		return edges;
	def getOutgoingEdges(self,vertex):
		#return: list of Edge objects with id's only
		vertex = vertexId(vertex);
			
		line = "getOutgoingEdges#"+str(self.id).rstrip() + "#" + str(vertex).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		edgeIdList = (sys.stdin.readline()).split("#");
		# print("endpointList: " , endpointList);
		edges = [];
		for i in range(len(edgeIdList)):
			id = edgeIdList[i].rstrip();

			e = self.getEdgeOrNew(id);
			edges.append(e);
		return edges;


	def getIncomingEdges(self,vertex):
		#return: list of Edge objects with id's only
		vertex = vertexId(vertex);
		line = "getIncomingEdges#"+str(self.id).rstrip() + "#" + str(vertex).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		edgeIdList = (sys.stdin.readline()).split("#");
		# print("endpointList: " , endpointList);
		edges = [];
		for i in range(len(edgeIdList)):
			id = edgeIdList[i].rstrip();

			e = self.getEdgeOrNew(id);
			edges.append(e);
		return edges;

	

	def getEdgeWeight(self,edge):
		return self.getEdgeProperty(edge,"weight");

	def getEdgeColor(self,edge):
		return self.getEdgeProperty(edge,"color");

	def getEdge(self,edge):
		line = "getEdge#"+str(self.id).rstrip() + "#"
		line = line + edgeSplitter(edge)

		print line.rstrip();
		sys.stdout.flush();

		edgeTuple = sys.stdin.readline().rstrip();
		edge.sourceProperties(edgeTuple);
		return edge;


	def getEdgeProperty(self,edge,prop):
		#internally: fill edge property dictionary
		#return: String representing queried property

		line = "getEdgeProperty#"+str(self.id).rstrip() + "#"
		line = line + edgeSplitter(edge)
		line = line + "#" + prop.rstrip().lower();

		print line.rstrip();
		sys.stdout.flush();

		edgeTuple = sys.stdin.readline().rstrip();
		edge.sourceProperties(edgeTuple);
		return edge.getProperty(prop);

	def getVertex(self,vertex):
		line = "getVertex#"+str(self.id).rstrip() + "#"
		line = line + str(vertex).rstrip();

		print line.rstrip();
		sys.stdout.flush();

		vertexTuple = sys.stdin.readline().rstrip();

		vertex.sourceProperties(vertexTuple);
		return vertex;

	def getVertexProperty(self,vertex,prop):
		#internally: fill edge property dictionary
		#return: String representing queried property

		vid = vertexId(vertex)

		line = "getVertexProperty#"+str(self.id).rstrip() + "#"
		line = line + vid
		line = line + "#" + prop.rstrip().lower();

		print line.rstrip();
		sys.stdout.flush();

		vertexTuple = sys.stdin.readline().rstrip();
		vertex.sourceProperties(vertexTuple);
		return vertex.getProperty(prop);


	def requestVertex(self):
		line = "requestVertex#"+str(self.id).rstrip();
		print line.rstrip();
		sys.stdout.flush();

		vid = sys.stdin.readline().rstrip();
		vertex = self.getVertexOrNew(vid);
		return vertex;

	def requestRandomVertex(self):
		line = "requestRandomVertex#"+str(self.id).rstrip();
		print line.rstrip();
		sys.stdout.flush();

		vid = sys.stdin.readline().rstrip();
		vertex = self.getVertexOrNew(vid);
		return vertex;

	def requestEdge(self):
		line = "requestEdge#"+str(self.id).rstrip();
		print line.rstrip();
		sys.stdout.flush();

		vid = sys.stdin.readline().rstrip();
		edge = self.getEdgeOrNew(vid);
		return edge;

	def requestRandomEdge(self):
		line = "requestRandomEdge#"+str(self.id).rstrip();
		print line.rstrip();
		sys.stdout.flush();

		eid = sys.stdin.readline().rstrip();
		edge = self.getEdgeOrNew(eid);
		return edge;

	# def getEdgesByPropertyValue(self,prop,val):
	# 	#return: list of Edge objects with id only
	# 	line = "getEdgesByPropertyValue#"+str(self.id).rstrip() + "#" + str(prop).rstrip() + "#" + str(val).rstrip();

	# 	print line.rstrip();
	# 	sys.stdout.flush();

	# 	endpointList = sys.stdin.readline();

	# 	endpointList = endpointList.split("#");
	# 	edges = [];
	# 	for i in range(len(endpointList)):
	# 		term = endpointList[i].rstrip();
	# 		term = term[1:-1]

	# 		e = self.termToEdge(term);
	# 		edges.append(e);
	# 	return edges;


	####end getter functions


	####runtime changer functions


	def pauseUntilSpacePressed(self,*args):
		# print("pauseUntilSpacePressed");
		line = "pauseUntilSpacePressed";
		rank = None;
		try:
			rank = int(args[0]);
		except:
			pass;
		if len(args) > 0 and rank != None:
			rank = int(args[0]);
			args = args[1:];

		argString = "";

		if rank != None:
			argString += "#"+str(rank).rstrip();

		for key in sorted(self.variablesToTrack.keys()):
			term = "#("+str(key).rstrip()+"="+str(self.variablesToTrack[key]).rstrip()+")";
			# print ("boi term : " + term);
			argString = argString + term.rstrip();
		# line = "pauseUntilSpacePressed (hello,world)";

		for x in args:
			if len(x) != 2:
				argString = "#(syntax=pauseUntilSpacePressed((key,val)))"
				break;
			if (type(x) == list):
				for each in x:
					term = "#("+"arrayyyy"+str(each[0])+"="+str(each[1])+")";
					argString = argString + term;
			else:	
				term = "#("+str(x[0])+"="+str(x[1])+")";
				argString = argString + term.rstrip();


		line = line + argString;
		print line;
		sys.stdout.flush();
		toSkip = sys.stdin.readline();
		# print ("we just got " + str(toSkip).rstrip() + " back from the pause func!");

	def track(self,name,var):
		#ideally, something like this:
		self.variablesToTrack[name] = var; #if this is a pointer, it will work;
		# if it is an int or str, or some other non-reference type, it will not

	def unTrack(self,name):
		del self.variablesToTrack[name];

	def sendMessage(self,toSend):
		print toSend;
		sys.stdout.flush();

	def sendErrorToGralog(self,toSend):
		print "error#"+str(self.id).rstrip() + "#"+str(toSend).rstrip();
		sys.stdout.flush();
		exit();

	def mistakeLine(self):
		print("wubbadubdub 3 men in a tub");
		sys.stdout.flush();
		sys.stdin.readline();

	def pause(self,*args):
		self.pauseUntilSpacePressed(*args);


	#####end runtime changer functions


	def __str__(self):
		return "todo: tgf"




