package AccionesSemanticas;
import AnalizadorLexico.*;
import static AnalizadorLexico.AnalizadorLexico.MAX_ID_VALUE;

public class AS4 extends AccionSemantica{

    // Chequear en la tabla de símbolos
    // si este ya se almacenó previamente. En caso de que no, se le da el alta y luego
    // se devuelve el par token-lexema. También se truncan los identificadores que
    // superan la longitud máxima.

    @Override
    public void ejecutar(char entrada) {

        String _buffer = AnalizadorLexico.buffer;
        // Si excede la longitud, se trunca y se reporta warning.
        if(_buffer.length() > MAX_ID_VALUE){
            AnalizadorLexico.listaWarnings.add(String.format(
                    "Warning línea %d: el identificador «%s» fue truncado a %d caracteres.",
                    AnalizadorLexico.numLinea, _buffer, MAX_ID_VALUE));
            _buffer = _buffer.substring(0, MAX_ID_VALUE);
            AnalizadorLexico.buffer = _buffer;
        }

            AnalizadorLexico.tablaDeSimbolos.putIfAbsent(_buffer, new TDSObject("ID"));
            AnalizadorLexico.refTDS = _buffer;
            AnalizadorLexico.token = AnalizadorLexico.getIdToken("ID");
        
    }
}
