package org.choongang.global.validators;

public interface MobileValidator {
    default boolean mobileCheck(String mobile) {
        /**
         * 01[016]-0000/000-0000
         * 01[016]-\d{3,4}-\d{4}
         * 010.1111.1111
         * 010 1111 1111
         * 020-1111-1111
         * 01011111111
         * 1. 숫자만 남긴다  2. 패턴만들기  3. 체크
         * 예) 0101111111111111111111
         */

        mobile = mobile.replaceAll("\\D", ""); // 숫자만 남기기
        String pattern = "01[016]\\d{3,4}\\d{4}$"; // $ : 4자리로 끝나도록? // 패턴 만들기


        return mobile.matches(pattern);
    }
}
