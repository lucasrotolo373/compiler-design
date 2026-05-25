package AccionesSemanticas;
import AnalizadorLexico.*;

public class AS13 extends AccionSemantica{

    // Acción semántica que concatena el caracter leído al buffer. En caso de encontrarse con saltos de línea, este se
    // elimina. Se utiliza mientras se leen cadenas de caracteres.

    @Override
    public void ejecutar(char ch) {
        int ultimaPos = AnalizadorLexico.archivo.length() - 1;
        int i = AnalizadorLexico.indiceArchivo;
        if (ch == '\n') {
            AnalizadorLexico.numLinea++;
        }
        else if (ch == '$') {
            if (i == ultimaPos) {
                // Es el EOF: cortar limpio
                AnalizadorLexico.listaErrores.add(String.format(
                        "AL - Línea %d: Cadena de caracteres sin cerrar antes de fin de archivo",
                        AnalizadorLexico.numLinea));
                AnalizadorLexico.token = 0;
                return;
            } else {
                AnalizadorLexico.buffer += String.valueOf(ch);
                AnalizadorLexico.indiceArchivo++;
                return;
            }
        }
        AnalizadorLexico.buffer += String.valueOf(ch);
        AnalizadorLexico.indiceArchivo++;
    }
}