package AccionesSemanticas;

import AnalizadorLexico.AnalizadorLexico;

public class AS11 extends AccionSemantica {
    //Inserta el carácter leído y devuelve el token de símbolo doble (‘<=’, ‘>=’, ‘==’, ‘=!’, ‘:=’, ‘->’).
    @Override
    public void ejecutar(char entrada) {
        AnalizadorLexico.buffer += String.valueOf(entrada);
        Integer tokenAux = AnalizadorLexico.getIdToken(AnalizadorLexico.buffer);
        if (tokenAux != null){
            AnalizadorLexico.token = tokenAux;
        }

        AnalizadorLexico.indiceArchivo++;

    }
}
