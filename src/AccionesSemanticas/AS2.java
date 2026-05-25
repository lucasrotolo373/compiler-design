package AccionesSemanticas;

import AnalizadorLexico.AnalizadorLexico;

public class AS2 extends AccionSemantica{

    // Acción semántica que concatena el carácter leído al buffer. Se utiliza mientras se lee un token que ocupa varios
    // caracteres consecutivos.

    @Override
    public void ejecutar(char entrada) {

        AnalizadorLexico.buffer += String.valueOf(entrada);
        AnalizadorLexico.indiceArchivo++;
    }
}
