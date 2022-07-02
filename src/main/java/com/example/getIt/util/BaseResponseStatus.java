package com.example.getIt.util;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {
    SUCCESS(true, 1000, "요청에 성공하였습니다."),
    DUPLICATE_NICKNAME(false, 2000, "닉네임이 중복되었습니다."),
    DUPLICATE_EMAIL(false, 2001, "이메일이 중복되었습니다."),
    EMPTY_JWT(false, 2002, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2003, "유효하지 않은 JWT입니다."),
    POST_USERS_EMPTY_NICKNAME(false, 2004, "아이디를 입력해주세요."),
    POST_USERS_EMPTY_PASSWORD(false, 2005, "비밀번호를 입력해주세요."),
    POST_USERS_INVALID_PASSWORD(false, 2006, "비밀번호가 틀렸습니다."),
    FAILED_TO_LOGIN(false, 2007, "없는 아이디이거나 비밀번호가 틀렸습니다."),
    /*
    * 4000: [POST]
    * */
    POST_USERS_EMPTY(false, 2002, "공백 없이 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 5000, "이메일 양식이 맞지 않습니다."),
    /*
     * 5000: database error
     * */
    PASSWORD_ENCRYPTION_ERROR(false, 4001, "비밀번호 암호화에 실패했습니다."),
    DATABASE_ERROR(false, 4001, "데이터베이스 연결에 실패하였습니다.");
    /*
    * 7000
    * */


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
