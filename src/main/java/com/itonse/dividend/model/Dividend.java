package com.itonse.dividend.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Dividend {    // model 클래스

    private LocalDateTime date;
    private String dividend;
}
