
//----------------------------------------------------
// The following code was generated by CUP v0.11b 20160615 (GIT 4ac7450)
//----------------------------------------------------

package gralog.npcompleteness.propositionallogic.parser;

import java_cup.runtime.*;
import gralog.algorithm.ParseError;
import gralog.npcompleteness.propositionallogic.formula.*;
import java.io.StringReader;
import java_cup.runtime.ComplexSymbolFactory.Location;
import java_cup.runtime.XMLElement;

/** CUP v0.11b 20160615 (GIT 4ac7450) generated parser.
  */
@SuppressWarnings({"rawtypes"})
public class PropositionalLogicParser extends java_cup.runtime.lr_parser {

 public final Class getSymbolContainer() {
    return PropositionalLogicScannerToken.class;
}

  /** Default constructor. */
  @Deprecated
  public PropositionalLogicParser() {super();}

  /** Constructor which sets the default scanner. */
  @Deprecated
  public PropositionalLogicParser(java_cup.runtime.Scanner s) {super(s);}

  /** Constructor which sets the default scanner. */
  public PropositionalLogicParser(java_cup.runtime.Scanner s, java_cup.runtime.SymbolFactory sf) {super(s,sf);}

  /** Production table. */
  protected static final short _production_table[][] = 
    unpackFromStrings(new String[] {
    "\000\011\000\002\002\003\000\002\002\004\000\002\003" +
    "\005\000\002\003\003\000\002\004\005\000\002\004\003" +
    "\000\002\005\004\000\002\005\005\000\002\005\003" });

  /** Access to production table. */
  public short[][] production_table() {return _production_table;}

  /** Parse-action table. */
  protected static final short[][] _action_table = 
    unpackFromStrings(new String[] {
    "\000\020\000\010\004\010\006\006\011\004\001\002\000" +
    "\012\002\ufff9\005\ufff9\007\ufff9\010\ufff9\001\002\000\012" +
    "\002\ufffc\005\ufffc\007\ufffc\010\ufffc\001\002\000\010\004" +
    "\010\006\006\011\004\001\002\000\012\002\ufffe\005\ufffe" +
    "\007\015\010\ufffe\001\002\000\010\004\010\006\006\011" +
    "\004\001\002\000\004\002\017\001\002\000\010\002\001" +
    "\005\001\010\013\001\002\000\010\004\010\006\006\011" +
    "\004\001\002\000\012\002\uffff\005\uffff\007\015\010\uffff" +
    "\001\002\000\010\004\010\006\006\011\004\001\002\000" +
    "\012\002\ufffd\005\ufffd\007\ufffd\010\ufffd\001\002\000\004" +
    "\002\000\001\002\000\004\005\021\001\002\000\012\002" +
    "\ufffa\005\ufffa\007\ufffa\010\ufffa\001\002\000\012\002\ufffb" +
    "\005\ufffb\007\ufffb\010\ufffb\001\002" });

  /** Access to parse-action table. */
  public short[][] action_table() {return _action_table;}

  /** <code>reduce_goto</code> table. */
  protected static final short[][] _reduce_table = 
    unpackFromStrings(new String[] {
    "\000\020\000\012\002\010\003\011\004\006\005\004\001" +
    "\001\000\002\001\001\000\002\001\001\000\004\005\021" +
    "\001\001\000\002\001\001\000\012\002\017\003\011\004" +
    "\006\005\004\001\001\000\002\001\001\000\002\001\001" +
    "\000\006\004\013\005\004\001\001\000\002\001\001\000" +
    "\004\005\015\001\001\000\002\001\001\000\002\001\001" +
    "\000\002\001\001\000\002\001\001\000\002\001\001" });

  /** Access to <code>reduce_goto</code> table. */
  public short[][] reduce_table() {return _reduce_table;}

  /** Instance of action encapsulation class. */
  protected CUP$PropositionalLogicParser$actions action_obj;

  /** Action encapsulation object initializer. */
  protected void init_actions()
    {
      action_obj = new CUP$PropositionalLogicParser$actions(this);
    }

  /** Invoke a user supplied parse action. */
  public java_cup.runtime.Symbol do_action(
    int                        act_num,
    java_cup.runtime.lr_parser parser,
    java.util.Stack            stack,
    int                        top)
    throws java.lang.Exception
  {
    /* call code in generated class */
    return action_obj.CUP$PropositionalLogicParser$do_action(act_num, parser, stack, top);
  }

  /** Indicates start state. */
  public int start_state() {return 0;}
  /** Indicates start production. */
  public int start_production() {return 1;}

  /** <code>EOF</code> Symbol index. */
  public int EOF_sym() {return 0;}

  /** <code>error</code> Symbol index. */
  public int error_sym() {return 1;}




    String errorMsg = null;
    private String inputString;

    public Boolean hasError()
    {
        return errorMsg == null;
    }
    
    public String getErrorMsg()
    {
        if(errorMsg == null)
            return "no error";
        else
            return errorMsg;
    }
    
    @Override
    public void report_error(String message, Object info) {
    }

    @Override
    public void syntax_error(Symbol cur_token)
    {
        if(errorMsg == null)
            errorMsg = "Syntax Error: " + cur_token.toString();
    }

    @Override
    public void report_fatal_error(String message, Object info) throws ParseError
    {
        java_cup.runtime.ComplexSymbolFactory.ComplexSymbol symbol = (java_cup.runtime.ComplexSymbolFactory.ComplexSymbol)info;
        throw new ParseError("Unexpected " + symbol.getName(), inputString, symbol.xleft.getColumn());
    }

    static public PropositionalLogicFormula parseString(String str) throws Exception
    {
        PropositionalLogicParser parser = new PropositionalLogicParser(
                new PropositionalLogicScanner(new StringReader(str)),
                new java_cup.runtime.ComplexSymbolFactory());
        parser.inputString = str;
        Symbol parserresult = parser.parse();
        return (PropositionalLogicFormula) parserresult.value;
    }


/** Cup generated class to encapsulate user supplied action code.*/
@SuppressWarnings({"rawtypes", "unchecked", "unused"})
class CUP$PropositionalLogicParser$actions {
  private final PropositionalLogicParser parser;

  /** Constructor */
  CUP$PropositionalLogicParser$actions(PropositionalLogicParser parser) {
    this.parser = parser;
  }

  /** Method 0 with the actual generated action code for actions 0 to 300. */
  public final java_cup.runtime.Symbol CUP$PropositionalLogicParser$do_action_part00000000(
    int                        CUP$PropositionalLogicParser$act_num,
    java_cup.runtime.lr_parser CUP$PropositionalLogicParser$parser,
    java.util.Stack            CUP$PropositionalLogicParser$stack,
    int                        CUP$PropositionalLogicParser$top)
    throws java.lang.Exception
    {
      /* Symbol object for return from actions */
      java_cup.runtime.Symbol CUP$PropositionalLogicParser$result;

      /* select the action based on the action number */
      switch (CUP$PropositionalLogicParser$act_num)
        {
          /*. . . . . . . . . . . . . . . . . . . .*/
          case 0: // formula ::= veeformula 
            {
              PropositionalLogicFormula RESULT =null;
		Location fxleft = ((java_cup.runtime.ComplexSymbolFactory.ComplexSymbol)CUP$PropositionalLogicParser$stack.peek()).xleft;
		Location fxright = ((java_cup.runtime.ComplexSymbolFactory.ComplexSymbol)CUP$PropositionalLogicParser$stack.peek()).xright;
		PropositionalLogicFormula f = (PropositionalLogicFormula)((java_cup.runtime.Symbol) CUP$PropositionalLogicParser$stack.peek()).value;
		 RESULT = f; 
              CUP$PropositionalLogicParser$result = parser.getSymbolFactory().newSymbol("formula",0, ((java_cup.runtime.Symbol)CUP$PropositionalLogicParser$stack.peek()), ((java_cup.runtime.Symbol)CUP$PropositionalLogicParser$stack.peek()), RESULT);
            }
          return CUP$PropositionalLogicParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 1: // $START ::= formula EOF 
            {
              Object RESULT =null;
		Location start_valxleft = ((java_cup.runtime.ComplexSymbolFactory.ComplexSymbol)CUP$PropositionalLogicParser$stack.elementAt(CUP$PropositionalLogicParser$top-1)).xleft;
		Location start_valxright = ((java_cup.runtime.ComplexSymbolFactory.ComplexSymbol)CUP$PropositionalLogicParser$stack.elementAt(CUP$PropositionalLogicParser$top-1)).xright;
		PropositionalLogicFormula start_val = (PropositionalLogicFormula)((java_cup.runtime.Symbol) CUP$PropositionalLogicParser$stack.elementAt(CUP$PropositionalLogicParser$top-1)).value;
		RESULT = start_val;
              CUP$PropositionalLogicParser$result = parser.getSymbolFactory().newSymbol("$START",0, ((java_cup.runtime.Symbol)CUP$PropositionalLogicParser$stack.elementAt(CUP$PropositionalLogicParser$top-1)), ((java_cup.runtime.Symbol)CUP$PropositionalLogicParser$stack.peek()), RESULT);
            }
          /* ACCEPT */
          CUP$PropositionalLogicParser$parser.done_parsing();
          return CUP$PropositionalLogicParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 2: // veeformula ::= veeformula OR wedgeformula 
            {
              PropositionalLogicFormula RESULT =null;
		Location leftxleft = ((java_cup.runtime.ComplexSymbolFactory.ComplexSymbol)CUP$PropositionalLogicParser$stack.elementAt(CUP$PropositionalLogicParser$top-2)).xleft;
		Location leftxright = ((java_cup.runtime.ComplexSymbolFactory.ComplexSymbol)CUP$PropositionalLogicParser$stack.elementAt(CUP$PropositionalLogicParser$top-2)).xright;
		PropositionalLogicFormula left = (PropositionalLogicFormula)((java_cup.runtime.Symbol) CUP$PropositionalLogicParser$stack.elementAt(CUP$PropositionalLogicParser$top-2)).value;
		Location rightxleft = ((java_cup.runtime.ComplexSymbolFactory.ComplexSymbol)CUP$PropositionalLogicParser$stack.peek()).xleft;
		Location rightxright = ((java_cup.runtime.ComplexSymbolFactory.ComplexSymbol)CUP$PropositionalLogicParser$stack.peek()).xright;
		PropositionalLogicFormula right = (PropositionalLogicFormula)((java_cup.runtime.Symbol) CUP$PropositionalLogicParser$stack.peek()).value;
		 RESULT = new PropositionalLogicOr(left, right); 
              CUP$PropositionalLogicParser$result = parser.getSymbolFactory().newSymbol("veeformula",1, ((java_cup.runtime.Symbol)CUP$PropositionalLogicParser$stack.elementAt(CUP$PropositionalLogicParser$top-2)), ((java_cup.runtime.Symbol)CUP$PropositionalLogicParser$stack.peek()), RESULT);
            }
          return CUP$PropositionalLogicParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 3: // veeformula ::= wedgeformula 
            {
              PropositionalLogicFormula RESULT =null;
		Location fxleft = ((java_cup.runtime.ComplexSymbolFactory.ComplexSymbol)CUP$PropositionalLogicParser$stack.peek()).xleft;
		Location fxright = ((java_cup.runtime.ComplexSymbolFactory.ComplexSymbol)CUP$PropositionalLogicParser$stack.peek()).xright;
		PropositionalLogicFormula f = (PropositionalLogicFormula)((java_cup.runtime.Symbol) CUP$PropositionalLogicParser$stack.peek()).value;
		 RESULT = f; 
              CUP$PropositionalLogicParser$result = parser.getSymbolFactory().newSymbol("veeformula",1, ((java_cup.runtime.Symbol)CUP$PropositionalLogicParser$stack.peek()), ((java_cup.runtime.Symbol)CUP$PropositionalLogicParser$stack.peek()), RESULT);
            }
          return CUP$PropositionalLogicParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 4: // wedgeformula ::= wedgeformula AND atomicformula 
            {
              PropositionalLogicFormula RESULT =null;
		Location leftxleft = ((java_cup.runtime.ComplexSymbolFactory.ComplexSymbol)CUP$PropositionalLogicParser$stack.elementAt(CUP$PropositionalLogicParser$top-2)).xleft;
		Location leftxright = ((java_cup.runtime.ComplexSymbolFactory.ComplexSymbol)CUP$PropositionalLogicParser$stack.elementAt(CUP$PropositionalLogicParser$top-2)).xright;
		PropositionalLogicFormula left = (PropositionalLogicFormula)((java_cup.runtime.Symbol) CUP$PropositionalLogicParser$stack.elementAt(CUP$PropositionalLogicParser$top-2)).value;
		Location rightxleft = ((java_cup.runtime.ComplexSymbolFactory.ComplexSymbol)CUP$PropositionalLogicParser$stack.peek()).xleft;
		Location rightxright = ((java_cup.runtime.ComplexSymbolFactory.ComplexSymbol)CUP$PropositionalLogicParser$stack.peek()).xright;
		PropositionalLogicFormula right = (PropositionalLogicFormula)((java_cup.runtime.Symbol) CUP$PropositionalLogicParser$stack.peek()).value;
		 RESULT = new PropositionalLogicAnd(left, right); 
              CUP$PropositionalLogicParser$result = parser.getSymbolFactory().newSymbol("wedgeformula",2, ((java_cup.runtime.Symbol)CUP$PropositionalLogicParser$stack.elementAt(CUP$PropositionalLogicParser$top-2)), ((java_cup.runtime.Symbol)CUP$PropositionalLogicParser$stack.peek()), RESULT);
            }
          return CUP$PropositionalLogicParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 5: // wedgeformula ::= atomicformula 
            {
              PropositionalLogicFormula RESULT =null;
		Location fxleft = ((java_cup.runtime.ComplexSymbolFactory.ComplexSymbol)CUP$PropositionalLogicParser$stack.peek()).xleft;
		Location fxright = ((java_cup.runtime.ComplexSymbolFactory.ComplexSymbol)CUP$PropositionalLogicParser$stack.peek()).xright;
		PropositionalLogicFormula f = (PropositionalLogicFormula)((java_cup.runtime.Symbol) CUP$PropositionalLogicParser$stack.peek()).value;
		 RESULT = f; 
              CUP$PropositionalLogicParser$result = parser.getSymbolFactory().newSymbol("wedgeformula",2, ((java_cup.runtime.Symbol)CUP$PropositionalLogicParser$stack.peek()), ((java_cup.runtime.Symbol)CUP$PropositionalLogicParser$stack.peek()), RESULT);
            }
          return CUP$PropositionalLogicParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 6: // atomicformula ::= NEG atomicformula 
            {
              PropositionalLogicFormula RESULT =null;
		Location fxleft = ((java_cup.runtime.ComplexSymbolFactory.ComplexSymbol)CUP$PropositionalLogicParser$stack.peek()).xleft;
		Location fxright = ((java_cup.runtime.ComplexSymbolFactory.ComplexSymbol)CUP$PropositionalLogicParser$stack.peek()).xright;
		PropositionalLogicFormula f = (PropositionalLogicFormula)((java_cup.runtime.Symbol) CUP$PropositionalLogicParser$stack.peek()).value;
		 RESULT = new PropositionalLogicNot(f); 
              CUP$PropositionalLogicParser$result = parser.getSymbolFactory().newSymbol("atomicformula",3, ((java_cup.runtime.Symbol)CUP$PropositionalLogicParser$stack.elementAt(CUP$PropositionalLogicParser$top-1)), ((java_cup.runtime.Symbol)CUP$PropositionalLogicParser$stack.peek()), RESULT);
            }
          return CUP$PropositionalLogicParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 7: // atomicformula ::= OPEN formula CLOSE 
            {
              PropositionalLogicFormula RESULT =null;
		Location fxleft = ((java_cup.runtime.ComplexSymbolFactory.ComplexSymbol)CUP$PropositionalLogicParser$stack.elementAt(CUP$PropositionalLogicParser$top-1)).xleft;
		Location fxright = ((java_cup.runtime.ComplexSymbolFactory.ComplexSymbol)CUP$PropositionalLogicParser$stack.elementAt(CUP$PropositionalLogicParser$top-1)).xright;
		PropositionalLogicFormula f = (PropositionalLogicFormula)((java_cup.runtime.Symbol) CUP$PropositionalLogicParser$stack.elementAt(CUP$PropositionalLogicParser$top-1)).value;
		 RESULT = f; 
              CUP$PropositionalLogicParser$result = parser.getSymbolFactory().newSymbol("atomicformula",3, ((java_cup.runtime.Symbol)CUP$PropositionalLogicParser$stack.elementAt(CUP$PropositionalLogicParser$top-2)), ((java_cup.runtime.Symbol)CUP$PropositionalLogicParser$stack.peek()), RESULT);
            }
          return CUP$PropositionalLogicParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 8: // atomicformula ::= STRING 
            {
              PropositionalLogicFormula RESULT =null;
		Location varxleft = ((java_cup.runtime.ComplexSymbolFactory.ComplexSymbol)CUP$PropositionalLogicParser$stack.peek()).xleft;
		Location varxright = ((java_cup.runtime.ComplexSymbolFactory.ComplexSymbol)CUP$PropositionalLogicParser$stack.peek()).xright;
		String var = (String)((java_cup.runtime.Symbol) CUP$PropositionalLogicParser$stack.peek()).value;
		 RESULT = new PropositionalLogicVariable(var); 
              CUP$PropositionalLogicParser$result = parser.getSymbolFactory().newSymbol("atomicformula",3, ((java_cup.runtime.Symbol)CUP$PropositionalLogicParser$stack.peek()), ((java_cup.runtime.Symbol)CUP$PropositionalLogicParser$stack.peek()), RESULT);
            }
          return CUP$PropositionalLogicParser$result;

          /* . . . . . .*/
          default:
            throw new Exception(
               "Invalid action number "+CUP$PropositionalLogicParser$act_num+"found in internal parse table");

        }
    } /* end of method */

  /** Method splitting the generated action code into several parts. */
  public final java_cup.runtime.Symbol CUP$PropositionalLogicParser$do_action(
    int                        CUP$PropositionalLogicParser$act_num,
    java_cup.runtime.lr_parser CUP$PropositionalLogicParser$parser,
    java.util.Stack            CUP$PropositionalLogicParser$stack,
    int                        CUP$PropositionalLogicParser$top)
    throws java.lang.Exception
    {
              return CUP$PropositionalLogicParser$do_action_part00000000(
                               CUP$PropositionalLogicParser$act_num,
                               CUP$PropositionalLogicParser$parser,
                               CUP$PropositionalLogicParser$stack,
                               CUP$PropositionalLogicParser$top);
    }
}

}
