package jikgong.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QWorker is a Querydsl query type for Worker
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QWorker extends BeanPath<Worker> {

    private static final long serialVersionUID = 658979294L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QWorker worker = new QWorker("worker");

    public final StringPath account = createString("account");

    public final StringPath bank = createString("bank");

    public final StringPath birth = createString("birth");

    public final BooleanPath credentialLiabilityConsent = createBoolean("credentialLiabilityConsent");

    public final StringPath educationCertificateImgPath = createString("educationCertificateImgPath");

    public final EnumPath<Gender> gender = createEnum("gender", Gender.class);

    public final BooleanPath hasVisa = createBoolean("hasVisa");

    public final BooleanPath isOffer = createBoolean("isOffer");

    public final EnumPath<Nationality> nationality = createEnum("nationality", Nationality.class);

    public final DatePath<java.time.LocalDate> visaExpiryDate = createDate("visaExpiryDate", java.time.LocalDate.class);

    public final StringPath workerCardImgPath = createString("workerCardImgPath");

    public final StringPath workerCardNumber = createString("workerCardNumber");

    public final StringPath workerName = createString("workerName");

    public final QWorkerNotificationInfo workerNotificationInfo;

    public QWorker(String variable) {
        this(Worker.class, forVariable(variable), INITS);
    }

    public QWorker(Path<? extends Worker> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QWorker(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QWorker(PathMetadata metadata, PathInits inits) {
        this(Worker.class, metadata, inits);
    }

    public QWorker(Class<? extends Worker> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.workerNotificationInfo = inits.isInitialized("workerNotificationInfo") ? new QWorkerNotificationInfo(forProperty("workerNotificationInfo")) : null;
    }

}

