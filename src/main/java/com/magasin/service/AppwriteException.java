package com.magasin.service;

/** Exception levee par les appels Appwrite REST. */
public class AppwriteException extends Exception {

    private final int codeHttp;

    public AppwriteException(String message, int codeHttp) {
        super(message);
        this.codeHttp = codeHttp;
    }

    public int getCodeHttp() { return codeHttp; }
}
