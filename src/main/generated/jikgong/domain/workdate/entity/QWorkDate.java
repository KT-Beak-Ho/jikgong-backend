package jikgong.domain.workdate.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWorkDate is a Querydsl query type for WorkDate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWorkDate extends EntityPathBase<WorkDate> {

    private static final long serialVersionUID = 415302692L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWorkDate workDate = new QWorkDate("workDate");

    public final ListPath<jikgong.domain.apply.entity.Apply, jikgong.domain.apply.entity.QApply> applyList = this.<jikgong.domain.apply.entity.Apply, jikgong.domain.apply.entity.QApply>createList("applyList", jikgong.domain.apply.entity.Apply.class, jikgong.domain.apply.entity.QApply.class, PathInits.DIRECT2);

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final jikgong.domain.jobpost.entity.jobpost.QJobPost jobPost;

    public final NumberPath<Integer> recruitNum = createNumber("recruitNum", Integer.class);

    public final NumberPath<Integer> registeredNum = createNumber("registeredNum", Integer.class);

    public QWorkDate(String variable) {
        this(WorkDate.class, forVariable(variable), INITS);
    }

    public QWorkDate(Path<? extends WorkDate> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWorkDate(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWorkDate(PathMetadata metadata, PathInits inits) {
        this(WorkDate.class, metadata, inits);
    }

    public QWorkDate(Class<? extends WorkDate> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.jobPost = inits.isInitialized("jobPost") ? new jikgong.domain.jobpost.entity.jobpost.QJobPost(forProperty("jobPost"), inits.get("jobPost")) : null;
    }

}

