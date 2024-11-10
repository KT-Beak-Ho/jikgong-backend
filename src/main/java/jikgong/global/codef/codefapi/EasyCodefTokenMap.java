package jikgong.global.codef.codefapi;

import java.util.HashMap;

public class EasyCodefTokenMap {

    /**    쉬운 코드에프 이용을 위한 토큰 저장 맵	*/
    private static HashMap<String, String> ACCESS_TOKEN_MAP = new HashMap<String, String>();

    /**
     * Desc : 토큰 저장
     */
    public static void setToken(String clientId, String accessToken) {
        ACCESS_TOKEN_MAP.put(clientId, accessToken);
    }

    /**
     * Desc : 토큰 반환
     */
    public static String getToken(String clientId) {
        return ACCESS_TOKEN_MAP.get(clientId);
    }
}
