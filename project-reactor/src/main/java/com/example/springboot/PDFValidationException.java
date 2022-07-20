package com.example.springboot;

public class PDFValidationException extends RuntimeException {

    public PDFValidationException(String msg) {
        super(msg);
    }

    public PDFValidationException(Throwable e) {
        super(e);
    }

    public PDFValidationException(String msg, Throwable e) {
        super(msg, e);
    }

}  