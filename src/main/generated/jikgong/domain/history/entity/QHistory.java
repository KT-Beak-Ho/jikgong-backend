package jikgong.domain.history.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHistory is a Querydsl query type for History
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHistory extends EntityPathBase<History> {

    private static final long serialVersionUID = -1380020756L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHistory history = new QHistory("history");

    public final jikgong.domain.common.QBaseEntity _super = new jikgong.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final EnumPath<WorkStatus> endStatus = createEnum("endStatus", WorkStatus.class);

    public final DateTimePath<java.time.LocalDateTime> endStatusDecisionTime = createDateTime("endStatusDecisionTime", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final jikgong.domain.member.entity.QMember member;

    public final EnumPath<WorkStatus> startStatus = createEnum("startStatus", WorkStatus.class);

    public final DateTimePath<java.time.LocalDateTime> startStatusDecisionTime = createDateTime("startStatusDecisionTime", java.time.LocalDateTime.class);

    public final jikgong.domain.workdate.entity.QWorkDate workDate;

    public QHistory(String variable) {
        this(History.class, forVariable(variable), INITS);
    }

    public QHistory(Path<? extends History> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHistory(PathMetadata metadata, PathInits inits) {
        this(History.class, metadata, inits);
    }

    public QHistory(Class<? extends History> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new jikgong.domain.member.entity.QMember(forProperty("member"), inits.get("member")) : null;
        this.workDate = inits.isInitialized("workDate") ? new jikgong.domain.workdate.entity.QWorkDate(forProperty("workDate"), inits.get("workDate")) : null;
    }

}

