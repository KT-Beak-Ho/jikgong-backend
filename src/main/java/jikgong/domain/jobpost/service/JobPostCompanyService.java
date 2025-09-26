package jikgong.domain.jobpost.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.jobpost.dto.company.JobPostListResponse;
import jikgong.domain.jobpost.dto.company.JobPostManageResponse;
import jikgong.domain.jobpost.dto.company.JobPostResponseForOffer;
import jikgong.domain.jobpost.dto.company.JobPostSaveRequest;
import jikgong.domain.jobpost.dto.company.TemporaryListResponse;
import jikgong.domain.jobpost.dto.company.TemporarySaveRequest;
import jikgong.domain.jobpost.dto.company.TemporaryUpdateRequest;
import jikgong.domain.jobpost.entity.jobpost.JobPost;
import jikgong.domain.jobpost.entity.jobpostimage.JobPostImage;
import jikgong.domain.jobpost.entity.jobpost.JobPostStatus;
import jikgong.domain.jobpost.repository.JobPostImageRepository;
import jikgong.domain.jobpost.repository.JobPostRepository;
import jikgong.domain.member.entity.ImgType;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.jobpost.entity.pickup.Pickup;
import jikgong.domain.jobpost.repository.PickupRepository;
import jikgong.domain.project.entity.Project;
import jikgong.domain.project.repository.ProjectRepository;
import jikgong.domain.workdate.entity.WorkDate;
import jikgong.domain.workdate.repository.WorkDateRepository;
import jikgong.global.exception.ErrorCode;
import jikgong.global.exception.JikgongException;
import jikgong.global.s3.ImageDto;
import jikgong.global.s3.S3Handler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JobPostCompanyService {

    private final JobPostRepository jobPostRepository;
    private final JobPostImageRepository jobPostImageRepository;
    private final MemberRepository memberRepository;
    private final PickupRepository pickupRepository;
    private final WorkDateRepository workDateRepository;
    private final S3Handler s3Handler;
    private final ProjectRepository projectRepository;
    private final ApplyRepository applyRepository;

    /**
     * 모집 공고 등록
     * pickup, workDate, image 저장
     */
    public Long saveJobPost(Long companyId, JobPostSaveRequest request, List<MultipartFile> imageList) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = projectRepository.findByIdAndMember(company.getId(), request.getProjectId())
            .orElseThrow(() -> new JikgongException(ErrorCode.PROJECT_NOT_FOUND));

        // 모집 공고 날짜가 프로젝트 기간에 속하는지 검사
        validateWorkDateRange(request, project);

        // 모집 공고 저장
        JobPost jobPost = JobPost.createEntityByJobPost(request, company, project);
        JobPost savedJobPost = jobPostRepository.save(jobPost);

        // 픽업 정보 저장
        if (request.getPickup()) {
            List<Pickup> pickupLocationList = request.getPickupList().stream()
                .map(address -> new Pickup(address, savedJobPost))
                .collect(Collectors.toList());
            pickupRepository.saveAll(pickupLocationList);
        }

        // 날짜 정보 저장
        List<WorkDate> workDateList = request.getDateList().stream()
            .map(date -> new WorkDate(date, request.getRecruitNum(), savedJobPost))
            .collect(Collectors.toList());
        workDateRepository.saveAll(workDateList);

        // 이미지 업로드 및 저장
        List<ImageDto> imageDtoList = s3Handler.uploadImagesWithImgType(imageList, ImgType.JOB_POST);
        List<JobPostImage> jobPostImageList = imageDtoList.stream()
            .map(imageDto -> JobPostImage.createEntity(imageDto, savedJobPost))
            .collect(Collectors.toList());
        jobPostImageRepository.saveAll(jobPostImageList);

        return savedJobPost.getId();
    }

    // 모집 공고 날짜가 프로젝트 기간에 속하는지 검사
    private void validateWorkDateRange(JobPostSaveRequest request, Project project) {
        List<LocalDate> dateList = request.getDateList();
        LocalDate startDate = project.getStartDate();
        LocalDate endDate = project.getEndDate();

        for (LocalDate date : dateList) {
            if (date.isBefore(startDate) || date.isAfter(endDate)) {
                throw new JikgongException(ErrorCode.WORK_DATE_INVALID_RANGE);
            }
        }
    }

    /**
     * 프로젝트 별 모집 공고 조회
     * 필터: 전체(null) | 완료됨 | 진행 중 | 예정
     */
    @Transactional(readOnly = true)
    public List<JobPostListResponse> findJobPostsByMemberAndProject(Long companyId, JobPostStatus jobPostStatus,
        Long projectId, Pageable pageable) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = projectRepository.findByIdAndMember(company.getId(), projectId)
            .orElseThrow(() -> new JikgongException(ErrorCode.PROJECT_NOT_FOUND));

        LocalDate now = LocalDate.now();
        List<JobPost> jobPostList = new ArrayList<>();

        if (jobPostStatus == JobPostStatus.COMPLETED) {
            jobPostList = jobPostRepository.findCompletedJobPostByMemberAndProject(now, project.getId(), pageable);
        } else if (jobPostStatus == JobPostStatus.IN_PROGRESS) {
            jobPostList = jobPostRepository.findInProgressJobPostByMemberAndProject(now, project.getId(), pageable);
        } else if (jobPostStatus == JobPostStatus.PLANNED) {
            jobPostList = jobPostRepository.findPlannedJobPostByMemberAndProject(now, project.getId(), pageable);
        } else {
            jobPostList = jobPostRepository.findJobPostByMember(project.getId(), pageable);
        }

        return jobPostList.stream()
            .map(JobPostListResponse::from)
            .collect(Collectors.toList());
    }

    /**
     * 인력 관리 화면에서 모집 공고 정보 일부 반환
     */
    @Transactional(readOnly = true)
    public JobPostManageResponse findJobPostForManage(Long jobPostId) {
        JobPost jobPost = jobPostRepository.findById(jobPostId)
            .orElseThrow(() -> new JikgongException(ErrorCode.JOB_POST_NOT_FOUND));

        return JobPostManageResponse.from(jobPost);
    }

    /**
     * 임시 저장 조회
     */
    @Transactional(readOnly = true)
    public List<TemporaryListResponse> findTemporaryJobPosts(Long companyId) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        return jobPostRepository.findTemporaryJobPostByMemberId(company.getId()).stream()
            .map(TemporaryListResponse::from)
            .collect(Collectors.toList());
    }

    /**
     * 임시 저장 삭제
     */
    public void deleteTemporaryJobPost(Long companyId, Long jobPostId) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findTemporaryForDelete(company.getId(), jobPostId, true)
            .orElseThrow(() -> new JikgongException(ErrorCode.JOB_POST_NOT_FOUND));

        // 관련 엔티티 삭제 (WorkDate, AddressInfo)
        workDateRepository.deleteByJobPost(jobPost.getId());
        pickupRepository.deleteByJobPost(jobPost.getId());

        // 임시 저장 삭제
        jobPostRepository.delete(jobPost);
    }

    /**
     * 임시 저장 업데이트
     * 기존 데이터 삭제 후 다시 저장
     */
    public void updateTemporaryJobPost(Long companyId, TemporaryUpdateRequest request) {
        deleteTemporaryJobPost(companyId, request.getJobPostId());
        saveTemporary(companyId, TemporarySaveRequest.from(request));
    }

    /**
     * 임시 저장 등록
     * 임지 저장 등록 시 이미지에 대한 정보는 저장 x
     */
    public Long saveTemporary(Long companyId, TemporarySaveRequest request) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = projectRepository.findByIdAndMember(company.getId(), request.getProjectId())
            .orElseThrow(() -> new JikgongException(ErrorCode.PROJECT_NOT_FOUND));

        // 모집 공고 저장
        JobPost jobPost = JobPost.createEntityByTemporary(request, company, project);
        JobPost savedJobPost = jobPostRepository.save(jobPost);

        // 픽업 정보 저장
        if (request.getPickup()) {
            if (request.getPickupList() != null) {
                List<Pickup> pickupLocationList = request.getPickupList().stream()
                    .map(address -> new Pickup(address, savedJobPost))
                    .collect(Collectors.toList());
                pickupRepository.saveAll(pickupLocationList);
            }
        }

        // 날짜 정보 저장
        if (request.getWorkDateList() != null) {
            List<WorkDate> workDayList = request.getWorkDateList().stream()
                .map(workDay -> new WorkDate(workDay, request.getRecruitNum(), savedJobPost))
                .collect(Collectors.toList());
            workDateRepository.saveAll(workDayList);
        }

        // 이미지 업로드 및 저장은 x

        return savedJobPost.getId();
    }


    /**
     * 제안 시 프로젝트 선택
     * 선택 후 제안 가능한 공고 조회
     * 각 모집 공고엔 [제안 가능 날짜], [인원 마감된 날짜], [출역일 지난 날짜] 반환
     */
    @Transactional(readOnly = true)
    public JobPostResponseForOffer findAvailableJobPosts(Long companyId, Long workerId, Long projectId) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        Member worker = memberRepository.findById(workerId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = projectRepository.findByIdAndMember(company.getId(), projectId)
            .orElseThrow(() -> new JikgongException(ErrorCode.PROJECT_NOT_FOUND));

//        // 노동자 작업 불가능한 날짜 조회
//        List<LocalDate> cantWorkDate = applyRepository.findAllCantWorkDate(worker.getId()).stream()
//                .map(apply -> apply.getWorkDate().getDate())
//                .collect(Collectors.toList());
//        Set<LocalDate> cantWorkDateSet = new HashSet<>(cantWorkDate);

        List<JobPost> jobPostList = jobPostRepository.findByProject(project.getId());

        return JobPostResponseForOffer.from(jobPostList, worker);
    }

    public void deleteJobPost(Long companyId, Long jobPostId) {
        Member company = memberRepository.findById(companyId)
            .orElseThrow(() -> new JikgongException(ErrorCode.MEMBER_NOT_FOUND));

        JobPost jobPost = jobPostRepository.findById(jobPostId)
            .orElseThrow(() -> new JikgongException(ErrorCode.JOB_POST_NOT_FOUND));

        List<WorkDate> workDateList = jobPost.getWorkDateList();

        // 모집 공고의 근무일자 중 가장 큰 날짜 찾기
        LocalDate maxDate = findMaxDate(workDateList);
        LocalDate today = LocalDate.now();

        // 근무일이 7일 이상 지난 공고: 삭제
        // 아닌 공고: 확정된 지원자 없다면 삭제
        validationBeforeDelete(today, maxDate, workDateList);

        // 조건이 충족되면 논리 삭제 실행
        jobPostRepository.delete(jobPost);
    }

    // 공고 삭제 전 조건 검사
    private void validationBeforeDelete(LocalDate today, LocalDate maxDate, List<WorkDate> workDateList) {
        if (!today.isAfter(maxDate.plusWeeks(1))) {
            // 일주일이 지나지 않은 경우 확정된 지원자가 없는지 확인
            List<Long> workDateListId = workDateList.stream()
                .map(WorkDate::getId)
                .collect(Collectors.toList());
            List<Apply> applyList = applyRepository.checkAcceptedApplyBeforeDeleteJobPost(workDateListId);
            if (!applyList.isEmpty()) {
                throw new JikgongException(ErrorCode.JOB_POST_DELETE_FAIL);
            }
        }
    }

    // 가장 마지막 날짜 추출
    private LocalDate findMaxDate(List<WorkDate> workDateList) {
        LocalDate maxDate = workDateList.stream()
            .map(WorkDate::getDate)
            .max(Comparator.naturalOrder())
            .orElseThrow(() -> new JikgongException(ErrorCode.WORK_DATE_NOT_FOUND));
        return maxDate;
    }
}
