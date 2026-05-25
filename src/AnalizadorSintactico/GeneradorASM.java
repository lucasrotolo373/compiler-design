package AnalizadorSintactico;

import AnalizadorLexico.AnalizadorLexico;
import AnalizadorLexico.TDSObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;


public class GeneradorASM {

    private static final Map<String, String> constMap = new LinkedHashMap<>();
    private static final Map<String, String> cadenas = new LinkedHashMap<>();
    private static final Map<String, String> auxTipos = new LinkedHashMap<>();

    private static final Map<String, List<String>> cuerpoLambda = new LinkedHashMap<>();
    private static List<String> instruccionesLambdaActual = null;
    private static List<String> instruccionesAntesDeLambda = null;
    private static String lambdaActualClave = null;



    public static void generarCodigoASM(List<String> polaca, String salidaAsm, List<String> errores) {
        Deque<String> pilaValores = new ArrayDeque<>();
        Deque<String> pilaTipos   = new ArrayDeque<>();

        List<String> instruccionesMain  = new ArrayList<>();

        // cuerpo de cada función: nombre función -> lista de instrucciones
        Map<String, List<String>> cuerpoFunciones = new LinkedHashMap<>();

        // pila de listas de instrucciones (main, función externa, función interna, etc.)
        Deque<List<String>> pilaListasInstr = new ArrayDeque<>();

        // pila de nombres de funciones y flags de RETURN
        Deque<String> pilaFunciones = new ArrayDeque<>();
        Deque<Boolean> pilaTieneReturn = new ArrayDeque<>();

        pilaListasInstr.push(instruccionesMain);
        List<String> instrucciones = instruccionesMain;


        int contadorAux = 0;

        Map<String, String> retTipos = new LinkedHashMap<>();   // @ret_F_MAIN_1 -> tipo ("UINT"/"DFLOAT")

        constMap.clear();
        cadenas.clear();

        for (String p : polaca) {
            String token = p.trim();
            if (token.isEmpty()) continue;

            switch (token) {

                case "+":
                case "-":
                case "*":
                case "/": {
                    String op2  = pilaValores.pop();
                    String tipo2 = pilaTipos.pop();
                    String op1  = pilaValores.pop();
                    String tipo1 = pilaTipos.pop();

                    if (tipo1 == null || tipo2 == null) {
                        errores.add("No se puede determinar el tipo de los operandos para '" + token + "'.");
                        pilaValores.push("@error");
                        pilaTipos.push(null);
                        break;
                    }
                    if (!tipo1.equals(tipo2)) {
                        errores.add("Tipos incompatibles en la operación " + token + ": " + tipo1 + " y " + tipo2);
                        pilaValores.push("@error");
                        pilaTipos.push(null);
                        break;
                    }

                    String tipoRes = tipo1;
                    contadorAux++;
                    String aux = "@aux" + contadorAux;
                    auxTipos.put(aux, tipoRes);

                    if ("UINT".equals(tipoRes)) {
                        switch (token) {
                            case "+":
                                instrucciones.add("    mov ax, " + mapOperand(op1) + "    ; op1 UINT");
                                instrucciones.add("    add ax, " + mapOperand(op2) + "    ; op1 + op2");
                                instrucciones.add("    jc  overflowAdd       ; overflow en suma UINT (16 bits)");
                                instrucciones.add("    mov " + mapOperand(aux) + ", ax     ; resultado");
                                break;

                            case "-":
                                instrucciones.add("    mov ax, " + mapOperand(op1) + "    ; op1 UINT");
                                instrucciones.add("    sub ax, " + mapOperand(op2) + "    ; op1 - op2");
                                instrucciones.add("    jc  negativeSub       ; resultado negativo en UINT");
                                instrucciones.add("    mov " + mapOperand(aux) + ", ax");
                                break;

                            case "*":
                                instrucciones.add("    mov ax, " + mapOperand(op1) + "    ; op1 UINT");
                                instrucciones.add("    mul " + mapOperand(op2) + "       ; DX:AX = AX * op2");
                                instrucciones.add("    cmp dx, 0             ; overflow si DX != 0");
                                instrucciones.add("    jne overflowMult");
                                instrucciones.add("    mov " + mapOperand(aux) + ", ax");
                                break;

                            case "/":
                                instrucciones.add("    mov dx, 0            ; preparar DX:AX");
                                instrucciones.add("    mov ax, " + mapOperand(op1) + "    ; dividendo UINT");
                                instrucciones.add("    div " + mapOperand(op2) + "        ; AX = cociente");
                                instrucciones.add("    mov " + mapOperand(aux) + ", ax");
                                break;
                        }
                    }
                     else {
                        // Operaciones sobre DFLOAT (64 bits) usando FPU
                        switch (token) {
                            case "+":
                                instrucciones.add("    fld  " + mapOperand(op1));
                                instrucciones.add("    fld  " + mapOperand(op2));
                                instrucciones.add("    fadd");
                                instrucciones.add("    fstp " + mapOperand(aux));
                                break;
                            case "-":
                                instrucciones.add("    fld  " + mapOperand(op1));
                                instrucciones.add("    fld  " + mapOperand(op2));
                                instrucciones.add("    fsub");
                                instrucciones.add("    fstp " + mapOperand(aux));
                                break;
                            case "*":
                                instrucciones.add("    fld  " + mapOperand(op1));
                                instrucciones.add("    fld  " + mapOperand(op2));
                                instrucciones.add("    fmul");
                                instrucciones.add("    fstp " + mapOperand(aux));
                                break;
                            case "/":
                                instrucciones.add("    fld  " + mapOperand(op1));
                                instrucciones.add("    fld  " + mapOperand(op2));
                                instrucciones.add("    fdiv");
                                instrucciones.add("    fstp " + mapOperand(aux));
                                break;
                        }
                    }

                    pilaValores.push(aux);
                    pilaTipos.push(tipoRes);
                    break;
                }
                case "<":
                case "<=":
                case ">":
                case ">=":
                case "==":
                case "=!": {   // tu "distinto"
                    String op2   = pilaValores.pop();
                    String tipo2 = pilaTipos.pop();
                    String op1   = pilaValores.pop();
                    String tipo1 = pilaTipos.pop();

                    if (tipo1 == null || tipo2 == null) {
                        errores.add("No se puede determinar el tipo de los operandos para comparador '" + token + "'.");
                        return;
                    }
                    if (!tipo1.equals(tipo2)) {
                        errores.add("Tipos incompatibles en la comparación (" + tipo1 + " con " + tipo2 + ").");
                        return;
                    }

                    // Booleano resultado: siempre lo modelamos como UINT 0/1
                    contadorAux++;
                    String aux = "@aux" + contadorAux;
                    auxTipos.put(aux, "UINT");

                    if ("UINT".equals(tipo1)) {
                        // ================== Comparación UINT (16 bits) ==================
                        instrucciones.add("    mov ax, " + mapOperand(op1) + "    ; op1 UINT");
                        instrucciones.add("    cmp ax, " + mapOperand(op2) + "    ; op1 ? op2");
                        instrucciones.add("    mov ax, 0");

                        switch (token) {
                            case "<":
                                instrucciones.add("    setb al                  ; op1 < op2 (unsigned)");
                                break;
                            case "<=":
                                instrucciones.add("    setbe al                  ; op1 <= op2 (unsigned)");
                                break;
                            case ">":
                                instrucciones.add("    seta al                   ; op1 > op2 (unsigned)");
                                break;
                            case ">=":
                                instrucciones.add("    setae al                  ; op1 >= op2 (unsigned)");
                                break;
                            case "==":
                                instrucciones.add("    sete al                   ; op1 == op2");
                                break;
                            case "=!":
                                instrucciones.add("    setne al                  ; op1 != op2");
                                break;
                        }

                        instrucciones.add("    mov " + mapOperand(aux) + ", ax     ; guardar bool 0/1 en " + aux);
                    } else if ("DFLOAT".equals(tipo1)) {
                        // ================== Comparación DFLOAT (64 bits) ==================
                        instrucciones.add("    fld  " + mapOperand(op1));
                        instrucciones.add("    fld  " + mapOperand(op2));
                        instrucciones.add("    fcompp");
                        instrucciones.add("    fstsw ax");
                        instrucciones.add("    sahf");
                        instrucciones.add("    mov ax, 0");

                        switch (token) {
                            case "<":
                                instrucciones.add("    setb al                   ; op1 < op2 (float)");
                                break;
                            case "<=":
                                instrucciones.add("    setbe al                  ; op1 <= op2 (float)");
                                break;
                            case ">":
                                instrucciones.add("    seta al                   ; op1 > op2 (float)");
                                break;
                            case ">=":
                                instrucciones.add("    setae al                  ; op1 >= op2 (float)");
                                break;
                            case "==":
                                instrucciones.add("    sete al                   ; op1 == op2 (float)");
                                break;
                            case "=!":
                                instrucciones.add("    setne al                  ; op1 != op2 (float)");
                                break;
                        }

                        instrucciones.add("    mov " + mapOperand(aux) + ", ax     ; bool 0/1 en " + aux);
                    } else {
                        errores.add("Comparación no soportada para tipo: " + tipo1);
                        return;
                    }

                    // En la pila dejamos el aux booleano
                    pilaValores.push(aux);
                    pilaTipos.push("UINT");
                    break;
                }

                case ":=": {
                    String destino     = pilaValores.pop();
                    String tipoDestino = pilaTipos.pop();
                    String valor       = pilaValores.pop();
                    String tipoValor   = pilaTipos.pop();

                    if (tipoDestino != null && tipoValor != null && !tipoDestino.equals(tipoValor)) {
                        errores.add("Incompatibilidad de tipos en asignación: "
                                + destino + " es " + tipoDestino
                                + " y la expresión es " + tipoValor);
                        return;
                    }

                    if ("UINT".equals(tipoDestino)) {
                        instrucciones.add("    mov ax, " + mapOperand(valor) + "    ; valor UINT");
                        instrucciones.add("    mov " + mapOperand(destino) + ", ax");
                    } else if ("DFLOAT".equals(tipoDestino)) {
                        instrucciones.add("    fld  " + mapOperand(valor) + "    ; valor DFLOAT");
                        instrucciones.add("    fstp " + mapOperand(destino));
                    } else {
                        instrucciones.add("    ; asignación a tipo desconocido: " + destino);
                    }
                    break;
                }

                default:

                    if (token.matches("L\\d+:")) {
                        instrucciones.add(token);   // p.ej. "L11:" en .code
                        break;
                    }

                    if ("BF".equals(token)) {
                        // En la pila: [ ..., condAux, labelConst ]
                        String labelConst = pilaValores.pop();  // ej "CONST_11"
                        pilaTipos.pop();                       // tipo del label, no lo usamos

                        String condAux = pilaValores.pop();    // ej "@aux3"
                        String tipoCond = pilaTipos.pop();     // deberia ser "UINT"

                        if (!"UINT".equals(tipoCond)) {
                            errores.add("BF espera condición de tipo UINT (bool 0/1); se encontró: " + tipoCond);
                            return;
                        }

                        // Recuperar el número de etiqueta a partir del nombre de constante
                        String sufijo = labelConst;
                        if (sufijo.startsWith("CONST_")) {
                            sufijo = sufijo.substring("CONST_".length());  // "11"
                        }
                        String asmLabel = "L" + sufijo;                    // "L11"

                        instrucciones.add("    mov ax, " + mapOperand(condAux) + "    ; cargar condicion");
                        instrucciones.add("    cmp ax, 0");
                        instrucciones.add("    je  " + asmLabel + "                  ; BF -> salta si es falso");
                        break;
                    }

                    if ("BI".equals(token)) {
                        String labelConst = pilaValores.pop();  // ej "CONST_15"
                        pilaTipos.pop();

                        String sufijo = labelConst;
                        if (sufijo.startsWith("CONST_")) {
                            sufijo = sufijo.substring("CONST_".length());
                        }
                        String asmLabel = "L" + sufijo;        // "L15"

                        instrucciones.add("    jmp " + asmLabel + "                  ; BI incondicional");
                        break;
                    }

                    if (token.startsWith("DECLARACION:")) {
                        // "DECLARACION: F:MAIN" o "DECLARACION: F:MAIN:F"
                        String kfun = token.substring("DECLARACION:".length()).trim();

                        // Crear lista de instrucciones para esta función
                        List<String> cuerpo = new ArrayList<>();
                        cuerpoFunciones.put(kfun, cuerpo);

                        // Apilar contexto de función
                        pilaFunciones.push(kfun);
                        pilaTieneReturn.push(false);

                        // Cambiar contexto de instrucciones a esta función
                        pilaListasInstr.push(cuerpo);
                        instrucciones = cuerpo;

                        // Etiqueta ASM de la función
                        String labelFun = kfun.replace(':', '_');
                        instrucciones.add("");
                        instrucciones.add(labelFun + ":");

                        break;
                    }

                    if (token.startsWith("FIN DECLARACION:")) {
                        String kfun = token.substring("FIN DECLARACION:".length()).trim();

                        String funToClose = pilaFunciones.pop();
                        Boolean teniaReturn = pilaTieneReturn.pop();
                        // RET implícito si nunca hubo RETURN
                        if (!teniaReturn) {
                            instrucciones.add("    ret");
                        }
                        instrucciones.add("");
                        // Volver al contexto anterior (función externa o main)
                        pilaListasInstr.pop();
                        instrucciones = pilaListasInstr.peek();
                    
                        break;
                    }

                    if ("RETURN".equals(token)) {
                        if (pilaFunciones.isEmpty()) {
                            errores.add("RETURN fuera de una declaración de función.");
                            return;
                        }

                        String funActual = pilaFunciones.peek();
                        TDSObject f = AnalizadorLexico.tablaDeSimbolos.get(funActual);
                        List<String> tiposRet = (f != null) ? f.getTiposRetorno() : null;
                        int nret = (tiposRet != null && !tiposRet.isEmpty()) ? tiposRet.size() : 1;

                        for (int i = nret; i >= 1; i--) {
                            String valor     = pilaValores.pop();
                            String tipoValor = pilaTipos.pop();

                            String tipoDestino;
                            if (tiposRet != null && tiposRet.size() >= i) {
                                tipoDestino = tiposRet.get(i - 1);
                            } else {
                                tipoDestino = tipoValor; // fallback
                            }

                            String nombreRet = getNombreRetorno(funActual, i);
                            retTipos.put(nombreRet, tipoDestino);

                            if ("UINT".equals(tipoDestino)) {
                                instrucciones.add("    mov ax, " + mapOperand(valor) + "    ; RETURN UINT #" + i);
                                instrucciones.add("    mov " + mapOperand(nombreRet) + ", ax");
                            } else if ("DFLOAT".equals(tipoDestino)) {
                                instrucciones.add("    fld  " + mapOperand(valor) + "    ; RETURN DFLOAT #" + i);
                                instrucciones.add("    fstp " + mapOperand(nombreRet));
                            } else {
                                instrucciones.add("    ; RETURN a tipo desconocido en " + nombreRet);
                            }
                        }

                        // marcar que esta función tiene RETURN explícito
                        Boolean top = pilaTieneReturn.pop();
                        pilaTieneReturn.push(true);

                        instrucciones.add("    ret");
                        break;
                    }

                    if (token.startsWith("RET#")) {
                        // Ej: "RET#1(F:MAIN)"
                        int idxPar = token.indexOf('(');
                        int idxParFin = token.lastIndexOf(')');
                        if (idxPar > 0 && idxParFin > idxPar) {
                            String numStr = token.substring(4, idxPar).trim();      // "1"
                            String kfun   = token.substring(idxPar + 1, idxParFin).trim(); // "F:MAIN"
                            int ordinal;
                            try {
                                ordinal = Integer.parseInt(numStr);
                            } catch (NumberFormatException e) {
                                errores.add("RET con ordinal inválido: " + token);
                                return;
                            }

                            TDSObject f = AnalizadorLexico.tablaDeSimbolos.get(kfun);
                            List<String> tiposRet = (f != null) ? f.getTiposRetorno() : null;
                            String tipo = null;
                            if (tiposRet != null && tiposRet.size() >= ordinal) {
                                tipo = tiposRet.get(ordinal - 1);
                            }

                            if (tipo == null) tipo = "UINT"; // por defecto

                            String nombreRet = getNombreRetorno(kfun, ordinal);
                            retTipos.put(nombreRet, tipo);

                            // Lo tratamos como un identificador que en .data tiene DW/DQ
                            pilaValores.push(nombreRet);
                            pilaTipos.push(tipo);
                        } else {
                            errores.add("Formato inválido de token de retorno: " + token);
                            return;
                        }
                        break;
                    }

                    if (token.startsWith("CALL_FUN:")) {
                        String kfun = token.substring("CALL_FUN:".length()).trim();
                        String labelFun = kfun.replace(':', '_');
                        instrucciones.add("    call " + labelFun);
                        // No tocamos pilaValores/pilaTipos; los RET#n se encargarán
                        break;
                    }

                    if (token.startsWith("LAMBDA_START:")) {
                        lambdaActualClave = token.substring("LAMBDA_START:".length()).trim(); // "X:MAIN:F"
                        instruccionesAntesDeLambda = instrucciones;
                        instruccionesLambdaActual = new ArrayList<>();
                        instrucciones = instruccionesLambdaActual;

                        continue;
                    }
                    if (token.startsWith("LAMBDA_END:")) {
                        if (lambdaActualClave != null && instruccionesLambdaActual != null) {
                            cuerpoLambda.put(lambdaActualClave, instruccionesLambdaActual);
                        }
                        instrucciones = instruccionesAntesDeLambda;

                        lambdaActualClave = null;
                        instruccionesLambdaActual = null;
                        instruccionesAntesDeLambda = null;

                        continue;
                    }
                    if (token.startsWith("CALL_LAMBDA:")) {
                        String kParam = token.substring("CALL_LAMBDA:".length()).trim(); // "X:MAIN:F"
                        String labelAsm = "lambda_" + kParam.replace(':', '_');   
                        instrucciones.add("    call " + labelAsm);
                        continue;
                    }

                    if (token.equalsIgnoreCase("tod")) {
                        String op = pilaValores.pop();
                        String tipoOp = pilaTipos.pop();
                        if (!"UINT".equals(tipoOp)) {
                            errores.add("tod solo se aplica a UINT; se encontró: " + tipoOp);
                            return;
                        } else {
                            contadorAux++;
                            String aux = "@aux" + contadorAux;
                            auxTipos.put(aux, "DFLOAT");
                            instrucciones.add("    fild " + mapOperand(op) + "    ; UINT -> DFLOAT");
                            instrucciones.add("    fstp " + mapOperand(aux));
                            pilaValores.push(aux);
                            pilaTipos.push("DFLOAT");
                        }
                        break;
                    }

                    if (token.equalsIgnoreCase("PRINT")) {
                        if (pilaValores.isEmpty()) {
                            errores.add("PRINT encontrado sin operando previo");
                            return;
                        }
                        String valor  = pilaValores.pop();
                        String tipoVal = pilaTipos.pop();
                        String opAsm  = mapOperand(valor);

                        if ("UINT".equals(tipoVal)) {
                            // Carga del UINT de 16 bits y extensión a 32 bits para printf
                            instrucciones.add("    xor eax, eax");
                            instrucciones.add("    mov ax, " + opAsm + "    ; extender UINT16 -> UINT32 en EAX");
                            instrucciones.add("    invoke printf, cfm$(\"%u \\n\"), eax");
                        }
                        else if ("DFLOAT".equals(tipoVal)) {
                            instrucciones.add("    invoke printf, cfm$(\"%.20Lf \\n\"), " + opAsm);
                        } else if ("CADENA".equals(tipoVal)) {
                            instrucciones.add("    invoke printf, ADDR " + opAsm);
                        } else {
                            errores.add("Tipo desconocido en PRINT: " + tipoVal);
                            return;
                        }
                        break;
                    }

                    // ---------- CADENA desde la TDS ----------
                    TDSObject objCad = AnalizadorLexico.tablaDeSimbolos.get(token);
                    if (objCad != null && "CADENA".equalsIgnoreCase(objCad.getTipoVariable())) {
                        String constName = getConstCadenaName(token);
                        registrarCadena(constName, token);  // texto sin &..&
                        pilaValores.push(constName);
                        pilaTipos.push("CADENA");
                        break;
                    }

                    if (esLiteralNumerico(token)) {
                        String valor = token;
                        String tipoLit = valor.contains(".") ? "DFLOAT" : "UINT";
                        String constName = getConstName(valor, tipoLit);
                        pilaValores.push(constName);
                        pilaTipos.push(tipoLit);
                        break;
                    }

                    // ---------- Identificadores de la TDS ----------
                    String tipoToken = determinarTipoToken(token);
                    pilaValores.push(token);
                    pilaTipos.push(tipoToken);
                    break;
            }
        }

        // ===================== Sección .data =====================
        List<String> dataSection = new ArrayList<>();
        dataSection.add(".data");

        for (Map.Entry<String, TDSObject> entry : AnalizadorLexico.tablaDeSimbolos.entrySet()) {
            String lexema = entry.getKey();
            TDSObject obj = entry.getValue();
            String tipo = obj.getTipoVariable();
            if (tipo == null) continue;

            // 1) No declarar cadenas acá (van aparte)
            if ("CADENA".equalsIgnoreCase(tipo)) continue;

            // 2) No declarar constantes numéricas como variables
            //    (las CTE se manejan con CONST_... en otra parte)
            if (lexema.matches("-?[0-9]+(\\.[0-9]+)?")) continue;

            String asmName = mapOperand(lexema);
            if ("UINT".equals(tipo)) {
                dataSection.add(String.format("    %-16s DW 0", asmName));
            } else if ("DFLOAT".equals(tipo)) {
                dataSection.add(String.format("    %-16s DQ 0.0", asmName));
            }
        }


        // Constantes numericas
        for (Map.Entry<String, String> entry : constMap.entrySet()) {
            String key = entry.getKey();   // "valor|tipo"
            String name = entry.getValue();
            int idx = key.lastIndexOf('|');
            String valor = key.substring(0, idx);
            String tipo = key.substring(idx + 1);
            if ("UINT".equals(tipo)) {
                dataSection.add(String.format("    %-16s DW %s", name, valor));
            } else if ("DFLOAT".equals(tipo)) {
                dataSection.add(String.format("    %-16s DQ %s", name, valor));
            }
        }


        for (Map.Entry<String, String> ret : retTipos.entrySet()) {
            String asmName = mapOperand(ret.getKey());
            String tipo = ret.getValue();
            if ("UINT".equals(tipo)) {
                dataSection.add(String.format("    %-16s DW 0", asmName));
            } else if ("DFLOAT".equals(tipo)) {
                dataSection.add(String.format("    %-16s DQ 0.0", asmName));
            } else {
                dataSection.add(String.format("    %-16s DW 0    ; tipo retorno desconocido", asmName));
            }
        }

        // Auxiliares de expresiones
        for (Map.Entry<String, String> aux : auxTipos.entrySet()) {
            String asmName = mapOperand(aux.getKey());
            String tipo = aux.getValue();
            if ("UINT".equals(tipo)) {
                dataSection.add(String.format("    %-16s DW 0", asmName));
            } else {
                dataSection.add(String.format("    %-16s DQ 0.0", asmName));
            }
        }

        // Mensajes de error y mensaje final
        dataSection.add("    msgOverflowAdd    DB \"Error: overflow en suma de enteros\", 13, 10, 0");
        dataSection.add("    msgOverflowMult   DB \"Error: overflow en multiplicacion de enteros\", 13, 10, 0");
        dataSection.add("    msgNegativeSub    DB \"Error: resta con resultado negativo en enteros sin signo\", 13, 10, 0");
        dataSection.add("    msgFinOk          DB \"Programa termino sin errores\", 13, 10, 0");

        // Cadenas del lenguaje (CADENA) con CRLF
        for (Map.Entry<String, String> cad : cadenas.entrySet()) {
            String nombre = cad.getKey();
            String texto = cad.getValue();
            String esc = texto.replace("\"", "\"\"");
            dataSection.add(String.format("    %-16s DB \"%s\", 13, 10, 0", nombre, esc));
        }

        dataSection.add("");

        // ===================== Seccion .code =====================
        List<String> codeSection = new ArrayList<>();
        codeSection.add(".code");
        for (Map.Entry<String, List<String>> entry : cuerpoFunciones.entrySet()) {
            for (String inst : entry.getValue()) {
                codeSection.add(inst);
            }
        }

        if (!cuerpoLambda.isEmpty()) {
            codeSection.add("");
            for (Map.Entry<String, List<String>> e : cuerpoLambda.entrySet()) {
                String clave = e.getKey(); // "X:MAIN:F"
                List<String> cuerpo = e.getValue();
                String labelAsm = "lambda_" + clave.replace(':', '_');

                codeSection.add(labelAsm + ":");
                for (String inst : cuerpo) {
                    codeSection.add(inst);
                }
                codeSection.add("    ret");
                codeSection.add("");
            }
        }
        codeSection.add("start:");

        // Instrucciones generadas desde la polaca
        codeSection.addAll(instruccionesMain);

        // Mensaje final y salida normal
        codeSection.add("    invoke printf, ADDR msgFinOk");
        codeSection.add("    exit");
        codeSection.add("");

        // Rutas de error: imprimen mensaje y salen
        codeSection.add("overflowAdd:");
        codeSection.add("    invoke printf, ADDR msgOverflowAdd");
        codeSection.add("    exit");
        codeSection.add("");
        codeSection.add("overflowMult:");
        codeSection.add("    invoke printf, ADDR msgOverflowMult");
        codeSection.add("    exit");
        codeSection.add("");
        codeSection.add("negativeSub:");
        codeSection.add("    invoke printf, ADDR msgNegativeSub");
        codeSection.add("    exit");
        codeSection.add("");
        codeSection.add("end start");

        // ===================== Header final + escritura =====================
        List<String> asmLines = new ArrayList<>();
        asmLines.add(".386");
        asmLines.add(".MODEL flat, stdcall");
        asmLines.add("option casemap:none");
        asmLines.add("include \\masm32\\include\\masm32rt.inc");
        asmLines.add("includelib  \\masm32\\lib\\kernel32.lib");
        asmLines.add("includelib \\masm32\\lib\\masm32.lib");
        asmLines.add("dll_dllcrt0 PROTO C");
        asmLines.add("printf PROTO C : VARARG");
        asmLines.add("");
        asmLines.addAll(dataSection);
        asmLines.addAll(codeSection);

        Path ruta = Paths.get(salidaAsm);
        try (BufferedWriter bw = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8)) {
            for (String l : asmLines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException ex) {
            errores.add("No se pudo escribir el fichero ASM: " + ex.getMessage());
        }
    }


    /** Determina el tipo de un token consultando la TDS y, en último caso, la forma. */
    private static String determinarTipoToken(String token) {
        if (token == null) return null;
        TDSObject obj = AnalizadorLexico.tablaDeSimbolos.get(token);
        if (obj != null) return obj.getTipoVariable();
        // Si no está en la TDS pero parece numérico crudo (sin sufijo)
        if (token.matches("-?[0-9]+(\\.[0-9]+)?")) {
            return token.contains(".") ? "DFLOAT" : "UINT";
        }
        return null;
    }

    /** True si el token tiene forma de literal numérico (con o sin sufijo UI/F). */
    private static boolean esLiteralNumerico(String token) {
        if (token == null) 
            return false;
        return token.matches("-?[0-9]+(\\.[0-9]+)?");
    }


    private static String getNombreRetorno(String kfun, int ordinal) {
        if (kfun == null) kfun = "fun_sin_nombre";
        String base = kfun.replace(':', '_');
        return "@ret_" + base + "_" + ordinal;
    }

    /** Mapea un nombre de polaca (mangleado, CONST_ o @aux) a identificador ASM válido. */
    private static String mapOperand(String name) {
        if (name == null) return "";
        name = name.trim();
        if (name.startsWith("@")) {
            return name.replace(':', '_');
        }
        if (name.startsWith("CONST_")) {
            return name;
        }
        if (name.contains(":")) {
            name = name.replace(':', '_');
        }
        if (!name.startsWith("_") && !name.startsWith("@")) {
            return "_" + name;
        }
        return name;
    }

    private static String getConstName(String valor, String tipo) {
        String key = valor + "|" + tipo;
        String existing = constMap.get(key);
        if (existing != null) return existing;
        String limpio = valor.replace('-', 'N').replace('.', '_');
        String name = "CONST_" + limpio;
        constMap.put(key, name);
        return name;
    }

    /** Devuelve un nombre de constante para una cadena. */
    private static String getConstCadenaName(String texto) {
        String limpio = texto.replaceAll("[^a-zA-Z0-9]", "_");
        return "CONST_" + limpio;
    }

    private static void registrarCadena(String nombre, String texto) {
        cadenas.putIfAbsent(nombre, texto);
    }
}
