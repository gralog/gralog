


# sudo apt-get install jflex cup

JFLEX := jflex
CUP := cup
# CUP := java -jar /path/to/java-cup-version.jar


.PHONY: all
all: ModalMuCalculusScanner.java ModalMuCalculusParser.java


ModalMuCalculusScanner.java: ModalMuCalculusScanner.jflex ModalMuCalculusScannerToken.java
	$(JFLEX) $<


ModalMuCalculusParser.java ModalMuCalculusScannerToken.java: ModalMuCalculusParser.cup
	$(CUP) -symbols ModalMuCalculusScannerToken -parser ModalMuCalculusParser $<


.PHONY: clean
clean:
	rm -f ModalMuCalculusParser.java \
	      ModalMuCalculusScanner.java \
              ModalMuCalculusScanner.java~ \
	      ModalMuCalculusScannerToken.java