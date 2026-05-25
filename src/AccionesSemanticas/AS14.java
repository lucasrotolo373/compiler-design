package AccionesSemanticas;
import AnalizadorLexico.*;

public class AS14 extends AccionSemantica{
    @Override
    public void ejecutar(char ch) {
        String lex = AnalizadorLexico.buffer;
        char prev = lex.isEmpty() ? '\0' : lex.charAt(lex.length() - 1);
        if (prev == '.') {
            AnalizadorLexico.listaErrores.add(String.format(
                    "AL - Línea %d: «%s» Se espera un digito luego de . (punto).",
                    AnalizadorLexico.numLinea, lex));
        }
        if (prev == 'D') {
            AnalizadorLexico.listaErrores.add(String.format(
                    "AL - Línea %d: «%s» Se espera el signo del exponente (simbolo + o -).",
                    AnalizadorLexico.numLinea, lex));
        }
        if ((prev == '+') || (prev == '-')) {
            AnalizadorLexico.listaErrores.add(String.format(
                    "AL - Línea %d: «%s» Se espera un digito luego de un simbolo + o -.",
                    AnalizadorLexico.numLinea, lex));
        }
        AnalizadorLexico.token = AnalizadorLexico.getIdToken("CTE");;
    }
}