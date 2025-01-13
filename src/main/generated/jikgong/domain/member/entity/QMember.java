package jikgong.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 363294970L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMember member = new QMember("member1");

    public final jikgong.domain.common.QBaseEntity _super = new jikgong.domain.common.QBaseEntity(this);

    public final ListPath<jikgong.domain.apply.entity.Apply, jikgong.domain.apply.entity.QApply> applyList = this.<jikgong.domain.apply.entity.Apply, jikgong.domain.apply.entity.QApply>createList("applyList", jikgong.domain.apply.entity.Apply.class, jikgong.domain.apply.entity.QApply.class, PathInits.DIRECT2);

    public final QCompany companyInfo;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final StringPath deviceToken = createString("deviceToken");

    public final StringPath email = createString("email");

    public final ListPath<jikgong.domain.history.entity.History, jikgong.domain.history.entity.QHistory> historyList = this.<jikgong.domain.history.entity.History, jikgong.domain.history.entity.QHistory>createList("historyList", jikgong.domain.history.entity.History.class, jikgong.domain.history.entity.QHistory.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isDeleted = createBoolean("isDeleted");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final ListPath<jikgong.domain.location.entity.Location, jikgong.domain.location.entity.QLocation> locationList = this.<jikgong.domain.location.entity.Location, jikgong.domain.location.entity.QLocation>createList("locationList", jikgong.domain.location.entity.Location.class, jikgong.domain.location.entity.QLocation.class, PathInits.DIRECT2);

    public final StringPath loginId = createString("loginId");

    public final StringPath password = createString("password");

    public final StringPath phone = createString("phone");

    public final BooleanPath privacyConsent = createBoolean("privacyConsent");

    public final EnumPath<Role> role = createEnum("role", Role.class);

    public final QWorker workerInfo;

    public final ListPath<jikgong.domain.workexperience.entity.WorkExperience, jikgong.domain.workexperience.entity.QWorkExperience> workExperienceList = this.<jikgong.domain.workexperience.entity.WorkExperience, jikgong.domain.workexperience.entity.QWorkExperience>createList("workExperienceList", jikgong.domain.workexperience.entity.WorkExperience.class, jikgong.domain.workexperience.entity.QWorkExperience.class, PathInits.DIRECT2);

    public QMember(String variable) {
        this(Member.class, forVariable(variable), INITS);
    }

    public QMember(Path<? extends Member> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMember(PathMetadata metadata, PathInits inits) {
        this(Member.class, metadata, inits);
    }

    public QMember(Class<? extends Member> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.companyInfo = inits.isInitialized("companyInfo") ? new QCompany(forProperty("companyInfo"), inits.get("companyInfo")) : null;
        this.workerInfo = inits.isInitialized("workerInfo") ? new QWorker(forProperty("workerInfo"), inits.get("workerInfo")) : null;
    }

}

