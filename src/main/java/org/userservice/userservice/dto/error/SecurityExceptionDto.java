package org.userservice.userservice.dto.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SecurityExceptionDto {

    private String message;
    private int statusCode;

}