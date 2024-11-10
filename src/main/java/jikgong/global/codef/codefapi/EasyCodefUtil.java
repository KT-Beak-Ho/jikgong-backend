package jikgong.global.codef.codefapi;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

public class EasyCodefUtil {

    /**
     * 토큰 맵 변환
     */
    @SuppressWarnings("unchecked")
    public static HashMap<String, Object> getTokenMap(String token)
        throws JsonParseException, JsonMappingException, IOException {

        /** 클라이언트 식별 값, 요청 식별 값 추출을 위한 디코드 */
        String[] split_string = token.split("\\.");
        String base64EncodedBody = split_string[1];
        String tokenBody = new String(Base64.getDecoder().decode(base64EncodedBody));

        /** 맵 변환 */
        return new ObjectMapper().readValue(tokenBody, HashMap.class);
    }

    /**
     * Comment  : 요청 토큰 정합성 체크
     */
    public static boolean checkValidity(int expInt) {
        long now = new Date().getTime();
        String expStr = expInt + "000";    // 현재 시간 타임스탬프와 자리수 맞추기(13자리)
        long exp = Long.parseLong(expStr);
        if (now > exp || (exp - now < 3600000)) { // 유효기간 확인::유효기간이 지났거나 한시간 이내로 만료되는 경우
            return false;
        }

        return true;
    }
}
