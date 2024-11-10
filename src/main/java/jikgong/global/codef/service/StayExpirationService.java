package jikgong.global.codef.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import jikgong.domain.member.dto.info.StayExpirationRequest;
import jikgong.domain.member.dto.info.StayExpirationResponse;
import jikgong.global.codef.codefapi.EasyCodef;
import jikgong.global.codef.codefapi.EasyCodefServiceType;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StayExpirationService {

    private final EasyCodef easyCodef;
    private final ObjectMapper mapper;
    @Value("${codef.client-id}")
    private String clientId;
    @Value("${codef.client-secret}")
    private String clientSecret;
    @Value("${codef.public-key}")
    private String codefPublicKey;
    private static final String PRODUCT_URL = "/v1/kr/public/mj/hi-korea/stay-expiration-date";

    public StayExpirationResponse checkStayExpiration(StayExpirationRequest request) throws JsonProcessingException {
        // 클라이언트 정보 및 퍼블릭 키 설정
        setClientInfo();

        // 요청 파라미터 설정
        HashMap<String, Object> parameterMap = createParameterMap(request);

        // API 요청 및 응답 처리
        String jsonResponse = executeRequest(parameterMap);

        return mapper.readValue(jsonResponse, StayExpirationResponse.class);
    }

    // 클라이언트 정보와 퍼블릭 키 설정
    private void setClientInfo() {
        easyCodef.setClientInfoForDemo(clientId, clientSecret);
        easyCodef.setPublicKey(codefPublicKey);
    }

    // 요청 파라미터 생성
    private HashMap<String, Object> createParameterMap(StayExpirationRequest request) {
        HashMap<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("organization", "0001");
        parameterMap.put("passportNo", request.getPassportNo());
        parameterMap.put("nationality", request.getNationality());
        parameterMap.put("country", request.getCountry());
        parameterMap.put("birthDate", request.getBirthDate());
        return parameterMap;
    }


    // 상품 요청을 실행하고 응답을 반환
    private String executeRequest(HashMap<String, Object> parameterMap) {
        try {
            return easyCodef.requestProduct(PRODUCT_URL, EasyCodefServiceType.DEMO, parameterMap);
        } catch (Exception e) {
            log.error("상품 요청 중 오류 발생: {}", e.getMessage(), e);
            throw new JikgongException(ErrorCode.CODEF_UNKNOWN_ERROR);
        }
    }
}