package jikgong.domain.jobPost.service;

import jikgong.domain.jobPost.dtos.JobPostListResponse;
import jikgong.domain.jobPost.dtos.JobPostSaveRequest;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.JobPostStatus;
import jikgong.domain.jobPost.entity.Park;
import jikgong.domain.jobPost.repository.JobPostRepository;
import jikgong.domain.jobPostImage.entity.JobPostImage;
import jikgong.domain.jobPostImage.repository.JobPostImageRepository;
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
    private final MemberRepository memberRepository;
    private final AddressInfoRepository addressInfoRepository;
    private final WorkDateRepository workDayRepository;
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
            List<AddressInfo> pickupLocationList = request.getPickupList().stream()
                    .map(address -> new AddressInfo(address, AddressType.PICK_UP, savedJobPost))
                    .collect(Collectors.toList());
            addressInfoRepository.saveAll(pickupLocationList);
        }

        // 주차 정보 저장
        if (request.getPark() != Park.NONE) {
            List<AddressInfo> parkLocationList = request.getPickupList().stream()
                    .map(address -> new AddressInfo(address, AddressType.PARK, savedJobPost))
                    .collect(Collectors.toList());
            addressInfoRepository.saveAll(parkLocationList);
        }

        // 날짜 정보 저장
        List<WorkDate> workDayList = request.getWorkDayList().stream()
                .map(workDay -> new WorkDate(workDay, savedJobPost))
                .collect(Collectors.toList());
        workDayRepository.saveAll(workDayList);

        // 이미지 업로드 및 저장
        List<ImageDto> imageDtoList = s3Handler.uploadImageList(imageList);
        List<JobPostImage> jobPostImageList = imageDtoList.stream()
                .map(imageDto -> JobPostImage.createEntity(imageDto))
                .collect(Collectors.toList());
        jobPostImageRepository.saveAll(jobPostImageList);

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
        }
        else if (jobPostStatus == JobPostStatus.IN_PROGRESS) {
            jobPostList = jobPostRepository.findInProgressJobPostByMemberAndProject(member.getId(), now, project.getId(), pageable);
        }
        else if (jobPostStatus == JobPostStatus.PLANNED) {
            jobPostList = jobPostRepository.findPlannedJobPostByMemberAndProject(member.getId(), now, project.getId(), pageable);
        }

        List<JobPostListResponse> jobPostListResponseList = jobPostList.stream()
                .map(JobPostListResponse::from)
                .collect(Collectors.toList());

        return jobPostListResponseList;
    }

    // 임시 등록한 공고 리스트
    public List<JobPostListResponse> findTemporaryJobPosts(Long memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<JobPostListResponse> temporaryJobPostList = jobPostRepository.findTemporaryJobPostByMemberId(member.getId(), pageable).stream()
                .map(JobPostListResponse::from)
                .collect(Collectors.toList());

        return temporaryJobPostList;
    }

}
