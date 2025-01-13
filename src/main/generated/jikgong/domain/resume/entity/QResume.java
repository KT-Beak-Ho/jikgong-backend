package jikgong.domain.resume.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QResume is a Querydsl query type for Resume
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QResume extends EntityPathBase<Resume> {

    private static final long serialVersionUID = -1194512672L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QResume resume = new QResume("resume");

    public final jikgong.domain.jobpost.entity.jobpost.QAvailableInfo availableInfo;

    public final NumberPath<Integer> career = createNumber("career", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final jikgong.domain.member.entity.QMember member;

    public final TimePath<java.time.LocalTime> preferTimeEnd = createTime("preferTimeEnd", java.time.LocalTime.class);

    public final TimePath<java.time.LocalTime> preferTimeStart = createTime("preferTimeStart", java.time.LocalTime.class);

    public final ListPath<jikgong.domain.skill.entity.Skill, jikgong.domain.skill.entity.QSkill> skillList = this.<jikgong.domain.skill.entity.Skill, jikgong.domain.skill.entity.QSkill>createList("skillList", jikgong.domain.skill.entity.Skill.class, jikgong.domain.skill.entity.QSkill.class, PathInits.DIRECT2);

    public QResume(String variable) {
        this(Resume.class, forVariable(variable), INITS);
    }

    public QResume(Path<? extends Resume> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QResume(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QResume(PathMetadata metadata, PathInits inits) {
        this(Resume.class, metadata, inits);
    }

    public QResume(Class<? extends Resume> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.availableInfo = inits.isInitialized("availableInfo") ? new jikgong.domain.jobpost.entity.jobpost.QAvailableInfo(forProperty("availableInfo")) : null;
        this.member = inits.isInitialized("member") ? new jikgong.domain.member.entity.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

