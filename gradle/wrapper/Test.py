#!/usr/bin/python

import sys
#data = sys.stdin.readlines()
line = sys.stdin.readline()
while(line != "x\n"):
    print line+"\n"
    line = sys.stdin.readline()
print "Hello " + sys.argv[1] + ", I'm printing."

#import subprocess

#p = subprocess.Popen(["java", "Piping"], stdout=subprocess.PIPE)
#line = p.stdout.readline()
#while(line != "x\n"):
#       print line
#       line = p.stdout.readline()

#print "Hello " + input + ", I'm printing."

#main()
#x = input('What?')
#print "Hello " + input + ", I'm printing."
#return "Hello, I'm a Python programme."
#
#if __name__== "__main__":
#       main()