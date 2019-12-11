package com.codeoftheweb.salvo;

import org.springframework.context.annotation.Bean;

import javax.persistence.Entity;
import java.util.*;

public class Tablero {
    private Map<String,Object> tablero = new HashMap<>();
    private List<String> columna = new ArrayList<>();
    private List<String> fila = new ArrayList<>();
    private List<String> auxColumna = Arrays.asList("1","2","3","4","5","6","7","8","9","10");
    private List<String> auxFila = Arrays.asList("A","B","C","D","E","F","G","H","I","J");


    public Map<String,Object> makeTablero () {
        this.columna.addAll(this.auxColumna);
        this.fila.addAll(this.auxFila);

        for(int i = 0; i < this.fila.size(); i++) {
            for(int j = 0; j < this.columna.size(); j++) {
                this.tablero.put(this.fila.get(i) + this.columna.get(j), shipSalvo());
            }
        }
        return this.tablero;
    }

    public Map<String, Boolean> shipSalvo () {
        Map<String, Boolean> shipSalvo = new HashMap<>();
        shipSalvo.put("ship", false);
        shipSalvo.put("salvo", false);
        return shipSalvo;
    }

    public Boolean controlPosicion (List<String> posiciones) {
        Boolean letraNum = false;

        if(posiciones.get(0).substring(0,1).equals(posiciones.get(1).substring(0,1))) {
            letraNum = true;
        }

        for(int i = 0; i < posiciones.size()-1; i++) {
            if(letraNum) {
                int aux = this.auxColumna.indexOf(posiciones.get(i).substring(1));
                if(!posiciones.get(i+1).substring(1).equals(this.auxColumna.get(aux+1))) {
                    return false;
                }
            } else {
                int aux = this.auxFila.indexOf(posiciones.get(i).substring(0,1));
                if(!posiciones.get(i+1).substring(0,1).equals(this.auxFila.get(aux+1))) {
                    return false;
                }
            }
        }

        return true;
    }


}
