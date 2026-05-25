//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 2 "gramatica.y"


package AnalizadorSintactico;
import AnalizadorLexico.TablaSimbolosControl;
import AnalizadorLexico.AnalizadorLexico;
import AnalizadorLexico.TDSObject;
import static AnalizadorLexico.AnalizadorLexico.tablaDeSimbolos;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;


//#line 36 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new ParserVal[YYSTACKSIZE];
  yyval=new ParserVal();
  yylval=new ParserVal();
  valptr=-1;
}
void val_push(ParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
ParserVal val_pop()
{
  if (valptr<0)
    return new ParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
ParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new ParserVal();
  return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
  ParserVal dup = new ParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short VAR=257;
public final static short DO=258;
public final static short WHILE=259;
public final static short LAMBDA=260;
public final static short IF=261;
public final static short ELSE=262;
public final static short ENDIF=263;
public final static short PRINT=264;
public final static short RETURN=265;
public final static short MENOR_IGUAL=266;
public final static short MAYOR_IGUAL=267;
public final static short DISTINTO=268;
public final static short IGUAL=269;
public final static short ASIGNACION_SIMPLE=270;
public final static short FLECHITA=271;
public final static short UINT=272;
public final static short DFLOAT=273;
public final static short ID=274;
public final static short CADENA=275;
public final static short CTE=276;
public final static short CV=277;
public final static short CR=278;
public final static short LE=279;
public final static short TOD=280;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    2,    0,    0,    1,    1,    1,    1,    1,    3,    5,
    4,    6,    6,    8,    8,    7,    7,    7,    9,    9,
    9,   12,   12,   12,   12,   10,   10,   15,   15,   16,
   16,   16,   18,   14,   14,   14,   20,   17,   17,   21,
   21,   19,   19,   22,   22,   22,   22,   22,   22,   22,
   22,   22,   22,   22,   22,   22,   23,   24,   25,   25,
   25,   25,   25,   25,   25,   25,   25,   25,   28,   28,
   28,   28,   28,   28,   28,   28,   28,   28,   30,   30,
   30,   30,   33,   33,   33,   33,   31,   26,   26,   26,
   26,   26,   26,   26,   26,   32,   32,   32,   32,   32,
   32,   32,   32,   34,   36,   36,   36,   36,   36,   36,
   27,   27,   27,   37,   37,   29,   29,   29,   38,   38,
   11,   11,   11,   11,   11,   11,   11,   13,   13,   13,
   13,   13,   13,   13,   13,   13,   42,   45,   45,   46,
   46,   46,   46,   47,   47,   47,   47,   48,   44,   44,
   49,   49,   49,   41,   41,   41,   41,   41,   41,   41,
   39,   39,   40,   40,   51,   51,   51,   50,   50,   52,
   52,   52,   54,   54,   54,   54,   54,   54,   54,   53,
   55,   55,   55,   55,   43,   43,   56,   56,   35,   35,
   35,   35,   35,   57,   57,   57,   57,   57,   58,   58,
   58,   58,   58,   58,   58,   58,
};
final static short yylen[] = {                            2,
    0,    3,    1,    3,    2,    2,    2,    1,    1,    1,
    1,    2,    1,    2,    1,    1,    1,    2,    1,    1,
    2,    1,    1,    1,    1,    1,    1,    3,    2,    3,
    1,    2,    0,    7,    7,    5,    3,    3,    1,    1,
    1,    3,    1,    2,    4,    4,    2,    1,    3,    3,
    1,    3,    3,    3,    3,    3,    0,    1,    7,    6,
    7,    7,    7,    6,    5,    4,    5,    4,    7,    6,
    7,    7,    7,    6,    5,    4,    5,    4,    5,    4,
    4,    6,    5,    4,    4,    6,    1,    3,    3,    3,
    2,    6,    4,    6,    6,    3,    3,    3,    2,    6,
    4,    6,    6,    3,    1,    1,    1,    1,    1,    1,
    1,    3,    2,    2,    1,    1,    3,    2,    2,    1,
    1,    1,    1,    1,    1,    2,    3,    1,    1,    1,
    1,    1,    2,    3,    2,    1,    4,    3,    1,    3,
    2,    2,    3,    4,    3,    3,    2,    4,    5,    5,
    1,    1,    2,    5,    5,    4,    6,    6,    5,    6,
    4,    5,    4,    5,    3,    2,    1,    1,    1,    3,
    2,    1,    1,    2,    2,    1,    1,    2,    1,    3,
    4,    4,    4,    3,    4,    5,    3,    1,    3,    3,
    3,    3,    1,    3,    3,    3,    3,    1,    1,    2,
    2,    1,    1,    2,    1,    1,
};
final static short yydefred[] = {                         0,
    0,    0,   87,    0,    0,   40,   41,    0,    9,    0,
    3,    0,    0,    0,   13,   16,   17,   26,   27,    0,
   39,  123,  124,    0,  121,  122,  125,    0,    0,    0,
  169,   24,   22,    0,   23,   25,   18,  166,   31,   29,
    0,    0,  199,    0,    0,    0,    0,    0,    0,  205,
  202,  206,    0,  198,    0,    0,    0,    0,    0,   10,
    0,    6,    7,   12,   33,    0,    0,    0,    0,    0,
    0,  111,    0,   57,  126,    0,    0,    0,   32,   28,
    0,    0,    0,    0,    0,    0,    0,  200,  201,    0,
    0,   57,   91,  105,  106,  107,  108,  109,  110,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  139,    0,    0,  180,    2,    4,    0,   38,
    0,   51,    0,    0,    0,    0,    0,   43,    0,    0,
    0,    0,  113,  115,    0,    0,    0,  127,    0,  165,
    0,  173,    0,  179,    0,  176,  172,   30,  184,   57,
   57,    0,   57,    0,   88,   89,    0,    0,   58,    0,
   68,    0,   66,    0,   90,  192,    0,  191,    0,    0,
  196,  194,  197,  195,    0,    0,   57,  156,    0,  142,
    0,    0,    0,  137,    0,    0,    0,    0,   47,    0,
    0,    0,    0,    0,    0,    0,   44,    0,    0,    0,
    0,   81,   57,   99,  112,  114,    0,   80,   57,  161,
    0,  174,  175,   57,  163,    0,  171,  183,  182,  181,
   93,    0,    0,    0,    0,   67,   65,    0,    0,   57,
   57,  154,  159,   57,  155,    0,  140,  138,  143,    0,
  145,    0,   52,    0,   54,   53,    0,   55,   56,   42,
    0,   36,    0,   57,    0,   96,   97,    0,    0,   98,
   57,   79,  162,  164,  170,   57,   57,   57,    0,    0,
   64,    0,   60,  160,  157,  158,  148,  144,    0,   45,
   46,    0,    0,    0,    0,    0,   15,   19,   20,  130,
    0,  131,  128,  129,  132,    0,    0,  136,    0,  101,
    0,    0,    0,   82,   94,   92,   95,   61,   62,   63,
   59,   34,   21,    0,    0,    0,   37,   14,    0,    0,
  116,    0,   57,  133,  135,   35,   57,   57,   57,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  118,
  120,    0,    0,    0,  134,  102,  100,  103,    0,   78,
    0,   76,    0,    0,    0,  185,    0,    0,    0,   85,
  117,  119,    0,   84,    0,   77,   75,    0,    0,  186,
    0,  150,  149,   57,   83,    0,    0,   74,    0,   70,
   86,   71,   72,   73,   69,
};
final static short yydgoto[] = {                         10,
   11,   58,   12,   13,   62,   14,   15,  286,  287,   16,
   17,   37,  321,   18,   19,   41,   20,  119,  126,  252,
   21,  128,   90,  160,   22,   47,   73,  290,  322,   23,
   24,  131,  292,  132,   49,  102,  135,  342,   25,   26,
   27,   50,  297,  298,  112,  113,  114,  115,  338,   29,
   30,  145,   51,  147,   52,  333,   53,   54,
};
final static short yysindex[] = {                       323,
   -1,  -40,    0,   60,   31,    0,    0,   17,    0,    0,
    0,  587,  -92,  255,    0,    0,    0,    0,    0,  -14,
    0,    0,    0,  525,    0,    0,    0,  -42, -228,    5,
    0,    0,    0,   19,    0,    0,    0,    0,    0,    0,
   54,   17,    0,  -32,   86, -116,  547,  152,  510,    0,
    0,    0,  130,    0,   32,   -9, -229,  345,   17,    0,
  -92,    0,    0,    0,    0, -162,  653,  -77,  -71,   61,
  461,    0,   -2,    0,    0,   98,  -71,  -36,    0,    0,
  -59,   -7,   95,  -38,  178,  490,   19,    0,    0, -130,
  230,    0,    0,    0,    0,    0,    0,    0,    0,  103,
  111,   98,  112,  124,  207,  236,   -6,   77,  -89, -162,
  -22,   55,    0,   15,  554,    0,    0,    0,  262,    0,
   39,    0,  -98,   25, -162,  132,   49,    0,  293,  125,
  331,  355,    0,    0,  627,   61,  349,    0,   64,    0,
   17,    0,  108,    0,  141,    0,    0,    0,    0,    0,
    0,  388,    0,   98,    0,    0,   98,  133,    0,  547,
    0,   24,    0,  547,    0,    0,  130,    0,  130,  409,
    0,    0,    0,    0,  155,   26,    0,    0,   45,    0,
  142,  161,   -9,    0,  175,  303,  642,  653,    0,  301,
  181,  318,  184,  201,  653,  311,    0,  653,  204,  436,
  494,    0,    0,    0,    0,    0,   58,    0,    0,    0,
   19,    0,    0,    0,    0,  -36,    0,    0,    0,    0,
    0,  433,  489,  448,  227,    0,    0,  233,  176,    0,
    0,    0,    0,    0,    0,  463,    0,    0,    0,  646,
    0,  148,    0,  241,    0,    0,  243,    0,    0,    0,
  691,    0,  160,    0,   98,    0,    0,   98,  138,    0,
    0,    0,    0,    0,    0,    0,    0,    0,  450,  455,
    0,   66,    0,    0,    0,    0,    0,    0,  311,    0,
    0,   -1,   60,  478,   75,  568,    0,    0,    0,    0,
  364,    0,    0,    0,    0,   71,  467,    0,  311,    0,
  495,  504,  500,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  532,   98,   29,    0,    0,   61,  192,
    0,   52,    0,    0,    0,    0,    0,    0,    0,  269,
  232,  409,  -30,  -34,   17,    0,  193,  505,  492,    0,
    0,  615,   61,  498,    0,    0,    0,    0,  532,    0,
   88,    0,  532,  507,   98,    0,  499,    0,  512,    0,
    0,    0,   91,    0,  313,    0,    0,  319,  235,    0,
  409,    0,    0,    0,    0,  524,  530,    0,   92,    0,
    0,    0,    0,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,  481,    0,    0,
    0,    0,  593,   10,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   68,    0,
    0,    0,    0,  110,    0,    0,    0,    0,    0,    0,
    0,  378,    0,    0,    0,    0,  332,  508,    0,    0,
    0,    0,  420,    0,    0,    0,    0,    0,  -17,    0,
  598,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  508,    0,  400,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,  240,    0,    0,    0,
    0,  100,    0,    0,    0,    0,    0,    0,    0,    0,
  150,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  336,    0,    0,  440,    0,  462,  304,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,  308,    0,    0,    0,    0,  334,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  100,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
  154,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  339,
    0,    0,    0,  317,    0,    0,  329,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  -17,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  332,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   20,    0,    0,   48,  165,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  336,    0,    0,    0,    0,  180,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
   40,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,
};
final static short yygindex[] = {                         0,
  542,    0,    0,  595,   41,    0,  597,    0,  326, -123,
  473,  333, -124,    0,    0,    0,    0,    0, -142, -244,
  677,  418,    2,  -85,    0,  338,  -27,    0, -169,    0,
  544,  -68,    0,   -4,  -16,  309,  -83,    0,  601,  604,
  631,    4,    0,    0,    0,  431,    0,    0,    0,   11,
    0,    0,    1, -108,    0,    0,  264,  299,
};
final static int YYTABLESIZE=984;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         48,
   31,   31,  153,   28,  137,  164,  357,   83,  143,   11,
  356,   38,   31,  355,   31,   28,   75,   28,   40,   91,
  101,   98,  100,   99,   31,   67,  168,   28,   86,   66,
  110,  187,   60,  150,  312,   46,  217,  130,  108,  111,
   85,   76,   46,  168,  116,  242,   89,   31,   77,   93,
   28,  149,  178,   63,  326,  253,   56,   36,   31,  139,
  188,   28,   57,  188,   57,   78,  152,  207,  110,   31,
   55,   31,  107,  337,   28,  138,   46,   31,  146,   38,
  187,  144,  227,  187,  232,  170,  156,  140,  151,  203,
  203,  130,  203,  165,  203,  184,   46,   81,  183,   45,
  130,  118,  240,  235,   46,   46,  101,  265,  100,    6,
    7,  167,   80,  201,  316,   31,  262,  179,   28,  101,
   57,  100,  210,   35,  311,  200,  289,  288,  167,  324,
   46,  159,  225,  204,   11,   31,  229,  222,   28,   46,
  223,  170,   46,  213,  331,  146,  367,   46,  144,  375,
  385,  218,  219,  168,  221,   46,   46,   87,   57,   88,
   31,  289,  288,   28,   31,  228,  111,   28,   46,   46,
  168,  103,  196,    6,    7,  195,  104,   46,  233,  365,
  190,  180,   46,  369,  216,  143,   31,   31,  279,   28,
   28,  195,   92,  177,  177,  341,  129,  178,  178,  215,
  299,  257,   34,  195,  260,  152,  199,  199,  177,  199,
  263,  199,  178,   74,  148,  264,  146,  362,  155,  144,
  153,  200,  200,   82,  200,  354,  200,   94,   95,   96,
   97,  274,  275,   39,  273,  276,  180,  141,  301,  142,
   31,  302,  170,   28,  254,  353,  109,  175,  182,  177,
  339,   31,  168,  344,  296,  300,  136,    6,    7,   65,
   32,   33,  304,   98,   42,   99,   43,  305,  306,  307,
   44,   42,   34,   43,  363,  188,  176,   44,   48,  226,
   48,  231,   31,   48,  334,  185,   31,  105,  163,  296,
  352,   31,   38,  380,  296,  187,    6,    7,  332,  111,
  234,  188,  335,  192,  336,   42,  106,   43,   44,   79,
  343,   44,  189,  261,   31,  330,  340,  296,  203,  209,
   31,  310,  197,  296,  345,   42,  323,   43,  346,  347,
  348,   44,  198,   42,   42,   43,   43,   89,  371,   44,
   44,   84,   31,  366,  104,  296,  374,  384,  141,   31,
  151,  141,  296,   31,  368,   57,  296,   49,  166,   42,
   49,   43,  104,  167,  169,   44,  168,  171,   42,   50,
   43,   42,   50,   43,   44,  381,   42,   44,   43,  173,
  199,  211,   44,  212,   42,   42,   43,   43,  224,  202,
   44,   44,  154,  303,  158,  203,  214,   42,   42,   43,
   43,  172,  174,   44,   44,  177,   42,  208,   43,  178,
  230,   42,   44,   43,  141,  236,  142,   44,  203,  203,
  203,  203,  203,  177,  203,  177,  104,  178,  220,  178,
  101,  271,  100,  251,  237,  199,  203,  203,  272,  203,
  204,  204,  204,  204,  204,    9,  204,   69,  239,    3,
  200,  101,  283,  100,  245,    5,  284,  248,  204,  204,
  193,  204,  193,  193,  193,  285,   87,    9,  358,   94,
   95,   96,   97,  266,  249,  101,  256,  100,  193,  193,
  190,  193,  190,  190,  190,  161,  320,  350,  268,  269,
  378,  159,  162,  159,  351,  270,   72,  379,  190,  190,
  203,  190,  189,  277,  189,  189,  189,  255,  308,  259,
    1,    2,    3,  309,  280,    4,  281,  315,    5,   72,
  189,  189,  204,  189,  168,  325,    6,    7,   59,  267,
  349,  101,  101,  100,  100,  327,  101,  101,  100,  100,
  329,  168,  193,  134,  328,  359,  101,  370,  100,   98,
  360,   99,  101,   98,  100,   99,  364,  372,   69,  104,
    3,  104,  190,    4,  104,  104,    5,  104,  104,   98,
  373,   99,    6,    7,  243,  376,   59,  104,    1,    2,
    3,  377,  382,    4,  189,  133,    5,  134,  383,    6,
    7,  246,    8,   57,    6,    7,    8,    5,   57,  117,
    1,    2,    3,    1,  147,    4,   61,  206,    5,  146,
   64,  318,  250,  238,  313,    0,    6,    7,   59,   69,
  314,    3,  319,    0,  283,    0,    0,    5,  284,    0,
   57,    0,   72,  203,    0,  203,   72,  285,  203,  203,
    0,  203,  203,  203,  203,  203,  203,   71,  203,    0,
    0,  203,    0,    0,  320,  204,    0,  204,  134,  206,
  204,  204,    0,  204,  204,  204,  204,  204,  204,   71,
  204,    0,    0,  204,    0,  193,  186,  193,    0,    0,
  193,  193,    0,  193,  193,  193,  193,  193,  193,    0,
  193,    0,  317,  193,    0,  190,   68,  190,    0,    0,
  190,  190,    0,  190,  190,  190,  190,  190,  190,    0,
  190,   60,  206,  190,    0,    0,   69,  189,    3,  189,
    0,    4,  189,  189,    5,  189,  189,  189,  189,  189,
  189,    0,  189,    0,   59,  189,    1,    1,    1,  361,
    0,    1,  120,  127,    1,  157,    0,    0,    0,  258,
  168,  205,    1,    1,    1,   94,   95,   96,   97,   94,
   95,   96,   97,   57,    0,   57,  241,    0,   57,   57,
  278,   57,   57,    0,    0,   94,   95,   96,   97,    0,
   69,   57,    3,   70,    0,    4,  181,   69,    5,    3,
    0,    0,  283,    0,  291,    5,  284,    0,   59,  191,
  193,  194,   69,    0,    3,  285,    0,    4,    0,   69,
    5,    3,    0,    0,    4,    0,    0,    5,    0,    0,
   59,    0,    0,  282,    2,    3,    0,   59,  283,  291,
    0,    5,  284,    0,  291,    0,    0,    0,    0,    6,
    7,  285,    1,    2,    3,    0,    0,    4,    0,    0,
    5,  293,    0,    0,  294,    0,    0,  291,    6,    7,
   59,    0,    0,  291,  127,    0,  244,    0,  247,    0,
   69,  127,    3,    0,  127,  283,    0,    0,    5,  284,
    0,  295,   69,    0,    3,  291,  293,    4,  285,  294,
    5,  293,  291,    0,  294,    0,  291,   69,    0,    3,
   59,   69,    4,    3,    0,    5,    4,    0,    0,    5,
    0,    0,  121,    0,  293,   59,  295,  294,    0,   59,
  293,  295,    0,  294,    6,    7,  122,    0,    0,  123,
  124,  125,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  293,    0,  295,  294,  282,    2,    3,  293,
  295,  283,  294,  293,    5,  284,  294,    0,    0,    0,
    0,    0,    6,    7,  285,    0,    0,    0,    0,    0,
    0,    0,  295,    0,    0,    0,    0,    0,    0,  295,
    0,    0,    0,  295,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                          4,
    0,    1,   41,    0,   73,   91,   41,   40,   45,    0,
   41,    1,   12,   44,   14,   12,   59,   14,   59,   47,
   43,   60,   45,   62,   24,   40,   44,   24,   45,   44,
   40,  115,  125,   41,  279,   45,  145,   40,   55,   56,
   45,  270,   45,   61,  274,  188,   46,   47,   44,   48,
   47,   59,   59,   13,  299,  198,   40,   59,   58,   76,
   41,   58,   46,   44,   46,   61,   83,  136,   40,   69,
   40,   71,   41,   45,   71,   74,   45,   77,   78,   69,
   41,   78,   59,   44,   59,  102,   85,   77,   41,   42,
   43,   40,   45,   92,   47,   41,   45,   44,   44,   40,
   40,   61,  186,   59,   45,   45,   43,  216,   45,  272,
  273,   44,   59,  130,   40,  115,   59,   41,  115,   43,
   46,   45,   59,  125,   59,  130,  251,  251,   61,   59,
   45,  262,  160,  132,  125,  135,  164,  154,  135,   45,
  157,  158,   45,  143,  314,  145,   59,   45,  145,   59,
   59,  150,  151,   44,  153,   45,   45,  274,   59,  276,
  160,  286,  286,  160,  164,  164,  183,  164,   45,   45,
   61,   42,   41,  272,  273,   44,   47,   45,  177,  349,
  279,  271,   45,  353,   44,   45,  186,  187,   41,  186,
  187,   44,   41,   44,   45,  320,  274,   44,   45,   59,
   41,  200,  274,   44,  203,   41,   42,   43,   59,   45,
  209,   47,   59,  256,  274,  214,  216,  342,   41,  216,
   41,   42,   43,  256,   45,  256,   47,  266,  267,  268,
  269,  230,  231,  274,   59,  234,  271,  274,  255,  276,
  240,  258,  259,  240,   41,  331,  256,   41,  271,  256,
  319,  251,  270,  322,  251,  254,  259,  272,  273,  274,
  262,  263,  261,   60,  274,   62,  276,  266,  267,  268,
  280,  274,  274,  276,  343,  256,   41,  280,  283,  256,
   41,  256,  282,   44,  256,  271,  286,  256,   59,  286,
   59,  291,  282,   59,  291,  256,  272,  273,  315,  316,
  256,   40,  274,  279,  276,  274,  275,  276,  280,  256,
  259,  280,  274,  256,  314,  314,  125,  314,  271,  256,
  320,  256,  274,  320,  323,  274,  256,  276,  327,  328,
  329,  280,   40,  274,  274,  276,  276,  337,  355,  280,
  280,  256,  342,  256,   41,  342,  256,  256,   41,  349,
  256,   44,  349,  353,  353,  256,  353,   41,  256,  274,
   44,  276,   59,  100,  101,  280,  256,  256,  274,   41,
  276,  274,   44,  276,  280,  374,  274,  280,  276,  256,
  256,  274,  280,  276,  274,  274,  276,  276,  256,   59,
  280,  280,   84,  256,   86,   41,  256,  274,  274,  276,
  276,  103,  104,  280,  280,  256,  274,   59,  276,  256,
  256,  274,  280,  276,  274,  274,  276,  280,   41,   42,
   43,   44,   45,  274,   47,  276,  123,  274,   41,  276,
   43,  256,   45,  123,  274,  271,   59,   60,  263,   62,
   41,   42,   43,   44,   45,  123,   47,  256,  274,  258,
  271,   43,  261,   45,  274,  264,  265,  274,   59,   60,
   41,   62,   43,   44,   45,  274,  274,  123,  276,  266,
  267,  268,  269,   41,  274,   43,   41,   45,   59,   60,
   41,   62,   43,   44,   45,  256,  123,  256,   41,  263,
  256,  262,  263,  262,  263,  263,   24,  263,   59,   60,
  123,   62,   41,   41,   43,   44,   45,  199,   59,  201,
  256,  257,  258,   59,  274,  261,  274,   40,  264,   47,
   59,   60,  123,   62,   44,   59,  272,  273,  274,   41,
  262,   43,   43,   45,   45,   41,   43,   43,   45,   45,
   41,   61,  123,   71,   41,   41,   43,   41,   45,   60,
   59,   62,   43,   60,   45,   62,   59,   59,  256,  256,
  258,  258,  123,  261,  261,  262,  264,  264,  265,   60,
   59,   62,  272,  273,  274,  263,  274,  274,  256,  257,
  258,  263,   59,  261,  123,  125,  264,  115,   59,  272,
  273,  274,    0,  262,  272,  273,  274,    0,  263,   58,
  256,  257,  258,  123,  271,  261,   12,  135,  264,  271,
   14,  286,  195,  183,  282,   -1,  272,  273,  274,  256,
  283,  258,  259,   -1,  261,   -1,   -1,  264,  265,   -1,
  123,   -1,  160,  256,   -1,  258,  164,  274,  261,  262,
   -1,  264,  265,  266,  267,  268,  269,  123,  271,   -1,
   -1,  274,   -1,   -1,  123,  256,   -1,  258,  186,  187,
  261,  262,   -1,  264,  265,  266,  267,  268,  269,  123,
  271,   -1,   -1,  274,   -1,  256,  123,  258,   -1,   -1,
  261,  262,   -1,  264,  265,  266,  267,  268,  269,   -1,
  271,   -1,  125,  274,   -1,  256,   20,  258,   -1,   -1,
  261,  262,   -1,  264,  265,  266,  267,  268,  269,   -1,
  271,  125,  240,  274,   -1,   -1,  256,  256,  258,  258,
   -1,  261,  261,  262,  264,  264,  265,  266,  267,  268,
  269,   -1,  271,   -1,  274,  274,  256,  257,  258,  125,
   -1,  261,   66,   67,  264,  256,   -1,   -1,   -1,  256,
  270,  125,  272,  273,  274,  266,  267,  268,  269,  266,
  267,  268,  269,  256,   -1,  258,  125,   -1,  261,  262,
  125,  264,  265,   -1,   -1,  266,  267,  268,  269,   -1,
  256,  274,  258,  259,   -1,  261,  110,  256,  264,  258,
   -1,   -1,  261,   -1,  251,  264,  265,   -1,  274,  123,
  124,  125,  256,   -1,  258,  274,   -1,  261,   -1,  256,
  264,  258,   -1,   -1,  261,   -1,   -1,  264,   -1,   -1,
  274,   -1,   -1,  256,  257,  258,   -1,  274,  261,  286,
   -1,  264,  265,   -1,  291,   -1,   -1,   -1,   -1,  272,
  273,  274,  256,  257,  258,   -1,   -1,  261,   -1,   -1,
  264,  251,   -1,   -1,  251,   -1,   -1,  314,  272,  273,
  274,   -1,   -1,  320,  188,   -1,  190,   -1,  192,   -1,
  256,  195,  258,   -1,  198,  261,   -1,   -1,  264,  265,
   -1,  251,  256,   -1,  258,  342,  286,  261,  274,  286,
  264,  291,  349,   -1,  291,   -1,  353,  256,   -1,  258,
  274,  256,  261,  258,   -1,  264,  261,   -1,   -1,  264,
   -1,   -1,  260,   -1,  314,  274,  286,  314,   -1,  274,
  320,  291,   -1,  320,  272,  273,  274,   -1,   -1,  277,
  278,  279,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  342,   -1,  314,  342,  256,  257,  258,  349,
  320,  261,  349,  353,  264,  265,  353,   -1,   -1,   -1,
   -1,   -1,  272,  273,  274,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  342,   -1,   -1,   -1,   -1,   -1,   -1,  349,
   -1,   -1,   -1,  353,
};
}
final static short YYFINAL=10;
final static short YYMAXTOKEN=280;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,"'('","')'","'*'","'+'","','",
"'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,null,"';'",
"'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
"'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"VAR","DO","WHILE","LAMBDA","IF","ELSE",
"ENDIF","PRINT","RETURN","MENOR_IGUAL","MAYOR_IGUAL","DISTINTO","IGUAL",
"ASIGNACION_SIMPLE","FLECHITA","UINT","DFLOAT","ID","CADENA","CTE","CV","CR",
"LE","TOD",
};
final static String yyrule[] = {
"$accept : programa",
"$$1 :",
"programa : ID $$1 bloque",
"programa : bloque",
"bloque : delimitador_inicio cuerpo delimitador_fin",
"bloque : delimitador_inicio cuerpo",
"bloque : delimitador_inicio delimitador_fin",
"bloque : cuerpo delimitador_fin",
"bloque : cuerpo",
"delimitador_inicio : '{'",
"delimitador_fin : '}'",
"cuerpo : lista_sentencias",
"lista_sentencias : lista_sentencias sentencia",
"lista_sentencias : sentencia",
"lista_sentencias_fun : lista_sentencias_fun sentencia_fun",
"lista_sentencias_fun : sentencia_fun",
"sentencia : declarativa",
"sentencia : ejecutable",
"sentencia : error sincronizador",
"sentencia_fun : declarativa",
"sentencia_fun : ejecutable_fun",
"sentencia_fun : error sincronizador",
"sincronizador : ENDIF",
"sincronizador : '}'",
"sincronizador : ELSE",
"sincronizador : ';'",
"declarativa : decl_funcion",
"declarativa : decl_reserva_nombres",
"decl_reserva_nombres : VAR lista_ids ';'",
"decl_reserva_nombres : VAR ';'",
"lista_ids : lista_ids ',' ID",
"lista_ids : ID",
"lista_ids : lista_ids error",
"$$2 :",
"decl_funcion : tipo_lista ID $$2 '(' lista_parametros_formales ')' bloque_funcion",
"decl_funcion : tipo_lista tipo ID '(' lista_parametros_formales ')' bloque_funcion",
"decl_funcion : tipo_lista '(' lista_parametros_formales ')' bloque_funcion",
"bloque_funcion : '{' lista_sentencias_fun '}'",
"tipo_lista : tipo_lista ',' tipo",
"tipo_lista : tipo",
"tipo : UINT",
"tipo : DFLOAT",
"lista_parametros_formales : lista_parametros_formales ',' parametro_formal",
"lista_parametros_formales : parametro_formal",
"parametro_formal : tipo ID",
"parametro_formal : CV LE tipo ID",
"parametro_formal : CR LE tipo ID",
"parametro_formal : LAMBDA ID",
"parametro_formal : tipo",
"parametro_formal : CV LE tipo",
"parametro_formal : CR LE tipo",
"parametro_formal : ID",
"parametro_formal : CV LE ID",
"parametro_formal : CR LE ID",
"parametro_formal : CV tipo ID",
"parametro_formal : CR tipo ID",
"parametro_formal : LE tipo ID",
"marca_fin :",
"marca_else : ELSE",
"sentencia_if : IF sentencia_control bloque_ejecutable marca_else bloque_ejecutable ENDIF ';'",
"sentencia_if : IF sentencia_control bloque_ejecutable marca_else bloque_ejecutable ';'",
"sentencia_if : IF sentencia_control marca_fin marca_else bloque_ejecutable ENDIF ';'",
"sentencia_if : IF sentencia_control bloque_ejecutable marca_else marca_fin ENDIF ';'",
"sentencia_if : IF sentencia_control bloque_ejecutable marca_else bloque_ejecutable ENDIF error",
"sentencia_if : IF sentencia_control bloque_ejecutable marca_else bloque_ejecutable error",
"sentencia_if : IF sentencia_control bloque_ejecutable ENDIF ';'",
"sentencia_if : IF sentencia_control bloque_ejecutable ';'",
"sentencia_if : IF sentencia_control bloque_ejecutable ENDIF error",
"sentencia_if : IF sentencia_control bloque_ejecutable error",
"sentencia_if_fun : IF sentencia_control bloque_ejecutable_fun marca_else bloque_ejecutable_fun ENDIF ';'",
"sentencia_if_fun : IF sentencia_control bloque_ejecutable_fun marca_else bloque_ejecutable_fun ';'",
"sentencia_if_fun : IF sentencia_control marca_fin ELSE bloque_ejecutable_fun ENDIF ';'",
"sentencia_if_fun : IF sentencia_control bloque_ejecutable_fun marca_else marca_fin ENDIF ';'",
"sentencia_if_fun : IF sentencia_control bloque_ejecutable_fun marca_else bloque_ejecutable_fun ENDIF error",
"sentencia_if_fun : IF sentencia_control bloque_ejecutable_fun marca_else bloque_ejecutable_fun error",
"sentencia_if_fun : IF sentencia_control bloque_ejecutable_fun ENDIF ';'",
"sentencia_if_fun : IF sentencia_control bloque_ejecutable_fun ';'",
"sentencia_if_fun : IF sentencia_control bloque_ejecutable_fun ENDIF error",
"sentencia_if_fun : IF sentencia_control bloque_ejecutable_fun error",
"sentencia_do_while : marca_do bloque_ejecutable WHILE cond_while ';'",
"sentencia_do_while : marca_do bloque_ejecutable cond_while ';'",
"sentencia_do_while : marca_do WHILE cond_while ';'",
"sentencia_do_while : marca_do bloque_ejecutable WHILE cond_while error marca_fin",
"sentencia_do_while_fun : marca_do bloque_ejecutable_fun WHILE cond_while ';'",
"sentencia_do_while_fun : marca_do bloque_ejecutable_fun cond_while ';'",
"sentencia_do_while_fun : marca_do WHILE cond_while ';'",
"sentencia_do_while_fun : marca_do bloque_ejecutable_fun WHILE cond_while error marca_fin",
"marca_do : DO",
"sentencia_control : '(' condicion ')'",
"sentencia_control : '(' condicion marca_fin",
"sentencia_control : condicion ')' marca_fin",
"sentencia_control : condicion marca_fin",
"sentencia_control : '(' expr error expr ')' marca_fin",
"sentencia_control : '(' error ')' marca_fin",
"sentencia_control : '(' error comparador expr ')' marca_fin",
"sentencia_control : '(' expr comparador error ')' marca_fin",
"cond_while : '(' condicion ')'",
"cond_while : '(' condicion marca_fin",
"cond_while : condicion ')' marca_fin",
"cond_while : condicion marca_fin",
"cond_while : '(' expr error expr ')' marca_fin",
"cond_while : '(' error ')' marca_fin",
"cond_while : '(' error comparador expr ')' marca_fin",
"cond_while : '(' expr comparador error ')' marca_fin",
"condicion : expr comparador expr",
"comparador : MENOR_IGUAL",
"comparador : MAYOR_IGUAL",
"comparador : DISTINTO",
"comparador : IGUAL",
"comparador : '<'",
"comparador : '>'",
"bloque_ejecutable : ejecutable",
"bloque_ejecutable : '{' lista_ejecutables '}'",
"bloque_ejecutable : '{' '}'",
"lista_ejecutables : lista_ejecutables ejecutable",
"lista_ejecutables : ejecutable",
"bloque_ejecutable_fun : ejecutable_fun",
"bloque_ejecutable_fun : '{' lista_ejecutables_fun '}'",
"bloque_ejecutable_fun : '{' '}'",
"lista_ejecutables_fun : lista_ejecutables_fun ejecutable_fun",
"lista_ejecutables_fun : ejecutable_fun",
"ejecutable : asignacion_simple",
"ejecutable : asignacion_multiple",
"ejecutable : sentencia_if",
"ejecutable : sentencia_do_while",
"ejecutable : llamado_print",
"ejecutable : invocacion_funcion ';'",
"ejecutable : invocacion_funcion error marca_fin",
"ejecutable_fun : asignacion_simple",
"ejecutable_fun : asignacion_multiple",
"ejecutable_fun : sentencia_if_fun",
"ejecutable_fun : sentencia_do_while_fun",
"ejecutable_fun : llamado_print",
"ejecutable_fun : invocacion_funcion ';'",
"ejecutable_fun : invocacion_funcion error marca_fin",
"ejecutable_fun : retorno ';'",
"ejecutable_fun : invocacion_lambda_param",
"invocacion_funcion : ID '(' lista_parametros_reales ')'",
"lista_parametros_reales : lista_parametros_reales ',' parametro_real",
"lista_parametros_reales : parametro_real",
"parametro_real : expr FLECHITA ID",
"parametro_real : expr FLECHITA",
"parametro_real : error FLECHITA",
"parametro_real : lambda_expr FLECHITA ID",
"lambda_expr : lambda_header '{' lista_ejecutables '}'",
"lambda_expr : lambda_header lista_ejecutables '}'",
"lambda_expr : lambda_header '{' lista_ejecutables",
"lambda_expr : lambda_header lista_ejecutables",
"lambda_header : '(' tipo ID ')'",
"invocacion_lambda_param : ID '(' arg_lambda ')' ';'",
"invocacion_lambda_param : ID '(' error ')' ';'",
"arg_lambda : ID",
"arg_lambda : CTE",
"arg_lambda : '-' CTE",
"llamado_print : PRINT '(' CADENA ')' ';'",
"llamado_print : PRINT '(' expr ')' ';'",
"llamado_print : PRINT '(' ')' ';'",
"llamado_print : PRINT '(' CADENA ')' error marca_fin",
"llamado_print : PRINT '(' expr ')' error marca_fin",
"llamado_print : PRINT '(' ')' error marca_fin",
"llamado_print : PRINT '(' error ')' error marca_fin",
"asignacion_simple : variable ASIGNACION_SIMPLE expr ';'",
"asignacion_simple : variable ASIGNACION_SIMPLE expr error marca_fin",
"asignacion_multiple : lista_variables_izq '=' lista_elem_lado_derecho ';'",
"asignacion_multiple : lista_variables_izq '=' lista_elem_lado_derecho error marca_fin",
"lista_variables_izq : lista_variables_izq ',' variable",
"lista_variables_izq : error variable",
"lista_variables_izq : variable",
"variable : ID",
"variable : prefijado_var",
"lista_elem_lado_derecho : lista_elem_lado_derecho ',' elem_lado_derecho",
"lista_elem_lado_derecho : lista_elem_lado_derecho elem_lado_derecho",
"lista_elem_lado_derecho : elem_lado_derecho",
"elem_lado_derecho : CTE",
"elem_lado_derecho : '-' CTE",
"elem_lado_derecho : '-' prefijado_var",
"elem_lado_derecho : prefijado_var",
"elem_lado_derecho : ID",
"elem_lado_derecho : '-' ID",
"elem_lado_derecho : invocacion_funcion",
"prefijado_var : ID '.' ID",
"conv_uint_a_dfloat : TOD '(' expr ')'",
"conv_uint_a_dfloat : TOD '(' error marca_fin",
"conv_uint_a_dfloat : TOD error ')' marca_fin",
"conv_uint_a_dfloat : TOD error ';'",
"retorno : RETURN '(' lista_expr ')'",
"retorno : RETURN '(' lista_expr error ')'",
"lista_expr : lista_expr ',' expr",
"lista_expr : expr",
"expr : expr '+' termino",
"expr : expr '-' termino",
"expr : expr '+' error",
"expr : expr '-' error",
"expr : termino",
"termino : termino '*' factor",
"termino : termino '/' factor",
"termino : termino '*' error",
"termino : termino '/' error",
"termino : factor",
"factor : CTE",
"factor : '-' CTE",
"factor : '-' prefijado_var",
"factor : prefijado_var",
"factor : ID",
"factor : '-' ID",
"factor : invocacion_funcion",
"factor : conv_uint_a_dfloat",
};

//#line 710 "gramatica.y"


private static int lastTok = -1;
private static String lastLex = "";
private static int lineaInicioPrograma = -1;
private static int lineaUltimoTokenValido = -1;
private static int lineaPrimerSentencia = -1;
private static String ambito = "";
private static final Deque<String> claveFuncionEnDeclActual = new ArrayDeque<>();
private static final List<String> polaca = new ArrayList<>();
private static final List<String> variablesIzquierda = new ArrayList<>();
private static final List<String> variablesDerecha = new ArrayList<>();
private static final Deque<Integer> pilaIF = new ArrayDeque<>();
private static final Deque<Integer> pilaDO = new ArrayDeque<>();
private static final List<String> listaReturn = new ArrayList<>();
private static final List<String> tiposDerecha = new ArrayList<>();
private static final boolean RET_ANOTADO = true;
private static String claveFunInvocada = null;        // p.ej. "F:MAIN:FUN"
private static final Deque<Boolean> pilaAperturaFuncion = new ArrayDeque<>();
private static final List<String> _formalesTmp = new ArrayList<>();
private static final List<String> _clasesTmp   = new ArrayList<>(); 
private static final List<String> _tiposRealesTmp = new ArrayList<>();
private static boolean _recolectando = false;
private static ArrayList<String> nombresFormalesParams = new ArrayList<>();
private static ArrayList<Integer> posicionesDestinoEntrada = new ArrayList<>();
private static ArrayList<String> lexemasRealesParams = new ArrayList<>();
private static ArrayList<Integer> esLValueReales = new ArrayList<>();
private static final Deque<Integer> pilaInicioLambda = new ArrayDeque<>();
private static final List<int[]> _rangosLambdaTmp = new ArrayList<>();
private static ArrayList<String> listaRetornosTipos = new ArrayList<>();
private static String ultimoLambdaArgVar = null;       
private static final List<String> _argVarLambdaTmp = new ArrayList<>(); // por parametro real LAMBDA
public static final String TIPO_ERROR = "ERROR";



private static void registrarParametroReal(String nombreFormal, String lexemaReal, int esLValue, int idxDestinoEntrada) {
    nombresFormalesParams.add(nombreFormal);
    lexemasRealesParams.add(lexemaReal);
    esLValueReales.add(esLValue);
    posicionesDestinoEntrada.add(idxDestinoEntrada);
}

private static int indiceActualPolaca() {
    return polaca.size();  
}

private static List<String> obtenerModosParametros(String claveFun) {
    TDSObject fun = tablaDeSimbolos.get(claveFun);
    if (fun == null) return null;
    return fun.getSemanticaParametros();    
}

private static void validarLValuesSegunSemantica(String claveFun) {
    List<String> modos = obtenerModosParametros(claveFun);
    if (modos == null) return;

    int n = Math.min(modos.size(), esLValueReales.size());

    for (int i = 0; i < n; i++) {
        String modo = modos.get(i);          // "CV", "CVR", "CR"
        int esLV    = esLValueReales.get(i); // 1 o 0

        // Para CVR y CR el real DEBE ser l-value (algo con memoria)
        if ((modo.equals("CVR") || modo.equals("CR")) && esLV == 0) {
            String formal = nombresFormalesParams.get(i);
            logErr("ERROR - En parámetro formal '" + formal +
                   "' con modo " + modo +
                   ", el parámetro real debe ser una variable (no una expresión).");
        }
    }
}

private static void remapearEntradaSegunSemantica(String claveFun) {
    String scopeParam = scopeParametrosDeFuncion(claveFun);  // ":MAIN:F"
    if (scopeParam == null) return;

    List<String> modos = obtenerModosParametros(claveFun);
    if (modos == null) return;

    int n = Math.min(modos.size(), nombresFormalesParams.size());

    for (int i = 0; i < n; i++) {
        String modo      = modos.get(i);               
        String formal    = nombresFormalesParams.get(i);
        int idxDestino   = posicionesDestinoEntrada.get(i);

        if (modo.equals("CR")) {
            // CR no hay copia de entrada -> borramos la asignación inicial
            // en polaca: [ idxDestino ] = placeholder, [idxDestino+1] = ":="
            polaca.set(idxDestino, "");   
            polaca.set(idxDestino + 1, ""); 
        } else {
            String kFormal = formal + scopeParam;  
            polaca.set(idxDestino, kFormal);      
        }
    }
}

private static void generarCopiasSalidaSegunSemantica(String claveFun) {
    String scopeParam = scopeParametrosDeFuncion(claveFun);  // ":MAIN:F"
    if (scopeParam == null) return;

    List<String> modos = obtenerModosParametros(claveFun);
    if (modos == null) return;

    int n = Math.min(modos.size(), nombresFormalesParams.size());

    for (int i = 0; i < n; i++) {
        String modo = modos.get(i);                 // "CV", "CVR", "CR"
        String formal = nombresFormalesParams.get(i); // "X"
        String real = lexemasRealesParams.get(i);   // "A:MAIN" (si es lvalue)

        if (modo.equals("CVR") || modo.equals("CR")) {
            String kFormal = formal + scopeParam;        // "X:MAIN:F"

            // Polaca copia formal al real
            emitir(kFormal);
            emitir(real);
            emitir(":=");
        }
    }
}


private static void iniciarRecoleccionParams() {
    _formalesTmp.clear();
    _clasesTmp.clear();
    _tiposRealesTmp.clear();
    _rangosLambdaTmp.clear();
    _argVarLambdaTmp.clear();
    _recolectando = true;
}
private static void limpiarRecoleccionParams() {
    _formalesTmp.clear();
    _clasesTmp.clear();
    _tiposRealesTmp.clear();
    _rangosLambdaTmp.clear();
    nombresFormalesParams.clear();
    lexemasRealesParams.clear();
    esLValueReales.clear();
    posicionesDestinoEntrada.clear();
    _argVarLambdaTmp.clear();
    _recolectando = false;
}

private static void agregarParamRealExpr(String nombreFormal, String tipoReal) {
    if (!_recolectando) iniciarRecoleccionParams();
    _clasesTmp.add("EXPR");
    _formalesTmp.add(nombreFormal);
    _tiposRealesTmp.add(tipoReal);
    _rangosLambdaTmp.add(null);
}

private static void agregarParamRealLambda(String nombreFormal, int[] rangoLambda, String argVar) {
    if (!_recolectando) iniciarRecoleccionParams();
    _clasesTmp.add("LAMBDA");
    _formalesTmp.add(nombreFormal);
    _tiposRealesTmp.add("LAMBDA");
    _rangosLambdaTmp.add(rangoLambda);
    _argVarLambdaTmp.add(argVar);
}


private static String scopeParametrosDeFuncion(String claveFun /* ej "F:MAIN" */) {
    int i = claveFun.indexOf(':');
    if (i < 0) return null;
    String nombreFun  = claveFun.substring(0, i);   // "F"
    String scopePadre = claveFun.substring(i);      // ":MAIN"
    return scopePadre + ":" + nombreFun;            // ":MAIN:F"
}

private static TDSObject getFormal(String claveFun, String nombreFormal) {
    String scopeParam = scopeParametrosDeFuncion(claveFun);
    if (scopeParam == null) return null;
    String k = nombreFormal + scopeParam;           // "X:MAIN:F"
    return tablaDeSimbolos.get(k);
}


private static void validarParametrosRealesContra(String claveFun) {
    // conjunto de formales declarados (por nombre) en la funcion
    final String scopeParam = scopeParametrosDeFuncion(claveFun); // ":MAIN:F"
    Set<String> declarados = new LinkedHashSet<>();
    for (Map.Entry<String,TDSObject> e : tablaDeSimbolos.entrySet()) {
        String k = e.getKey(); 
        TDSObject o = e.getValue();
        if (!"Parametro".equals(o.getUso())) continue;
        if (!k.endsWith(scopeParam)) continue;
        int idx = k.indexOf(':');           // toma nombre formal al inicio
        String nombreFormal = (idx > 0) ? k.substring(0, idx) : k;
        declarados.add(nombreFormal);
    }

    // validar mapeos uno por uno
    Set<String> usados = new HashSet<>();
    for (int i = 0; i < _formalesTmp.size(); i++) {
        String formal = _formalesTmp.get(i);
        String clase  = _clasesTmp.get(i); // "EXPR" | "LAMBDA"
        String tipoReal = _tiposRealesTmp.get(i);
        int[] rango = (i < _rangosLambdaTmp.size()) ? _rangosLambdaTmp.get(i) : null;
        String argVar = (i < _argVarLambdaTmp.size()) ? _argVarLambdaTmp.get(i) : null;
        TDSObject p = getFormal(claveFun, formal);
        if (p == null || !"Parametro".equals(p.getUso())) {
            logErr("ERROR - Parámetro formal no declarado o fuera de alcance: '" + formal + "'.");
            continue;
        }
        if (!usados.add(formal)) {
            logErr("ERROR - Parámetro formal '" + formal + "' mapeado más de una vez.");
        }
        String tipoFormal = p.getTipoVariable(); // "LAMBDA","UINT","DFLOAT",...
        if ("LAMBDA".equals(tipoFormal) && !"LAMBDA".equals(clase)) {
            logErr("ERROR - Al parámetro LAMBDA '" + formal + "' se le pasó una expresión.");
        }
        if (!"LAMBDA".equals(tipoFormal) && "LAMBDA".equals(clase)) {
            logErr("ERROR - Se pasó una LAMBDA al parámetro no-LAMBDA '" + formal + "'.");
        }
        if (tipoReal != null && !tipoReal.equals(tipoFormal)) {
            logErr("ERROR - El parámetro real asociado a '" + formal +
                   "' es de tipo " + tipoReal + " pero el parámetro formal es de tipo " +
                   tipoFormal + ".");
        }
        if ("LAMBDA".equals(tipoFormal) && "LAMBDA".equals(clase) && rango != null) {
            if (scopeParam != null) {
                String kParam = formal + scopeParam;      // "X:MAIN:F"
                int posStart = rango[0];
                int posEnd   = rango[1];
                polaca.set(posStart, "LAMBDA_START:" + kParam);
                polaca.set(posEnd,   "LAMBDA_END:"   + kParam);
                if (argVar != null) {
                    String callTok = "CALL_LAMBDA:" + kParam;

                    for (int idx = 0; idx < polaca.size(); idx++) {
                        if (callTok.equals(polaca.get(idx))) {
                            polaca.add(idx, argVar); 
                            polaca.add(idx + 1, ":="); 
                            idx += 2; // salteo lo que acabo de insertar
                        }
                    }
                }
            }
        }
    }

    for (String f : declarados) {
        if (!usados.contains(f)) {
            logErr("ERROR - Falta mapear el parámetro formal '" + f + "'.");
        }
    }
}


private static void setTipoParametroFormal(String nombreFormal, String tipo) {
    String funActual = getClaveFuncionEnDeclActual(); //"F:MAIN"
    if (funActual == null) return;

    String nombreFun = funActual.substring(0, funActual.indexOf(':'));     // "F"
    String scopePadre = funActual.substring(funActual.indexOf(':'));       // ":MAIN"
    String scopeParam = scopePadre + ":" + nombreFun;                      // ":MAIN:F"

    String kParam = nombreFormal + scopeParam;                             // "X:MAIN:F"

    TDSObject p = tablaDeSimbolos.get(kParam);
    if (p != null && "Parametro".equals(p.getUso())) {
        p.setTipoVariable(tipo); // "LAMBDA", "UINT", "DFLOAT"
    }
}

private static boolean paramFormalEsLambda(String nombreFormal) {
    String funActual = getClaveFuncionEnDeclActual(); // "F:MAIN"
    if (funActual == null) return false;

    String nombreFun = funActual.substring(0, funActual.indexOf(':'));     // "F"
    String scopePadre = funActual.substring(funActual.indexOf(':'));       // ":MAIN"
    String scopeParam = scopePadre + ":" + nombreFun;                      // ":MAIN:F"

    String kParam = nombreFormal + scopeParam;                             // "X:MAIN:F"
    TDSObject p = tablaDeSimbolos.get(kParam);
    return p != null && "Parametro".equals(p.getUso()) && "LAMBDA".equals(p.getTipoVariable());
}

private static String devolverParamLambda(String nombreFormal) {
    String funActual = getClaveFuncionEnDeclActual(); // "F:MAIN"

    String nombreFun = funActual.substring(0, funActual.indexOf(':'));     // "F"
    String scopePadre = funActual.substring(funActual.indexOf(':'));       // ":MAIN"
    String scopeParam = scopePadre + ":" + nombreFun;                      // ":MAIN:F"

    String kParam = nombreFormal + scopeParam;                             // "X:MAIN:F"
    TDSObject p = tablaDeSimbolos.get(kParam);
    if( p != null && "Parametro".equals(p.getUso()) && "LAMBDA".equals(p.getTipoVariable()))
        return kParam;
    else
        return null;
}


private static boolean abrirDeclaracionFuncion(String nombreFun, List<String> tiposRet) {
    String ambitoPadre = ambito;
    String claveFun    = nombreFun + ambitoPadre;   

    boolean existiaAntes = tablaDeSimbolos.containsKey(claveFun);

    setAmbito(nombreFun, "Funcion");

    TDSObject f = tablaDeSimbolos.get(claveFun);
    boolean creadaAhora = (!existiaAntes) && (f != null) && "Funcion".equals(f.getUso());
    pilaAperturaFuncion.push(creadaAhora);

    if (!creadaAhora) {
        return false;
    }
    emitir("DECLARACION: "+ claveFun);
    setTiposRetornoFuncion(claveFun, tiposRet); 
    setClaveFuncionEnDeclActual(claveFun);
    registrarUnidad(nombreFun, ambitoPadre);
    ambito = ambitoPadre + ":" + nombreFun;
    return true;
}


private static void cerrarDeclaracionFuncion() {
    boolean abierta = !pilaAperturaFuncion.isEmpty() && pilaAperturaFuncion.pop();
    if (abierta) {
        eliminarUltimoAmbito();
        String funActual = getClaveFuncionEnDeclActual();
        clearClaveFuncionEnDeclActual();
        emitir("FIN DECLARACION: "+ funActual);
    }
}



private static void emitirRET(String kfun, int ordinal) {
    if (!RET_ANOTADO || kfun == null) { emitir("RET"); return; }
    emitir("RET#" + ordinal + "(" + kfun + ")");
}


private static void procesarAsignacionMultiple() {
    try {
        logInfo("Asignacion multiple reconocida (lista = lista).");

        final int ladoIzqCant = variablesIzquierda.size();
        final int ladoDerCant = contarElementosDerechaTotales();

        if (ladoDerCant < ladoIzqCant) {
            logErr("ERROR - Cantidad insuficiente de valores en el lado derecho: se esperan "
                    + ladoIzqCant + " y hay " + ladoDerCant + ".");
            return;
        }
        if (ladoDerCant > ladoIzqCant) {
            System.out.println("ENTRE WARNING");
            logWarn("WARNING - Exceso de valores en el lado derecho (" + ladoDerCant +
                    " vs " + ladoIzqCant + "), se descartan los sobrantes.");
        }

        int iL = 0;
        for (int jR = 0; jR < variablesDerecha.size() && iL < ladoIzqCant; jR++) {
            String elemDer = variablesDerecha.get(jR);
            String tipoDer = (jR < tiposDerecha.size()) ? tiposDerecha.get(jR) : null;

            if (elemDer.startsWith("CALL ")) {
                String kfun = elemDer.substring(5).trim();
                TDSObject f = tablaDeSimbolos.get(kfun);
                List<String> tiposRet = (f != null) ? f.getTiposRetorno() : null;
                int nret = (tiposRet != null && !tiposRet.isEmpty()) ? tiposRet.size() : 1;
                int k = Math.min(nret, ladoIzqCant - iL);
                for (int r = 1; r <= k; r++) {
                    emitirRET(kfun, r);
                    String varIzq = variablesIzquierda.get(iL++);
                    emitir(varIzq);
                    emitir(":=");
                    String tipoRet = (tiposRet != null && tiposRet.size() >= r) ? tiposRet.get(r-1) : null;
                    if (tipoRet != null) {
                        TDSObject v = tablaDeSimbolos.get(varIzq);
                        String tipoVar = v.getTipoVariable();
                        if (tipoVar == null) {
                            v.setTipoVariable(tipoRet);
                        } else if (!tipoVar.equals(tipoRet)) {
                            logErr("ERROR - Incompatibilidad de tipos en asignación múltiple: '" + varIzq +
                                   "' es de tipo " + tipoVar + " y se asigna valor de tipo " + tipoRet + ".");
                        }
                    }
                }
            } else {
                // constante o variable (con o sin signo)
                if (elemDer.endsWith("-")) {
                    String limpio = elemDer.substring(0, elemDer.length() - 1);
                    emitir(limpio);
                    emitir("-");
                } else {
                    emitir(elemDer);
                }
                String varIzq = variablesIzquierda.get(iL++);
                emitir(varIzq);
                emitir(":=");
                if (tipoDer != null) {
                    TDSObject v = tablaDeSimbolos.get(varIzq);
                    String tipoVar = v.getTipoVariable();
                    if (tipoVar == null) {
                        v.setTipoVariable(tipoDer);
                    } else if (!tipoVar.equals(tipoDer)) {
                        logErr("ERROR - Incompatibilidad de tipos en asignación múltiple: '" + varIzq +
                               "' es de tipo " + tipoVar + " y se asigna valor de tipo " + tipoDer + ".");
                    }
                }
            }

        }
    } finally {
        iniciarAsignacionMultiple();
    }
}



private static int contarElementosDerechaTotales() {
    int total = 0;
    for (String elem : variablesDerecha) {

        if (elem.startsWith("CALL ")) {
            String claveFun = elem.substring(5).trim();  // ej: "F:MAIN" (sin tags)

            TDSObject f = tablaDeSimbolos.get(claveFun);

            // si no existe o no es funcion, contamos 1 por defecto
            if (f == null || !"Funcion".equals(f.getUso())) {
                total += 1;
                continue;
            }

            // si existe una lista de retornos declarada, sumamos esa cantidad
            if (f.getTiposRetorno() != null && !f.getTiposRetorno().isEmpty()) {
                total += f.getTiposRetorno().size();
            } else {
                // si la funcion no declaro retornos explicitos, por convencion 1 retorno
                total += 1;
            }
        }
        else {
            // CTE, variables cuentan como 1
            total += 1;
        }
    }
    return total;
}


private static void iniciarAsignacionMultiple() {
    variablesIzquierda.clear();
    variablesDerecha.clear();
    tiposDerecha.clear();
}


//==========================================POLACA===================================================//
private static int reservarHueco() {
    polaca.add("aCompletar");
    return polaca.size() - 1;
}

private static void completarEn(int pos, int dest) {
    polaca.set(pos, String.valueOf(dest));
}

private static void emitir(String tok) {
    if (tok == null || tok.isEmpty()) return;
    polaca.add(tok);   
}


public static List<String> getPolaca() {
    return polaca;
}


//=====================================================================================================//
private static void eliminarUltimoAmbito(){
  int i = ambito.lastIndexOf(':');
  if (i != -1)
    ambito = ambito.substring(0, i);
}


private static void setClaveFuncionEnDeclActual(String clave) { claveFuncionEnDeclActual.push(clave); }

private static void clearClaveFuncionEnDeclActual() {
    if (!claveFuncionEnDeclActual.isEmpty()) claveFuncionEnDeclActual.pop();
}

private static String getClaveFuncionEnDeclActual() { 
    return claveFuncionEnDeclActual.isEmpty() ? null : claveFuncionEnDeclActual.peek();
}

private static void setTiposRetornoFuncion(String claveFun, List<String> tipos) {
    TDSObject f = tablaDeSimbolos.get(claveFun);
    if (f != null && "Funcion".equals(f.getUso())) {
        f.setTiposRetorno(tipos);
    }
}

private static void addModoParametroFuncionActual(String modo) {
    String funActual = getClaveFuncionEnDeclActual();
    if (funActual == null) return;
    TDSObject f = tablaDeSimbolos.get(funActual);
    if (f != null && "Funcion".equals(f.getUso())) {
        f.addSemanticaParametros(modo);
    }
}

private static void addTipoParametroFuncionActual(String tipo) {
    String funActual = getClaveFuncionEnDeclActual();
    if (funActual == null) return;
    TDSObject f = tablaDeSimbolos.get(funActual);
    if (f != null && "Funcion".equals(f.getUso())) {
        f.addTipoParametro(tipo);
    }
}

private static String resolverClaveFunVisible(String nombre) {
    String mejorClave = null;
    int mejorProfundidad = -1;

    for (String k : tablaDeSimbolos.keySet()) {
        if (!k.startsWith(nombre + ":")) continue;  // mismo lexema
        TDSObject o = tablaDeSimbolos.get(k);
        if (!o.getUso().equals("Funcion")) continue;

        String scope = k.substring(nombre.length()); // ej: ":MAIN:F"
        if (esVisibleDesde(scope, ambito)) {
            int profundidad = scope.length();
            if (profundidad > mejorProfundidad) {
                mejorProfundidad = profundidad;
                mejorClave = k;
            }
        }
    }
    return mejorClave; // puede ser null -> funcion no visible
}



private static String resolverUnidadScope(String unidad) {
    if (unidad == null || unidad.isEmpty()) return null;

    // es un programa?
    TDSObject prog = tablaDeSimbolos.get(unidad); // clave sin tags
    if (prog != null && "Programa".equals(prog.getUso())) {
        String scopeProg = ":" + unidad;
        return esVisibleDesde(scopeProg, ambito) ? scopeProg : null;
    }

    // es una función visible (tomar la mas cercana)?
    String mejorScopePadre = null;
    int mejorLen = -1;

    for (Map.Entry<String, TDSObject> e : tablaDeSimbolos.entrySet()) {
        String k = e.getKey();       // ej: "F:MAIN" o "F:MAIN:G"
        TDSObject o = e.getValue();
        if (!"Funcion".equals(o.getUso())) continue;
        if (!k.startsWith(unidad + ":")) continue;

        String scopePadre = k.substring(unidad.length()); // ":MAIN" o ":MAIN:G"
        if (!esVisibleDesde(scopePadre, ambito)) continue;

        int len = scopePadre.length();
        if (len > mejorLen) {
            mejorLen = len;
            mejorScopePadre = scopePadre;
        }
    }

    return mejorScopePadre; // null si no hay unidad visible
}



private static String claveVarLocal(String lexema) {
    String clave = lexema + ambito;
    TDSObject o = tablaDeSimbolos.get(clave);
    if (o != null && (o.getUso().equals("Variable") || o.getUso().equals("Parametro")))
        return clave;
    return null;
}



private static String claveVarEnUnidad(String unidad, String nombre) {
    String scopeUnidad = resolverUnidadScope(unidad);
    if (scopeUnidad == null) return null;

    String clave = nombre + scopeUnidad;
    TDSObject o = tablaDeSimbolos.get(clave);

    return (o != null && (o.getUso().equals("Variable") || o.getUso().equals("Parametro")))
           ? clave
           : null;
}



private static boolean resolverUsoPrefijado(String unidad, String nombre) {
    removeCrudoSiCorresponde(unidad);
    removeCrudoSiCorresponde(nombre);

    String clave = claveVarEnUnidad(unidad, nombre);
    return clave != null;
}

private static void removeCrudoSiCorresponde(String lex) {
    TDSObject o = tablaDeSimbolos.get(lex);
    if (o != null && o.getUso() == null) {
        tablaDeSimbolos.remove(lex);
    }
}


private static boolean resolverParametroFormal(String nombreFormal) {
    if (claveFunInvocada == null) return false;

    // claveFunInvocada = "F:MAIN"  (lexema + scopePadre)
    String nombreFun  = claveFunInvocada.substring(0, claveFunInvocada.indexOf(':')); // "F"
    String scopePadre = claveFunInvocada.substring(nombreFun.length());               // ":MAIN"

    // ámbito donde están los parámetros de la función
    String scopeCuerpo = scopePadre + ":" + nombreFun;                                // ":MAIN:F"

    // variable/param en ese ambito
    String claveParam = nombreFormal + scopeCuerpo;   // "X:MAIN:F"
    TDSObject obj = tablaDeSimbolos.get(claveParam);

    return obj != null && "Parametro".equals(obj.getUso());
}



private static boolean existeVarOParamEnAmbitoActual(String lexema) {
    String clave = lexema + ambito;
    TDSObject o = tablaDeSimbolos.get(clave);
    return o != null && ("Variable".equals(o.getUso()) || "Parametro".equals(o.getUso()));
}

private static boolean existeFunEnAmbitoActual(String lexema) {
    String clave = lexema + ambito;
    TDSObject o = tablaDeSimbolos.get(clave);
    return o != null && "Funcion".equals(o.getUso());
}


private static boolean existeFuncionVisible(String nombre) {
    for (Map.Entry<String, TDSObject> e : tablaDeSimbolos.entrySet()) {
        String key = e.getKey();
        TDSObject o = e.getValue();
        if (!key.startsWith(nombre + ":")) continue;
        if (!"Funcion".equals(o.getUso())) continue;

        String scopePadre = key.substring(nombre.length());  //":MAIN" o ":MAIN:F"
        if (esVisibleDesde(scopePadre, ambito)) return true;
    }
    return false;
}

private static void setAmbito(String lexema, String uso){
    tablaDeSimbolos.remove(lexema);  

    String clave = lexema + ambito;

    if (tablaDeSimbolos.containsKey(clave)) {
        logErr("ERROR - Redeclaracion de '" + lexema + "' en el ambito " + ambito);
        return;
    }

    TDSObject simbolo = new TDSObject(null);
    simbolo.setUso(uso);

    tablaDeSimbolos.put(clave, simbolo);
}




private static void setNombrePrograma(String lexema){
    // limpiamos el crudo del lexico
    TDSObject original = tablaDeSimbolos.remove(lexema);
    if (original == null) original = new TDSObject(null);

    original.setUso("Programa");
    tablaDeSimbolos.put(lexema, original);

    registrarUnidad(lexema, ":" + lexema);
}



private static boolean existeDeclaradaLocal(String lexema) {
    String clave = lexema + ambito;              // p.ej. "A:MAIN" o "X:MAIN:F"
    TDSObject o = tablaDeSimbolos.get(clave);
    return o != null && ("Variable".equals(o.getUso()) || "Parametro".equals(o.getUso()));
}


private static boolean resolverUsoVariable(String lexema) {
    tablaDeSimbolos.remove(lexema);  // no toca claves mangleadas (ej. A:MAIN)
    return true;
}


private static boolean esVisibleDesde(String padre, String hijo) {
    if (padre == null || padre.isEmpty()) return false;
    if (hijo == null) return false;
    return hijo.startsWith(padre);
}


private static void registrarUnidad(String nombre, String scope) {
    logInfo("Unidad registrada: " + nombre + " con scope " + scope);
}


private void YYERROK() { yyerrflag = 0; }

private static void capturaFinSentencia() {
    if (lineaPrimerSentencia == -1)
        lineaPrimerSentencia = AnalizadorLexico.lineaTokenPrevio;
    lineaUltimoTokenValido = AnalizadorLexico.lineaTokenPrevio;
}


private static void logInfo(String msg) {
    AnalizadorSintactico.add(String.format("AS - Línea %d %s", AnalizadorLexico.lineaToken, msg));
}
private static void logWarn(String msg) {
    AnalizadorSintactico.addWarning(String.format("AS - Línea %d %s", AnalizadorLexico.lineaToken, msg));
}
private static void logErr(String msg) {
    AnalizadorSintactico.addErrores(String.format("AS - Línea %d %s", AnalizadorLexico.lineaToken, msg));
}
private static void logErrAt(int linea, String msg){
    AnalizadorSintactico.addErrores(String.format("AS - Línea %d %s", linea, msg));
}

public int yylex() {
    int value = AnalizadorLexico.yylex();
    yylval = new ParserVal(AnalizadorLexico.refTDS);

    lastTok = value;
    lastLex = (AnalizadorLexico.refTDS == null) ? "" : AnalizadorLexico.refTDS;

    return value;
}

public void yyerror(String msg) {
    String lex = (lastLex == null || lastLex.isEmpty()) ? "(sin lexema)" : lastLex;
    logErr(String.format("ERROR sintáctico: %s. Último Token: %d Lexema: %s", msg, lastTok, lex));
}
//#line 1517 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 26 "gramatica.y"
{ambito = ":" + val_peek(0).sval; setNombrePrograma(val_peek(0).sval);}
break;
case 2:
//#line 26 "gramatica.y"
{ logInfo("Programa reconocido");}
break;
case 3:
//#line 27 "gramatica.y"
{
                                         int linea = (lineaInicioPrograma != -1) ? lineaInicioPrograma : lineaPrimerSentencia;
                                         logErrAt(linea, "ERROR - programa sin nombre.");
                                     }
break;
case 4:
//#line 34 "gramatica.y"
{ logInfo("Bloque reconocido"); }
break;
case 5:
//#line 35 "gramatica.y"
{ logErr("ERROR - Falta delimitador '}' en el bloque."); }
break;
case 6:
//#line 36 "gramatica.y"
{ logWarn("WARNING - programa vacio"); }
break;
case 7:
//#line 37 "gramatica.y"
{ logErr("ERROR - Falta delimitador '{' en el bloque."); }
break;
case 8:
//#line 38 "gramatica.y"
{ logErrAt(lineaPrimerSentencia, "ERROR - programa sin delimitadores."); }
break;
case 9:
//#line 43 "gramatica.y"
{
                                                                          if (lineaInicioPrograma == -1)
                                                                              lineaInicioPrograma = AnalizadorLexico.lineaToken;
                                                                          capturaFinSentencia();
                                                                        }
break;
case 13:
//#line 57 "gramatica.y"
{ logInfo("Sentencia dentro de programa reconocida."); }
break;
case 15:
//#line 61 "gramatica.y"
{ logInfo("Sentencia dentro de función reconocida."); }
break;
case 18:
//#line 66 "gramatica.y"
{ logErr("ERROR - Sentencia inválida."); YYERROK(); }
break;
case 21:
//#line 71 "gramatica.y"
{ logErr("ERROR - Sentencia inválida."); YYERROK(); }
break;
case 28:
//#line 84 "gramatica.y"
{logInfo("Declaracion VAR reconocida.");}
break;
case 29:
//#line 85 "gramatica.y"
{logErr("ERROR - Falta lista de identificadores luego de VAR.");}
break;
case 30:
//#line 89 "gramatica.y"
{setAmbito(val_peek(0).sval, "Variable");}
break;
case 31:
//#line 90 "gramatica.y"
{setAmbito(val_peek(0).sval, "Variable");}
break;
case 32:
//#line 91 "gramatica.y"
{logErr("ERROR - Falta de ',' o ';' en sentencia VAR.");}
break;
case 33:
//#line 95 "gramatica.y"
{ abrirDeclaracionFuncion(val_peek(0).sval, (List<String>) val_peek(1).obj);}
break;
case 34:
//#line 95 "gramatica.y"
{logInfo("Función reconocida."); }
break;
case 35:
//#line 96 "gramatica.y"
{ logErr("ERROR - Falta ',' en lista de tipos en la declaración de la función."); }
break;
case 36:
//#line 97 "gramatica.y"
{ logErr("ERROR - Falta de nombre en la declaración de la función."); }
break;
case 37:
//#line 100 "gramatica.y"
{cerrarDeclaracionFuncion();}
break;
case 38:
//#line 103 "gramatica.y"
{ArrayList<String> l = (ArrayList<String>)val_peek(2).obj;
                                                   l.add((String)val_peek(0).sval);
                                                   yyval.obj = l;}
break;
case 39:
//#line 107 "gramatica.y"
{ArrayList<String> l = new ArrayList<>();
                                                    l.add((String)val_peek(0).sval);
                                                    yyval.obj = l;}
break;
case 40:
//#line 112 "gramatica.y"
{yyval.sval = "UINT";}
break;
case 41:
//#line 113 "gramatica.y"
{yyval.sval = "DFLOAT";}
break;
case 44:
//#line 120 "gramatica.y"
{ setAmbito(val_peek(0).sval,"Parametro");setTipoParametroFormal(val_peek(0).sval, val_peek(1).sval);addTipoParametroFuncionActual(val_peek(1).sval);addModoParametroFuncionActual("CVR");}
break;
case 45:
//#line 121 "gramatica.y"
{ setAmbito(val_peek(0).sval,"Parametro");setTipoParametroFormal(val_peek(0).sval, val_peek(1).sval);addTipoParametroFuncionActual(val_peek(1).sval);addModoParametroFuncionActual("CV");}
break;
case 46:
//#line 122 "gramatica.y"
{ setAmbito(val_peek(0).sval,"Parametro");setTipoParametroFormal(val_peek(0).sval, val_peek(1).sval);addTipoParametroFuncionActual(val_peek(1).sval);addModoParametroFuncionActual("CR");}
break;
case 47:
//#line 123 "gramatica.y"
{ setAmbito(val_peek(0).sval,"Parametro");setTipoParametroFormal(val_peek(0).sval, "LAMBDA");addTipoParametroFuncionActual("LAMBDA");}
break;
case 48:
//#line 124 "gramatica.y"
{ logErr("ERROR - Falta de nombre en parametro formal."); }
break;
case 49:
//#line 125 "gramatica.y"
{ logErr("ERROR - Falta de nombre en parametro formal."); }
break;
case 50:
//#line 126 "gramatica.y"
{ logErr("ERROR - Falta de nombre en parametro formal."); }
break;
case 51:
//#line 127 "gramatica.y"
{ logErr("ERROR - Falta de tipo en parametro formal."); }
break;
case 52:
//#line 128 "gramatica.y"
{ logErr("ERROR - Falta de tipo en parametro formal."); }
break;
case 53:
//#line 129 "gramatica.y"
{ logErr("ERROR - Falta de tipo en parametro formal."); }
break;
case 54:
//#line 130 "gramatica.y"
{ logErr("ERROR - Falta de 'LE' en parametro formal."); }
break;
case 55:
//#line 131 "gramatica.y"
{ logErr("ERROR - Falta de 'LE' en parametro formal."); }
break;
case 56:
//#line 132 "gramatica.y"
{ logErr("ERROR - Falta de 'CV' o 'CR' en parametro formal."); }
break;
case 57:
//#line 135 "gramatica.y"
{capturaFinSentencia();}
break;
case 58:
//#line 139 "gramatica.y"
{    int posBI = reservarHueco();
                                        emitir("BI");
                                        int posBF = pilaIF.pop();
                                        int destElse = polaca.size() + 1;   
                                        completarEn(posBF, destElse);       
                                        emitir("L" + destElse + ":");             
                                        pilaIF.push(posBI);}
break;
case 59:
//#line 149 "gramatica.y"
{ logInfo("Sentencia IF-ELSE reconocida");
                                                                                                                        int posBI = pilaIF.pop();
                                                                                                                        int destEnd = polaca.size() + 1;    
                                                                                                                        completarEn(posBI, destEnd);        
                                                                                                                        emitir("L" + destEnd + ":"); }
break;
case 60:
//#line 154 "gramatica.y"
{ logErr("ERROR - Falta de ENDIF en sentencia IF-ELSE.");}
break;
case 61:
//#line 155 "gramatica.y"
{ logErrAt(lineaUltimoTokenValido, "ERROR - Falta de cuerpo en IF.");}
break;
case 62:
//#line 156 "gramatica.y"
{ logErrAt(lineaUltimoTokenValido, "ERROR - Falta de cuerpo en ELSE.");}
break;
case 63:
//#line 157 "gramatica.y"
{ logErr("ERROR - Falta de ';' en sentencia IF-ELSE.");}
break;
case 64:
//#line 158 "gramatica.y"
{ logErr("ERROR - Falta de ';' y ENDIF en sentencia IF-ELSE.");}
break;
case 65:
//#line 159 "gramatica.y"
{ logInfo("Sentencia IF reconocida");
                                                                                                                        int posBF = pilaIF.pop();
                                                                                                                        int dest = polaca.size() + 1;  
                                                                                                                        completarEn(posBF, dest);         
                                                                                                                        emitir("L" + dest + ":");}
break;
case 66:
//#line 164 "gramatica.y"
{ logErr("ERROR - Falta de ENDIF en sentencia IF.");}
break;
case 67:
//#line 165 "gramatica.y"
{ logErr("ERROR - Falta de ';' en sentencia IF.");}
break;
case 68:
//#line 166 "gramatica.y"
{ logErr("ERROR - Falta de ';' y ENDIF en sentencia IF.");}
break;
case 69:
//#line 169 "gramatica.y"
{ logInfo("Sentencia IF-ELSE reconocida");
                                                                                                                        int posBI = pilaIF.pop();
                                                                                                                        int destEnd = polaca.size() + 1;    
                                                                                                                        completarEn(posBI, destEnd);        
                                                                                                                        emitir("L" + destEnd + ":"); }
break;
case 70:
//#line 174 "gramatica.y"
{ logErr("ERROR - Falta de ENDIF en sentencia IF-ELSE.");}
break;
case 71:
//#line 175 "gramatica.y"
{ logErrAt(lineaUltimoTokenValido, "ERROR - Falta de cuerpo en IF.");}
break;
case 72:
//#line 176 "gramatica.y"
{ logErrAt(lineaUltimoTokenValido, "ERROR - Falta de cuerpo en ELSE.");}
break;
case 73:
//#line 177 "gramatica.y"
{ logErr("ERROR - Falta de ';' en sentencia IF-ELSE.");}
break;
case 74:
//#line 178 "gramatica.y"
{ logErr("ERROR - Falta de ';' y ENDIF en sentencia IF-ELSE.");}
break;
case 75:
//#line 179 "gramatica.y"
{ logInfo("Sentencia IF reconocida");
                                                                                                                        int posBF = pilaIF.pop();
                                                                                                                        int dest = polaca.size() + 1;  
                                                                                                                        completarEn(posBF, dest);         
                                                                                                                        emitir("L" + dest + ":");}
break;
case 76:
//#line 184 "gramatica.y"
{ logErr("ERROR - Falta de ENDIF en sentencia IF.");}
break;
case 77:
//#line 185 "gramatica.y"
{ logErr("ERROR - Falta de ';' en sentencia IF.");}
break;
case 78:
//#line 186 "gramatica.y"
{ logErr("ERROR - Falta de ';' y ENDIF en sentencia IF.");}
break;
case 79:
//#line 190 "gramatica.y"
{ logInfo("Sentencia DO-WHILE reconocida.");
                                                                                              int inicio = pilaDO.pop();       
                                                                                              int posBF = reservarHueco();      
                                                                                              emitir("BF");
                                                                                              emitir(String.valueOf(inicio));   
                                                                                              emitir("BI");
                                                                                              int dest = polaca.size() + 1;     
                                                                                              completarEn(posBF, dest);         
                                                                                              emitir("L" + dest + ":");}
break;
case 80:
//#line 199 "gramatica.y"
{ logErr("ERROR - Falta de WHILE en sentencia DO-WHILE.");}
break;
case 81:
//#line 200 "gramatica.y"
{ logErr("ERROR - Falta de cuerpo en sentencia DO-WHILE."); }
break;
case 82:
//#line 201 "gramatica.y"
{ logErrAt(lineaUltimoTokenValido, "ERROR - Falta de ';' en sentencia DO-WHILE.");}
break;
case 83:
//#line 204 "gramatica.y"
{logInfo("Sentencia DO-WHILE reconocida.");
                                                                                                    int inicio = pilaDO.pop();       
                                                                                                    int posBF = reservarHueco();      
                                                                                                    emitir("BF");
                                                                                                    emitir(String.valueOf(inicio));   
                                                                                                    emitir("BI");
                                                                                                    int dest = polaca.size() + 1;     
                                                                                                    completarEn(posBF, dest);         
                                                                                                    emitir("L" + dest + ":");}
break;
case 84:
//#line 213 "gramatica.y"
{ logErr("ERROR - Falta de WHILE en sentencia DO-WHILE.");}
break;
case 85:
//#line 214 "gramatica.y"
{ logErr("Falta de cuerpo en sentencia DO-WHILE."); }
break;
case 86:
//#line 215 "gramatica.y"
{ logErrAt(lineaUltimoTokenValido, "ERROR - Falta de ';' en sentencia DO-WHILE.");}
break;
case 87:
//#line 219 "gramatica.y"
{int inicio = polaca.size() + 1;
                                  pilaDO.push(inicio);
                                  emitir("L" + inicio + ":");}
break;
case 88:
//#line 224 "gramatica.y"
{ int posDirBF = reservarHueco();
                                                                          emitir("BF");
                                                                          pilaIF.push(posDirBF);}
break;
case 89:
//#line 227 "gramatica.y"
{ logErrAt(lineaUltimoTokenValido, "ERROR - Falta de ')' en sentencia CONTROL.");}
break;
case 90:
//#line 228 "gramatica.y"
{ logErrAt(lineaUltimoTokenValido, "ERROR - Falta de '(' en sentencia CONTROL.");}
break;
case 91:
//#line 229 "gramatica.y"
{ logErrAt(lineaUltimoTokenValido, "ERROR - Falta de '()' en sentencia CONTROL.");}
break;
case 92:
//#line 230 "gramatica.y"
{ logErrAt(lineaUltimoTokenValido, "ERROR - Falta de comparador en sentencia CONTROL.");YYERROK();}
break;
case 93:
//#line 231 "gramatica.y"
{ logErrAt(lineaUltimoTokenValido, "ERROR - expresion mal formada en sentencia CONTROL."); YYERROK(); }
break;
case 94:
//#line 232 "gramatica.y"
{ logErrAt(lineaUltimoTokenValido, "ERROR - Expresión izquierda mal formada en condición."); }
break;
case 95:
//#line 233 "gramatica.y"
{ logErrAt(lineaUltimoTokenValido, "ERROR - Expresión derecha mal formada en condición."); }
break;
case 97:
//#line 238 "gramatica.y"
{ logErrAt(lineaUltimoTokenValido, "ERROR - Falta de ')' en sentencia CONTROL.");}
break;
case 98:
//#line 239 "gramatica.y"
{ logErrAt(lineaUltimoTokenValido, "ERROR - Falta de '(' en sentencia CONTROL.");}
break;
case 99:
//#line 240 "gramatica.y"
{ logErrAt(lineaUltimoTokenValido, "ERROR - Falta de '()' en sentencia CONTROL.");}
break;
case 100:
//#line 241 "gramatica.y"
{ logErrAt(lineaUltimoTokenValido, "ERROR - Falta de comparador en sentencia CONTROL.");YYERROK();}
break;
case 101:
//#line 242 "gramatica.y"
{ logErrAt(lineaUltimoTokenValido, "ERROR - expresion mal formada en sentencia CONTROL."); YYERROK(); }
break;
case 102:
//#line 243 "gramatica.y"
{ logErrAt(lineaUltimoTokenValido, "ERROR - Expresión izquierda mal formada en condición."); }
break;
case 103:
//#line 244 "gramatica.y"
{ logErrAt(lineaUltimoTokenValido, "ERROR - Expresión derecha mal formada en condición."); }
break;
case 104:
//#line 248 "gramatica.y"
{emitir(val_peek(1).sval);}
break;
case 105:
//#line 251 "gramatica.y"
{yyval.sval = "<=";}
break;
case 106:
//#line 252 "gramatica.y"
{yyval.sval = ">=";}
break;
case 107:
//#line 253 "gramatica.y"
{yyval.sval = "=!";}
break;
case 108:
//#line 254 "gramatica.y"
{yyval.sval = "==";}
break;
case 109:
//#line 255 "gramatica.y"
{yyval.sval = "<";}
break;
case 110:
//#line 256 "gramatica.y"
{yyval.sval = ">";}
break;
case 111:
//#line 260 "gramatica.y"
{ logInfo("Bloque ejecutable reconocido."); }
break;
case 113:
//#line 262 "gramatica.y"
{ logWarn("WARNING - Bloque ejecutable vacio.");}
break;
case 118:
//#line 271 "gramatica.y"
{ logWarn("WARNING - Bloque ejecutable vacio.");}
break;
case 121:
//#line 278 "gramatica.y"
{ logInfo("Asignación simple reconocida."); }
break;
case 122:
//#line 279 "gramatica.y"
{ logInfo("Asignación múltiple reconocida."); }
break;
case 127:
//#line 284 "gramatica.y"
{logErrAt(lineaUltimoTokenValido, "ERROR - Falta ';' invocacion a funcion.");}
break;
case 128:
//#line 287 "gramatica.y"
{ logInfo("Asignación simple reconocida."); }
break;
case 129:
//#line 288 "gramatica.y"
{ logInfo("Asignación múltiple reconocida."); }
break;
case 134:
//#line 293 "gramatica.y"
{logErrAt(lineaUltimoTokenValido, "ERROR - Falta ';' invocacion a funcion.");}
break;
case 137:
//#line 299 "gramatica.y"
{
                                                                        String kfun = resolverClaveFunVisible(val_peek(3).sval);   /*"F:MAIN"*/
                                                                        if (kfun == null) {
                                                                            logErr("ERROR - Invocación a función no declarada o fuera de alcance: '" + val_peek(3).sval + "'.");
                                                                            yyval.sval = val_peek(3).sval;
                                                                            yyval.obj  = TIPO_ERROR;  
                                                                            resolverUsoVariable(val_peek(3).sval);
                                                                            limpiarRecoleccionParams();
                                                                        } else {
                                                                            logInfo("Invocación de función reconocida.");
                                                                            TDSObject f = tablaDeSimbolos.get(kfun);/*cambiado*/
                                                                            claveFunInvocada = kfun; 
                                                                            validarParametrosRealesContra(kfun);
                                                                            validarLValuesSegunSemantica(kfun);
                                                                            yyval.sval = kfun;
                                                                            yyval.obj = f.getTiposRetorno().get(0);
                                                                            resolverUsoVariable(val_peek(3).sval);
                                                                            remapearEntradaSegunSemantica(kfun);
                                                                            emitir("CALL_FUN: " + kfun);
                                                                            generarCopiasSalidaSegunSemantica(kfun);
                                                                            limpiarRecoleccionParams();
                                                                        }
                                                                      }
break;
case 140:
//#line 330 "gramatica.y"
{if(val_peek(2).obj == null){
                                                                                        logErr("ERROR - Parametro Real no posee Tipo.");
                                                                                    }
                                                                                    agregarParamRealExpr(val_peek(0).sval, (String)val_peek(2).obj);
                                                                                    resolverUsoVariable(val_peek(0).sval);  
                                                                                    int esLValue = val_peek(2).ival;       /* 1 = lvalue, 0 = no*/
                                                                                    String lexReal = val_peek(2).sval;     
                                                                                    int idx = indiceActualPolaca();
                                                                                    emitir("aCompletar");       
                                                                                    emitir(":=");
                                                                                    registrarParametroReal(val_peek(0).sval, lexReal, esLValue, idx);
                                                                                   ;}
break;
case 141:
//#line 342 "gramatica.y"
{ logErr("ERROR - Falta de especificación del parámetro formal al que corresponde el parámetro real."); }
break;
case 142:
//#line 343 "gramatica.y"
{ logErr("ERROR - Expresion mal formada en parametro real."); }
break;
case 143:
//#line 344 "gramatica.y"
{   int[] rango = (int[]) val_peek(2).obj;
                                                                                agregarParamRealLambda(val_peek(0).sval, rango, ultimoLambdaArgVar);
                                                                                resolverUsoVariable(val_peek(0).sval);
                                                                                logInfo("Lambda pasada como parámetro a '" + val_peek(0).sval + "'.");}
break;
case 144:
//#line 350 "gramatica.y"
{   int posStart = pilaInicioLambda.pop();
                                                                                  int posEnd   = polaca.size();
                                                                                  emitir("LAMBDA_END:");
                                                                                  yyval.obj = new int[]{ posStart, posEnd};
                                                                                  logInfo("Expresión LAMBDA reconocida."); }
break;
case 145:
//#line 355 "gramatica.y"
{ logErr("ERROR - Expresión LAMBDA sin delimitador '{'."); }
break;
case 146:
//#line 356 "gramatica.y"
{ logErr("ERROR - Expresión LAMBDA sin delimitador '}'."); }
break;
case 147:
//#line 357 "gramatica.y"
{ logErr("ERROR - Expresión LAMBDA sin delimitadores '{}'."); }
break;
case 148:
//#line 360 "gramatica.y"
{  int posStart = polaca.size();
                                                          setAmbito(val_peek(1).sval,"Parametro");
                                                          emitir("LAMBDA_START:");
                                                          pilaInicioLambda.push(posStart);
                                                          ultimoLambdaArgVar = claveVarLocal(val_peek(1).sval);
                                                          if (ultimoLambdaArgVar != null) {
                                                              TDSObject v = tablaDeSimbolos.get(ultimoLambdaArgVar);
                                                              if (v != null && v.getTipoVariable() == null) {
                                                                  v.setTipoVariable(val_peek(2).sval);  
                                                              }
                                                            }
                                                        }
break;
case 149:
//#line 374 "gramatica.y"
{ if (!paramFormalEsLambda(val_peek(4).sval)) {
                                                                logErr("ERROR - '" + val_peek(4).sval + "' no es un parámetro LAMBDA en esta función.");
                                                         } else {
                                                                String kParam = devolverParamLambda(val_peek(4).sval);
                                                                emitir("CALL_LAMBDA:" + kParam);
                                                                resolverUsoVariable(val_peek(4).sval);   
                                                         } }
break;
case 150:
//#line 381 "gramatica.y"
{ logErr("ERROR - Argumento inválido para invocación de LAMBDA."); YYERROK(); }
break;
case 151:
//#line 384 "gramatica.y"
{
                                            if (!existeDeclaradaLocal(val_peek(0).sval)) {
                                                logErr("ERROR - Variable no declarada: " + val_peek(0).sval + " (uso sin prefijo fuera del ámbito local).");
                                            }
                                            String k = claveVarLocal(val_peek(0).sval);
                                            emitir(k != null ? k : val_peek(0).sval);
                                            resolverUsoVariable(val_peek(0).sval);
                                          }
break;
case 152:
//#line 392 "gramatica.y"
{
                                            if (AnalizadorLexico.ultimoEsUint) {
                                                String u = TablaSimbolosControl.guardarUint(val_peek(0).sval);
                                                emitir(u);
                                            } else {
                                                String d = TablaSimbolosControl.registrarDfloatPositiva(val_peek(0).sval);
                                                emitir(d);
                                            }
                                          }
break;
case 153:
//#line 401 "gramatica.y"
{
                                            if (AnalizadorLexico.ultimoEsUint) {
                                                String uerr = TablaSimbolosControl.errorEnteroNegativo(val_peek(0).sval);
                                                emitir(uerr); emitir("-");
                                            } else {
                                                String dneg = TablaSimbolosControl.registrarDfloatNegativo(val_peek(0).sval);
                                                emitir(dneg); emitir("-");
                                            }
                                          }
break;
case 154:
//#line 412 "gramatica.y"
{logInfo("PRINT con cadena reconocido.");
                                                                                  emitir(val_peek(2).sval);
                                                                                  emitir("PRINT");}
break;
case 155:
//#line 415 "gramatica.y"
{logInfo("PRINT con expresión reconocido."); 
                                                                                  emitir("PRINT");
                                                                                  if(val_peek(2).obj == null){
                                                                                    logErr("ERROR - Variable no inicializada." + val_peek(2).sval);
                                                                                    }}
break;
case 156:
//#line 420 "gramatica.y"
{logErr("ERROR - Falta de argumento en sentencia print().");}
break;
case 157:
//#line 421 "gramatica.y"
{logErrAt(lineaUltimoTokenValido, "ERROR - Falta ';' sentencia PRINT.");}
break;
case 158:
//#line 422 "gramatica.y"
{logErrAt(lineaUltimoTokenValido, "ERROR - Falta ';' sentencia PRINT.");}
break;
case 159:
//#line 423 "gramatica.y"
{logErrAt(lineaUltimoTokenValido, "ERROR - Falta ';' y argumento sentencia PRINT.");}
break;
case 160:
//#line 424 "gramatica.y"
{logErrAt(lineaUltimoTokenValido, "ERROR - Expresion mal formada en sentencia print().");}
break;
case 161:
//#line 427 "gramatica.y"
{ logInfo("Asignación simple reconocida (variable := expr).");
                                                                                    emitir(val_peek(3).sval);
                                                                                    emitir(":=");
                                                                                    String tipoDer  = (String) val_peek(1).obj;
                                                                                    TDSObject v = tablaDeSimbolos.get(val_peek(3).sval);
                                                                                    if (v != null) {
                                                                                        String tipoIzq = v.getTipoVariable();
                                                                                        if (tipoIzq != null && tipoDer != null && !tipoIzq.equals(tipoDer)) {
                                                                                            logErr("ERROR - Incompatibilidad de tipos en asignacion: " + val_peek(3).sval +
                                                                                                   " es de tipo " + tipoIzq + " y la expresion es de tipo " + tipoDer);
                                                                                        } else if (tipoIzq == null && tipoDer != null) {
                                                                                            /* primera asignacion fija el tipo*/
                                                                                            v.setTipoVariable(tipoDer);
                                                                                        }
                                                                                    }
                                                                                  }
break;
case 162:
//#line 443 "gramatica.y"
{logErrAt(lineaUltimoTokenValido, "ERROR - Falta ';'en la asignación."); YYERROK(); }
break;
case 163:
//#line 446 "gramatica.y"
{procesarAsignacionMultiple();}
break;
case 164:
//#line 447 "gramatica.y"
{logErrAt(lineaUltimoTokenValido, "ERROR - Falta ';'en la asignación múltiple."); iniciarAsignacionMultiple();YYERROK(); }
break;
case 165:
//#line 452 "gramatica.y"
{ variablesIzquierda.add(val_peek(0).sval); }
break;
case 166:
//#line 453 "gramatica.y"
{ logErr("ERROR - Falta de ',' en lista de elementos del lado izquierdo en asignacion multiple."); }
break;
case 167:
//#line 454 "gramatica.y"
{ iniciarAsignacionMultiple(); variablesIzquierda.add(val_peek(0).sval); }
break;
case 168:
//#line 457 "gramatica.y"
{if (!existeDeclaradaLocal(val_peek(0).sval)) {
                                                            logErr("ERROR - Variable no declarada: "+val_peek(0).sval+" (uso sin prefijo fuera del ambito local).");
                                                        }
                                                        String k = claveVarLocal(val_peek(0).sval);
                                                        if(k != null)
                                                            yyval.sval = k;
                                                        else
                                                            yyval.sval = val_peek(0).sval;
                                                        resolverUsoVariable(val_peek(0).sval);}
break;
case 169:
//#line 466 "gramatica.y"
{yyval.sval = val_peek(0).sval;}
break;
case 171:
//#line 470 "gramatica.y"
{ logErr("ERROR - Falta de ',' en lista de elementos del lado derecho en asignacion multiple."); }
break;
case 173:
//#line 474 "gramatica.y"
{ if (AnalizadorLexico.ultimoEsUint) {
                                                                                yyval.sval = TablaSimbolosControl.guardarUint(val_peek(0).sval);
                                                                                tiposDerecha.add("UINT");
                                                                            } else {
                                                                                yyval.sval = TablaSimbolosControl.registrarDfloatPositiva(val_peek(0).sval);
                                                                                tiposDerecha.add("DFLOAT");
                                                                            }
                                                                            variablesDerecha.add(yyval.sval);

                                                                            logInfo(String.format("CTE SIN SIGNO '%s' reconocido", ((ParserVal)val_peek(0)).sval)); }
break;
case 174:
//#line 484 "gramatica.y"
{ if (AnalizadorLexico.ultimoEsUint) {
                                                                                yyval.sval = TablaSimbolosControl.errorEnteroNegativo(val_peek(0).sval);
                                                                                tiposDerecha.add("UINT");
                                                                            } else {
                                                                                yyval.sval = TablaSimbolosControl.registrarDfloatNegativo(val_peek(0).sval);
                                                                                tiposDerecha.add("DFLOAT");
                                                                            }
                                                                            variablesDerecha.add(val_peek(0).sval + "-");
                                                                            logInfo(String.format("emitir($1.sval);CTE CON SIGNO -%s reconocido", ((ParserVal)val_peek(0)).sval)); }
break;
case 175:
//#line 493 "gramatica.y"
{   variablesDerecha.add(val_peek(0).sval + "-");
                                                                                TDSObject o = tablaDeSimbolos.get(val_peek(0).sval);
                                                                                String tipoVar = (o != null) ? o.getTipoVariable() : null;
                                                                                tiposDerecha.add(tipoVar);}
break;
case 176:
//#line 497 "gramatica.y"
{   variablesDerecha.add(val_peek(0).sval); 
                                                                                TDSObject o = tablaDeSimbolos.get(val_peek(0).sval);
                                                                                String tipoVar = (o != null) ? o.getTipoVariable() : null;
                                                                                tiposDerecha.add(tipoVar);}
break;
case 177:
//#line 501 "gramatica.y"
{
                                                                            String k = claveVarLocal(val_peek(0).sval);
                                                                            if (!existeDeclaradaLocal(val_peek(0).sval)) {
                                                                                logErr("ERROR - Variable no declarada: "+val_peek(0).sval+" (uso sin prefijo fuera del ambito local).");
                                                                            }
                                                                            variablesDerecha.add(k);
                                                                            resolverUsoVariable(val_peek(0).sval);
                                                                            TDSObject o = tablaDeSimbolos.get(k);
                                                                            String tipoVar = (o != null) ? o.getTipoVariable() : null;
                                                                            tiposDerecha.add(tipoVar);
                                                                            }
break;
case 178:
//#line 512 "gramatica.y"
{if (!existeDeclaradaLocal(val_peek(0).sval)) {
                                                                                logErr("ERROR - Variable no declarada: "+val_peek(1).sval+" (uso sin prefijo fuera del ambito local).");
                                                                            }
                                                                            String k = claveVarLocal(val_peek(0).sval);
                                                                            variablesDerecha.add((k != null ? k : val_peek(0).sval) + "-");
                                                                            resolverUsoVariable(val_peek(0).sval);
                                                                            TDSObject o = tablaDeSimbolos.get((k != null) ? k : val_peek(0).sval);
                                                                            String tipoVar = (o != null) ? o.getTipoVariable() : null;
                                                                            tiposDerecha.add(tipoVar);
                                                                            }
break;
case 179:
//#line 522 "gramatica.y"
{tiposDerecha.add(null);
                                                                             String kfun = val_peek(0).sval;
                                                                             variablesDerecha.add("CALL " + kfun);}
break;
case 180:
//#line 527 "gramatica.y"
{logInfo("Acceso prefijado reconocido (ID.ID).");
                                                                            if (!resolverUsoPrefijado(val_peek(2).sval, val_peek(0).sval)) {
                                                                                        logErr("ERROR - Prefijo invalido o variable no declarada/visible en esa unidad.");
                                                                            }
                                                                            String k = claveVarEnUnidad(val_peek(2).sval, val_peek(0).sval);
                                                                            if(k != null)
                                                                                yyval.sval = k;
                                                                            else
                                                                                yyval.sval = val_peek(2).sval + "." + val_peek(0).sval;
                                                                            }
break;
case 181:
//#line 539 "gramatica.y"
{ logInfo("Conversión explícita (TOD) reconocida.");
                                                                          emitir("TOD"); }
break;
case 182:
//#line 541 "gramatica.y"
{ logErrAt(lineaUltimoTokenValido, "ERROR - Falta de ')' en conversión TOD."); }
break;
case 183:
//#line 542 "gramatica.y"
{ logErrAt(lineaUltimoTokenValido, "ERROR - Falta de '(' en conversión TOD."); }
break;
case 184:
//#line 543 "gramatica.y"
{ logErr("ERROR - Falta '(expr)' en conversión TOD."); YYERROK(); }
break;
case 185:
//#line 546 "gramatica.y"
{ logInfo("RETURN reconocido.");

                                                                              emitir("RETURN");
                                                                              String claveActual = getClaveFuncionEnDeclActual();
                                                                              if (claveActual != null){
                                                                                TDSObject o = tablaDeSimbolos.get(claveActual);
                                                                                if (o != null){
                                                                                    int esperados = (o.getTiposRetorno() != null) ? o.getTiposRetorno().size() : 0;
                                                                                    int devueltos = (listaReturn != null) ? listaReturn.size() : 0;
                                                                                    if (esperados != devueltos){
                                                                                        logErr("ERROR - Cantidad de valores de retorno (" + devueltos +
                                                                                                                        ") no coincide con lo declarado (" + esperados + ") en función '" +
                                                                                                                        claveActual + "'.");
                                                                                    }else{
                                                                                        for(int i=0; i< o.getTiposRetorno().size(); i++){
                                                                                            if (!o.getTiposRetorno().get(i).equals(listaRetornosTipos.get(i))){
                                                                                                logErr("ERROR - Incompatibilidad de tipos en retorno");
                                                                                                break;
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                              };
                                                                             }
break;
case 186:
//#line 570 "gramatica.y"
{ logErr("ERROR - Argumento invalido en retorno."); YYERROK();}
break;
case 187:
//#line 573 "gramatica.y"
{listaReturn.add(val_peek(0).sval);
                                                         listaRetornosTipos.add((String)val_peek(0).obj);}
break;
case 188:
//#line 575 "gramatica.y"
{listaReturn.clear(); 
                                                         listaReturn.add(val_peek(0).sval);
                                                         listaRetornosTipos.add((String)val_peek(0).obj);}
break;
case 189:
//#line 580 "gramatica.y"
{   Object t1 = val_peek(2).obj;
                                                    Object t2 = val_peek(0).obj;
                                                    if (t1 != null && t2 != null && !t1.equals(t2)) {
                                                        logErr("ERROR - Suma de tipos incompatibles (" + t1 + " con " + t2 + ").");
                                                        yyval.obj = null;
                                                    } else {
                                                        /* si uno de los dos es null, tomamos el otro, sirve para variables sin tipo*/
                                                        yyval.obj = (t1 != null) ? t1 : t2;
                                                    }
                                                    yyval.ival = 0;
                                                    emitir("+"); }
break;
case 190:
//#line 591 "gramatica.y"
{   Object t1 = val_peek(2).obj;
                                                    Object t2 = val_peek(0).obj;
                                                    if (t1 != null && t2 != null && !t1.equals(t2)) {
                                                        logErr("ERROR - Resta de tipos incompatibles (" + t1 + " con " + t2 + ").");
                                                        yyval.obj = null;
                                                    } else {
                                                        yyval.obj = (t1 != null) ? t1 : t2;
                                                    }
                                                    yyval.ival = 0;
                                                    emitir("-"); }
break;
case 191:
//#line 601 "gramatica.y"
{ logErr("ERROR - Falta operando en expresion."); YYERROK(); }
break;
case 192:
//#line 602 "gramatica.y"
{ logErr("ERROR - Falta operando en expresion."); YYERROK(); }
break;
case 193:
//#line 603 "gramatica.y"
{   yyval.sval = val_peek(0).sval;
                                                    yyval.obj = val_peek(0).obj;}
break;
case 194:
//#line 607 "gramatica.y"
{Object t1 = val_peek(2).obj;
                                                    Object t2 = val_peek(0).obj;
                                                    if (t1 != null && t2 != null && !t1.equals(t2)) {
                                                        logErr("ERROR - Producto de tipos incompatibles (" + t1 + " con " + t2 + ").");
                                                        yyval.obj = null;
                                                    } else {
                                                        yyval.obj = (t1 != null) ? t1 : t2;
                                                    }
                                                    yyval.ival = 0;
                                                    emitir("*");}
break;
case 195:
//#line 617 "gramatica.y"
{Object t1 = val_peek(2).obj;
                                                    Object t2 = val_peek(0).obj;
                                                    if (t1 != null && t2 != null && !t1.equals(t2)) {
                                                        logErr("ERROR - Division de tipos incompatibles (" + t1 + " con " + t2 + ").");
                                                        yyval.obj = null;
                                                    } else {
                                                        yyval.obj = (t1 != null) ? t1 : t2;
                                                    }
                                                    yyval.ival = 0;
                                                    emitir("/");}
break;
case 196:
//#line 627 "gramatica.y"
{ logErr("ERROR - Falta factor en termino."); YYERROK(); }
break;
case 197:
//#line 628 "gramatica.y"
{ logErr("ERROR - Falta factor en termino."); YYERROK(); }
break;
case 198:
//#line 629 "gramatica.y"
{yyval.sval = val_peek(0).sval;
                                                    yyval.obj = val_peek(0).obj;}
break;
case 199:
//#line 633 "gramatica.y"
{ if (AnalizadorLexico.ultimoEsUint) {
                                                                                yyval.sval = TablaSimbolosControl.guardarUint(val_peek(0).sval);
                                                                                yyval.obj = "UINT";
                                                                                yyval.ival = 0; /*no es un lvalue*/
                                                                            } else {
                                                                                yyval.sval = TablaSimbolosControl.registrarDfloatPositiva(val_peek(0).sval);
                                                                                yyval.obj = "DFLOAT";
                                                                                yyval.ival = 0; /*no es un lvalue*/
                                                                            }
                                                                            emitir(yyval.sval);
                                                                            logInfo(String.format("CTE SIN SIGNO '%s' reconocido", ((ParserVal)val_peek(0)).sval));
                                                                             }
break;
case 200:
//#line 645 "gramatica.y"
{ if (AnalizadorLexico.ultimoEsUint) {
                                                                                yyval.sval = TablaSimbolosControl.errorEnteroNegativo(val_peek(0).sval);
                                                                                yyval.obj = "UINT";
                                                                                yyval.ival = 0; /*no es un lvalue*/
                                                                            } else {
                                                                                yyval.sval = TablaSimbolosControl.registrarDfloatNegativo(val_peek(0).sval);
                                                                                yyval.obj = "DFLOAT";
                                                                                yyval.ival = 0; /*no es un lvalue*/
                                                                            }
                                                                            emitir(yyval.sval);
                                                                            emitir("-");
                                                                            logInfo(String.format("emitir($1.sval);CTE CON SIGNO -%s reconocido", ((ParserVal)val_peek(0)).sval)); }
break;
case 201:
//#line 657 "gramatica.y"
{   yyval.sval = val_peek(0).sval;
                                                                                TDSObject o = tablaDeSimbolos.get(yyval.sval);
                                                                                yyval.obj = (o != null) ? o.getTipoVariable() : null;
                                                                                yyval.ival = 0; /*no es un lvalue*/
                                                                                emitir(val_peek(0).sval); 
                                                                                emitir("-"); }
break;
case 202:
//#line 663 "gramatica.y"
{   yyval.sval = val_peek(0).sval;
                                                                                TDSObject o = tablaDeSimbolos.get(yyval.sval);
                                                                                yyval.obj = (o != null) ? o.getTipoVariable() : null;
                                                                                yyval.ival = 1; /*es un lvalue*/
                                                                                emitir(val_peek(0).sval); }
break;
case 203:
//#line 668 "gramatica.y"
{if (!existeDeclaradaLocal(val_peek(0).sval)) {
                                                                                logErr("ERROR - Variable no declarada: "+val_peek(0).sval+" (uso sin prefijo fuera del ambito local).");
                                                                            }
                                                                            String k = claveVarLocal(val_peek(0).sval);
                                                                            if(k != null)
                                                                                emitir(k);
                                                                            else
                                                                                emitir(val_peek(0).sval);
                                                                            resolverUsoVariable(val_peek(0).sval);
                                                                            yyval.sval = (k != null) ? k : val_peek(0).sval; 
                                                                            TDSObject o = tablaDeSimbolos.get(yyval.sval);
                                                                            yyval.obj = (o != null) ? o.getTipoVariable() : null;
                                                                            yyval.ival = 1; /*es un lvalue*/
                                                                            }
break;
case 204:
//#line 682 "gramatica.y"
{if (!existeDeclaradaLocal(val_peek(0).sval)) {
                                                                                logErr("ERROR - Variable no declarada: "+val_peek(1).sval+" (uso sin prefijo fuera del ambito local).");
                                                                            }
                                                                            String k = claveVarLocal(val_peek(0).sval);
                                                                            if(k != null)
                                                                                emitir(k);
                                                                            else
                                                                                emitir(val_peek(0).sval);
                                                                            emitir("-");
                                                                            resolverUsoVariable(val_peek(0).sval);
                                                                            yyval.sval = (k != null) ? k : val_peek(0).sval;
                                                                            TDSObject o = tablaDeSimbolos.get(yyval.sval);
                                                                            yyval.obj = (o != null) ? o.getTipoVariable() : null;
                                                                            yyval.ival = 0; /*no es un lvalue*/
                                                                            }
break;
case 205:
//#line 697 "gramatica.y"
{   yyval.sval = val_peek(0).sval;
                                                                                yyval.ival = 0;
                                                                                if(yyval.obj == TIPO_ERROR){
                                                                                    yyval.obj = TIPO_ERROR;
                                                                                }else{
                                                                                    yyval.obj = val_peek(0).obj;
                                                                                    emitirRET(val_peek(0).sval, 1);
                                                                                }
                                                                            }
break;
case 206:
//#line 706 "gramatica.y"
{yyval.obj = "DFLOAT";}
break;
//#line 2685 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public Parser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public Parser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
