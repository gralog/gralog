#!/usr/bin/python

import sys
#data = sys.stdin.readlines()
# line = sys.stdin.readline()
# while(line != "x\n"):
#     print line+"\n"
#     line = sys.stdin.readline()
# print "Hello " + sys.argv[1] + ", I'm printing."







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
		print ("addVertex " + str(self.id).rstrip() + " " +  str(self.lastIndex));
		sys.stdout.flush();
		idFromGralog = sys.stdin.readline();
		newVertex = Vertex(idFromGralog,x,y,color);
		self.vertices.append(newVertex);
		return idFromGralog;

	def deleteVertex(self,vertexIndex):
		# indices = map(lambda vertex: vertex.id, self.vertices);
		# if vertexIndex in indices:
		# self.vertices.remove(self.vertices[indices.index(vertexIndex)]);
		print "deleteVertex " +str(self.id).rstrip() + " " +  str(vertexIndex);
		sys.stdout.flush();
		sys.stdin.readline();

	def setVertexFillColor(self,vertexIndex,colorHex=-1,colorRGB=-1):
		indices = map(lambda vertex: vertex.id, self.vertices);
		# print("colorhex: " + str(colorHex));
	
		if not (colorHex==-1):
			line = "setVertexFillColor " + self.id + " " + str(vertexIndex).rstrip() + " " + str(colorHex).rstrip();
			print line.rstrip();
		elif not (colorRGB == -1) and len(colorRGB) == 3:
			line = "setVertexFillColor "+str(self.id).rstrip() + " " + str(vertexIndex).rstrip() + " " + str(colorRGB[0]).rstrip() + " " + str(colorRGB[1]).rstrip() + " " + str(colorRGB[2]).rstrip();
			print(line.rstrip());
		else:
			return
		sys.stdout.flush();
		sys.stdin.readline();
	

	def setVertexStrokeColor(self,vertexIndex,colorHex=-1,colorRGB=-1):
		indices = map(lambda vertex: vertex.id, self.vertices);
		# print("colorhex: " + str(colorHex));

		if not (colorHex==-1):
			line = "setVertexStrokeColor "+str(self.id).rstrip() + " " + str(vertexIndex).rstrip() + " " + str(colorHex).rstrip();
			print line.rstrip();
		elif not (colorRGB == -1) and len(colorRGB) == 3:
			line = "setVertexStrokeColor "+str(self.id).rstrip() + " " + str(vertexIndex).rstrip() + " " + str(colorRGB[0]).rstrip() + " " + str(colorRGB[1]).rstrip() + " " + str(colorRGB[2]).rstrip();
			print(line.rstrip());
		sys.stdout.flush();
		sys.stdin.readline();


	def setVertexRadius(self,vertexIndex,newRadius):
		indices = map(lambda vertex: vertex.id, self.vertices);
		# print("colorhex: " + str(colorHex));
		# if vertexIndex in indices:
			
		line = "setVertexRadius "+str(self.id).rstrip() + " " + str(vertexIndex).rstrip() + " " + str(newRadius).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		sys.stdin.readline();


	def getConnectedNeighbours(self,vertexIndex):
		indices = map(lambda vertex: vertex.id, self.vertices);
		# print("colorhex: " + str(colorHex));
		# if vertexIndex in indices:
			
		line = "getConnectedNeighbours "+str(self.id).rstrip() + " " + str(vertexIndex).rstrip();
		print line.rstrip();
	
		sys.stdout.flush();
		connectedNeighboursList = sys.stdin.readline();
		return connectedNeighboursList;


	def addEdge(self,sourceVertexIndex, targetVertexIndex, directed = False):
		# indices = map(lambda vertex: vertex.id, self.vertices);
		# print("colorhex: " + str(colorHex));
		# if vertexIndex in indices:

			
		line = "addEdge "+str(self.id).rstrip() + " " + str(sourceVertexIndex).rstrip() + " " + str(targetVertexIndex).rstrip() + " " + str(directed).lower();


		print line.rstrip();
	
		sys.stdout.flush();
		sys.stdin.readline();
		# return connectedNeighboursList;



	def pauseUntilSpacePressed(self):
		print("pauseUntilSpacePressed");
		sys.stdout.flush();
		sys.stdin.readline();





	def __str__(self):
		return "todo: tgf"


#####functional program #######
# recd = "null";
# for i in range(3):
# 	print recd; #send hello to java
# 	sys.stdout.flush()
# 	line = sys.stdin.readline(); #read read response from java
# 	recd = line;
##############################



g = Graph("undirected");#type \in buechi, directed, etc. or None

###algorithm: add 3 nodes to tha homie graph.
# recd = "null";
# myid = g.addVertex();
# g.pauseUntilSpacePressed();
# # g.

# g.setVertexFillColor(myid,colorHex = "423097");
# g.pauseUntilSpacePressed();
# g.setVertexRadius(myid,5);

# myidConnectedVertices = g.getConnectedNeighbours(myid);

# myidConnectedVertices = myidConnectedVertices.split(" ");

# g.pauseUntilSpacePressed();

# for neighbourId in myidConnectedVertices:
# 	g.setVertexFillColor(neighbourId,colorRGB=(255,0,0));

# g.pauseUntilSpacePressed();

# g.deleteVertex(0);


source = g.addVertex();
target = g.addVertex();

g.pauseUntilSpacePressed();

g.addEdge(source,target);

	


# for i in range(3):
# 	print("helloo");