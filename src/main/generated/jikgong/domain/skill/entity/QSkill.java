package jikgong.domain.skill.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSkill is a Querydsl query type for Skill
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSkill extends EntityPathBase<Skill> {

    private static final long serialVersionUID = 1645638988L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSkill skill = new QSkill("skill");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final jikgong.domain.resume.entity.QResume resume;

    public final NumberPath<Integer> skillPeriod = createNumber("skillPeriod", Integer.class);

    public final EnumPath<jikgong.domain.workexperience.entity.Tech> tech = createEnum("tech", jikgong.domain.workexperience.entity.Tech.class);

    public QSkill(String variable) {
        this(Skill.class, forVariable(variable), INITS);
    }

    public QSkill(Path<? extends Skill> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSkill(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSkill(PathMetadata metadata, PathInits inits) {
        this(Skill.class, metadata, inits);
    }

    public QSkill(Class<? extends Skill> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.resume = inits.isInitialized("resume") ? new jikgong.domain.resume.entity.QResume(forProperty("resume"), inits.get("resume")) : null;
    }

}

