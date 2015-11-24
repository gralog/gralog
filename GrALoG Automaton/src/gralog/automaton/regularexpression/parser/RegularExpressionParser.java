
//----------------------------------------------------
// The following code was generated by CUP v0.11a beta 20060608
// Thu Nov 19 16:47:48 CET 2015
//----------------------------------------------------

package gralog.automaton.regularexpression.parser;

import java_cup.runtime.*;
import gralog.automaton.regularexpression.*;
import java.io.ByteArrayInputStream;

/** CUP v0.11a beta 20060608 generated parser.
  * @version Thu Nov 19 16:47:48 CET 2015
  */
public class RegularExpressionParser extends java_cup.runtime.lr_parser {

  /** Default constructor. */
  public RegularExpressionParser() {super();}

  /** Constructor which sets the default scanner. */
  public RegularExpressionParser(java_cup.runtime.Scanner s) {super(s);}

  /** Constructor which sets the default scanner. */
  public RegularExpressionParser(java_cup.runtime.Scanner s, java_cup.runtime.SymbolFactory sf) {super(s,sf);}

  /** Production table. */
  protected static final short _production_table[][] = 
    unpackFromStrings(new String[] {
    "\000\007\000\002\003\003\000\002\002\004\000\002\003" +
    "\004\000\002\002\003\000\002\002\004\000\002\002\005" +
    "\000\002\002\005" });

  /** Access to production table. */
  public short[][] production_table() {return _production_table;}

  /** Parse-action table. */
  protected static final short[][] _action_table = 
    unpackFromStrings(new String[] {
    "\000\014\000\006\004\006\010\005\001\002\000\010\002" +
    "\016\004\006\010\005\001\002\000\016\002\ufffe\004\ufffe" +
    "\005\ufffe\006\ufffe\007\ufffe\010\ufffe\001\002\000\006\004" +
    "\006\010\005\001\002\000\016\002\001\004\001\005\001" +
    "\006\010\007\011\010\001\001\002\000\016\002\ufffd\004" +
    "\ufffd\005\ufffd\006\ufffd\007\ufffd\010\ufffd\001\002\000\006" +
    "\004\006\010\005\001\002\000\016\002\ufffc\004\ufffc\005" +
    "\ufffc\006\010\007\ufffc\010\ufffc\001\002\000\010\004\006" +
    "\005\014\010\005\001\002\000\016\002\ufffb\004\ufffb\005" +
    "\ufffb\006\ufffb\007\ufffb\010\ufffb\001\002\000\016\002\uffff" +
    "\004\uffff\005\uffff\006\010\007\011\010\uffff\001\002\000" +
    "\004\002\000\001\002" });

  /** Access to parse-action table. */
  public short[][] action_table() {return _action_table;}

  /** <code>reduce_goto</code> table. */
  protected static final short[][] _reduce_table = 
    unpackFromStrings(new String[] {
    "\000\014\000\006\002\006\003\003\001\001\000\004\002" +
    "\014\001\001\000\002\001\001\000\006\002\006\003\012" +
    "\001\001\000\002\001\001\000\002\001\001\000\004\002" +
    "\011\001\001\000\002\001\001\000\004\002\014\001\001" +
    "\000\002\001\001\000\002\001\001\000\002\001\001" });

  /** Access to <code>reduce_goto</code> table. */
  public short[][] reduce_table() {return _reduce_table;}

  /** Instance of action encapsulation class. */
  protected CUP$RegularExpressionParser$actions action_obj;

  /** Action encapsulation object initializer. */
  protected void init_actions()
    {
      action_obj = new CUP$RegularExpressionParser$actions(this);
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
    return action_obj.CUP$RegularExpressionParser$do_action(act_num, parser, stack, top);
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
    
    public void syntax_error(Symbol cur_token)
    {
        if(errorMsg == null)
            errorMsg = "Syntax Error: " + cur_token.toString();
    }

    public void report_fatal_error(String message, Object info) throws Exception
    {
        throw new Exception("Fatal parsing error: " + message + "\n" + info.toString());
    }

    public RegularExpression parseString(String str) throws Exception
    {
        String charset = "UTF8";
        byte[] bytes = str.getBytes(charset);
        ByteArrayInputStream stringstream = new ByteArrayInputStream(bytes);
                
        DefaultSymbolFactory symbolfactory = new DefaultSymbolFactory();
        RegularExpressionScanner scanner = new RegularExpressionScanner(stringstream, symbolfactory);
        this.setScanner(scanner);
        
        Symbol parserresult = this.parse();
        return (RegularExpression) parserresult.value;
    }


}

/** Cup generated class to encapsulate user supplied action code.*/
class CUP$RegularExpressionParser$actions {
  private final RegularExpressionParser parser;

  /** Constructor */
  CUP$RegularExpressionParser$actions(RegularExpressionParser parser) {
    this.parser = parser;
  }

  /** Method with the actual generated action code. */
  public final java_cup.runtime.Symbol CUP$RegularExpressionParser$do_action(
    int                        CUP$RegularExpressionParser$act_num,
    java_cup.runtime.lr_parser CUP$RegularExpressionParser$parser,
    java.util.Stack            CUP$RegularExpressionParser$stack,
    int                        CUP$RegularExpressionParser$top)
    throws java.lang.Exception
    {
      /* Symbol object for return from actions */
      java_cup.runtime.Symbol CUP$RegularExpressionParser$result;

      /* select the action based on the action number */
      switch (CUP$RegularExpressionParser$act_num)
        {
          /*. . . . . . . . . . . . . . . . . . . .*/
          case 6: // regexp ::= PARENTHESISLEFT regexps PARENTHESISRIGHT 
            {
              RegularExpression RESULT =null;
		int fleft = ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.elementAt(CUP$RegularExpressionParser$top-1)).left;
		int fright = ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.elementAt(CUP$RegularExpressionParser$top-1)).right;
		RegularExpression f = (RegularExpression)((java_cup.runtime.Symbol) CUP$RegularExpressionParser$stack.elementAt(CUP$RegularExpressionParser$top-1)).value;
		 RESULT = f; 
              CUP$RegularExpressionParser$result = parser.getSymbolFactory().newSymbol("regexp",0, ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.elementAt(CUP$RegularExpressionParser$top-2)), ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.peek()), RESULT);
            }
          return CUP$RegularExpressionParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 5: // regexp ::= regexp ALTERNATION regexp 
            {
              RegularExpression RESULT =null;
		int lleft = ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.elementAt(CUP$RegularExpressionParser$top-2)).left;
		int lright = ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.elementAt(CUP$RegularExpressionParser$top-2)).right;
		RegularExpression l = (RegularExpression)((java_cup.runtime.Symbol) CUP$RegularExpressionParser$stack.elementAt(CUP$RegularExpressionParser$top-2)).value;
		int rleft = ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.peek()).left;
		int rright = ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.peek()).right;
		RegularExpression r = (RegularExpression)((java_cup.runtime.Symbol) CUP$RegularExpressionParser$stack.peek()).value;
		 RESULT = new RegularExpressionAlternation(l,r); 
              CUP$RegularExpressionParser$result = parser.getSymbolFactory().newSymbol("regexp",0, ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.elementAt(CUP$RegularExpressionParser$top-2)), ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.peek()), RESULT);
            }
          return CUP$RegularExpressionParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 4: // regexp ::= regexp KLEENESTAR 
            {
              RegularExpression RESULT =null;
		int fleft = ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.elementAt(CUP$RegularExpressionParser$top-1)).left;
		int fright = ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.elementAt(CUP$RegularExpressionParser$top-1)).right;
		RegularExpression f = (RegularExpression)((java_cup.runtime.Symbol) CUP$RegularExpressionParser$stack.elementAt(CUP$RegularExpressionParser$top-1)).value;
		 RESULT = new RegularExpressionKleeneStar(f); 
              CUP$RegularExpressionParser$result = parser.getSymbolFactory().newSymbol("regexp",0, ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.elementAt(CUP$RegularExpressionParser$top-1)), ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.peek()), RESULT);
            }
          return CUP$RegularExpressionParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 3: // regexp ::= STRING 
            {
              RegularExpression RESULT =null;
		int sleft = ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.peek()).left;
		int sright = ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.peek()).right;
		String s = (String)((java_cup.runtime.Symbol) CUP$RegularExpressionParser$stack.peek()).value;
		 RESULT = new RegularExpressionString(s); 
              CUP$RegularExpressionParser$result = parser.getSymbolFactory().newSymbol("regexp",0, ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.peek()), ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.peek()), RESULT);
            }
          return CUP$RegularExpressionParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 2: // regexps ::= regexps regexp 
            {
              RegularExpression RESULT =null;
		int lleft = ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.elementAt(CUP$RegularExpressionParser$top-1)).left;
		int lright = ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.elementAt(CUP$RegularExpressionParser$top-1)).right;
		RegularExpression l = (RegularExpression)((java_cup.runtime.Symbol) CUP$RegularExpressionParser$stack.elementAt(CUP$RegularExpressionParser$top-1)).value;
		int rleft = ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.peek()).left;
		int rright = ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.peek()).right;
		RegularExpression r = (RegularExpression)((java_cup.runtime.Symbol) CUP$RegularExpressionParser$stack.peek()).value;
		 RESULT = new RegularExpressionConcatenation(l,r); 
              CUP$RegularExpressionParser$result = parser.getSymbolFactory().newSymbol("regexps",1, ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.elementAt(CUP$RegularExpressionParser$top-1)), ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.peek()), RESULT);
            }
          return CUP$RegularExpressionParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 1: // $START ::= regexps EOF 
            {
              Object RESULT =null;
		int start_valleft = ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.elementAt(CUP$RegularExpressionParser$top-1)).left;
		int start_valright = ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.elementAt(CUP$RegularExpressionParser$top-1)).right;
		RegularExpression start_val = (RegularExpression)((java_cup.runtime.Symbol) CUP$RegularExpressionParser$stack.elementAt(CUP$RegularExpressionParser$top-1)).value;
		RESULT = start_val;
              CUP$RegularExpressionParser$result = parser.getSymbolFactory().newSymbol("$START",0, ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.elementAt(CUP$RegularExpressionParser$top-1)), ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.peek()), RESULT);
            }
          /* ACCEPT */
          CUP$RegularExpressionParser$parser.done_parsing();
          return CUP$RegularExpressionParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 0: // regexps ::= regexp 
            {
              RegularExpression RESULT =null;
		int rleft = ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.peek()).left;
		int rright = ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.peek()).right;
		RegularExpression r = (RegularExpression)((java_cup.runtime.Symbol) CUP$RegularExpressionParser$stack.peek()).value;
		 RESULT = r; 
              CUP$RegularExpressionParser$result = parser.getSymbolFactory().newSymbol("regexps",1, ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.peek()), ((java_cup.runtime.Symbol)CUP$RegularExpressionParser$stack.peek()), RESULT);
            }
          return CUP$RegularExpressionParser$result;

          /* . . . . . .*/
          default:
            throw new Exception(
               "Invalid action number found in internal parse table");

        }
    }
}

