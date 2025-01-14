package jikgong.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCompany is a Querydsl query type for Company
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QCompany extends BeanPath<Company> {

    private static final long serialVersionUID = -1621155299L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCompany company = new QCompany("company");

    public final StringPath businessNumber = createString("businessNumber");

    public final StringPath companyName = createString("companyName");

    public final QCompanyNotificationInfo companyNotificationInfo;

    public final StringPath manager = createString("manager");

    public final StringPath region = createString("region");

    public final StringPath requestContent = createString("requestContent");

    public QCompany(String variable) {
        this(Company.class, forVariable(variable), INITS);
    }

    public QCompany(Path<? extends Company> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCompany(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCompany(PathMetadata metadata, PathInits inits) {
        this(Company.class, metadata, inits);
    }

    public QCompany(Class<? extends Company> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.companyNotificationInfo = inits.isInitialized("companyNotificationInfo") ? new QCompanyNotificationInfo(forProperty("companyNotificationInfo")) : null;
    }

}

