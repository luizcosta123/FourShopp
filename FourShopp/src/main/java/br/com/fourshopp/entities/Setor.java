package br.com.fourshopp.entities;

import java.util.HashMap;
import java.util.Map;

public enum Setor {

    MERCEARIA(1,"MERCEARIA"),
    BAZAR(2,"BAZAR"),
    ELETRONICOS(3,"ELETRONICOS"),
    COMERCIAL(4,"COMERCIAL");


    private int cd;
    private String setor;

    private static final Map<Integer, Setor> funcByCd = new HashMap<>();

    static {
        for(Setor setorEnum : Setor.values()) {
            funcByCd.put(setorEnum.getCd(), setorEnum);
        }
    }

    Setor(int cd, String setor) {
        this.cd = cd;
        this.setor = setor;
    }

    public int getCd() {
        return cd;
    }

    public String getSetor() {
        return setor;
    }

    public static Setor findByCd(int valor) {
        return funcByCd.get(valor);
    }

}
