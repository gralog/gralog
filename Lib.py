#!/usr/bin/python

import sys
from random import randint



class Vertex:
	def __init__(self,graph):
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
	def sourceProperties(self,stringFromGralog):
		strings = stringFromGralog.split("|");
		for string in strings:
			propVal = string.split("=");
			prop = propVal[0];
			val = propVal[1];
			self.properties[prop] = val;

	def getId(self):
		return self.properties["id"];
	def setLabel(self,label):
		self.properties["label"] = label;
		self.graph.setVertexLabel(self.id,label);
	def getLabel(self):
		return self.properties["label"];
	def setFillColor(self,colorHex=-1,colorRGB=-1):
		if not (colorHex == -1):
			self.properties["color"] = colorHex;
			self.graph.setVertexFillColor(self.id,colorHex=colorHex);
		elif not (colorRGB == -1) and len(colorRGB) == 3:
			self.properties["color"] =colorRGB;
			self.graph.setVertexFillColor(self.id,colorRGB=colorRGB);
	def getFillColor(self):
		return self.properties["color"];
	def setStrokeColor(self,colorHex=-1,colorRGB=-1):
		if not (colorHex == -1):
			self.color = colorHex;
			self.graph.setVertexStrokeColor(self.id,colorHex=colorHex);
		elif not (colorRGB == -1) and len(colorRGB) == 3:
			self.color=colorRGB;
			self.graph.setVertexStrokeColor(self.id,colorRGB=colorRGB);
	def getStrokeColor(self):
		return self.properties["strokeColor"];
	def setColor(self,label,colorHex=-1,colorRGB=-1):
		self.setVertexFillColor(label,colorHex,colorRGB);
	def getColor(self):
		return self.fillColor;
	def setShape(self,shape):
		self.shape = shape;
		self.graph.setVertexShape(self.id,shape);
	def getShape(self):
		return self.shape;
	def setOtherProperty(self,otherProperty,value):
		self.otherProperties[otherProperty] = value;
		self.graph.setEdgeProperty(otherProperty,value);
	def getProperty(self,prop):
		return self.otherProperties[prop];
	def __str__(self):
		return str(self.getId());

	# def getIncidentEdges:
	# 	return self.incidentEdges;

	#what if i want to get a vertex? should i also get all its neighbours? how about incidenet edges? This is all v aufw\"andig and leads to the paradigm by which we just store the grpah in python???



class Edge:
	def __init__(self,graph):
		self.id = None;
		self.graph = graph;
		self.properties = dict();
		self.properties["id"] = None;
		self.properties["label"] = None;
		self.properties["color"] = None;
		self.properties["weight"] = None;
		self.properties["contour"] = None;
		self.properties["source"] = None;
		self.properties["target"] = None;

	def sourceProperties(self,stringFromGralog):
		strings = stringFromGralog.split("|");
		for string in strings:
			propVal = string.split("=");
			prop = propVal[0];
			val = propVal[1];
			self.properties[prop] = val;
	def getId(self):
		return self.properties["id"];
	def setLabel(self,label):
		self.properties["label"] = label;
		self.graph.setEdgeLabel(self.id,label);
	def getLabel(self):
		return self.properties["label"];
	def setColor(self,colorHex=-1,colorRGB=-1):
		if not (colorHex == -1):
			self.properties["color"] = colorHex;
			self.graph.setEdgeColor(self.id,colorHex=colorHex);
		elif not (colorRGB == -1) and len(colorRGB) == 3:
			self.properties["color"] =colorRGB;
			self.graph.setEdgeColor(self.id,colorRGB=colorRGB);
	def getColor(self):
		return self.properties["color"];
	def setWeight(self,weight):
		self.weight = int(weight);
		self.graph.setEdgeWeight(self.id,weight);
	def getWeight(self):
		return self.weight;
	def setContour(self,contour):
		self.contour = int(contour);
		self.graph.setEdgeContour(self.id,contour);
	def getContour(self):
		return self.contour;
	def setSource(self,source):
		self.properties["source"] = source;
	def getSource(self):
		return self.properties["source"];
	def setTarget(self,target):
		self.properties["target"] = target;
	def getTarget(self):
		return self.properties["target"];
	def setOtherProperty(self,otherProperty,value):
		self.otherProperties[otherProperty] = value;
		self.graph.setEdgeProperty(otherProperty,value);
	def getProperty(self,prop):
		return self.otherProperties[prop];


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
	

	def getVertexOrNew(self,vertexString):
		currId = extractIdFromProperties;
		v = None;
		try:
			v=self.vertices[currid];
		except:
			v=Vertex(self);
			v.sourceProperties(vertexString);
		return v;

	def addVertex(self,x=None,y=None,vertexId=-1):
		idString = "";
		if vertexId != -1:
			idString = "#"+str(vertexId).rstrip();
		print ("addVertex#" + str(self.id).rstrip() + idString)
		sys.stdout.flush();
		v = Vertex(self);

		vertexIngredients = sys.stdin.readline();
		v.sourceProperties(vertexIngredients);
		self.vertices[v.getId()] = v;
		
		return v;

	def deleteVertex(self,vertex):
		vertex = vertexId(vertex);
		
		print "deleteVertex#" +str(self.id).rstrip() + "#" +  str(vertex);
		sys.stdout.flush();
		# sys.stdin.readline();

	def setVertexFillColor(self,vertex,colorHex=-1,colorRGB=-1):
		# print("colorhex: " + str(colorHex));
		vertex = vertexId(vertex);
		line = "setVertexFillColor#" + str(self.id).rstrip() + "#" + str(vertex).rstrip() + "#"
		if not (colorHex==-1):
			line = line + hexFormatter(str(colorHex));
		elif not (colorRGB == -1) and len(colorRGB) == 3:
			line = line + rgbFormatter(colorRGB);
			
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
		vertex = vertexId(vertex);
		
		line = "setVertexRadius#"+str(self.id).rstrip() + "#" + str(vertex).rstrip() + "#" + str(newRadius).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		# sys.stdin.readline();

	def setVertexShape(self,vertex,shape):
		vertex = vertexId(vertex);
			
		line = "setVertexShape#"+str(self.id).rstrip() + "#" + str(vertex).rstrip() + "#" + str(shape).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		# sys.stdin.readline();

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

	def sendGraph(self,format):
		line = "sendGraph#"+str(self.id).rstrip() + "#" + format.rstrip();
		print line;
		##TODO: impliment this somehow haha
	def getVertexOfNew(verexString):
		vid = extractIdFromProperties(vertexString);
		v = None;
		if vid == None:
			print("oh shit that's an error");
			return;

		v = self.graph.getVertexById(vid);
		if v == None:
			v = Vertex(self);
		v.sourceProperties(vertexString);
		return v;

	def getAllVertices(self):
		
		line = "getAllVertices#"+str(self.id).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		vertexStringList = (sys.stdin.readline()).split("#");
		vertexList = [];
		for vertexString in vertexStringList:
			v = self.getVertexOrNew(vertexString);

			vertexList.append(v);
		return vertexList;


	def getNeighbours(self,vertex):
		vertex = vertexId(vertex);
			
		line = "getNeighbours#"+str(self.id).rstrip() + "#" + str(vertex).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		neighbourStringList = (sys.stdin.readline()).split("#");
		neighboursList = [];
		for neighbourString in neighbourStringList:
			v = getVertexOrNew(neighbourString);
			neighboursList.append(v);
		return neighboursList;

	def getOutgoingNeighbours(self,vertex):
		vertex = vertexId(vertex);
			
		line = "getOutgoingNeighbours#"+str(self.id).rstrip() + "#" + str(vertex).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		outgoingNeighbourStringList = (sys.stdin.readline()).split("#");
		outgoingNeighboursList = [];
		for outgoingNeighbourString in outgoingNeighbourStringList:
			v = getVertexOrNew(outgoingNeighbourString);
			outgoingNeighboursList.append(v);
		return outgoingNeighboursList;


	def getIncomingNeighbours(self,vertex):
		vertex = vertexId(vertex);
			
		line = "getIncomingNeighbours#"+str(self.id).rstrip() + "#" + str(vertex).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		incomingNeighbourStringList = (sys.stdin.readline()).split("#");
		incomingNeighboursList = [];
		for incomingNeighbourString in incomingNeighbourStringList:
			v = getVertexOrNew(incomingNeighbourString);
			incomingNeighboursList.append(v);
		return incomingNeighboursList;

	def termToEdge(self,term):
		endpoints = term.split(",");
		e = Edge(self);
		e.sourceProperties(endpoints[0]);
		source = self.getVertexOrNew(endpoints[1]);
		target = self.getVertexOrNew(endpoints[2]);
		e.setSource(source);
		e.setTarget(target);
		return e;

	def getAllEdges(self):
		
			
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
		vertex = vertexId(vertex);

			
		line = "getIncidentEdges#"+str(self.id).rstrip() + "#" + str(vertex).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		endpointList = (sys.stdin.readline()).split("#");
		# print("endpointList: " , endpointList);
		edges = [];
		for i in range(len(endpointList)):
			term = endpointList[i].rstrip();
			term = term[1:-1]
			# print("current term",term);
			# endpoints = term.split(",");
			# endpointList[i] = (int(endpoints[0]),int(endpoints[1]),int(endpoints[2]));
			e = self.termToEdge(term);
			edges.append(e);

		return edges;

	def getAdjacentEdges(self,edge):
		line = "getAdjacentEdges#"+str(self.id).rstrip() + "#";
		line = line + edgeSplitter(edge);

		print line.rstrip();
	
		sys.stdout.flush();
		endpointList = (sys.stdin.readline()).split("#");
		# print("endpointList: " , endpointList);
		edges = [];
		for i in range(len(endpointList)):
			term = endpointList[i].rstrip();
			term = term[1:-1]
			# print("current term",term);
			# endpoints = term.split(",");
			# endpointList[i] = (int(endpoints[0]),int(endpoints[1]),int(endpoints[2]));
			e = self.termToEdge(term);
			edges.append(e);

		return edges;
	def getOutgoingEdges(self,vertex):
		vertex = vertexId(vertex);
			
		line = "getOutgoingEdges#"+str(self.id).rstrip() + "#" + str(vertex).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		outGoingNeighboursList = (sys.stdin.readline()).split("#");
		edges = [];
		for i in range(len(outGoingNeighboursList)):
			term = outGoingNeighboursList[i].rstrip()
			term = term[1:-1];

			# endpoints = term.split(",");
			# outGoingNeighboursList[i] = (int(endpoints[0]),int(endpoints[1]));
			e = self.termToEdge(term);
			edges.append(e);
		return edges;


	def getIncomingEdges(self,vertex):
		vertex = vertexId(vertex);
		line = "getIncomingEdges#"+str(self.id).rstrip() + "#" + str(vertex).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		incomingNeighboursList = (sys.stdin.readline()).split("#");


		edges = [];
		for i in range(len(incomingNeighboursList)):
			
			term = incomingNeighboursList[i].rstrip()
			term = term[1:-1];

			
			# endpoints = term.split(",");
			# incomingNeighboursList[i] = (int(endpoints[0]),int(endpoints[1]));
			e = self.termToEdge(term);
			edges.append(e);
		return edges;


	def addEdge(self,sourceVertex, targetVertex, directed = False, edgeId = -1):
			
		sourceVertex = vertexId(sourceVertex);
		targetVertex = vertexId(targetVertex);


		idSubString = "";
		if not edgeId==-1:
			idSubString = "#"+str(id);
			
		line = "addEdge#"+str(self.id).rstrip() + "#" + str(sourceVertex).rstrip() + "#" + str(targetVertex).rstrip() + "#" + str(directed).lower()+idSubString.rstrip();


		print line.rstrip();
	
		sys.stdout.flush();
		edgeString = sys.stdin.readline();
		
		e = self.termToEdge(edgeString);
		self.edges[e.getId()] = e;
		return e;

	def addDirectedEdge(self,sourceVertex, targetVertex, edgeId=-1):
		sourceVertex = vertexId(sourceVertex);
		targetVertex = vertexId(targetVertex);

		idSubString = "";	
		if not edgeId==-1:
			idSubString = "#"+str(id);

		line = "addEdge#"+str(self.id).rstrip() + "#" + str(sourceVertex).rstrip() + "#" + str(targetVertex).rstrip() + "#" + str(True).lower()+idSubString.rstrip();

		print line.rstrip();
	
		sys.stdout.flush();
		edgeString = sys.stdin.readline();
		
		e = self.termToEdge(edgeString);
		return e;

	def getEdgeWeight(self,edge):
		return self.getEdgeProperty(edge,"weight");

	def getEdgeColor(self,edge):
		return self.getEdgeProperty(edge,"color");


	def getEdgeProperty(self,edge,property):
		

		line = "getEdgeProperty#"+str(self.id).rstrip() + "#"
		line = line + edgeSplitter(edge)
		line = line + "#" + property.rstrip().lower();

		print line.rstrip();
		sys.stdout.flush();

		return sys.stdin.readline().rstrip();

	def getEdgesByPropertyValue(self,prop,val):
		line = "getEdgesByPropertyValue#"+str(self.id).rstrip() + "#" + str(prop).rstrip() + "#" + str(val).rstrip();

		print line.rstrip();
		sys.stdout.flush();

		endpointList = sys.stdin.readline();

		endpointList = endpointList.split("#");
		edges = [];
		for i in range(len(endpointList)):
			term = endpointList[i].rstrip();
			term = term[1:-1]

			e = self.termToEdge(term);
			edges.append(e);
		return edges;


	def setEdgeWeight(self,edge,weight):
		setEdgeProperty(edge,"weight",weight);

	def setEdgeProperty(self,edge,property,value):
		line = "setEdgeProperty#"+str(self.id).rstrip() + "#"
		line = line + edgeSplitter(edge);

		line = line + "#" + property.rstrip().lower() +  "#" + str(value).rstrip().lower()+idString.rstrip();

		print line.rstrip();
		sys.stdout.flush();

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

	def representsInt(s):
	    try: 
	        int(s)
	        return True
	    except ValueError:
	        return False


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

	def send(self,toSend):
		print toSend;
		sys.stdout.flush();

	def mistakeLine(self):
		print("wubbadubdub 3 men in a tub");
		sys.stdout.flush();
		sys.stdin.readline();


	def __str__(self):
		return "todo: tgf"




