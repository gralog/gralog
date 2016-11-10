


# sudo apt-get install jflex cup

JFLEX := jflex
CUP := cup
# CUP := java -jar /path/to/java-cup-version.jar


.PHONY: all
all: ModalLogicScanner.java ModalLogicParser.java


ModalLogicScanner.java: ModalLogicScanner.jflex ModalLogicScannerToken.java
	$(JFLEX) $<


ModalLogicParser.java ModalLogicScannerToken.java: ModalLogicParser.cup
	$(CUP) -symbols ModalLogicScannerToken -parser ModalLogicParser $<


.PHONY: clean
clean:
	rm -f ModalLogicParser.java \
	      ModalLogicScanner.java \
	      ModalLogicScannerToken.java