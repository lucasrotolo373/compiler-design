package AccionesSemanticas;
import AnalizadorLexico.AnalizadorLexico;

public class AS15 extends AccionSemantica {

    @Override
    public void ejecutar(char entrada) {
        if (entrada == ':' || entrada == '%'){
            AnalizadorLexico.listaErrores.add(String.format(
                    "AL - Línea %d: «%s» Este simbolo no corresponde a ningun token valido.",
                    AnalizadorLexico.numLinea, entrada));
        }else{
            AnalizadorLexico.listaErrores.add(
                    String.format("AL - Línea %d: carácter inválido «%c».", AnalizadorLexico.numLinea, entrada));
        }
        AnalizadorLexico.indiceArchivo++;
    }
}
