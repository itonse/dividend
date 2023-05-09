package com.itonse.dividend.exception.impl;

import com.itonse.dividend.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class NoCompanyException extends AbstractException {
    @Override
    public int getStatusCode() {   // 상태코드
        return HttpStatus.BAD_REQUEST.value();
    }

    @Override
    public String getMessage() {   // 메시지
        return "존재하지 않는 회사명 입니다.";
    }
}
