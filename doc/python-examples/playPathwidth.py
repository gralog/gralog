#!/usr/bin/python

import sys
from Gralog import *
from Queue import *

clean_color = "White"
cop_color = "Blue"
dirty_color = "Red"
non_mon_color = "Black"

def recontaminate(g,v, contaminated,cops):
    gPrint("Checking recontamination...")
    q = Queue()
    q.put(v)
    visited = set()
    while not q.empty():
        w = q.get()
        visited.add(w)
        for u in w.getIncomingNeighbours():
            if u.getId() in cops:
                continue
            if u.getId() in contaminated:
                gPrint("The cop is being removed from a vertex reachable from vertex with id " + str(u.getId()))
                return True
            if u not in visited:
                q.put(u)
    gPrint("No recontamination.")
    return False


# MAIN

g = Graph(None)
contaminated_list = g.getAllVertices()
contaminated = set()
for vertex in contaminated_list:
    contaminated.add(vertex.getId())
for i in contaminated:
    gPrint(str(i))
for v in contaminated_list:
    v.setFillColor(dirty_color)
cops = set()
max_cops = 0
g.track("cops_used",max_cops)
clean = set()
    
cops_source = g.addVertex()
cops_source.setFillColor(cop_color)
cops_source.setLabel("COPS")
cops.add(cops_source.getId())

gPrint("Welcome to the pathwidth game!")
gPrint("The robber contaminated the whole graph. \
Place and move cops to clean it. Use as few cops as possible. \
You can always take additional cops from a special vertex labeled by COPS.")

while contaminated:
    gPrint("Choose a vertex to take the next cop from. Take a new cop from COPS.")
    remove_cop_from = g.requestVertex()
    if remove_cop_from.getId() not in cops:
        gPrint("There is no cop there. Let us try again.")
        continue
    if (remove_cop_from.getId() != cops_source.getId()) and (recontaminate(g,remove_cop_from,contaminated,cops)): # returns true if non-robber-monotonicity occurs at remove_cop_from
        v.setFillColor(non_mon_color)
        gPrint("Robber wins: removing the cop from the blue vertex is non-monotone.")
        exit()
    if remove_cop_from.getId() != cops_source.getId():
        remove_cop_from.setColor(clean_color)
        cops.remove(remove_cop_from.getId())
        clean.add(remove_cop_from.getId())
    gPrint("Choose a vertex to place the cop on.")
    place_cop_on = g.requestVertex()
    place_cop_on.setFillColor(cop_color)
    cops.add(place_cop_on.getId())
    if max_cops < len(cops)-1:
        max_cops = len(cops)-1
        g.unTrack("cops_used")
        g.track("cops_used",max_cops)
    contaminated.remove(place_cop_on.getId())
    
gPrint("Cops won.")
