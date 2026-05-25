package AccionesSemanticas;
import AnalizadorLexico.*;

public class AS6 extends AccionSemantica {

    public static final int MIN_ENTERO_SIN_SIGNO = 0;
    public static final int MAX_ENTERO_SIN_SIGNO = (int)Math.pow(2, 16) - 1;

    public void ejecutar(char entrada){

        String lex = AnalizadorLexico.buffer;
        if (!lex.endsWith("UI")){
            AnalizadorLexico.listaErrores.add(String.format(
                    "ERROR línea %d: constante sin sufijo 'UI' «%s».", AnalizadorLexico.numLinea, lex));
            AnalizadorLexico.token = AnalizadorLexico.getIdToken("CTE");
            return;
        }
        // Elimino UI
        String parteNumerica = lex.substring(0, lex.length() - 2);
        try{
            int valor = Integer.parseInt(parteNumerica);
            if (valor < MIN_ENTERO_SIN_SIGNO || valor > MAX_ENTERO_SIN_SIGNO){
                AnalizadorLexico.listaErrores.add(String.format(
                        "ERROR línea %d: %s fuera de rango uint [0..2^16-1].", AnalizadorLexico.numLinea, lex));
                AnalizadorLexico.token = AnalizadorLexico.getIdToken("CTE");
                return;
            }
            String clave = String.valueOf(valor);
            // No insertamos acá
            AnalizadorLexico.ultimoEsUint = true;                
            AnalizadorLexico.refTDS = clave;                  
            AnalizadorLexico.token  = AnalizadorLexico.getIdToken("CTE");
        } catch (NumberFormatException e){
            AnalizadorLexico.listaErrores.add(String.format(
                    "AL - Línea %d: constante UI inválida «%s».", AnalizadorLexico.numLinea, lex));
            AnalizadorLexico.token = -1;
        }
    }
}
