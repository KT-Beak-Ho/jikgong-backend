package jikgong.domain.profit.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProfit is a Querydsl query type for Profit
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProfit extends EntityPathBase<Profit> {

    private static final long serialVersionUID = 1238640334L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProfit profit = new QProfit("profit");

    public final DatePath<java.time.LocalDate> date = createDate("date", java.time.LocalDate.class);

    public final TimePath<java.time.LocalTime> endTime = createTime("endTime", java.time.LocalTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final jikgong.domain.member.entity.QMember member;

    public final EnumPath<ProfitType> profitType = createEnum("profitType", ProfitType.class);

    public final TimePath<java.time.LocalTime> startTime = createTime("startTime", java.time.LocalTime.class);

    public final EnumPath<jikgong.domain.workexperience.entity.Tech> tech = createEnum("tech", jikgong.domain.workexperience.entity.Tech.class);

    public final StringPath title = createString("title");

    public final NumberPath<Integer> wage = createNumber("wage", Integer.class);

    public QProfit(String variable) {
        this(Profit.class, forVariable(variable), INITS);
    }

    public QProfit(Path<? extends Profit> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProfit(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProfit(PathMetadata metadata, PathInits inits) {
        this(Profit.class, metadata, inits);
    }

    public QProfit(Class<? extends Profit> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new jikgong.domain.member.entity.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

