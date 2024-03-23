package jikgong.domain.jobPost.service;

import jikgong.domain.jobPost.dtos.company.*;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.JobPostStatus;
import jikgong.domain.jobPost.repository.JobPostRepository;
import jikgong.domain.jobPostImage.entity.JobPostImage;
import jikgong.domain.jobPostImage.repository.JobPostImageRepository;
import jikgong.domain.jobPostImage.service.JobPostImageService;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.pickup.entity.Pickup;
import jikgong.domain.pickup.repository.AddressInfoRepository;
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
public class JobPostCompanyService {
    private final JobPostRepository jobPostRepository;
    private final JobPostImageRepository jobPostImageRepository;
    private final JobPostImageService jobPostImageService;
    private final MemberRepository memberRepository;
    private final AddressInfoRepository addressInfoRepository;
    private final WorkDateRepository workDateRepository;
    private final S3Handler s3Handler;
    private final ProjectRepository projectRepository;

    // 모집 공고: 저장
    public Long saveJobPost(Long memberId, JobPostSaveRequest request, List<MultipartFile> imageList) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        // 모집 공고 저장
        JobPost jobPost = JobPost.createEntityByJobPost(request, member, project);
        JobPost savedJobPost = jobPostRepository.save(jobPost);

        // 픽업 정보 저장
        if (request.getPickup()) {
            List<Pickup> pickupLocationList = request.getPickupList().stream()
                    .map(address -> new Pickup(address, savedJobPost))
                    .collect(Collectors.toList());
            addressInfoRepository.saveAll(pickupLocationList);
        }

        // 날짜 정보 저장
        List<WorkDate> workDateList = request.getWorkDateList().stream()
                .map(workDate -> new WorkDate(workDate, request.getRecruitNum(), savedJobPost))
                .collect(Collectors.toList());
        workDateRepository.saveAll(workDateList);

        // 이미지 업로드 및 저장
        List<ImageDto> imageDtoList = s3Handler.uploadImageList(imageList);
        List<JobPostImage> jobPostImageList = imageDtoList.stream()
                .map(imageDto -> JobPostImage.createEntity(imageDto, savedJobPost))
                .collect(Collectors.toList());
        jobPostImageRepository.saveAll(jobPostImageList);

        return savedJobPost.getId();
    }

    // 모집 공고: 조회 with 프로젝트
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

    // 인력 관리: 모집 공고 정보 조회
    public JobPostManageResponse findJobPostForManage(Long jobPostId) {
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));

        return JobPostManageResponse.from(jobPost);
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

    // 임시 저장: 삭제
    public void deleteTemporaryJobPost(Long memberId, Long jobPostId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        JobPost jobPost = jobPostRepository.findJobPostByIdAndMemberAndTemporary(member.getId(), jobPostId, true)
                .orElseThrow(() -> new CustomException(ErrorCode.JOB_POST_NOT_FOUND));

        // 이미지 관련 정보 삭제
        jobPostImageService.deleteEntityAndS3(member.getId(), jobPost.getId());
        // 관련 엔티티 삭제 (WorkDate, AddressInfo)
        jobPost.deleteChildeEntity(jobPost);

        // 임시 저장 삭제
        jobPostRepository.delete(jobPost);
    }

    // 임시 저장: 업데이트
    public void updateTemporaryJobPost(Long memberId, TemporaryUpdateRequest request) {
        deleteTemporaryJobPost(memberId, request.getJobPostId());
        saveTemporary(memberId, TemporarySaveRequest.from(request));
    }

    // 임시 저장: 저장
    public Long saveTemporary(Long memberId, TemporarySaveRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = null;
        if (request.getProjectId() != null) {
            project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));
        }

        // 모집 공고 저장
        JobPost jobPost = JobPost.createEntityByTemporary(request, member, project);
        JobPost savedJobPost = jobPostRepository.save(jobPost);

        // 픽업 정보 저장
        if (request.getPickup()) {
            if (request.getPickupList() != null) {
                List<Pickup> pickupLocationList = request.getPickupList().stream()
                        .map(address -> new Pickup(address, savedJobPost))
                        .collect(Collectors.toList());
                addressInfoRepository.saveAll(pickupLocationList);
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
}
