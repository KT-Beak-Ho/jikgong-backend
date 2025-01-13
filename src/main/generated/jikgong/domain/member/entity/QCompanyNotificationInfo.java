package jikgong.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QCompanyNotificationInfo is a Querydsl query type for CompanyNotificationInfo
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QCompanyNotificationInfo extends BeanPath<CompanyNotificationInfo> {

    private static final long serialVersionUID = -504987530L;

    public static final QCompanyNotificationInfo companyNotificationInfo = new QCompanyNotificationInfo("companyNotificationInfo");

    public final BooleanPath companyEvent = createBoolean("companyEvent");

    public final BooleanPath companyOfferDecision = createBoolean("companyOfferDecision");

    public QCompanyNotificationInfo(String variable) {
        super(CompanyNotificationInfo.class, forVariable(variable));
    }

    public QCompanyNotificationInfo(Path<? extends CompanyNotificationInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCompanyNotificationInfo(PathMetadata metadata) {
        super(CompanyNotificationInfo.class, metadata);
    }

}

