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
	def __init__(self,format):
		#perform analysis of graph
		self.vertices = [];
		self.edges = [];
		self.lastIndex = -1;
		if format == None or format.lower() == "none":
			#we want a new graph

			print("useCurrentGraph");
			sys.stdout.flush();
			self.vertices = [];
			self.edges = [];
			self.lastIndex = -1;
			sys.stdin.readline();
	
		else:
			print(format);
			sys.stdout.flush();
			graph = sys.stdin.readline();
			
		

	def addVertex(self,x=None,y=None,color=None):
		# newVertex = Vertex(self.lastIndex + 1,x,y,color);
		# self.vertices.append(newVertex);
		self.lastIndex += 1;
		print ("addVertex " + str(self.lastIndex));
		sys.stdout.flush();
		idFromGralog = sys.stdin.readline();
		newVertex = Vertex(idFromGralog,x,y,color);
		self.vertices.append(newVertex);
		return idFromGralog;

	def deleteVertex(self,vertexIndex):
		indices = map(lambda vertex: vertex.id, self.vertices);
		if vertexIndex in indices:
			self.vertices.remove(self.vertices[indices.index(vertexIndex)]);
			print "deleteVertex " + str(vertexIndex);
			sys.stdout.flush();
			sys.stdin.readline();


	def pauseUntilSpacePresed(self):
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



g = Graph("none");#type \in buechi, directed, etc. or None

###algorithm: add 3 nodes to tha homie graph.
# recd = "null";
myid = g.addVertex();
g.deleteVertex(myid);
g.pauseUntilSpacePresed();
helloid = g.addVertex();
# g.deleteVertex(0);


	


# for i in range(3):
# 	print("helloo");