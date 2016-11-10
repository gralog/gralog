


# sudo apt-get install jflex cup

JFLEX := jflex
CUP := cup
# CUP := java -jar /path/to/java-cup-version.jar


.PHONY: all
all: ComputationTreeLogicScanner.java ComputationTreeLogicParser.java


ComputationTreeLogicScanner.java: ComputationTreeLogicScanner.jflex ComputationTreeLogicScannerToken.java
	$(JFLEX) $<


ComputationTreeLogicParser.java ComputationTreeLogicScannerToken.java: ComputationTreeLogicParser.cup
	$(CUP) -symbols ComputationTreeLogicScannerToken -parser ComputationTreeLogicParser $<


.PHONY: clean
clean:
	rm -f ComputationTreeLogicParser.java \
	      ComputationTreeLogicScanner.java \
	      ComputationTreeLogicScanner.java~ \
	      ComputationTreeLogicScannerToken.java