package jikgong.domain.jobpost.entity.jobpost;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QJobPostAddress is a Querydsl query type for JobPostAddress
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QJobPostAddress extends BeanPath<JobPostAddress> {

    private static final long serialVersionUID = 1689976473L;

    public static final QJobPostAddress jobPostAddress = new QJobPostAddress("jobPostAddress");

    public final StringPath address = createString("address");

    public final StringPath city = createString("city");

    public final StringPath district = createString("district");

    public final NumberPath<Float> latitude = createNumber("latitude", Float.class);

    public final NumberPath<Float> longitude = createNumber("longitude", Float.class);

    public QJobPostAddress(String variable) {
        super(JobPostAddress.class, forVariable(variable));
    }

    public QJobPostAddress(Path<? extends JobPostAddress> path) {
        super(path.getType(), path.getMetadata());
    }

    public QJobPostAddress(PathMetadata metadata) {
        super(JobPostAddress.class, metadata);
    }

}

