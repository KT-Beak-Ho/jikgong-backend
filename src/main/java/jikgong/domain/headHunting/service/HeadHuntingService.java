package jikgong.domain.headHunting.service;

import jikgong.domain.apply.entity.Apply;
import jikgong.domain.apply.repository.ApplyRepository;
import jikgong.domain.headHunting.dtos.HeadHuntingListResponse;
import jikgong.domain.headHunting.dtos.WorkerInfoResponse;
import jikgong.domain.headHunting.dtos.offer.OfferJobPostRequest;
import jikgong.domain.headHunting.dtos.offer.OfferRequest;
import jikgong.domain.headHunting.dtos.offer.SelectOfferJobPostResponse;
import jikgong.domain.headHunting.entity.HeadHunting;
import jikgong.domain.headHunting.entity.OfferDate;
import jikgong.domain.headHunting.entity.SortType;
import jikgong.domain.headHunting.repository.HeadHuntingRepository;
import jikgong.domain.jobPost.entity.JobPost;
import jikgong.domain.jobPost.entity.Tech;
import jikgong.domain.jobPost.repository.JobPostRepository;
import jikgong.domain.member.entity.Member;
import jikgong.domain.member.repository.MemberRepository;
import jikgong.domain.notification.entity.NotificationType;
import jikgong.domain.project.entity.Project;
import jikgong.domain.project.repository.ProjectRepository;
import jikgong.domain.resume.entity.Resume;
import jikgong.domain.resume.repository.ResumeRepository;
import jikgong.global.event.dtos.NotificationEvent;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import jikgong.global.utils.TimeTransfer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HeadHuntingService {
    private final MemberRepository memberRepository;
    private final ProjectRepository projectRepository;
    private final HeadHuntingRepository headHuntingRepository;
    private final ApplyRepository applyRepository;
    private final ApplicationEventPublisher publisher;
    private final JobPostRepository jobPostRepository;
    private final ResumeRepository resumeRepository;

    public Page<HeadHuntingListResponse> findHeadHuntingList(Long memberId, Long projectId, Tech tech, Float bound, SortType sortType, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));

        return resumeRepository.findHeadHuntingMemberList(project.getAddress(), tech, bound, sortType, pageable);
    }

    public void offerJobPost(Long companyId, OfferRequest request) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Resume resume = resumeRepository.findByIdWithMember(request.getResumeId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESUME_NOT_FOUND));

        Member worker = resume.getMember();

        // 요청한 jobPostId 유효 체크
        validationJobPostId(request);

        List<HeadHunting> headHuntingList = new ArrayList<>();
        for (OfferJobPostRequest offerJobPostRequest : request.getOfferJobPostRequest()) {
            // headHunting 저장 및 offerDate 영속화
            HeadHunting headHunting = HeadHunting.createEntity(company, worker);
            headHunting.addOfferDate(OfferDate.createEntityList(headHunting, offerJobPostRequest.getWorkDateList()));
            headHuntingList.add(headHunting);

            publisher.publishEvent(new NotificationEvent(company.getCompanyInfo().getCompanyName(), offerJobPostRequest.getWorkDateList(), offerJobPostRequest.getJobPostId(), NotificationType.OFFER, worker.getId()));
        }
        headHuntingRepository.saveAll(headHuntingList);
    }

    private void validationJobPostId(OfferRequest request) {
        List<Long> jobPostIdList = request.getOfferJobPostRequest().stream()
                .map(OfferJobPostRequest::getJobPostId)
                .collect(Collectors.toList());
        Long findJobPostCount = jobPostRepository.findByIdList(jobPostIdList);
        if (jobPostIdList.size() != findJobPostCount) {
            throw new CustomException(ErrorCode.JOB_POST_NOT_FOUND);
        }
    }

    public WorkerInfoResponse findWorkerInfo(Long companyId, Long resumeId, LocalDate selectMonth) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Resume resume = resumeRepository.findByIdWithMember(resumeId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESUME_NOT_FOUND));

        List<Apply> findCantWorkDate = applyRepository.findCantWorkDate(
                resume.getMember().getId(),
                TimeTransfer.getFirstDayOfMonth(selectMonth),
                TimeTransfer.getLastDayOfMonth(selectMonth));

        return WorkerInfoResponse.from(resume, findCantWorkDate);
    }

    public SelectOfferJobPostResponse findAvailableJobPosts(Long companyId, Long workerId, Long projectId) {
        Member company = memberRepository.findById(companyId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Member worker = memberRepository.findById(workerId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new CustomException(ErrorCode.PROJECT_NOT_FOUND));


        List<LocalDate> cantWorkDate = applyRepository.findAllCantWorkDate(worker.getId()).stream()
                .map(apply -> apply.getWorkDate().getWorkDate())
                .collect(Collectors.toList());
        Set<LocalDate> cantWorkDateSet = new HashSet<>(cantWorkDate);

        List<JobPost> jobPostList = jobPostRepository.findByProject(project.getId());

        return SelectOfferJobPostResponse.from(jobPostList, worker, cantWorkDateSet);
    }
}
