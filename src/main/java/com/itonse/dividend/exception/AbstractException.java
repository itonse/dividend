package com.itonse.dividend.exception;

public abstract class AbstractException extends RuntimeException{   // 추상클래스

    abstract public int getStatusCode();  // 추상메소드
    abstract public String getMessage();
}
