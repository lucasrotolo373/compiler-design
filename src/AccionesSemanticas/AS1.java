package AccionesSemanticas;
import AnalizadorLexico.*;

public class AS1 extends AccionSemantica {

    // Acción semántica que inicializa el buffer de lectura con el carácter actual. Se ejecuta al leer el primer
    // carácter de un token.

    @Override
    public void ejecutar(char entrada) {

        AnalizadorLexico.buffer = String.valueOf(entrada);
        AnalizadorLexico.indiceArchivo++;
    }
}
