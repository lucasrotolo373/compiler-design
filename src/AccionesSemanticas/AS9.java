package AccionesSemanticas;

import AnalizadorLexico.AnalizadorLexico;

public class AS9 extends AccionSemantica {
    // Inicializar buffer ignorando el carácter leído.

    @Override
    public void ejecutar(char entrada) {
        AnalizadorLexico.buffer = "";
        AnalizadorLexico.comienzoLineaCadena = AnalizadorLexico.numLinea;
        AnalizadorLexico.indiceArchivo++;
    }
}
