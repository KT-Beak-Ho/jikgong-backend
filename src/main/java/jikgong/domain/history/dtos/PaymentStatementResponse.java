package jikgong.domain.history.dtos;

import jikgong.domain.history.entity.WorkStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class PaymentStatementResponse {
    private Integer totalPayment; // 지급 총액
    private List<PaymentMemberInfo> paymentMemberList;
    private List<PaymentMemberInfo> notPaymentMemberList;


    public static PaymentStatementResponse from(List<PaymentMemberInfo> paymentMemberInfoList) {
        int totalPayment = paymentMemberInfoList.stream()
                .mapToInt(PaymentMemberInfo::getPayment)
                .sum();
        List<PaymentMemberInfo> paymentMemberList = new ArrayList<>();
        List<PaymentMemberInfo> notPaymentMemberList = new ArrayList<>();
        for (PaymentMemberInfo i : paymentMemberInfoList) {
            if(i.getStartStatus() == WorkStatus.NOT_WORK) {
                notPaymentMemberList.add(i);
            } else {
                paymentMemberList.add(i);
            }
        }
        return PaymentStatementResponse.builder()
                .totalPayment(totalPayment)
                .paymentMemberList(paymentMemberList)
                .notPaymentMemberList(notPaymentMemberList)
                .build();
    }
}
