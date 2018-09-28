#!/usr/bin/python
#bfs.py
from Gralog import *
g = Graph("undirected");
g.generateRandomGraph(15);

def finish(goal):
	curr = goal;
	goal.setLabel("goal");
	goal.setColor("red");
	prev = goal;
	curr = goal.getProperty("prev");
	while curr != None:
		curr.setLabel("onPath");
		myEdge=prev.getAllEdgesBetween(curr)[0];
		myEdge.setColor("orange");
		gPrint("orange");
		curr.setColor("red");
		prev = curr;
		curr = curr.getProperty("prev");
	prev.setColor("red");
	goal.setLabel("goal");

def populateBFS(g,start):
	
	start.setProperty("prev",None);
	vertexQueue = [];
	vertexQueue.append(start);
	while len(vertexQueue) > 0:
		currentVertex = vertexQueue.pop(0);
		currentVertex.setLabel("curr");
		currentVertex.setColor("green");
		neighbours = [];
		for neighbour in currentVertex.getNeighbours():
			if not neighbour.getLabel() == "disc":
				neighbours.append(neighbour);
				neighbour.setProperty("prev",currentVertex);
		for neighbour in neighbours:
			neighbour.setLabel("disc");
			neighbour.setColor("blue");
			vertexQueue.append(neighbour);
		g.pause();

		for neighbour in neighbours:
			neighbour.setColor("ffffff");
		currentVertex.setColor("ffffff");
		currentVertex.setLabel("disc");


