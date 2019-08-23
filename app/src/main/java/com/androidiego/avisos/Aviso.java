package com.androidiego.avisos;

/**
 * Modelo de Datos
 * POJO
 * (Plain Old Java Object)
 */

public class Aviso {
    private int mId; //número único usado para identificar el aviso
    private String mContent; // contiene el texto del aviso
    private int mImportant; // indicador numérico que señale cada uno de los avisos como importantes '1' o '0' como no importante

    public Aviso(int Id, String Content, int Important) {
        this.mId = Id;
        this.mContent = Content;
        this.mImportant = Important;
    }

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }

    public int getImportant() {
        return mImportant;
    }

    public void setImportant(int mImportant) {
        this.mImportant = mImportant;
    }
}
