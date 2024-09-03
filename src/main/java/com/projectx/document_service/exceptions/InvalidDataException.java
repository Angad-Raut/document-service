package com.projectx.document_service.exceptions;

public class InvalidDataException extends RuntimeException{
    public InvalidDataException(String msg){
        super(msg);
    }
}
