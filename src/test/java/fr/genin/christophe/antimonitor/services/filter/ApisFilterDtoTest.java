package fr.genin.christophe.antimonitor.services.filter;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.Test;

import static fr.genin.christophe.antimonitor.jooq.generated.Tables.APIS;
import static org.assertj.core.api.Assertions.assertThat;

public class ApisFilterDtoTest {

    public static final String WHERE_DOMAIN = "\"apis\".\"idproject\" in (select \"public\".\"projects\".\"id\" from \"public\".\"projects\" where \"public\".\"projects\".\"name\" ilike ?";
    public static final String WHERE_PATH = "\"apis\".\"fullurl\" ilike ?";
    public static final String WHERE_Q = "\"apis\".\"document\" ilike ?";
    public static final String WHERE_METHOD = "\"apis\".\"method\" = ?";
    private DSLContext dsl = DSL.using(SQLDialect.POSTGRES);

    @Test
    public void shouldFilterDomain(){
        final ApisFilterDto filter = new ApisFilterDto(null, null, "domaine", null);
        assertThat(dsl.select()
                .from(APIS)
                .where(filter.where(dsl))
                .getSQL())
                .doesNotContain(WHERE_Q)
                .doesNotContain(WHERE_PATH)
                .doesNotContain(WHERE_METHOD)
                .contains(WHERE_DOMAIN);
    }

    @Test
    public void shouldFilterPath(){
        final ApisFilterDto filter = new ApisFilterDto(null, null, null, "path");
        assertThat(dsl.select()
                .from(APIS)
                .where(filter.where(dsl))
                .getSQL())
                .doesNotContain(WHERE_DOMAIN)
                .doesNotContain(WHERE_Q)
                .doesNotContain(WHERE_METHOD)
                .contains(WHERE_PATH);
    }

    @Test
    public void shouldFilterQ(){
        final ApisFilterDto filter = new ApisFilterDto("q", null, null, null);
        assertThat(dsl.select()
                .from(APIS)
                .where(filter.where(dsl))
                .getSQL())
                .doesNotContain(WHERE_DOMAIN)
                .doesNotContain(WHERE_PATH)
                .doesNotContain(WHERE_METHOD)
                .contains(WHERE_Q);
    }

    @Test
    public void shouldFilterMethod(){
        final ApisFilterDto filter = new ApisFilterDto(null, "GET", null, null);
        assertThat(dsl.select()
                .from(APIS)
                .where(filter.where(dsl))
                .getSQL())
                .doesNotContain(WHERE_DOMAIN)
                .doesNotContain(WHERE_PATH)
                .doesNotContain(WHERE_Q)
            .contains(WHERE_METHOD);
    }

    @Test
    public void shouldFilterAll(){
        final ApisFilterDto filter = new ApisFilterDto("q", "GET", "d", "p");
        assertThat(dsl.select()
                .from(APIS)
                .where(filter.where(dsl))
                .getSQL())
                .contains(WHERE_DOMAIN)
                .contains(WHERE_PATH)
                .contains(WHERE_Q)
                .contains(WHERE_METHOD);
    }

    @Test
    public void shouldFilterNone(){
        assertThat(dsl.select()
                .from(APIS)
                .where(new ApisFilterDto(null, null, null, null).where(dsl))
                .getSQL())
                .doesNotContain(WHERE_DOMAIN)
                .doesNotContain(WHERE_PATH)
                .doesNotContain(WHERE_Q)
                .doesNotContain(WHERE_METHOD);

        assertThat(dsl.select()
                .from(APIS)
                .where(new ApisFilterDto("", "", "", "").where(dsl))
                .getSQL())
                .doesNotContain(WHERE_DOMAIN)
                .doesNotContain(WHERE_PATH)
                .doesNotContain(WHERE_Q)
                .doesNotContain(WHERE_METHOD);
    }
}
