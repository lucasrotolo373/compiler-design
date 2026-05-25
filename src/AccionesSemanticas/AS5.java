package AccionesSemanticas;
import AnalizadorLexico.*;

public class AS5 extends AccionSemantica {
    // Se reconoce si la palabra es una palabra reservada. En caso de ser así se valida
    // y se devuelve el token.

    @Override
    public void ejecutar(char entrada) {
        String lex = AnalizadorLexico.buffer;
        Integer tok = AnalizadorLexico.getIdToken(lex.toUpperCase());
        boolean esPalRes = AnalizadorLexico.palabrasReservadas.contains(lex);
        if(esPalRes){
            AnalizadorLexico.token = tok;
        }else {
            AnalizadorLexico.listaErrores.add(String.format(
                    "AL - Línea %d: «%s» no es palabra reservada (los identificadores deben escribirse en MAYÚSCULAS).",
                    AnalizadorLexico.numLinea, lex));
            AnalizadorLexico.token = AnalizadorLexico.getIdToken("ID");
        }
    }
}
