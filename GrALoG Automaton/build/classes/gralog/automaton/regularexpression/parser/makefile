


# sudo apt-get install jflex cup

JFLEX := jflex
CUP := cup
# CUP := java -jar /path/to/java-cup-version.jar


.PHONY: all
all: RegularExpressionScanner.java RegularExpressionParser.java


RegularExpressionScanner.java: RegularExpressionScanner.jflex RegularExpressionScannerToken.java
	$(JFLEX) $<


RegularExpressionParser.java RegularExpressionScannerToken.java: RegularExpressionParser.cup
	$(CUP) -symbols RegularExpressionScannerToken -parser RegularExpressionParser $<


.PHONY: clean
clean:
	rm -f RegularExpressionParser.java \
	      RegularExpressionScanner.java \
	      RegularExpressionScannerToken.java
