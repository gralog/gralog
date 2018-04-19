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
	def __init__(self,graph):
		#perform analysis of graph
		self.vertices = [];
		self.edges = [];
		self.lastIndex = -1;

	def addVertex(self,x=None,y=None,color=None):
		newVertex = Vertex(self.lastIndex + 1,x,y,color);
		self.vertices.append(newVertex);
		self.lastIndex += 1;
		print ("addVertex " + str(self.lastIndex));
		sys.stdout.flush();
		sys.stdin.readline();
		return 

	def deleteVertex(self,vertexIndex):
		indices = map(lambda vertex: vertex.id, self.vertices);
		if vertexIndex in indices:
			self.vertices.remove(self.vertices[indices.index(vertexIndex)]);
			print "deleteVertex " + str(vertexIndex);
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



g = Graph("test");

###algorithm: add 3 nodes to tha homie graph.
# recd = "null";
for i in range(5):
	# print recd; #send hello to java
	# sys.stdout.flush()
	g.addVertex();

g.deleteVertex(0);


	


# for i in range(3):
# 	print("helloo");