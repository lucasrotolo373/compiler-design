%{


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


%}

%token VAR DO WHILE LAMBDA IF ELSE ENDIF PRINT RETURN MENOR_IGUAL MAYOR_IGUAL DISTINTO IGUAL ASIGNACION_SIMPLE FLECHITA UINT DFLOAT ID CADENA CTE CV CR LE TOD

%%

programa                    : ID {ambito = ":" + $1.sval; setNombrePrograma($1.sval);} bloque        { logInfo("Programa reconocido");}
                            | bloque {
                                         int linea = (lineaInicioPrograma != -1) ? lineaInicioPrograma : lineaPrimerSentencia;
                                         logErrAt(linea, "ERROR - programa sin nombre.");
                                     }
                            ;


bloque                      : delimitador_inicio cuerpo delimitador_fin   { logInfo("Bloque reconocido"); }
                            | delimitador_inicio cuerpo                   { logErr("ERROR - Falta delimitador '}' en el bloque."); }
                            | delimitador_inicio delimitador_fin          { logWarn("WARNING - programa vacio"); }
                            | cuerpo delimitador_fin                      { logErr("ERROR - Falta delimitador '{' en el bloque."); }
                            | cuerpo                                      { logErrAt(lineaPrimerSentencia, "ERROR - programa sin delimitadores."); }
                            ;



delimitador_inicio          : '{'                                       {
                                                                          if (lineaInicioPrograma == -1)
                                                                              lineaInicioPrograma = AnalizadorLexico.lineaToken;
                                                                          capturaFinSentencia();
                                                                        }
                            ;

delimitador_fin             : '}'
                            ;

cuerpo                      : lista_sentencias
                            ;

lista_sentencias            : lista_sentencias sentencia
                            | sentencia                                   { logInfo("Sentencia dentro de programa reconocida."); }
                            ;

lista_sentencias_fun        : lista_sentencias_fun sentencia_fun
                            | sentencia_fun                               { logInfo("Sentencia dentro de función reconocida."); }
                            ;

sentencia                   : declarativa
                            | ejecutable
                            | error sincronizador                         { logErr("ERROR - Sentencia inválida."); YYERROK(); }
                            ;

sentencia_fun               : declarativa
                            | ejecutable_fun
                            | error sincronizador                          { logErr("ERROR - Sentencia inválida."); YYERROK(); }
                            ;

sincronizador               : ENDIF
                            | '}'
                            | ELSE
                            | ';'
                            ;

declarativa                 : decl_funcion
                            | decl_reserva_nombres
                            ;

decl_reserva_nombres        : VAR lista_ids ';'            {logInfo("Declaracion VAR reconocida.");}
                            | VAR ';'                      {logErr("ERROR - Falta lista de identificadores luego de VAR.");}
                            ;


lista_ids                   : lista_ids ',' ID              {setAmbito($3.sval, "Variable");}
                            | ID                            {setAmbito($1.sval, "Variable");}
                            | lista_ids error               {logErr("ERROR - Falta de ',' o ';' en sentencia VAR.");}
                            ;


decl_funcion                : tipo_lista ID { abrirDeclaracionFuncion($2.sval, (List<String>) $1.obj);} '(' lista_parametros_formales ')' bloque_funcion       {logInfo("Función reconocida."); }
                            | tipo_lista tipo ID '(' lista_parametros_formales ')' bloque_funcion   { logErr("ERROR - Falta ',' en lista de tipos en la declaración de la función."); }
                            | tipo_lista     '(' lista_parametros_formales ')' bloque_funcion       { logErr("ERROR - Falta de nombre en la declaración de la función."); }
                            ;

bloque_funcion              : '{' lista_sentencias_fun '}'    {cerrarDeclaracionFuncion();}
                            ;

tipo_lista                  : tipo_lista ',' tipo {ArrayList<String> l = (ArrayList<String>)$1.obj;
                                                   l.add((String)$3.sval);
                                                   yyval.obj = l;}

                            | tipo                 {ArrayList<String> l = new ArrayList<>();
                                                    l.add((String)$1.sval);
                                                    yyval.obj = l;}
                            ;

tipo                        : UINT    {yyval.sval = "UINT";}
                            | DFLOAT  {yyval.sval = "DFLOAT";}
                            ;

lista_parametros_formales   : lista_parametros_formales ',' parametro_formal
                            | parametro_formal
                            ;

parametro_formal            : tipo ID           { setAmbito($2.sval,"Parametro");setTipoParametroFormal($2.sval, $1.sval);addTipoParametroFuncionActual($1.sval);addModoParametroFuncionActual("CVR");}
                            | CV LE tipo ID     { setAmbito($4.sval,"Parametro");setTipoParametroFormal($4.sval, $3.sval);addTipoParametroFuncionActual($3.sval);addModoParametroFuncionActual("CV");}
                            | CR LE tipo ID     { setAmbito($4.sval,"Parametro");setTipoParametroFormal($4.sval, $3.sval);addTipoParametroFuncionActual($3.sval);addModoParametroFuncionActual("CR");}
                            | LAMBDA ID         { setAmbito($2.sval,"Parametro");setTipoParametroFormal($2.sval, "LAMBDA");addTipoParametroFuncionActual("LAMBDA");}
                            | tipo                                        { logErr("ERROR - Falta de nombre en parametro formal."); }
                            | CV LE tipo                                  { logErr("ERROR - Falta de nombre en parametro formal."); }
                            | CR LE tipo                                  { logErr("ERROR - Falta de nombre en parametro formal."); }
                            | ID                                          { logErr("ERROR - Falta de tipo en parametro formal."); }
                            | CV LE ID                                    { logErr("ERROR - Falta de tipo en parametro formal."); }
                            | CR LE ID                                    { logErr("ERROR - Falta de tipo en parametro formal."); }
                            | CV tipo ID                                  { logErr("ERROR - Falta de 'LE' en parametro formal."); }
                            | CR tipo ID                                  { logErr("ERROR - Falta de 'LE' en parametro formal."); }
                            | LE tipo ID                                  { logErr("ERROR - Falta de 'CV' o 'CR' en parametro formal."); }
                            ;

marca_fin                   : {capturaFinSentencia();}
                            ;


marca_else                  : ELSE {    int posBI = reservarHueco();
                                        emitir("BI");
                                        int posBF = pilaIF.pop();
                                        int destElse = polaca.size() + 1;   
                                        completarEn(posBF, destElse);       
                                        emitir("L" + destElse + ":");             
                                        pilaIF.push(posBI);}
                            ;


sentencia_if                : IF sentencia_control bloque_ejecutable marca_else bloque_ejecutable ENDIF ';'           { logInfo("Sentencia IF-ELSE reconocida");
                                                                                                                        int posBI = pilaIF.pop();
                                                                                                                        int destEnd = polaca.size() + 1;    
                                                                                                                        completarEn(posBI, destEnd);        
                                                                                                                        emitir("L" + destEnd + ":"); }
                            | IF sentencia_control bloque_ejecutable marca_else bloque_ejecutable ';'                 { logErr("ERROR - Falta de ENDIF en sentencia IF-ELSE.");}
                            | IF sentencia_control marca_fin marca_else bloque_ejecutable ENDIF ';'                   { logErrAt(lineaUltimoTokenValido, "ERROR - Falta de cuerpo en IF.");}
                            | IF sentencia_control bloque_ejecutable marca_else marca_fin ENDIF ';'                   { logErrAt(lineaUltimoTokenValido, "ERROR - Falta de cuerpo en ELSE.");}
                            | IF sentencia_control bloque_ejecutable marca_else bloque_ejecutable ENDIF error         { logErr("ERROR - Falta de ';' en sentencia IF-ELSE.");}
                            | IF sentencia_control bloque_ejecutable marca_else bloque_ejecutable error               { logErr("ERROR - Falta de ';' y ENDIF en sentencia IF-ELSE.");}
                            | IF sentencia_control bloque_ejecutable ENDIF ';'                                        { logInfo("Sentencia IF reconocida");
                                                                                                                        int posBF = pilaIF.pop();
                                                                                                                        int dest = polaca.size() + 1;  
                                                                                                                        completarEn(posBF, dest);         
                                                                                                                        emitir("L" + dest + ":");}
                            | IF sentencia_control bloque_ejecutable ';'                                        { logErr("ERROR - Falta de ENDIF en sentencia IF.");}
                            | IF sentencia_control bloque_ejecutable ENDIF error                                { logErr("ERROR - Falta de ';' en sentencia IF.");}
                            | IF sentencia_control bloque_ejecutable error                                      { logErr("ERROR - Falta de ';' y ENDIF en sentencia IF.");}
                            ;

sentencia_if_fun            : IF sentencia_control bloque_ejecutable_fun marca_else bloque_ejecutable_fun ENDIF ';'   { logInfo("Sentencia IF-ELSE reconocida");
                                                                                                                        int posBI = pilaIF.pop();
                                                                                                                        int destEnd = polaca.size() + 1;    
                                                                                                                        completarEn(posBI, destEnd);        
                                                                                                                        emitir("L" + destEnd + ":"); }
                            | IF sentencia_control bloque_ejecutable_fun marca_else bloque_ejecutable_fun ';'         { logErr("ERROR - Falta de ENDIF en sentencia IF-ELSE.");}
                            | IF sentencia_control marca_fin ELSE bloque_ejecutable_fun ENDIF ';'                     { logErrAt(lineaUltimoTokenValido, "ERROR - Falta de cuerpo en IF.");}
                            | IF sentencia_control bloque_ejecutable_fun marca_else marca_fin ENDIF ';'               { logErrAt(lineaUltimoTokenValido, "ERROR - Falta de cuerpo en ELSE.");}
                            | IF sentencia_control bloque_ejecutable_fun marca_else bloque_ejecutable_fun ENDIF error { logErr("ERROR - Falta de ';' en sentencia IF-ELSE.");}
                            | IF sentencia_control bloque_ejecutable_fun marca_else bloque_ejecutable_fun error       { logErr("ERROR - Falta de ';' y ENDIF en sentencia IF-ELSE.");}
                            | IF sentencia_control bloque_ejecutable_fun ENDIF ';'                                    { logInfo("Sentencia IF reconocida");
                                                                                                                        int posBF = pilaIF.pop();
                                                                                                                        int dest = polaca.size() + 1;  
                                                                                                                        completarEn(posBF, dest);         
                                                                                                                        emitir("L" + dest + ":");}
                            | IF sentencia_control bloque_ejecutable_fun ';'                                    { logErr("ERROR - Falta de ENDIF en sentencia IF.");}
                            | IF sentencia_control bloque_ejecutable_fun ENDIF error                            { logErr("ERROR - Falta de ';' en sentencia IF.");}
                            | IF sentencia_control bloque_ejecutable_fun error                                  { logErr("ERROR - Falta de ';' y ENDIF en sentencia IF.");}
                            ;


sentencia_do_while          : marca_do bloque_ejecutable WHILE cond_while ';'              { logInfo("Sentencia DO-WHILE reconocida.");
                                                                                              int inicio = pilaDO.pop();       
                                                                                              int posBF = reservarHueco();      
                                                                                              emitir("BF");
                                                                                              emitir(String.valueOf(inicio));   
                                                                                              emitir("BI");
                                                                                              int dest = polaca.size() + 1;     
                                                                                              completarEn(posBF, dest);         
                                                                                              emitir("L" + dest + ":");}
                            | marca_do bloque_ejecutable cond_while ';'                    { logErr("ERROR - Falta de WHILE en sentencia DO-WHILE.");}
                            | marca_do WHILE cond_while ';'                                { logErr("ERROR - Falta de cuerpo en sentencia DO-WHILE."); }
                            | marca_do bloque_ejecutable WHILE cond_while error marca_fin  { logErrAt(lineaUltimoTokenValido, "ERROR - Falta de ';' en sentencia DO-WHILE.");}
                            ;

sentencia_do_while_fun      : marca_do bloque_ejecutable_fun WHILE cond_while ';'                  {logInfo("Sentencia DO-WHILE reconocida.");
                                                                                                    int inicio = pilaDO.pop();       
                                                                                                    int posBF = reservarHueco();      
                                                                                                    emitir("BF");
                                                                                                    emitir(String.valueOf(inicio));   
                                                                                                    emitir("BI");
                                                                                                    int dest = polaca.size() + 1;     
                                                                                                    completarEn(posBF, dest);         
                                                                                                    emitir("L" + dest + ":");}
                            | marca_do bloque_ejecutable_fun cond_while ';'                        { logErr("ERROR - Falta de WHILE en sentencia DO-WHILE.");}
                            | marca_do WHILE cond_while ';'                                        { logErr("Falta de cuerpo en sentencia DO-WHILE."); }
                            | marca_do bloque_ejecutable_fun WHILE cond_while error marca_fin      { logErrAt(lineaUltimoTokenValido, "ERROR - Falta de ';' en sentencia DO-WHILE.");}
                            ;


marca_do                    : DO {int inicio = polaca.size() + 1;
                                  pilaDO.push(inicio);
                                  emitir("L" + inicio + ":");}
                            ;

sentencia_control           : '(' condicion ')'                         { int posDirBF = reservarHueco();
                                                                          emitir("BF");
                                                                          pilaIF.push(posDirBF);}
                            | '(' condicion marca_fin                   { logErrAt(lineaUltimoTokenValido, "ERROR - Falta de ')' en sentencia CONTROL.");}
                            | condicion ')' marca_fin                   { logErrAt(lineaUltimoTokenValido, "ERROR - Falta de '(' en sentencia CONTROL.");}
                            | condicion marca_fin                       { logErrAt(lineaUltimoTokenValido, "ERROR - Falta de '()' en sentencia CONTROL.");}
                            | '(' expr error expr ')' marca_fin         { logErrAt(lineaUltimoTokenValido, "ERROR - Falta de comparador en sentencia CONTROL.");YYERROK();}
                            | '(' error ')' marca_fin                   { logErrAt(lineaUltimoTokenValido, "ERROR - expresion mal formada en sentencia CONTROL."); YYERROK(); }
                            | '(' error comparador expr ')' marca_fin   { logErrAt(lineaUltimoTokenValido, "ERROR - Expresión izquierda mal formada en condición."); }
                            | '(' expr comparador error ')' marca_fin   { logErrAt(lineaUltimoTokenValido, "ERROR - Expresión derecha mal formada en condición."); }
                            ;


cond_while                  : '(' condicion ')'
                            | '(' condicion marca_fin                   { logErrAt(lineaUltimoTokenValido, "ERROR - Falta de ')' en sentencia CONTROL.");}
                            | condicion ')' marca_fin                   { logErrAt(lineaUltimoTokenValido, "ERROR - Falta de '(' en sentencia CONTROL.");}
                            | condicion marca_fin                       { logErrAt(lineaUltimoTokenValido, "ERROR - Falta de '()' en sentencia CONTROL.");}
                            | '(' expr error expr ')' marca_fin         { logErrAt(lineaUltimoTokenValido, "ERROR - Falta de comparador en sentencia CONTROL.");YYERROK();}
                            | '(' error ')' marca_fin                   { logErrAt(lineaUltimoTokenValido, "ERROR - expresion mal formada en sentencia CONTROL."); YYERROK(); }
                            | '(' error comparador expr ')' marca_fin   { logErrAt(lineaUltimoTokenValido, "ERROR - Expresión izquierda mal formada en condición."); }
                            | '(' expr comparador error ')' marca_fin   { logErrAt(lineaUltimoTokenValido, "ERROR - Expresión derecha mal formada en condición."); }
                            ;


condicion                   : expr comparador expr    {emitir($2.sval);}
                            ;

comparador                  : MENOR_IGUAL       {yyval.sval = "<=";}
                            | MAYOR_IGUAL       {yyval.sval = ">=";}
                            | DISTINTO          {yyval.sval = "=!";}
                            | IGUAL             {yyval.sval = "==";}
                            | '<'               {yyval.sval = "<";}
                            | '>'               {yyval.sval = ">";}
                            ;


bloque_ejecutable           : ejecutable                                  { logInfo("Bloque ejecutable reconocido."); }
                            | '{' lista_ejecutables '}'
                            | '{' '}'                                     { logWarn("WARNING - Bloque ejecutable vacio.");}
                            ;

lista_ejecutables           : lista_ejecutables ejecutable
                            | ejecutable
                            ;

bloque_ejecutable_fun       : ejecutable_fun
                            | '{' lista_ejecutables_fun '}'
                            | '{' '}'                                     { logWarn("WARNING - Bloque ejecutable vacio.");}
                            ;

lista_ejecutables_fun       : lista_ejecutables_fun ejecutable_fun
                            | ejecutable_fun
                            ;

ejecutable                  : asignacion_simple                             { logInfo("Asignación simple reconocida."); }
                            | asignacion_multiple                           { logInfo("Asignación múltiple reconocida."); }
                            | sentencia_if
                            | sentencia_do_while
                            | llamado_print
                            | invocacion_funcion ';'
                            | invocacion_funcion error marca_fin            {logErrAt(lineaUltimoTokenValido, "ERROR - Falta ';' invocacion a funcion.");}
                            ;

ejecutable_fun              : asignacion_simple                         { logInfo("Asignación simple reconocida."); }
                            | asignacion_multiple                       { logInfo("Asignación múltiple reconocida."); }
                            | sentencia_if_fun
                            | sentencia_do_while_fun
                            | llamado_print
                            | invocacion_funcion ';'
                            | invocacion_funcion error marca_fin        {logErrAt(lineaUltimoTokenValido, "ERROR - Falta ';' invocacion a funcion.");}
                            | retorno ';'
                            | invocacion_lambda_param 
                            ;


invocacion_funcion          : ID '(' lista_parametros_reales ')'      {
                                                                        String kfun = resolverClaveFunVisible($1.sval);   //"F:MAIN"
                                                                        if (kfun == null) {
                                                                            logErr("ERROR - Invocación a función no declarada o fuera de alcance: '" + $1.sval + "'.");
                                                                            yyval.sval = $1.sval;
                                                                            yyval.obj  = TIPO_ERROR;  
                                                                            resolverUsoVariable($1.sval);
                                                                            limpiarRecoleccionParams();
                                                                        } else {
                                                                            logInfo("Invocación de función reconocida.");
                                                                            TDSObject f = tablaDeSimbolos.get(kfun);//cambiado
                                                                            claveFunInvocada = kfun; 
                                                                            validarParametrosRealesContra(kfun);
                                                                            validarLValuesSegunSemantica(kfun);
                                                                            yyval.sval = kfun;
                                                                            yyval.obj = f.getTiposRetorno().get(0);
                                                                            resolverUsoVariable($1.sval);
                                                                            remapearEntradaSegunSemantica(kfun);
                                                                            emitir("CALL_FUN: " + kfun);
                                                                            generarCopiasSalidaSegunSemantica(kfun);
                                                                            limpiarRecoleccionParams();
                                                                        }
                                                                      }
                            ;



lista_parametros_reales     : lista_parametros_reales ',' parametro_real
                            | parametro_real
                            ;

parametro_real              : expr FLECHITA ID                                     {if($1.obj == null){
                                                                                        logErr("ERROR - Parametro Real no posee Tipo.");
                                                                                    }
                                                                                    agregarParamRealExpr($3.sval, (String)$1.obj);
                                                                                    resolverUsoVariable($3.sval);  
                                                                                    int esLValue = $1.ival;       // 1 = lvalue, 0 = no
                                                                                    String lexReal = $1.sval;     
                                                                                    int idx = indiceActualPolaca();
                                                                                    emitir("aCompletar");       
                                                                                    emitir(":=");
                                                                                    registrarParametroReal($3.sval, lexReal, esLValue, idx);
                                                                                   ;}
                            | expr FLECHITA                               { logErr("ERROR - Falta de especificación del parámetro formal al que corresponde el parámetro real."); }
                            | error FLECHITA                              { logErr("ERROR - Expresion mal formada en parametro real."); }
                            | lambda_expr FLECHITA ID                       {   int[] rango = (int[]) $1.obj;
                                                                                agregarParamRealLambda($3.sval, rango, ultimoLambdaArgVar);
                                                                                resolverUsoVariable($3.sval);
                                                                                logInfo("Lambda pasada como parámetro a '" + $3.sval + "'.");}
                            ;

lambda_expr                 : lambda_header '{' lista_ejecutables '}'         {   int posStart = pilaInicioLambda.pop();
                                                                                  int posEnd   = polaca.size();
                                                                                  emitir("LAMBDA_END:");
                                                                                  yyval.obj = new int[]{ posStart, posEnd};
                                                                                  logInfo("Expresión LAMBDA reconocida."); }
                            | lambda_header lista_ejecutables '}'             { logErr("ERROR - Expresión LAMBDA sin delimitador '{'."); }
                            | lambda_header '{' lista_ejecutables             { logErr("ERROR - Expresión LAMBDA sin delimitador '}'."); }
                            | lambda_header lista_ejecutables                 { logErr("ERROR - Expresión LAMBDA sin delimitadores '{}'."); }
                            ;

lambda_header               : '(' tipo ID ')'          {  int posStart = polaca.size();
                                                          setAmbito($3.sval,"Parametro");
                                                          emitir("LAMBDA_START:");
                                                          pilaInicioLambda.push(posStart);
                                                          ultimoLambdaArgVar = claveVarLocal($3.sval);
                                                          if (ultimoLambdaArgVar != null) {
                                                              TDSObject v = tablaDeSimbolos.get(ultimoLambdaArgVar);
                                                              if (v != null && v.getTipoVariable() == null) {
                                                                  v.setTipoVariable($2.sval);  
                                                              }
                                                            }
                                                        }
                            ;                            

invocacion_lambda_param     : ID '(' arg_lambda ')' ';'  { if (!paramFormalEsLambda($1.sval)) {
                                                                logErr("ERROR - '" + $1.sval + "' no es un parámetro LAMBDA en esta función.");
                                                         } else {
                                                                String kParam = devolverParamLambda($1.sval);
                                                                emitir("CALL_LAMBDA:" + kParam);
                                                                resolverUsoVariable($1.sval);   
                                                         } }
                            | ID '(' error ')' ';'    { logErr("ERROR - Argumento inválido para invocación de LAMBDA."); YYERROK(); }
                            ;

arg_lambda                  : ID          {
                                            if (!existeDeclaradaLocal($1.sval)) {
                                                logErr("ERROR - Variable no declarada: " + $1.sval + " (uso sin prefijo fuera del ámbito local).");
                                            }
                                            String k = claveVarLocal($1.sval);
                                            emitir(k != null ? k : $1.sval);
                                            resolverUsoVariable($1.sval);
                                          }
                            | CTE         {
                                            if (AnalizadorLexico.ultimoEsUint) {
                                                String u = TablaSimbolosControl.guardarUint($1.sval);
                                                emitir(u);
                                            } else {
                                                String d = TablaSimbolosControl.registrarDfloatPositiva($1.sval);
                                                emitir(d);
                                            }
                                          }
                            | '-' CTE     {
                                            if (AnalizadorLexico.ultimoEsUint) {
                                                String uerr = TablaSimbolosControl.errorEnteroNegativo($2.sval);
                                                emitir(uerr); emitir("-");
                                            } else {
                                                String dneg = TablaSimbolosControl.registrarDfloatNegativo($2.sval);
                                                emitir(dneg); emitir("-");
                                            }
                                          }
    ;                        

llamado_print               : PRINT '(' CADENA ')' ';'                           {logInfo("PRINT con cadena reconocido.");
                                                                                  emitir($3.sval);
                                                                                  emitir("PRINT");}
                            | PRINT '(' expr ')'   ';'                           {logInfo("PRINT con expresión reconocido."); 
                                                                                  emitir("PRINT");
                                                                                  if($3.obj == null){
                                                                                    logErr("ERROR - Variable no inicializada." + $3.sval);
                                                                                    }}
                            | PRINT '(' ')'        ';'                           {logErr("ERROR - Falta de argumento en sentencia print().");}
                            | PRINT '(' CADENA ')' error marca_fin               {logErrAt(lineaUltimoTokenValido, "ERROR - Falta ';' sentencia PRINT.");}
                            | PRINT '(' expr ')' error marca_fin                 {logErrAt(lineaUltimoTokenValido, "ERROR - Falta ';' sentencia PRINT.");}
                            | PRINT '('')' error marca_fin                       {logErrAt(lineaUltimoTokenValido, "ERROR - Falta ';' y argumento sentencia PRINT.");}
                            | PRINT '('error ')' error marca_fin                 {logErrAt(lineaUltimoTokenValido, "ERROR - Expresion mal formada en sentencia print().");}
                            ;

asignacion_simple           : variable ASIGNACION_SIMPLE expr ';'                 { logInfo("Asignación simple reconocida (variable := expr).");
                                                                                    emitir($1.sval);
                                                                                    emitir(":=");
                                                                                    String tipoDer  = (String) $3.obj;
                                                                                    TDSObject v = tablaDeSimbolos.get($1.sval);
                                                                                    if (v != null) {
                                                                                        String tipoIzq = v.getTipoVariable();
                                                                                        if (tipoIzq != null && tipoDer != null && !tipoIzq.equals(tipoDer)) {
                                                                                            logErr("ERROR - Incompatibilidad de tipos en asignacion: " + $1.sval +
                                                                                                   " es de tipo " + tipoIzq + " y la expresion es de tipo " + tipoDer);
                                                                                        } else if (tipoIzq == null && tipoDer != null) {
                                                                                            // primera asignacion fija el tipo
                                                                                            v.setTipoVariable(tipoDer);
                                                                                        }
                                                                                    }
                                                                                  }
                            | variable ASIGNACION_SIMPLE expr error marca_fin     {logErrAt(lineaUltimoTokenValido, "ERROR - Falta ';'en la asignación."); YYERROK(); }
                            ;

asignacion_multiple         : lista_variables_izq '=' lista_elem_lado_derecho ';'               {procesarAsignacionMultiple();} 
                            | lista_variables_izq '=' lista_elem_lado_derecho error marca_fin   {logErrAt(lineaUltimoTokenValido, "ERROR - Falta ';'en la asignación múltiple."); iniciarAsignacionMultiple();YYERROK(); }
                            ;



lista_variables_izq         : lista_variables_izq ',' variable                       { variablesIzquierda.add($3.sval); }
                            | error variable                                         { logErr("ERROR - Falta de ',' en lista de elementos del lado izquierdo en asignacion multiple."); }
                            | variable                                               { iniciarAsignacionMultiple(); variablesIzquierda.add($1.sval); }
                            ;

variable                    : ID                        {if (!existeDeclaradaLocal($1.sval)) {
                                                            logErr("ERROR - Variable no declarada: "+$1.sval+" (uso sin prefijo fuera del ambito local).");
                                                        }
                                                        String k = claveVarLocal($1.sval);
                                                        if(k != null)
                                                            yyval.sval = k;
                                                        else
                                                            yyval.sval = $1.sval;
                                                        resolverUsoVariable($1.sval);}
                            | prefijado_var             {yyval.sval = $1.sval;}
                            ;

lista_elem_lado_derecho     : lista_elem_lado_derecho ',' elem_lado_derecho
                            | lista_elem_lado_derecho elem_lado_derecho             { logErr("ERROR - Falta de ',' en lista de elementos del lado derecho en asignacion multiple."); }
                            | elem_lado_derecho
                            ;

elem_lado_derecho           : CTE                                         { if (AnalizadorLexico.ultimoEsUint) {
                                                                                yyval.sval = TablaSimbolosControl.guardarUint($1.sval);
                                                                                tiposDerecha.add("UINT");
                                                                            } else {
                                                                                yyval.sval = TablaSimbolosControl.registrarDfloatPositiva($1.sval);
                                                                                tiposDerecha.add("DFLOAT");
                                                                            }
                                                                            variablesDerecha.add(yyval.sval);

                                                                            logInfo(String.format("CTE SIN SIGNO '%s' reconocido", ((ParserVal)$1).sval)); }
                            | '-' CTE                                     { if (AnalizadorLexico.ultimoEsUint) {
                                                                                yyval.sval = TablaSimbolosControl.errorEnteroNegativo($2.sval);
                                                                                tiposDerecha.add("UINT");
                                                                            } else {
                                                                                yyval.sval = TablaSimbolosControl.registrarDfloatNegativo($2.sval);
                                                                                tiposDerecha.add("DFLOAT");
                                                                            }
                                                                            variablesDerecha.add($2.sval + "-");
                                                                            logInfo(String.format("emitir($1.sval);CTE CON SIGNO -%s reconocido", ((ParserVal)$2).sval)); }
                            | '-' prefijado_var                             {   variablesDerecha.add($2.sval + "-");
                                                                                TDSObject o = tablaDeSimbolos.get($2.sval);
                                                                                String tipoVar = (o != null) ? o.getTipoVariable() : null;
                                                                                tiposDerecha.add(tipoVar);}
                            | prefijado_var                                 {   variablesDerecha.add($1.sval); 
                                                                                TDSObject o = tablaDeSimbolos.get($1.sval);
                                                                                String tipoVar = (o != null) ? o.getTipoVariable() : null;
                                                                                tiposDerecha.add(tipoVar);}
                            | ID                                            {
                                                                            String k = claveVarLocal($1.sval);
                                                                            if (!existeDeclaradaLocal($1.sval)) {
                                                                                logErr("ERROR - Variable no declarada: "+$1.sval+" (uso sin prefijo fuera del ambito local).");
                                                                            }
                                                                            variablesDerecha.add(k);
                                                                            resolverUsoVariable($1.sval);
                                                                            TDSObject o = tablaDeSimbolos.get(k);
                                                                            String tipoVar = (o != null) ? o.getTipoVariable() : null;
                                                                            tiposDerecha.add(tipoVar);
                                                                            }
                            | '-' ID                                        {if (!existeDeclaradaLocal($2.sval)) {
                                                                                logErr("ERROR - Variable no declarada: "+$1.sval+" (uso sin prefijo fuera del ambito local).");
                                                                            }
                                                                            String k = claveVarLocal($2.sval);
                                                                            variablesDerecha.add((k != null ? k : $2.sval) + "-");
                                                                            resolverUsoVariable($2.sval);
                                                                            TDSObject o = tablaDeSimbolos.get((k != null) ? k : $2.sval);
                                                                            String tipoVar = (o != null) ? o.getTipoVariable() : null;
                                                                            tiposDerecha.add(tipoVar);
                                                                            }
                            | invocacion_funcion                            {tiposDerecha.add(null);
                                                                             String kfun = $1.sval;
                                                                             variablesDerecha.add("CALL " + kfun);}
                            ;

prefijado_var               : ID '.' ID                                    {logInfo("Acceso prefijado reconocido (ID.ID).");
                                                                            if (!resolverUsoPrefijado($1.sval, $3.sval)) {
                                                                                        logErr("ERROR - Prefijo invalido o variable no declarada/visible en esa unidad.");
                                                                            }
                                                                            String k = claveVarEnUnidad($1.sval, $3.sval);
                                                                            if(k != null)
                                                                                yyval.sval = k;
                                                                            else
                                                                                yyval.sval = $1.sval + "." + $3.sval;
                                                                            }
                            ;

conv_uint_a_dfloat          : TOD '(' expr ')'                          { logInfo("Conversión explícita (TOD) reconocida.");
                                                                          emitir("TOD"); }
                            | TOD '(' error marca_fin                   { logErrAt(lineaUltimoTokenValido, "ERROR - Falta de ')' en conversión TOD."); }
                            | TOD error ')' marca_fin                   { logErrAt(lineaUltimoTokenValido, "ERROR - Falta de '(' en conversión TOD."); }
                            | TOD error ';'                             { logErr("ERROR - Falta '(expr)' en conversión TOD."); YYERROK(); }
                            ;

retorno                     : RETURN '(' lista_expr ')'                     { logInfo("RETURN reconocido.");

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
                            | RETURN '(' lista_expr error ')'               { logErr("ERROR - Argumento invalido en retorno."); YYERROK();}
                            ;

lista_expr                  : lista_expr ',' expr       {listaReturn.add($3.sval);
                                                         listaRetornosTipos.add((String)$3.obj);}
                            | expr                      {listaReturn.clear(); 
                                                         listaReturn.add($1.sval);
                                                         listaRetornosTipos.add((String)$1.obj);}
                            ;

expr                        : expr '+' termino  {   Object t1 = $1.obj;
                                                    Object t2 = $3.obj;
                                                    if (t1 != null && t2 != null && !t1.equals(t2)) {
                                                        logErr("ERROR - Suma de tipos incompatibles (" + t1 + " con " + t2 + ").");
                                                        yyval.obj = null;
                                                    } else {
                                                        // si uno de los dos es null, tomamos el otro, sirve para variables sin tipo
                                                        yyval.obj = (t1 != null) ? t1 : t2;
                                                    }
                                                    yyval.ival = 0;
                                                    emitir("+"); }
                            | expr '-' termino  {   Object t1 = $1.obj;
                                                    Object t2 = $3.obj;
                                                    if (t1 != null && t2 != null && !t1.equals(t2)) {
                                                        logErr("ERROR - Resta de tipos incompatibles (" + t1 + " con " + t2 + ").");
                                                        yyval.obj = null;
                                                    } else {
                                                        yyval.obj = (t1 != null) ? t1 : t2;
                                                    }
                                                    yyval.ival = 0;
                                                    emitir("-"); }
                            | expr '+' error    { logErr("ERROR - Falta operando en expresion."); YYERROK(); }
                            | expr '-' error    { logErr("ERROR - Falta operando en expresion."); YYERROK(); }
                            | termino           {   yyval.sval = $1.sval;
                                                    yyval.obj = $1.obj;}
                            ;

termino                     : termino '*' factor   {Object t1 = $1.obj;
                                                    Object t2 = $3.obj;
                                                    if (t1 != null && t2 != null && !t1.equals(t2)) {
                                                        logErr("ERROR - Producto de tipos incompatibles (" + t1 + " con " + t2 + ").");
                                                        yyval.obj = null;
                                                    } else {
                                                        yyval.obj = (t1 != null) ? t1 : t2;
                                                    }
                                                    yyval.ival = 0;
                                                    emitir("*");}
                            | termino '/' factor   {Object t1 = $1.obj;
                                                    Object t2 = $3.obj;
                                                    if (t1 != null && t2 != null && !t1.equals(t2)) {
                                                        logErr("ERROR - Division de tipos incompatibles (" + t1 + " con " + t2 + ").");
                                                        yyval.obj = null;
                                                    } else {
                                                        yyval.obj = (t1 != null) ? t1 : t2;
                                                    }
                                                    yyval.ival = 0;
                                                    emitir("/");}
                            | termino '*' error { logErr("ERROR - Falta factor en termino."); YYERROK(); }
                            | termino '/' error { logErr("ERROR - Falta factor en termino."); YYERROK(); }
                            | factor               {yyval.sval = $1.sval;
                                                    yyval.obj = $1.obj;}
                            ;

factor                      : CTE                                          { if (AnalizadorLexico.ultimoEsUint) {
                                                                                yyval.sval = TablaSimbolosControl.guardarUint($1.sval);
                                                                                yyval.obj = "UINT";
                                                                                yyval.ival = 0; //no es un lvalue
                                                                            } else {
                                                                                yyval.sval = TablaSimbolosControl.registrarDfloatPositiva($1.sval);
                                                                                yyval.obj = "DFLOAT";
                                                                                yyval.ival = 0; //no es un lvalue
                                                                            }
                                                                            emitir(yyval.sval);
                                                                            logInfo(String.format("CTE SIN SIGNO '%s' reconocido", ((ParserVal)$1).sval));
                                                                             }
                            | '-' CTE                                     { if (AnalizadorLexico.ultimoEsUint) {
                                                                                yyval.sval = TablaSimbolosControl.errorEnteroNegativo($2.sval);
                                                                                yyval.obj = "UINT";
                                                                                yyval.ival = 0; //no es un lvalue
                                                                            } else {
                                                                                yyval.sval = TablaSimbolosControl.registrarDfloatNegativo($2.sval);
                                                                                yyval.obj = "DFLOAT";
                                                                                yyval.ival = 0; //no es un lvalue
                                                                            }
                                                                            emitir(yyval.sval);
                                                                            emitir("-");
                                                                            logInfo(String.format("emitir($1.sval);CTE CON SIGNO -%s reconocido", ((ParserVal)$2).sval)); }
                            | '-' prefijado_var                             {   yyval.sval = $2.sval;
                                                                                TDSObject o = tablaDeSimbolos.get(yyval.sval);
                                                                                yyval.obj = (o != null) ? o.getTipoVariable() : null;
                                                                                yyval.ival = 0; //no es un lvalue
                                                                                emitir($2.sval); 
                                                                                emitir("-"); }
                            | prefijado_var                                 {   yyval.sval = $1.sval;
                                                                                TDSObject o = tablaDeSimbolos.get(yyval.sval);
                                                                                yyval.obj = (o != null) ? o.getTipoVariable() : null;
                                                                                yyval.ival = 1; //es un lvalue
                                                                                emitir($1.sval); }
                            | ID                                            {if (!existeDeclaradaLocal($1.sval)) {
                                                                                logErr("ERROR - Variable no declarada: "+$1.sval+" (uso sin prefijo fuera del ambito local).");
                                                                            }
                                                                            String k = claveVarLocal($1.sval);
                                                                            if(k != null)
                                                                                emitir(k);
                                                                            else
                                                                                emitir($1.sval);
                                                                            resolverUsoVariable($1.sval);
                                                                            yyval.sval = (k != null) ? k : $1.sval; 
                                                                            TDSObject o = tablaDeSimbolos.get(yyval.sval);
                                                                            yyval.obj = (o != null) ? o.getTipoVariable() : null;
                                                                            yyval.ival = 1; //es un lvalue
                                                                            }
                            | '-' ID                                        {if (!existeDeclaradaLocal($2.sval)) {
                                                                                logErr("ERROR - Variable no declarada: "+$1.sval+" (uso sin prefijo fuera del ambito local).");
                                                                            }
                                                                            String k = claveVarLocal($2.sval);
                                                                            if(k != null)
                                                                                emitir(k);
                                                                            else
                                                                                emitir($2.sval);
                                                                            emitir("-");
                                                                            resolverUsoVariable($2.sval);
                                                                            yyval.sval = (k != null) ? k : $2.sval;
                                                                            TDSObject o = tablaDeSimbolos.get(yyval.sval);
                                                                            yyval.obj = (o != null) ? o.getTipoVariable() : null;
                                                                            yyval.ival = 0; //no es un lvalue
                                                                            }
                            | invocacion_funcion                            {   yyval.sval = $1.sval;
                                                                                yyval.ival = 0;
                                                                                if(yyval.obj == TIPO_ERROR){
                                                                                    yyval.obj = TIPO_ERROR;
                                                                                }else{
                                                                                    yyval.obj = $1.obj;
                                                                                    emitirRET($1.sval, 1);
                                                                                }
                                                                            }
                            | conv_uint_a_dfloat                            {yyval.obj = "DFLOAT";}
                            ;

%%


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
