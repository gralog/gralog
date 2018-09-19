#!/usr/bin/python
#dfs.py
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



start = g.requestVertex();
start.setLabel("start");
goal = g.requestVertex();
goal.setLabel("goal");
start.setProperty("prev",None);

prev = None;
vertexQueue = [];
vertexQueue.append(start);

while len(vertexQueue) > 0:
	gPrint(str(vertexQueue));
	currentVertex = vertexQueue.pop();
	
	currentVertex.setProperty("prev",prev);
	if currentVertex == goal:
		finish(goal);
		break;
	currentVertex.setLabel("curr");
	currentVertex.setColor("green");
	neighbours = [];
	for neighbour in currentVertex.getNeighbours():
		if not neighbour.getLabel() == "disc" and neighbour != start:
			neighbours.append(neighbour);
	for neighbour in neighbours:
		neighbour.setLabel("disc");
		neighbour.setColor("blue");
		vertexQueue.append(neighbour);
	start.setLabel("start");
	g.pause();
	prev = currentVertex;

	for neighbour in neighbours:
		neighbour.setColor("ffffff");
	currentVertex.setColor("ffffff");
	currentVertex.setLabel("disc");


