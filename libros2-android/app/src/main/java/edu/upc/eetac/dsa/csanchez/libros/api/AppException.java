package edu.upc.eetac.dsa.csanchez.libros.api;

public class AppException extends Exception {
    public AppException() {
        super();
    }

    public AppException(String detailMessage) {
        super(detailMessage);
    }
}