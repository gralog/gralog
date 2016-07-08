
# sudo apt-get install jflex cup

JFLEX := jflex
CUP := cup
# CUP := java -jar /path/to/java-cup-version.jar


.PHONY: all
all: FirstOrderScanner.java FirstOrderParser.java


FirstOrderScanner.java: FirstOrderScanner.jflex FirstOrderScannerToken.java
	$(JFLEX) $<


FirstOrderParser.java FirstOrderScannerToken.java: FirstOrderParser.cup
	$(CUP) -symbols FirstOrderScannerToken -parser FirstOrderParser $<


.PHONY: clean
clean:
	rm -f FirstOrderParser.java \
	      FirstOrderScanner.java \
	      FirstOrderScanner.java~ \
	      FirstOrderScannerToken.java
