package AnalizadorLexico;
import java.util.*;
import AccionesSemanticas.*;

public class AnalizadorLexico {

    public static String archivo;
    public static String buffer = "";
    public static int indiceArchivo = 0;
    public static int numLinea = 1;
    public static int lineaToken = 1; //Linea del token recien emitido
    public static int lineaTokenPrevio = 1; //Linea del token anterior
    public static int comienzoLineaCadena = -1;
    public static int token = -1;
    public static String refTDS = "";
    public static int estadoActual;
    public static boolean ultimoEsUint = false;
    public static HashMap<String, TDSObject> tablaDeSimbolos = new HashMap<String, TDSObject>();
    public static LinkedList<String> listaWarnings = new LinkedList<>();
    public static LinkedList<String> listaErrores = new LinkedList<>();
    public static LinkedList<String> salidaTokens = new LinkedList<>();

    public static final int MAX_ID_VALUE = 20;

    // Estados (filas) x Símbolos (columnas)

    private static final AccionSemantica AS1 = new AS1();
    private static final AccionSemantica AS2 = new AS2();
    private static final AccionSemantica AS3 = new AS3();
    private static final AccionSemantica AS4 = new AS4();
    private static final AccionSemantica AS5 = new AS5();
    private static final AccionSemantica AS6 = new AS6();
    private static final AccionSemantica AS7 = new AS7();
    private static final AccionSemantica AS8 = new AS8();
    private static final AccionSemantica AS9 = new AS9();
    private static final AccionSemantica AS10 = new AS10();
    private static final AccionSemantica AS11 = new AS11();
    private static final AccionSemantica AS12 = new AS12();
    private static final AccionSemantica AS13 = new AS13();
    private static final AccionSemantica AS14 = new AS14();
    private static final AccionSemantica AS15= new AS15();


    private static final int[][] MatrizTransicionEstados = {
            // "F" -> -1, "Error" -> -2
            {-1, -1,  1,  1,  2,  3,  4,  5,  0, 12, 11,  0, -2,  2, 10, -2, -2, 13,  2,  2, 16, -2},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
            {-1, -1, -1, -1,  2, -1,  2, -1, -1, -1, -1, -1, -1,  2, -1, -1,  2, -1,  2,  2, -1, -1},
            { 3,  3,  3,  3,  3, -1,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3,  3},
            {-2, -2, -2, -2, -2, -2,  4,  6, -2, -2, -2, -2, -2, -2, -1, -2, -2, -2, 14, -2, -2, -2},
            {-1, -1, -1, -1, -1, -1,  6, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
            {-1, -1, -1, -1, -1, -1,  6, -1, -1, -1, -1, -1, -1,  7, -1, -1, -1, -1, -1, -1, -1, -1},
            {-2,  8, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2,  8, -2, -2, -2, -2},
            {-2, -2, -2, -2, -2, -2,  9, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2},
            {-1, -1, -1, -1, -1, -1,  9, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
            {10, 10, 10, 10, 10, 10, 10, 10,  0, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10},
            {-2, -2, -2, -2, -2, -2, -2, -2, -2, -1, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
            {-2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, 15, -2, -2},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1},
            {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 16, -1},
    };


    private static final Object[][] MatrizAccionSemantica = {
            {AS12, AS12, AS1,  AS1,  AS1,  AS9,  AS1,  AS1,  AS3,  AS1,  AS1,  AS3,  null, AS1,   AS3,  AS15, AS15, AS1,  AS1,  AS1,  AS1,  AS15},
            {AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS8, AS11,  AS8,  AS8,  AS8,  AS8,   AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS8 },
            {AS4,  AS4,  AS4,  AS4,  AS2,  AS4,  AS2,  AS4,  AS4,  AS4,  AS4,  AS4,  AS4,  AS2,   AS4,  AS4,  AS2,  AS4,  AS2,  AS2,  AS4,  AS4 },
            {AS13, AS13, AS13, AS13, AS13, AS10, AS13, AS13, AS13, AS13, AS13, AS13, AS13, AS13,  AS13, AS13, AS13, AS13, AS13, AS13, AS13, AS13},
            {AS6,  AS6,  AS6,  AS6,  AS6,  AS6,  AS2,  AS2,  AS6,  AS6,  AS6,  AS6,  AS6,  AS6,   AS6,  AS6,  AS6,  AS6,  AS2,  AS6,  AS6,  AS6 } ,
            {AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS2,  AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS8,   AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS8 },
            {AS7,  AS7,  AS7,  AS7,  AS7,  AS7,  AS2,  AS7,  AS7,  AS7,  AS7,  AS7,  AS7,  AS2,   AS7,  AS7,  AS7,  AS7,  AS7,  AS7,  AS7,  AS7 },
            {AS14, AS2,  AS14, AS14, AS14, AS14, AS14, AS14, AS14, AS14, AS14, AS14, AS14, AS14,  AS14, AS14, AS14, AS2,  AS14, AS14, AS14, AS14},
            {AS14, AS14, AS14, AS14, AS14, AS14, AS2,  AS14, AS14, AS14, AS14, AS14, AS14, AS14,  AS14, AS14, AS14, AS14, AS14, AS14, AS14, AS14},
            {AS7,  AS7,  AS7,  AS7,  AS7,  AS7,  AS2,  AS7,  AS7,  AS7,  AS7,  AS7,  AS7,  AS7,   AS7,  AS7,  AS7,  AS7,  AS7,  AS7,  AS7,  AS7 },
            {AS3,  AS3,  AS3,  AS3,  AS3,  AS3,  AS3,  AS3,  AS3,  AS3,  AS3,  AS3,  AS3,  AS3,   AS3,  AS3,  AS3,  AS3,  AS3,  AS3,  AS3,  AS3 },
            {AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS11, AS8,  AS8,  AS8,  AS8,   AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS8},
            {AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS11, AS8,  AS8,  AS8,  AS8,   AS8,  AS11, AS8,  AS8,  AS8,  AS8,  AS8,  AS8 },
            {AS8,  AS8,  AS8,  AS11, AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS8,   AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS8,  AS8 },
            {AS6,  AS6,  AS6,  AS6,  AS6,  AS6,  AS6,  AS6,  AS6,  AS6,  AS6,  AS6,  AS6,  AS6,   AS6,  AS6,  AS6,  AS6,  AS6,  AS2,  AS6,  AS6 },
            {AS6,  AS6,  AS6,  AS6,  AS6,  AS6,  AS6,  AS6,  AS6,  AS6,  AS6,  AS6,  AS6,  AS6,   AS6,  AS6,  AS6,  AS6,  AS6,  AS6,  AS6,  AS6 },
            {AS5,  AS5,  AS5,  AS5,  AS5,  AS5,  AS5,  AS5,  AS5,  AS5,  AS5,  AS5,  AS5,  AS5,   AS5,  AS5,  AS5,  AS5,  AS5,  AS5,  AS2,  AS5 }
    };

    public static final LinkedList<String> palabrasReservadas;
    static {
        palabrasReservadas = new LinkedList<>();
        palabrasReservadas.add("var");
        palabrasReservadas.add("do");
        palabrasReservadas.add("while");
        palabrasReservadas.add("lambda");
        palabrasReservadas.add("if");
        palabrasReservadas.add("else");
        palabrasReservadas.add("endif");
        palabrasReservadas.add("print");
        palabrasReservadas.add("return");
        palabrasReservadas.add("uint");
        palabrasReservadas.add("dfloat");
        palabrasReservadas.add("cv");
        palabrasReservadas.add("cr");
        palabrasReservadas.add("le");
        palabrasReservadas.add("tod");
    }


    public static Map<Integer, String> listaTokens;
    static {
        listaTokens = new HashMap<>();
        listaTokens.put(257, "VAR");
        listaTokens.put(258, "DO");
        listaTokens.put(259, "WHILE");
        listaTokens.put(260, "LAMBDA");
        listaTokens.put(261, "IF");
        listaTokens.put(262, "ELSE");
        listaTokens.put(263, "ENDIF");
        listaTokens.put(264, "PRINT");
        listaTokens.put(265, "RETURN");
        listaTokens.put(266, "<=");//MENOR_IGUAL
        listaTokens.put(267, ">=");//MAYOR_IGUAL
        listaTokens.put(268, "=!");//DISTINTO
        listaTokens.put(269, "==");//IGUAL
        listaTokens.put(270, ":=");//ASIGNACION_SIMPLE
        listaTokens.put(271, "->");//FLECHITA
        listaTokens.put(272, "UINT");
        listaTokens.put(273, "DFLOAT");
        listaTokens.put(274, "ID");
        listaTokens.put(275, "CADENA");
        listaTokens.put(276, "CTE");
        listaTokens.put(277, "CV");
        listaTokens.put(278, "CR");
        listaTokens.put(279, "LE");
        listaTokens.put(280, "TOD");
        listaTokens.put((int) '+', "+");
        listaTokens.put((int) '-', "-");
        listaTokens.put((int) '>', ">");
        listaTokens.put((int) '<', "<");
        listaTokens.put((int) '(', "(");
        listaTokens.put((int) ')', ")");
        listaTokens.put((int) '*', "*");
        listaTokens.put((int) '/', "/");
        listaTokens.put((int) ';', ";");
        listaTokens.put((int) '_', "_");
        listaTokens.put((int) '{', "{");
        listaTokens.put((int) '}', "}");
        listaTokens.put((int) '=', "=");
        listaTokens.put((int) '.', ".");
        listaTokens.put((int) ',', ",");
        //listaTokens.put(30, "@"); MISMO QUE %
        //listaTokens.put(28, "&");
        //listaTokens.put(32, "%"); TIENE QUE TENER token?
    }


    //Lo usamos en las acciones semanticas para obtener el token a partir de su tipo
    public static final Map<String,Integer> listaTokenInvertido = new HashMap<>();
    static {
        for (Map.Entry<Integer,String> e : listaTokens.entrySet()) {
            listaTokenInvertido.put(e.getValue(), e.getKey());
        }
    }

    public static Integer getIdToken(String entrada) {
        return listaTokenInvertido.getOrDefault(entrada, null);
    }


    private static int clasificar(char ch) {
        // grupo de operadores o separadores simples
        if (ch == '*' || ch == '/' || ch == '(' || ch == ')' ||
                ch == '{' || ch == '}' || ch == ';' || ch == '_' || ch == ',') {  //AGREGAMOS LA COMA
            return 0;
        }
        if (ch == '+') return 1;
        if (ch == '<') return 2;
        if (ch == '>') return 3;

        // letras mayúsculas con distinción de D, I y U
        if (Character.isUpperCase(ch)) {
            if (ch == 'D') return 13;
            if (ch == 'U') return 18;
            if (ch == 'I') return 19;
            return 4;  // cualquier otra mayúscula
        }

        // resto de símbolos
        if (ch == '&') return 5;
        if (Character.isDigit(ch)) return 6;
        if (ch == '.') return 7;
        if (ch == '\n') return 8;
        if (ch == '=') return 9;
        if (ch == ':') return 10;
        if (ch == ' ' || ch == '\t') return 11;
        if (ch == '$') return 12;
        if (ch == '@') return 14;
        if (ch == '!') return 15;
        if (ch == '%') return 16;
        if (ch == '-') return 17;

        // letras minúsculas
        if (Character.isLowerCase(ch)) return 20;

        // cualquier otro carácter desconocido (#, ¡, '", etc.)
        return 21;
    }

    public static int yylex() {
        buffer = "";
        refTDS  = "";
        token   = -1;
        estadoActual  = 0;

        int comienzoDeLineaToken = numLinea; //Linea donde empieza este token

        while (token == -1 && indiceArchivo < archivo.length()) {
            char ch = archivo.charAt(indiceArchivo);

            if (estadoActual == 0 && buffer.isEmpty()) {
                comienzoDeLineaToken = numLinea; //No empece a armar token, capturo linea actual
            }
            int columna = clasificar(ch);
            // obtener transición y acción
            int siguienteEstado = MatrizTransicionEstados[estadoActual][columna];
            // carácter no reconocido en ninguna columna → registrar error y avanzar
            AccionSemantica accion = (AccionSemantica) MatrizAccionSemantica[estadoActual][columna];

            // ejecutar acción si existe
            if (accion != null) {
                accion.ejecutar(ch);
            } else {
                // si no hay acción, consumir el carácter para no quedar en bucle
                indiceArchivo++;
            }

            // interpretar el siguiente estado
            if (siguienteEstado == -2) {
                // -2 → condición de error o fin de archivo
                // -2 -> condición de error o fin de archivo
                if (ch == '$'){
                    token = 0;
                    break;
                }
                estadoActual = 0;
                token = -1;
            } else if (siguienteEstado == -1) {
                // -1 → estado final: la acción ya debio fijar 'token'
                // -1 -> estado final: la acción ya debio fijar 'token'
                break;
            } else {
                // pasar al nuevo estado y continuar
                estadoActual = siguienteEstado;
            }
        }

        if (token != -1) {
            // 1) Decidir la línea para este token ya cerrado
            int lineaParaEsteToken;
            if (token == 0) {
                lineaParaEsteToken = numLinea;
            } else if (token == 36) { // CADENA multilínea
                lineaParaEsteToken = comienzoLineaCadena;
            } else {
                lineaParaEsteToken = comienzoDeLineaToken;
            }
            // 2) Actualizar fotos para el parser
            // 2) Actualizar para el parser
            lineaTokenPrevio = lineaToken;        
            lineaToken       = lineaParaEsteToken; 
            
            if (token == 0) {
                salidaTokens.add(
                    String.format("AL - Línea %d Token: %d End Of File", lineaToken, token));
            } else if (token == 36) {
                String nombreToken = listaTokens.getOrDefault(token, buffer);
                salidaTokens.add(
                    String.format("AL - Línea %d Token: %d Lexema: %s",
                                  lineaToken, token, nombreToken));
            } else {
                String nombreToken = listaTokens.getOrDefault(token, buffer);
                salidaTokens.add(
                    String.format("AL - Línea %d Token: %d Lexema: %s",
                                  lineaToken, token, nombreToken));
            }
        }

        return token;
    }

}