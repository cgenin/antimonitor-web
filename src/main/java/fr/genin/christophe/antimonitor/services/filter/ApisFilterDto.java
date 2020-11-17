package fr.genin.christophe.antimonitor.services.filter;

import org.jooq.Condition;
import org.jooq.DSLContext;

import java.util.Objects;

import static fr.genin.christophe.antimonitor.jooq.generated.Tables.APIS;
import static fr.genin.christophe.antimonitor.jooq.generated.Tables.PROJECTS;

public class ApisFilterDto {

    public final String q;
    public final String method;
    public final String domain;
    public final String path;

    public ApisFilterDto(String q, String method, String domain, String path) {
        this.q = q;
        this.method = method;
        this.domain = domain;
        this.path = path;
    }


    public Condition whereQ(){
        if(Objects.isNull(q) || q.trim().isEmpty()){
            return APIS.DOCUMENT.isNotNull();
        }
        return APIS.DOCUMENT.likeIgnoreCase(q);
    }

    public Condition whereMethod(){
        if(Objects.isNull(method) || method.trim().isEmpty()){
            return APIS.METHOD.isNotNull();
        }
        return APIS.METHOD.eq(method);
    }

    public Condition wherePath(){
        if(Objects.isNull(path) || path.trim().isEmpty()){
            return APIS.FULLURL.isNotNull();
        }
        return APIS.FULLURL.likeIgnoreCase(path);
    }

    public Condition whereDomain(DSLContext dsl){
        if(Objects.isNull(domain) || domain.trim().isEmpty()){
            return APIS.IDPROJECT.isNotNull();
        }

        return APIS.IDPROJECT.in(dsl
                .select(PROJECTS.ID)
                .from(PROJECTS)
                .where(PROJECTS.NAME.likeIgnoreCase(domain))
        );
    }

    public Condition where(DSLContext dsl){
        return whereDomain(dsl)
                .and(whereMethod())
                .and(wherePath())
                .and(whereQ());

    }
}
