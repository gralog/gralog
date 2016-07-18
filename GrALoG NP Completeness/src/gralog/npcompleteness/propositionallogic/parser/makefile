
# sudo apt-get install jflex cup

JFLEX := jflex
CUP := cup
# CUP := java -jar /path/to/java-cup-version.jar


.PHONY: all
all: PropositionalLogicScanner.java PropositionalLogicParser.java


PropositionalLogicScanner.java: PropositionalLogicScanner.jflex PropositionalLogicScannerToken.java
	$(JFLEX) $<


PropositionalLogicParser.java PropositionalLogicScannerToken.java: PropositionalLogicParser.cup
	$(CUP) -symbols PropositionalLogicScannerToken -parser PropositionalLogicParser $<


.PHONY: clean
clean:
	rm -f PropositionalLogicParser.java \
	      PropositionalLogicScanner.java \
	      PropositionalLogicScanner.java~ \
	      PropositionalLogicScannerToken.java
