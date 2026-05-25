package AnalizadorLexico;

import AccionesSemanticas.AS7;

public final class TablaSimbolosControl {

    private TablaSimbolosControl() {}

    public static String registrarDfloatPositiva(String lexPos) {
        final double numero;
        try {
            numero = Double.parseDouble(lexPos);
        } catch (NumberFormatException ex) {
            AnalizadorLexico.listaErrores.add("ERROR línea " + AnalizadorLexico.numLinea +
                    ": DFLOAT inválida «" + lexPos + "».");
            return lexPos;
        }

        final boolean enRango =
            ((numero > AS7.MIN_VALUE && numero < AS7.MAX_NEGATIVE) ||
             (numero > AS7.MIN_POSITIVE && numero < AS7.MAX_VALUE) ||
             (numero == AS7.ZERO));

        if (!enRango) {
            AnalizadorLexico.listaErrores.add("ERROR línea " + AnalizadorLexico.numLinea +
                    ": dfloat fuera de rango: " + lexPos);
            return lexPos;
        }

        String canonico = Double.toString(numero);
        if ("-0.0".equals(canonico)) canonico = "0.0";

        AnalizadorLexico.tablaDeSimbolos.putIfAbsent(canonico, new TDSObject("DFLOAT"));
        return canonico;
    }

    public static String registrarDfloatNegativo(String lexPos) {
        final String lexNeg = lexPos.startsWith("-") ? lexPos : "-" + lexPos;

        final double numeroNeg;
        try {
            numeroNeg = Double.parseDouble(lexNeg);
        } catch (NumberFormatException exn) {
            AnalizadorLexico.listaErrores.add("ERROR línea " + AnalizadorLexico.numLinea +
                    ": DFLOAT inválida «" + lexNeg + "».");
            return lexPos; 
        }

        final boolean enRango =
            ((numeroNeg > AS7.MIN_VALUE && numeroNeg < AS7.MAX_NEGATIVE) ||
             (numeroNeg > AS7.MIN_POSITIVE && numeroNeg < AS7.MAX_VALUE) ||
             (numeroNeg == AS7.ZERO));

        if (!enRango) {
            AnalizadorLexico.listaErrores.add("ERROR línea " + AnalizadorLexico.numLinea +
                    ": dfloat fuera de rango: " + lexNeg);
            return lexPos;
        }

        String canonicoNeg = Double.toString(numeroNeg);
        if ("-0.0".equals(canonicoNeg)) canonicoNeg = "0.0";

        AnalizadorLexico.tablaDeSimbolos.putIfAbsent(canonicoNeg, new TDSObject("DFLOAT"));
        return canonicoNeg;
    }

    public static String guardarUint(String lex) {
        if(!lex.isEmpty())
            AnalizadorLexico.tablaDeSimbolos.putIfAbsent(lex, new TDSObject("UINT"));
        return lex;
    }

    public static String errorEnteroNegativo(String lex) {
        AnalizadorLexico.listaErrores.add(
            "ERROR línea " + AnalizadorLexico.numLinea + ": no se admite constante negativa para tipo UINT (" + lex + ")."
        );
        return lex;
    }

}
