This C++ program is a test-application for Gralog's invokation of
external applications.

To make an external application:

 - Write your application, (e.g. foo.cpp),
 
 - Have a standard input-format for your structures (e.g. TGF)
 
 - Compile your application and put the binary (e.g. foo) into a directory
   which is in your systems $PATH variable - (e.g. /usr/bin/)
   
 - Derive a class from gralog.algorithm.AlgorithmExternal (Gralog core package)
   and have a Constructor which calls the base-constructor with the following
   parameters:
   1. a gralog.export.ExportFilter instance for the format that your
      application accepts
   2. a boolean which specifies whether the structure should be supplied
      via file (true) or via stdin (false)
   3. the call to your compiled binary (e.g. "foo" is sufficient, if the binary
      is in a directory that is in your $PATH variable)

       @AlgorithmDescription(
         name="External Algorithm Test",
         text="Test-Class for running external algorithms",
         url=""
       )
       public class AlgorithmExternalTest extends AlgorithmExternal {
           public AlgorithmExternalTest() {
               super(new TrivialGraphFormatExport(), false, "foo");
           }
       }
       
 - If your program does not read the structure from stdin but from a file,
   which is given via commandline parameter like "foo -f=FILELOCATION -bar",
   then supply the parameters as additional, but INDIVIDUAL strings (a %u is
   replaced with the location of the temp file)
   
       @AlgorithmDescription(
         name="External Algorithm Test",
         text="Test-Class for running external algorithms",
         url=""
       )
       public class AlgorithmExternalTest extends AlgorithmExternal {
           public AlgorithmExternalTest() {
               super(new TrivialGraphFormatExport(), true, "foo", "-f=%u", "-bar");
           }
       }
