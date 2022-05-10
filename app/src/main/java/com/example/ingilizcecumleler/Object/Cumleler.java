package com.example.ingilizcecumleler.Object;


public class Cumleler {
    public String tr;
    public String en;
    public String kategoriID;
    public Boolean silindiMi;

    public Cumleler(String tr, String en, String kategoriID, Boolean silindiMi) {
        this.tr = tr;
        this.en = en;
        this.kategoriID = kategoriID;
        this.silindiMi = silindiMi;
    }



    public Cumleler(){}

    public String getTr() {
        return tr;
    }

    public void setTr(String tr) {
        this.tr = tr;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getKategoriID() {
        return kategoriID;
    }

    public void setKategoriID(String kategoriID) {
        this.kategoriID = kategoriID;
    }

    public Boolean getSilindiMi() {
        return silindiMi;
    }

    public void setSilindiMi(Boolean silindiMi) {
        this.silindiMi = silindiMi;
    }


}
