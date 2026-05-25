package AccionesSemanticas;

import AnalizadorLexico.AnalizadorLexico;

public class AS12 extends AccionSemantica {
    //Inicializa buffer, inserta el carácter leído y devuelve el token de símbolo simple.
    @Override
    public void ejecutar(char entrada) {
        AnalizadorLexico.buffer = String.valueOf(entrada);
        Integer tokenAux = AnalizadorLexico.getIdToken(AnalizadorLexico.buffer);
        if (tokenAux != null){
            AnalizadorLexico.token = tokenAux;
        }
        AnalizadorLexico.indiceArchivo++;

    }
}