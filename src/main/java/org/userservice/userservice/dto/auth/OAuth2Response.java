package org.userservice.userservice.dto.auth;

public interface OAuth2Response {

    //제공자 (Ex. naver, google, ...)
    String getProvider();
    //제공자에서 발급해주는 아이디(번호)
    String getProviderId();
    //이메일
    String getEmail();
    //사용자 실명 (설정한 이름)
    String getName();
    //프로필이미지 url
    String getProfileImage();
    //성별(F: 여성, M: 남성, U: 확인불가)
    String getGender();
    //생일 (MM-DD 형식)
    String getBirthday();
    //출생연도
    String getBirthYear();
    //휴대전화번호
    String getMobile();
}