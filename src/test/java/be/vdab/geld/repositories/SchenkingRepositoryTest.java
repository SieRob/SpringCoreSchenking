package be.vdab.geld.repositories;

import be.vdab.geld.domain.Schenking;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import(SchenkingRepository.class)
@Sql("/mensen.sql")
class SchenkingRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {
    private static final String SCHENKINGEN = "schenkingen";
    private final SchenkingRepository schenkingRepo;

    public SchenkingRepositoryTest(SchenkingRepository schenkingRepo) {
        this.schenkingRepo = schenkingRepo;
    }

    private long idVanTestMens1(){
        return jdbcTemplate.queryForObject(
                "SELECT id FROM mensen WHERE naam = 'test1'", Long.class
        );
    }

    private long idVanTestMens2(){
        return jdbcTemplate.queryForObject(
                "SELECT id FROM mensen WHERE naam = 'test2'", Long.class
        );
    }

    @Test
    void create(){
        var vanMensid = idVanTestMens1();
        var aanMensId = idVanTestMens2();

        schenkingRepo.create(new Schenking(vanMensid,aanMensId, BigDecimal.ONE));

        assertThat(countRowsInTableWhere(SCHENKINGEN, "vanMensId = " + vanMensid + " AND aanMensId = " + aanMensId)).isOne();
    }
}