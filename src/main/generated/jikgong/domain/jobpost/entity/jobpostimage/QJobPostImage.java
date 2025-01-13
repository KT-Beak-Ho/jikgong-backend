package jikgong.domain.jobpost.entity.jobpostimage;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QJobPostImage is a Querydsl query type for JobPostImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QJobPostImage extends EntityPathBase<JobPostImage> {

    private static final long serialVersionUID = 1541719837L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QJobPostImage jobPostImage = new QJobPostImage("jobPostImage");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath isThumbnail = createBoolean("isThumbnail");

    public final jikgong.domain.jobpost.entity.jobpost.QJobPost jobPost;

    public final StringPath s3Url = createString("s3Url");

    public final StringPath storeImgName = createString("storeImgName");

    public QJobPostImage(String variable) {
        this(JobPostImage.class, forVariable(variable), INITS);
    }

    public QJobPostImage(Path<? extends JobPostImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QJobPostImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QJobPostImage(PathMetadata metadata, PathInits inits) {
        this(JobPostImage.class, metadata, inits);
    }

    public QJobPostImage(Class<? extends JobPostImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.jobPost = inits.isInitialized("jobPost") ? new jikgong.domain.jobpost.entity.jobpost.QJobPost(forProperty("jobPost"), inits.get("jobPost")) : null;
    }

}

