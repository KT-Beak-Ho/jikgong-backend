package jikgong.domain.offer.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOffer is a Querydsl query type for Offer
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOffer extends EntityPathBase<Offer> {

    private static final long serialVersionUID = -306176212L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOffer offer = new QOffer("offer");

    public final jikgong.domain.common.QBaseEntity _super = new jikgong.domain.common.QBaseEntity(this);

    public final jikgong.domain.member.entity.QMember company;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDate = _super.createdDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final jikgong.domain.jobpost.entity.jobpost.QJobPost jobPost;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final EnumPath<OfferStatus> offerStatus = createEnum("offerStatus", OfferStatus.class);

    public final ListPath<OfferWorkDate, QOfferWorkDate> offerWorkDateList = this.<OfferWorkDate, QOfferWorkDate>createList("offerWorkDateList", OfferWorkDate.class, QOfferWorkDate.class, PathInits.DIRECT2);

    public final jikgong.domain.member.entity.QMember worker;

    public QOffer(String variable) {
        this(Offer.class, forVariable(variable), INITS);
    }

    public QOffer(Path<? extends Offer> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOffer(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOffer(PathMetadata metadata, PathInits inits) {
        this(Offer.class, metadata, inits);
    }

    public QOffer(Class<? extends Offer> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.company = inits.isInitialized("company") ? new jikgong.domain.member.entity.QMember(forProperty("company"), inits.get("company")) : null;
        this.jobPost = inits.isInitialized("jobPost") ? new jikgong.domain.jobpost.entity.jobpost.QJobPost(forProperty("jobPost"), inits.get("jobPost")) : null;
        this.worker = inits.isInitialized("worker") ? new jikgong.domain.member.entity.QMember(forProperty("worker"), inits.get("worker")) : null;
    }

}

