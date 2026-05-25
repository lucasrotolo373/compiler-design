package AccionesSemanticas;

import AnalizadorLexico.AnalizadorLexico;


public class AS3 extends AccionSemantica {

    // Acción semántica para ignorar caracteres. Incrementa la línea si se encuentra un salto de línea, y consume el
    // carácter sin añadirlo al buffer.

    public void ejecutar(char ch) {
        // Atajo: última posición del buffer (sentinela EOF)
        int ultimaPos = AnalizadorLexico.archivo.length() - 1;
        int i = AnalizadorLexico.indiceArchivo;

        if (ch == '\n') {
            // Fin de comentario + avanzar línea
            AnalizadorLexico.numLinea++;
            AnalizadorLexico.estadoActual = 0;
            AnalizadorLexico.indiceArchivo++;  // consumís el \n
            return;
        } else if (ch == '$') {
            if (i == ultimaPos) {
                // Es el EOF: cortar limpio
                AnalizadorLexico.listaWarnings.add(String.format(
                        "WARNING línea %d: comentario sin salto de línea antes de fin de archivo",
                        AnalizadorLexico.numLinea
                ));
                AnalizadorLexico.token = 0;
                return;
            } else {
                AnalizadorLexico.indiceArchivo++;
                return;
            }
        }
        AnalizadorLexico.indiceArchivo++;
    }
}
