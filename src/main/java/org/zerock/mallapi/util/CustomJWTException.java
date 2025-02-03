package org.zerock.mallapi.util;

// 사용자 정의 예외처리 클래스
public class CustomJWTException extends RuntimeException {
    public CustomJWTException(String msg) {

        super(msg);
    }
}
