package jikgong.domain.etc.workDate;

import jikgong.domain.workDate.entity.WorkDate;
import jikgong.domain.workDate.repository.WorkDateRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkDateTestService {
    private final WorkDateRepository workDateRepository;

    public void plusRegisteredNum(Long workDateId) {
        WorkDate workDate = workDateRepository.findByIdWithLock(workDateId)
                .orElseThrow(() -> new CustomException(ErrorCode.WORK_DATE_NOT_FOUND));

        workDate.plusRegisteredNum(1);
    }
}
