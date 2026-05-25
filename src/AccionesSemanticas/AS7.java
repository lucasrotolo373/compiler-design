package AccionesSemanticas;
import AnalizadorLexico.*;

public class AS7 extends AccionSemantica {
    // Revisa el límite del número con punto flotante de 64 bits. Si está dentro del rango, chequea tabla de símbolos y lo agrega si no existe.
    // No avanza el último carácter en la cadena (no leo el carácter que vino, lo evalúo en la siguiente lectura).

    public static final double MAX_VALUE = 1.7976931348623157E308;
    public static final double MIN_POSITIVE = 2.2250738585072014E-308;
    public static final double MIN_VALUE = -1.7976931348623157E308;
    public static final double MAX_NEGATIVE = -2.2250738585072014E-308;
    public static final double ZERO = 0.0;

    @Override
    public void ejecutar(char entrada) {
        double numero;
        String lex = AnalizadorLexico.buffer;
        boolean error = false;
        lex = lex.replace('D', 'E');
        numero = Double.parseDouble(lex);
        if (!((numero > MIN_VALUE && numero < MAX_NEGATIVE) || (numero > MIN_POSITIVE && numero < MAX_VALUE) || (numero == ZERO))) {
            String aviso = String.format("AL - Línea %d:«%s» flotante fuera de rango.", AnalizadorLexico.numLinea, numero);
            AnalizadorLexico.listaErrores.add(aviso);
            AnalizadorLexico.token = AnalizadorLexico.getIdToken("CTE");
            error = true;
        }

        if (!error) {
            //AnalizadorLexico.tablaDeSimbolos.putIfAbsent(lex, new TDSObject("DFLOAT"));
            AnalizadorLexico.ultimoEsUint = false;
            AnalizadorLexico.token = AnalizadorLexico.getIdToken("CTE");
            AnalizadorLexico.refTDS = lex;
        }
    }
}