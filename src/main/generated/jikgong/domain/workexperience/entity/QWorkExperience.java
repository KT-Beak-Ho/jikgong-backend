package jikgong.domain.workexperience.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWorkExperience is a Querydsl query type for WorkExperience
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWorkExperience extends EntityPathBase<WorkExperience> {

    private static final long serialVersionUID = 479035036L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWorkExperience workExperience = new QWorkExperience("workExperience");

    public final NumberPath<Integer> experienceMonths = createNumber("experienceMonths", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final jikgong.domain.member.entity.QMember member;

    public final EnumPath<Tech> tech = createEnum("tech", Tech.class);

    public QWorkExperience(String variable) {
        this(WorkExperience.class, forVariable(variable), INITS);
    }

    public QWorkExperience(Path<? extends WorkExperience> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWorkExperience(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWorkExperience(PathMetadata metadata, PathInits inits) {
        this(WorkExperience.class, metadata, inits);
    }

    public QWorkExperience(Class<? extends WorkExperience> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new jikgong.domain.member.entity.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

