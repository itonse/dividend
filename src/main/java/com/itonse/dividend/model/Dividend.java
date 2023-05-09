package com.itonse.dividend.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dividend {    // model 클래스

    @JsonSerialize(using = LocalDateTimeSerializer.class)    // 직렬화 Serializer 지정
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)  // 역직렬화 Serializer 지정
    private LocalDateTime date;

    private String dividend;
}
