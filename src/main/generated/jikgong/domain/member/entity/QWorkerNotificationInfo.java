package jikgong.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWorkerNotificationInfo is a Querydsl query type for WorkerNotificationInfo
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QWorkerNotificationInfo extends BeanPath<WorkerNotificationInfo> {

    private static final long serialVersionUID = -1395591625L;

    public static final QWorkerNotificationInfo workerNotificationInfo = new QWorkerNotificationInfo("workerNotificationInfo");

    public final BooleanPath workerApplyDecision = createBoolean("workerApplyDecision");

    public final BooleanPath workerEvent = createBoolean("workerEvent");

    public final BooleanPath workerOffer = createBoolean("workerOffer");

    public QWorkerNotificationInfo(String variable) {
        super(WorkerNotificationInfo.class, forVariable(variable));
    }

    public QWorkerNotificationInfo(Path<? extends WorkerNotificationInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWorkerNotificationInfo(PathMetadata metadata) {
        super(WorkerNotificationInfo.class, metadata);
    }

}

