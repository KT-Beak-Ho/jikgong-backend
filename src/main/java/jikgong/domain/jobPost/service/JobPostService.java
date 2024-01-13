package jikgong.domain.jobPost.service;

import jikgong.domain.jobPost.dtos.*;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.JobPostStatus;
import jikgong.domain.jobPost.repository.JobPostRepository;
import jikgong.domain.jobPostImage.entity.JobPostImage;
import jikgong.domain.jobPostImage.repository.JobPostImageRepository;
import jikgong.domain.jobPostImage.service.JobPostImageService;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.addressInfo.entity.AddressInfo;
import jikgong.domain.addressInfo.entity.AddressType;
import jikgong.domain.addressInfo.repository.AddressInfoRepository;
import jikgong.domain.project.entity.Project;
import jikgong.domain.project.repository.ProjectRepository;
import jikgong.domain.workDate.entity.WorkDate;
import jikgong.domain.workDate.repository.WorkDateRepository;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import jikgong.global.handler.ImageDto;
import jikgong.global.handler.S3Handler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class JobPostService {
    private final JobPostRepository jobPostRepository;
    private final JobPostImageRepository jobPostImageRepository;
    private final JobPostImageService jobPostImageService;
    private final MemberRepository memberRepository;
    private final AddressInfoRepository addressInfoRepository;
    private final WorkDateRepository workDateRepository;
    private final S3Handler s3Handler;
    private final ProjectRepository projectRepository;

    //    @CachePut(value = "JobPost", cacheManager = "contentCacheManager", key = "#result")
    public Long saveJobPost(Long memberId, JobPostSaveRequest request, List<MultipartFile> imageList) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        // 모집 공고 저장
        JobPost jobPost = JobPost.createEntity(request, member, project);
        JobPost savedJobPost = jobPostRepository.save(jobPost);

        // 픽업 정보 저장
        if (request.getPickup()) {
            if (request.getPickupList() != null) {
                List<AddressInfo> pickupLocationList = request.getPickupList().stream()
                        .map(address -> new AddressInfo(address, AddressType.PICK_UP, savedJobPost))
                        .collect(Collectors.toList());
                addressInfoRepository.saveAll(pickupLocationList);
            }
        }

        // 날짜 정보 저장
        if (request.getWorkDayList() != null) {
            List<WorkDate> workDayList = request.getWorkDayList().stream()
                    .map(workDay -> new WorkDate(workDay, savedJobPost))
                    .collect(Collectors.toList());
            workDateRepository.saveAll(workDayList);
        }

        // 이미지 업로드 및 저장
        if (!request.getIsTemporary()) {
            List<ImageDto> imageDtoList = s3Handler.uploadImageList(imageList);
            List<JobPostImage> jobPostImageList = imageDtoList.stream()
                    .map(imageDto -> JobPostImage.createEntity(imageDto, savedJobPost))
                    .collect(Collectors.toList());
            jobPostImageRepository.saveAll(jobPostImageList);
        }

        return savedJobPost.getId();
    }

    // 등록한 공고 리스트 in 프로젝트
//    @Cacheable(value = "JobPost", cacheManager = "contentCacheManager")
    public List<JobPostListResponse> findJobPostsByMemberAndProject(Long memberId, JobPostStatus jobPostStatus, Long projectId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        LocalDate now = LocalDate.now();
        List<JobPost> jobPostList = new ArrayList<>();

        if (jobPostStatus == JobPostStatus.COMPLETED) {
            jobPostList = jobPostRepository.findCompletedJobPostByMemberAndProject(member.getId(), now, project.getId(), pageable);
        } else if (jobPostStatus == JobPostStatus.IN_PROGRESS) {
            jobPostList = jobPostRepository.findInProgressJobPostByMemberAndProject(member.getId(), now, project.getId(), pageable);
        } else if (jobPostStatus == JobPostStatus.PLANNED) {
            jobPostList = jobPostRepository.findPlannedJobPostByMemberAndProject(member.getId(), now, project.getId(), pageable);
        }

        List<JobPostListResponse> jobPostListResponseList = jobPostList.stream()
                .map(JobPostListResponse::from)
                .collect(Collectors.toList());

        return jobPostListResponseList;
    }

    // 임시 저장: 목록 조회
    public Page<TemporaryListResponse> findTemporaryJobPosts(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Page<JobPost> temporaryJobPostPage = jobPostRepository.findTemporaryJobPostByMemberId(member.getId(), pageable);

        List<TemporaryListResponse> temporaryJobPostList = temporaryJobPostPage.getContent().stream()
                .map(TemporaryListResponse::from)
                .collect(Collectors.toList());

        return new PageImpl<>(temporaryJobPostList, pageable, temporaryJobPostPage.getTotalElements());
    }

    // 인력 관리: 모집 공고 정보 조회
    public JobPostManageResponse findJobPostForManage(Long jobPostId) {
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));

        return JobPostManageResponse.from(jobPost);
    }

    // 임시 저장: 삭제
    public void deleteTemporaryJobPost(Long memberId, Long jobPostId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findJobPostByIdAndTemporary(jobPostId, true)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));
        // todo: 수정

        // 위치 관련 정보 삭제
        addressInfoRepository.deleteByMemberAndJobPost(member.getId(), jobPost.getId());
        // 이미지 관련 정보 삭제
        jobPostImageService.deleteEntityAndS3(member.getId(), jobPost.getId());
        // 근무 날짜 정보 삭제
        workDateRepository.deleteByMemberAndJobPost(member.getId(), jobPost.getId());

        // 임시 저장 삭제
        jobPostRepository.delete(jobPost);
    }

    public void deleteChildEntityFromJobPost(Long memberId, Long jobPostId) {
        // 근무 날짜 정보 삭제
        workDateRepository.deleteByMemberAndJobPost(memberId, jobPostId);
        // 위치 관련 정보 삭제
        addressInfoRepository.deleteByMemberAndJobPost(memberId, jobPostId);
        log.info("임시 저장 전 자식 엔티티 제거");
    }

    public void deleteChildEntityFromJobPost(Long jobPostId) {
        JobPost jobPost = jobPostRepository.findJobPostByIdAndTemporary(jobPostId, true)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));
        jobPost.deleteChildeEntity(jobPost);
        log.info("임시 저장 전 자식 엔티티 제거");
    }

    public void updateTemporaryJobPost(Long memberId, TemporaryUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findJobPostByIdAndTemporary(request.getJobPostId(), true)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        // 픽업 정보 저장
        if (request.getPickup()) {
            if (request.getPickupList() != null) {
                List<AddressInfo> pickupLocationList = request.getPickupList().stream()
                        .map(address -> new AddressInfo(address, AddressType.PICK_UP, jobPost))
                        .collect(Collectors.toList());
                addressInfoRepository.saveAll(pickupLocationList);
            }
        }

        // 날짜 정보 저장
        if (request.getWorkDayList() != null) {
            List<WorkDate> workDayList = request.getWorkDayList().stream()
                    .map(workDay -> new WorkDate(workDay, jobPost))
                    .collect(Collectors.toList());
            workDateRepository.saveAll(workDayList);
        }

        // jobPost 필드 정보 update
        JobPost.updateTemporary(jobPost, request, project);
    }
}
