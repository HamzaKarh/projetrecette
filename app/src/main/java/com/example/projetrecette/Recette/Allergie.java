package com.example.projetrecette.Recette;

import java.util.HashMap;
import java.util.Map;

public class Allergie {

    boolean gluten, arachid, lait, crustace, celeri, fruitcoq, poisson, moutarde;
    Map<String, Boolean> allergie = new HashMap<>();

    public Allergie(){
        gluten = false;
        arachid = false;
        lait = false;
        crustace = false;
        celeri = false;
        fruitcoq = false;
        poisson = false;
        moutarde = false;

    }

    public Map<String, Boolean> sendMap(){
        allergie.put("Gluten", gluten);
        allergie.put("Arachid", arachid);
        allergie.put("Lait", lait);
        allergie.put("Crustacé", crustace);
        allergie.put("Céléri", celeri);
        allergie.put("Fruit_à_Coq", fruitcoq);
        allergie.put("Poisson", poisson);
        allergie.put("Moutarde", moutarde);

        return allergie;
    }

    public void setArachid(boolean arachid) {
        this.arachid = arachid;
    }

    public void setCeleri(boolean celeri) {
        this.celeri = celeri;
    }

    public void setCrustace(boolean crustace) {
        this.crustace = crustace;
    }

    public void setFruitcoq(boolean fruitcoq) {
        this.fruitcoq = fruitcoq;
    }

    public void setGluten(boolean gluten) {
        this.gluten = gluten;
    }

    public void setLait(boolean lait) {
        this.lait = lait;
    }

    public void setMoutarde(boolean moutarde) {
        this.moutarde = moutarde;
    }

    public void setPoisson(boolean poisson) {
        this.poisson = poisson;
    }

}
