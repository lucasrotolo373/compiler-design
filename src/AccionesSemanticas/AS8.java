package AccionesSemanticas;

import AnalizadorLexico.AnalizadorLexico;

public class AS8 extends AccionSemantica {

    // Devolver token correspondiente si existe y no avanza en la cadena (no leo el carácter que vino, lo evaluó en la siguiente
    // lectura).
    @Override
    public void ejecutar(char entrada) {
        Integer tokenAux = AnalizadorLexico.getIdToken(AnalizadorLexico.buffer);
        if (tokenAux != null){
            AnalizadorLexico.token = tokenAux;
        } else {
            int i = AnalizadorLexico.indiceArchivo;
            // retroceder saltando blancos y saltos
            int j = i - 1;
            while (j >= 0) {
                char cj = AnalizadorLexico.archivo.charAt(j);
                if (cj != ' ' && cj != '\t' && cj != '\n' && cj != '\r') break;
                j--;
            }
            String simb;
            if (j < 0) {
                simb = "Comienzo de archivo";
            } else {
                char c = AnalizadorLexico.archivo.charAt(j);
                if (c == '\n')
                    simb = "\\n";
                else if (c == '\t')
                    simb = "\\t";
                else
                    simb = String.valueOf(c);
            }
            AnalizadorLexico.listaErrores.add(String.format(
                    "AL - Línea %d: «%s» este token no existe.",
                    AnalizadorLexico.numLinea, simb
            ));
            AnalizadorLexico.token = -1;
        }
    }
}