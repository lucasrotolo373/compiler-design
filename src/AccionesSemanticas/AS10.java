package AccionesSemanticas;
import AnalizadorLexico.*;

public class AS10 extends AccionSemantica{
    @Override
    // Añade la cadena eliminando los saltos de línea. Ignora el &
    public void ejecutar(char entrada) {
        String aux = AnalizadorLexico.buffer;
        aux = aux.replaceAll("[\n\r]", "");
        Integer auxToken= AnalizadorLexico.getIdToken("CADENA");
        if (auxToken != null) {
            AnalizadorLexico.token = auxToken;
            AnalizadorLexico.tablaDeSimbolos.putIfAbsent(aux, new TDSObject("CADENA"));
            AnalizadorLexico.refTDS = aux;
        }
        else {
            AnalizadorLexico.listaErrores.add(String.format(
                    "AL - Línea %d: «%s» no existe el token",
                    AnalizadorLexico.numLinea, aux));
            AnalizadorLexico.token = -1;
        }
        AnalizadorLexico.indiceArchivo++;
    }
}
