package jikgong.domain.jobpost.entity.jobpost;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAvailableInfo is a Querydsl query type for AvailableInfo
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QAvailableInfo extends BeanPath<AvailableInfo> {

    private static final long serialVersionUID = -1970221195L;

    public static final QAvailableInfo availableInfo = new QAvailableInfo("availableInfo");

    public final BooleanPath meal = createBoolean("meal");

    public final EnumPath<Park> park = createEnum("park", Park.class);

    public final BooleanPath pickup = createBoolean("pickup");

    public QAvailableInfo(String variable) {
        super(AvailableInfo.class, forVariable(variable));
    }

    public QAvailableInfo(Path<? extends AvailableInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAvailableInfo(PathMetadata metadata) {
        super(AvailableInfo.class, metadata);
    }

}

