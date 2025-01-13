package jikgong.domain.jobpost.entity.jobpost;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QJobPost is a Querydsl query type for JobPost
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QJobPost extends EntityPathBase<JobPost> {

    private static final long serialVersionUID = 704354523L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QJobPost jobPost = new QJobPost("jobPost");

    public final jikgong.domain.common.QBaseEntity _super = new jikgong.domain.common.QBaseEntity(this);

    public final QAvailableInfo availableInfo;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final TimePath<java.time.LocalTime> endTime = createTime("endTime", java.time.LocalTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isTemporary = createBoolean("isTemporary");

    public final QJobPostAddress jobPostAddress;

    public final ListPath<jikgong.domain.jobpost.entity.jobpostimage.JobPostImage, jikgong.domain.jobpost.entity.jobpostimage.QJobPostImage> jobPostImageList = this.<jikgong.domain.jobpost.entity.jobpostimage.JobPostImage, jikgong.domain.jobpost.entity.jobpostimage.QJobPostImage>createList("jobPostImageList", jikgong.domain.jobpost.entity.jobpostimage.JobPostImage.class, jikgong.domain.jobpost.entity.jobpostimage.QJobPostImage.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath managerName = createString("managerName");

    public final jikgong.domain.member.entity.QMember member;

    public final StringPath parkDetail = createString("parkDetail");

    public final StringPath phone = createString("phone");

    public final ListPath<jikgong.domain.jobpost.entity.pickup.Pickup, jikgong.domain.jobpost.entity.pickup.QPickup> pickupList = this.<jikgong.domain.jobpost.entity.pickup.Pickup, jikgong.domain.jobpost.entity.pickup.QPickup>createList("pickupList", jikgong.domain.jobpost.entity.pickup.Pickup.class, jikgong.domain.jobpost.entity.pickup.QPickup.class, PathInits.DIRECT2);

    public final StringPath preparation = createString("preparation");

    public final jikgong.domain.project.entity.QProject project;

    public final NumberPath<Integer> recruitNum = createNumber("recruitNum", Integer.class);

    public final ListPath<jikgong.domain.scrap.entity.Scrap, jikgong.domain.scrap.entity.QScrap> scrapList = this.<jikgong.domain.scrap.entity.Scrap, jikgong.domain.scrap.entity.QScrap>createList("scrapList", jikgong.domain.scrap.entity.Scrap.class, jikgong.domain.scrap.entity.QScrap.class, PathInits.DIRECT2);

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public final TimePath<java.time.LocalTime> startTime = createTime("startTime", java.time.LocalTime.class);

    public final EnumPath<jikgong.domain.workexperience.entity.Tech> tech = createEnum("tech", jikgong.domain.workexperience.entity.Tech.class);

    public final StringPath title = createString("title");

    public final NumberPath<Integer> wage = createNumber("wage", Integer.class);

    public final ListPath<jikgong.domain.workdate.entity.WorkDate, jikgong.domain.workdate.entity.QWorkDate> workDateList = this.<jikgong.domain.workdate.entity.WorkDate, jikgong.domain.workdate.entity.QWorkDate>createList("workDateList", jikgong.domain.workdate.entity.WorkDate.class, jikgong.domain.workdate.entity.QWorkDate.class, PathInits.DIRECT2);

    public QJobPost(String variable) {
        this(JobPost.class, forVariable(variable), INITS);
    }

    public QJobPost(Path<? extends JobPost> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QJobPost(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QJobPost(PathMetadata metadata, PathInits inits) {
        this(JobPost.class, metadata, inits);
    }

    public QJobPost(Class<? extends JobPost> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.availableInfo = inits.isInitialized("availableInfo") ? new QAvailableInfo(forProperty("availableInfo")) : null;
        this.jobPostAddress = inits.isInitialized("jobPostAddress") ? new QJobPostAddress(forProperty("jobPostAddress")) : null;
        this.member = inits.isInitialized("member") ? new jikgong.domain.member.entity.QMember(forProperty("member"), inits.get("member")) : null;
        this.project = inits.isInitialized("project") ? new jikgong.domain.project.entity.QProject(forProperty("project"), inits.get("project")) : null;
    }

}

