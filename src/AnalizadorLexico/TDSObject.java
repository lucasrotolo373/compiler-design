package AnalizadorLexico;

import java.util.List;
import java.util.ArrayList;

public class TDSObject {

    private String tipoVariable;
    private ArrayList<String> tiposRetorno = new ArrayList<>();
    private ArrayList<String> tiposParametros = new ArrayList<>();
    private ArrayList<String> semanticaParametros = new ArrayList<>();
    private String uso;

    public TDSObject(String tipoVariable) {
        this.tipoVariable = tipoVariable;
    }

    public String getTipoVariable() {
        return tipoVariable;
    }

    public void setTipoVariable(String tipoVariable) {
        this.tipoVariable = tipoVariable;
    }

    public void setUso(String uso){ this.uso = uso;}

    public String getUso(){ return this.uso;}

    public void setTiposRetorno(List<String> tipos) {
        this.tiposRetorno.clear();
        if (tipos != null) this.tiposRetorno.addAll(tipos);
    }

    public List<String> getTiposRetorno(){
        return tiposRetorno;
    }

    public void addTipoParametro(String tipo) {
        if (tipo != null) this.tiposParametros.add(tipo);
    }

    public List<String> getTiposParametros() { 
        return tiposParametros; 
    }

    public void addSemanticaParametros (String modo){
        if (modo != null) this.semanticaParametros.add(modo);
    }

    public List<String> getSemanticaParametros() { 
        return semanticaParametros; 
    }
}
