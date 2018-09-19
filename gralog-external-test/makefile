

gralogexternaltest: externaltest.cpp
	g++ -o $@ $<


.PHONY: test
test: gralogexternaltest
	echo -e "1\n2\n3\n#\n1 2\n2 3\n3 1\n" | $<


.PHONY: install
install: gralogexternaltest
	sudo cp $< /usr/bin/


.PHONY: clean
clean:
	rm -f gralogexternaltest
