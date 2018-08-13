f = open("colorFormatter.txt","r");


name = f.readline();
c = f.readline();


colors = [];



while (name != "" or name == None):
	colors.append((name,c));
	f.readline();
	name = f.readline();
	c = f.readline();


for x in colors:
	print('colorPresets.put(\"' + x[0].strip() + '\",\"' + x[1].strip() + '\");');