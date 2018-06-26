#!/usr/bin/python

import sys
from random import randint



class Vertex:
	def __init__(self,id,x = None,y=None,color = None):
		self.x = x;
		self.y = y;
		self.color = color;
		self.id=id;
	def __str__(self):
		return str(self.id);

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


def edgeSplitter(edge):
	if type(edge) == tuple and len(edge)==2:
		return str(edge[0]).rstrip()+","+str(edge[1]).rstrip();
	return str(edge).rstrip();

class Graph:
	def __init__(self,format="Undirected Graph"):
		#perform analysis of graph
		self.vertices = [];
		self.edges = [];
		self.lastIndex = -1;
		self.id = -1;
		self.variablesToTrack = {};
		if format == None or format.lower() == "none":
			#we want a new graph

			print("useCurrentGraph");
			sys.stdout.flush();
			self.vertices = [];
			self.edges = [];
			self.lastIndex = -1;
			self.id = sys.stdin.readline();
			
	
		else:
			print(format);
			sys.stdout.flush();
			self.id = sys.stdin.readline();
			
		

	def addVertex(self,x=None,y=None,vertexId=-1):
		idString = "";
		if vertexId != -1:
			idString = "#"+str(vertexId).rstrip();
		print ("addVertex#" + str(self.id).rstrip() + idString)
		sys.stdout.flush();
		idFromGralog = sys.stdin.readline();
		
		return int(idFromGralog.rstrip());

	def deleteVertex(self,vertexId):
		
		print "deleteVertex#" +str(self.id).rstrip() + "#" +  str(vertexId);
		sys.stdout.flush();
		# sys.stdin.readline();

	def setVertexFillColor(self,vertexId,colorHex=-1,colorRGB=-1):
		# print("colorhex: " + str(colorHex));
		line = "setVertexFillColor#" + str(self.id).rstrip() + "#" + str(vertexId).rstrip() + "#"
		if not (colorHex==-1):
			line = line + hexFormatter(str(colorHex));
		elif not (colorRGB == -1) and len(colorRGB) == 3:
			line = line + rgbFormatter(colorRGB);
			
		print(line.rstrip());
		sys.stdout.flush();
		# sys.stdin.readline();
	

	def setVertexStrokeColor(self,vertexId,colorHex=-1,colorRGB=-1):
		indices = map(lambda vertex: vertex.id, self.vertices);
		# print("colorhex: " + str(colorHex));
		line = "setVertexStrokeColor#"+str(self.id).rstrip() + "#" + str(vertexId).rstrip() + "#"
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


	def setVertexRadius(self,vertexId,newRadius):
		
		line = "setVertexRadius#"+str(self.id).rstrip() + "#" + str(vertexId).rstrip() + "#" + str(newRadius).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		# sys.stdin.readline();

	def setVertexShape(self,vertexId,shape):
		
			
		line = "setVertexShape#"+str(self.id).rstrip() + "#" + str(vertexId).rstrip() + "#" + str(shape).rstrip();
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


	def getAllVertices(self):
		
		line = "getAllVertices#"+str(self.id).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		vertexList = (sys.stdin.readline()).split("#");
		return vertexList;


	def getNeighbours(self,vertexId):
	
			
		line = "getNeighbours#"+str(self.id).rstrip() + "#" + str(vertexId).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		neighboursList = (sys.stdin.readline()).split("#");
		return neighboursList;

	def getOutgoingNeighbours(self,vertexId):
		
			
		line = "getOutgoingNeighbours#"+str(self.id).rstrip() + "#" + str(vertexId).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		outGoingNeighboursList = (sys.stdin.readline()).split("#");
		return outGoingNeighboursList;


	def getIncomingNeighbours(self,vertexId):
		
			
		line = "getIncomingNeighbours#"+str(self.id).rstrip() + "#" + str(vertexId).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		incomingNeighboursList = (sys.stdin.readline()).split("#");
		return incomingNeighboursList;

	def getAllEdges(self):
		
			
		line = "getAllEdges#"+str(self.id).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		endpointList = sys.stdin.readline();

		endpointList = endpointList.split("#");

		for i in range(len(endpointList)):
			term = endpointList[i].rstrip();
			term = term[1:-1]

			endpoints = term.split(",");
			endpointList[i] = (int(endpoints[0]),int(endpoints[1]),int(endpoints[2]));


		return endpointList;

	def getIncidentEdges(self,vertexId):
		

			
		line = "getIncidentEdges#"+str(self.id).rstrip() + "#" + str(vertexId).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		endpointList = (sys.stdin.readline()).split("#");
		# print("endpointList: " , endpointList);
		for i in range(len(endpointList)):
			term = endpointList[i].rstrip();
			term = term[1:-1]
			# print("current term",term);
			endpoints = term.split(",");
			endpointList[i] = (int(endpoints[0]),int(endpoints[1]),int(endpoints[2]));


		return endpointList;

	def getAdjacentEdges(self,edge):
		line = "getAdjacentEdges#"+str(self.id).rstrip() + "#";
		line = line + edgeSplitter(edge);

		print line.rstrip();
	
		sys.stdout.flush();
		endpointList = (sys.stdin.readline()).split("#");
		# print("endpointList: " , endpointList);
		for i in range(len(endpointList)):
			term = endpointList[i].rstrip();
			term = term[1:-1]
			# print("current term",term);
			endpoints = term.split(",");
			endpointList[i] = (int(endpoints[0]),int(endpoints[1]),int(endpoints[2]));


		return endpointList;
	def getOutgoingEdges(self,vertexId):
		
			
		line = "getOutgoingEdges#"+str(self.id).rstrip() + "#" + str(vertexId).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		outGoingNeighboursList = (sys.stdin.readline()).split("#");
		for i in range(len(outGoingNeighboursList)):
			term = outGoingNeighboursList[i].rstrip()
			term = term[1:-1];

			endpoints = term.split(",");
			outGoingNeighboursList[i] = (int(endpoints[0]),int(endpoints[1]));
		return outGoingNeighboursList;


	def getIncomingEdges(self,vertexId):
			
		line = "getIncomingEdges#"+str(self.id).rstrip() + "#" + str(vertexId).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		incomingNeighboursList = (sys.stdin.readline()).split("#");



		# print("incomingNeighboursList: " , incomingNeighboursList)
		for i in range(len(incomingNeighboursList)):
			
			term = incomingNeighboursList[i].rstrip()
			term = term[1:-1];

			
			endpoints = term.split(",");
			incomingNeighboursList[i] = (int(endpoints[0]),int(endpoints[1]));
		return incomingNeighboursList;


	def addEdge(self,sourceVertexId, targetVertexId, directed = False, edgeId = -1):
		
		idSubString = "";
		if not edgeId==-1:
			idSubString = "#"+str(id);
			
		line = "addEdge#"+str(self.id).rstrip() + "#" + str(sourceVertexId).rstrip() + "#" + str(targetVertexId).rstrip() + "#" + str(directed).lower()+idSubString.rstrip();


		print line.rstrip();
	
		sys.stdout.flush();
		return int(sys.stdin.readline());

	def addDirectedEdge(self,sourceVertexId, targetVertexId, edgeId=-1):
		

		idSubString = "";	
		if not edgeId==-1:
			idSubString = "#"+str(id);

		line = "addEdge#"+str(self.id).rstrip() + "#" + str(sourceVertexId).rstrip() + "#" + str(targetVertexId).rstrip() + "#" + str(True).lower()+idSubString.rstrip();

		print line.rstrip();
	
		sys.stdout.flush();
		return int(sys.stdin.readline());

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

		for i in range(len(endpointList)):
			term = endpointList[i].rstrip();
			term = term[1:-1]

			endpoints = term.split(",");
			endpointList[i] = (int(endpoints[0]),int(endpoints[1]),int(endpoints[2]));

		return endpointList;


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

	def setVertexLabel(self,vertexId, label):
		line = "setVertexLabel#"+str(self.id).rstrip() + "#" + str(vertexId).rstrip() + "#" + label;
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




