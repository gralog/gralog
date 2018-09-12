s = str(input("bla"))
s0 = s[0:6]
s2 = s[6:len(s)];
i = 6
className = "";
while s[i] != '(':
	className += s[i];
	i += 1;
ret = s0 + "\\textbf{*}"+s2+"\\quad \\hyperref["+className + "Class]{\\textit{See proper method}}"
print("\n"+ret+"\n\n");
s = str(input("bla"))
ret = "\\label{"+className+"Class}"+s + "\\quad(corresponds to \\textit{"+className+"})";
print("\n"+ret+"\n");