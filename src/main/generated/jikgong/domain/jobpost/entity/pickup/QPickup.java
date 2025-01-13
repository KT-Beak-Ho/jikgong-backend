package jikgong.domain.jobpost.entity.pickup;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPickup is a Querydsl query type for Pickup
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPickup extends EntityPathBase<Pickup> {

    private static final long serialVersionUID = 1166332957L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPickup pickup = new QPickup("pickup");

    public final StringPath address = createString("address");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final jikgong.domain.jobpost.entity.jobpost.QJobPost jobPost;

    public QPickup(String variable) {
        this(Pickup.class, forVariable(variable), INITS);
    }

    public QPickup(Path<? extends Pickup> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPickup(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPickup(PathMetadata metadata, PathInits inits) {
        this(Pickup.class, metadata, inits);
    }

    public QPickup(Class<? extends Pickup> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.jobPost = inits.isInitialized("jobPost") ? new jikgong.domain.jobpost.entity.jobpost.QJobPost(forProperty("jobPost"), inits.get("jobPost")) : null;
    }

}

