import sys

def schwerpunkt(P):
  x, y = [node[0] for node in P], [node[1] for node in P]
  return (sum(x)/len(x), sum(y)/len(y))

for line in sys.stdin:
    P = [list([int(a) for a in i.split(',')]) for i in line.split()]
    print(schwerpunkt(P))
