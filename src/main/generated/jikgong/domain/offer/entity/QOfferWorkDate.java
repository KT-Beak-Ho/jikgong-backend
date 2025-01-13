package jikgong.domain.offer.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOfferWorkDate is a Querydsl query type for OfferWorkDate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOfferWorkDate extends EntityPathBase<OfferWorkDate> {

    private static final long serialVersionUID = -119877205L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOfferWorkDate offerWorkDate = new QOfferWorkDate("offerWorkDate");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QOffer offer;

    public final EnumPath<OfferWorkDateStatus> offerWorkDateStatus = createEnum("offerWorkDateStatus", OfferWorkDateStatus.class);

    public final jikgong.domain.workdate.entity.QWorkDate workDate;

    public QOfferWorkDate(String variable) {
        this(OfferWorkDate.class, forVariable(variable), INITS);
    }

    public QOfferWorkDate(Path<? extends OfferWorkDate> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOfferWorkDate(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOfferWorkDate(PathMetadata metadata, PathInits inits) {
        this(OfferWorkDate.class, metadata, inits);
    }

    public QOfferWorkDate(Class<? extends OfferWorkDate> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.offer = inits.isInitialized("offer") ? new QOffer(forProperty("offer"), inits.get("offer")) : null;
        this.workDate = inits.isInitialized("workDate") ? new jikgong.domain.workdate.entity.QWorkDate(forProperty("workDate"), inits.get("workDate")) : null;
    }

}

