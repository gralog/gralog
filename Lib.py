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
			
		

	def addVertex(self,x=None,y=None,color=None):
		# newVertex = Vertex(self.lastIndex + 1,x,y,color);
		# self.vertices.append(newVertex);
		self.lastIndex += 1;
		print ("addVertex#" + str(self.id).rstrip() + "#" +  str(self.lastIndex));
		sys.stdout.flush();
		idFromGralog = sys.stdin.readline();
		newVertex = Vertex(idFromGralog,x,y,color);
		self.vertices.append(newVertex);
		return int(idFromGralog.rstrip());

	def deleteVertex(self,vertexId):
		# indices = map(lambda vertex: vertex.id, self.vertices);
		# if vertexId in indices:
		# self.vertices.remove(self.vertices[indices.index(vertexId)]);
		print "deleteVertex#" +str(self.id).rstrip() + "#" +  str(vertexId);
		sys.stdout.flush();
		# sys.stdin.readline();

	def setVertexFillColor(self,vertexId,colorHex=-1,colorRGB=-1):
		indices = map(lambda vertex: vertex.id, self.vertices);
		# print("colorhex: " + str(colorHex));
	
		if not (colorHex==-1):
			line = "setVertexFillColor#" + str(self.id).rstrip() + "#" + str(vertexId).rstrip() + "#" + str(colorHex).rstrip();
			print line.rstrip();
		elif not (colorRGB == -1) and len(colorRGB) == 3:
			line = "setVertexFillColor#"+str(self.id).rstrip() + "#" + str(vertexId).rstrip() + "#" + str(colorRGB[0]).rstrip() + "#" + str(colorRGB[1]).rstrip() + "#" + str(colorRGB[2]).rstrip();
			print(line.rstrip());
		else:
			return
		sys.stdout.flush();
		# sys.stdin.readline();
	

	def setVertexStrokeColor(self,vertexId,colorHex=-1,colorRGB=-1):
		indices = map(lambda vertex: vertex.id, self.vertices);
		# print("colorhex: " + str(colorHex));

		if not (colorHex==-1):
			line = "setVertexStrokeColor#"+str(self.id).rstrip() + "#" + str(vertexId).rstrip() + "#" + str(colorHex).rstrip();
			print line.rstrip();
		elif not (colorRGB == -1) and len(colorRGB) == 3:
			line = "setVertexStrokeColor#"+str(self.id).rstrip() + "#" + str(vertexId).rstrip()  + "#" + str(colorRGB[0]).rstrip() + "#" + str(colorRGB[1]).rstrip() + "#" + str(colorRGB[2]).rstrip();
			print line.rstrip();
		sys.stdout.flush();
		# sys.stdin.readline();

	def setEdgeContour(self, (sourceVertexId,targetVertexId),contour):
		line = line = "setEdgeContour#"+str(self.id).rstrip() + "#" + str(sourceVertexId).rstrip() + "#" + str(targetVertexId).rstrip() + "#";
		line = line + str(contour).rstrip();
		print(line);
		sys.stdout.flush()


	def setEdgeColor(self,sourceVertexId,targetVertexId,colorHex=-1,colorRGB=-1):
		# indices = map(lambda vertex: vertex.id, self.vertices);
		# print("colorhex: " + str(colorHex));
		line = line = "setEdgeColor#"+str(self.id).rstrip() + "#" + str(sourceVertexId).rstrip() + "#" + str(targetVertexId).rstrip() + "#";
		if not (colorHex==-1):
			line = line + str(colorHex).rstrip();
			print line.rstrip();
		elif not (colorRGB == -1) and len(colorRGB) == 3:
			line = line + str(colorRGB[0]).rstrip() + "#" + str(colorRGB[1]).rstrip() + "#" + str(colorRGB[2]).rstrip();
			print(line.rstrip());
		sys.stdout.flush();
		# sys.stdin.readline();

	def setEdgeColor(self,(sourceVertexId,targetVertexId),colorHex=-1,colorRGB=-1):
		# indices = map(lambda vertex: vertex.id, self.vertices);
		# print("colorhex: " + str(colorHex));
		line = line = "setEdgeColor#"+str(self.id).rstrip() + "#" + str(sourceVertexId).rstrip() + "#" + str(targetVertexId).rstrip() + "#";
		if not (colorHex==-1):
			line = line + str(colorHex).rstrip();
			print line.rstrip();
		elif not (colorRGB == -1) and len(colorRGB) == 3:
			line = line + str(colorRGB[0]).rstrip() + "#" + str(colorRGB[1]).rstrip() + "#" + str(colorRGB[2]).rstrip();
			print(line.rstrip());
		sys.stdout.flush();
		# sys.stdin.readline();


	def setVertexRadius(self,vertexId,newRadius):
		indices = map(lambda vertex: vertex.id, self.vertices);
		# print("colorhex: " + str(colorHex));
		# if vertexId in indices:
			
		line = "setVertexRadius#"+str(self.id).rstrip() + "#" + str(vertexId).rstrip() + "#" + str(newRadius).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		# sys.stdin.readline();

	def setVertexShape(self,vertexId,shape):
		# indices = map(lambda vertex: vertex.id, self.vertices);
		# print("colorhex: " + str(colorHex));
		# if vertexId in indices:
			
		line = "setVertexShape#"+str(self.id).rstrip() + "#" + str(vertexId).rstrip() + "#" + str(shape).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		# sys.stdin.readline();

	def getGraph(self,format):
		# indices = map(lambda vertex: vertex.id, self.vertices);
		# print("colorhex: " + str(colorHex));
		# if vertexId in indices:
			
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
		# indices = map(lambda vertex: vertex.id, self.vertices);
		# print("colorhex: " + str(colorHex));
		# if vertexId in indices:
			
		line = "getAllVertices#"+str(self.id).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		vertexList = (sys.stdin.readline()).split("#");
		return vertexList;


	def getNeighbours(self,vertexId):
		# indices = map(lambda vertex: vertex.id, self.vertices);
		# print("colorhex: " + str(colorHex));
		# if vertexId in indices:
			
		line = "getNeighbours#"+str(self.id).rstrip() + "#" + str(vertexId).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		neighboursList = (sys.stdin.readline()).split("#");
		return neighboursList;

	def getOutgoingNeighbours(self,vertexId):
		# indices = map(lambda vertex: vertex.id, self.vertices);
		# print("colorhex: " + str(colorHex));
		# if vertexId in indices:
			
		line = "getOutgoingNeighbours#"+str(self.id).rstrip() + "#" + str(vertexId).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		outGoingNeighboursList = (sys.stdin.readline()).split("#");
		return outGoingNeighboursList;


	def getIncomingNeighbours(self,vertexId):
		# indices = map(lambda vertex: vertex.id, self.vertices);
		# print("colorhex: " + str(colorHex));
		# if vertexId in indices:
			
		line = "getIncomingNeighbours#"+str(self.id).rstrip() + "#" + str(vertexId).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		incomingNeighboursList = (sys.stdin.readline()).split("#");
		return incomingNeighboursList;

	def getAllEdges(self):
		# indices = map(lambda vertex: vertex.id, self.vertices);
		# print("colorhex: " + str(colorHex));
		# if vertexId in indices:
			
		line = "getAllEdges#"+str(self.id).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		endpointList = sys.stdin.readline();

		endpointList = endpointList.split("#");

		for i in range(len(endpointList)):
			term = endpointList[i].rstrip();
			term = term[1:-1]

			endpoints = term.split(",");
			endpointList[i] = (int(endpoints[0]),int(endpoints[1]));


		return endpointList;

	def getIncidentEdges(self,vertexId):
		# indices = map(lambda vertex: vertex.id, self.vertices);
		# print("colorhex: " + str(colorHex));
		# if vertexId in indices:
			
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
			endpointList[i] = (int(endpoints[0]),int(endpoints[1]));


		return endpointList;

	def getAdjacentEdges(self,sourceVertexId,targetVertexId):
		line = "getAdjacentEdges#"+str(self.id).rstrip() + "#" + str(sourceVertexId).rstrip() +  "#" + str(targetVertexId).rstrip();

		print line.rstrip();
	
		sys.stdout.flush();
		endpointList = (sys.stdin.readline()).split("#");
		# print("endpointList: " , endpointList);
		for i in range(len(endpointList)):
			term = endpointList[i].rstrip();
			term = term[1:-1]
			# print("current term",term);
			endpoints = term.split(",");
			endpointList[i] = (int(endpoints[0]),int(endpoints[1]));


		return endpointList;
	def getOutgoingEdges(self,vertexId):
		# indices = map(lambda vertex: vertex.id, self.endpoints);
		# print("colorhex: " + str(colorHex));
		# if vertexId in indices:
			
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


	def addEdge(self,sourceVertexId, targetVertexId, directed = False, id = -1):
		# indices = map(lambda vertex: vertex.id, self.vertices);
		# print("colorhex: " + str(colorHex));
		# if vertexIndex in indices:
		idSubString = "";
		if not id==-1:
			idSubString = "#"+str(id);
			
		line = "addEdge#"+str(self.id).rstrip() + "#" + str(sourceVertexId).rstrip() + "#" + str(targetVertexId).rstrip() + "#" + str(directed).lower()+idSubString.rstrip();


		print line.rstrip();
	
		sys.stdout.flush();
		return int(sys.stdin.readline());

	def addDirectedEdge(self,sourceVertexId, targetVertexId, id=-1):
		# indices = map(lambda vertex: vertex.id, self.vertices);
		# print("colorhex: " + str(colorHex));
		# if vertexIndex in indices:

		idSubString = "";	
		if not id==-1:
			idSubString = "#"+str(id);

		line = "addEdge#"+str(self.id).rstrip() + "#" + str(sourceVertexId).rstrip() + "#" + str(targetVertexId).rstrip() + "#" + str(True).lower()+idSubString.rstrip();

		print line.rstrip();
	
		sys.stdout.flush();
		return int(sys.stdin.readline());

	def getEdgeWeight(self,(sourceVertexId,targetVertexId),edgeId=-1):
		return self.getEdgeProperty((sourceVertexId,targetVertexId),"weight",edgeId);

	def getEdgeColor(self,(sourceVertexId,targetVertexId),edgeId=-1):
		return self.getEdgeProperty((sourceVertexId,targetVertexId),"color",edgeId);


	def getEdgeProperty(self,(sourceVertexId,targetVertexId),property,edgeId=-1):
		idString = "";
		if not edgeId == -1:
			idString = "#"+str(edgeId);

		line = "getEdgeProperty#"+str(self.id).rstrip() + "#" + str(sourceVertexId).rstrip() + "#" + str(targetVertexId).rstrip() + "#" + property.rstrip().lower()+idString.rstrip();

		print line.rstrip();
		sys.stdout.flush();

		return sys.stdin.readline();

	def setEdgeWeight(self,(sourceVertexId,targetVertexId),weight):
		setEdgeProperty((sourceVertexId,targetVertexId),"weight",weight);

	def setEdgeProperty(self,(sourceVertexId,targetVertexId),property,value):
		line = "setEdgeProperty#"+str(self.id).rstrip() + "#" + str(sourceVertexId).rstrip() + "#" + str(targetVertexId).rstrip() + "#" + property.rstrip().lower() +  "#" + str(value).rstrip().lower();

		print line.rstrip();
		sys.stdout.flush();

	def deleteEdge(self,(sourceVertexId,targetVertexId),edgeId=-1):

		idString = "";
		if not edgeId == -1:
			idString = "#"+str(edgeId);


		line = "deleteEdge#"+str(self.id).rstrip() + "#" + str(sourceVertexId).rstrip() + "#" + str(targetVertexId).rstrip()+idString.rstrip();

		print line.rstrip();

		sys.stdout.flush();
		# sys.stdin.readline();

	def setEdgeLabel(self,(sourceVertexId, targetVertexId), label):
		line = "setEdgeLabel#"+str(self.id).rstrip() + "#" + str(sourceVertexId).rstrip() + "#" + str(targetVertexId).rstrip() + "#" + label;
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

		if rank != None:
			line += "#"+str(rank).rstrip();

		for key in sorted(self.variablesToTrack.keys()):
			term = "#("+str(key).rstrip()+"="+str(self.variablesToTrack[key]).rstrip()+")";
			# print ("boi term : " + term);
			line = line + term.rstrip();
		# line = "pauseUntilSpacePressed (hello,world)";
		for x in args:
			if (type(x) == list):
				for each in x:
					term = "#("+"arrayyyy"+str(each[0])+"="+str(each[1])+")";
					line = line + term;
			else:	
				term = "#("+str(x[0])+"="+str(x[1])+")";
				line = line + term.rstrip();
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




