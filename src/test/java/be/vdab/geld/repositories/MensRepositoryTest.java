package be.vdab.geld.repositories;

import be.vdab.geld.domain.Mens;
import be.vdab.geld.exceptions.MensNietGevondenException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


@JdbcTest
@Import(MensRepository.class)
@Sql({"/mensen.sql", "/schenkingen.sql"})
class MensRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {
    private static final String MENSEN = "mensen";
    private final MensRepository mensRepo;

    public MensRepositoryTest(MensRepository mensRepo) {
        this.mensRepo = mensRepo;
    }

    @Test
    void findAantal() {
        assertThat(mensRepo.findAantal()).isEqualTo(countRowsInTable(MENSEN));
    }

    @Test
    void findAllGeeftAlleMensenGesorteerdOpId() {
        assertThat(mensRepo.findAll())
                .hasSize(countRowsInTable(MENSEN))
                .extracting(Mens::getId)
                .isSorted();
    }

    @Test
    void create(){
        var id = mensRepo.create(new Mens(0,"test3", BigDecimal.TEN));
        assertThat(id).isPositive();
        assertThat(countRowsInTableWhere(MENSEN, "id = "+ id)).isOne();
    }

    private long idVantTestMens1(){
        return jdbcTemplate.queryForObject("SELECT id FROM mensen WHERE naam = 'test1'", Long.class);
    }
    private long idVantTestMens2(){
        return jdbcTemplate.queryForObject("SELECT id FROM mensen WHERE naam = 'test2'", Long.class);
    }
    @Test
    void delete(){
        var id = idVantTestMens1();
        mensRepo.delete(id);
        assertThat(countRowsInTableWhere(MENSEN, "id = "+ id)).isZero();
    }

    @Test
    void findById(){
        assertThat(mensRepo.findById(idVantTestMens1())).hasValueSatisfying(
                mens -> assertThat(mens.getNaam()).isEqualTo("test1")
        );
    }

    @Test
    void findByOnbestaandeIdVindtGeenMens(){
        assertThat(mensRepo.findById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void update(){
        var id = idVantTestMens1();
        var mens = new Mens(id, "mens1", BigDecimal.TEN);
        mensRepo.update(mens);
        assertThat(countRowsInTableWhere(MENSEN, "geld = 10 AND id =" +id)).isOne();
    }

    @Test
    void updateOnbestaandeMensGeeftEenFout(){
        assertThatExceptionOfType(MensNietGevondenException.class).isThrownBy(
                ()->mensRepo.update(
                        new Mens(Long.MAX_VALUE, "test3", BigDecimal.TEN)
                )
        );
    }

    @Test
    void findAndLockById(){
        assertThat(mensRepo.findAndLockById(idVantTestMens1())).hasValueSatisfying(
                mens -> assertThat(mens.getNaam()).isEqualTo("test1")
        );
    }

    @Test
    void findAndLockByOnbestaandeIdVindtGeenMens() {
        assertThat(mensRepo.findAndLockById(Long.MAX_VALUE)).isEmpty();
    }

    @Test void findByGeldBetween() {
        var van = BigDecimal.ONE;
        var tot = BigDecimal.TEN;

        assertThat(mensRepo.findByGeldBetween(van, tot))
                .hasSize(countRowsInTableWhere(MENSEN,"geld BETWEEN 1 AND 10"))
                .extracting(Mens::getGeld)
                .allSatisfy(geld -> assertThat(geld).isBetween(van, tot))
                .isSorted();
    }

    @Test
    void findSchenkStatistiekPerMens() {
        var stat = mensRepo.findSchenkStatistiekPerMens();

        assertThat(stat).hasSize(jdbcTemplate.queryForObject("SELECT COUNT(DISTINCT vanMensId) FROM schenkingen", Integer.class))
                .extracting(statRow -> statRow.id()).isSorted();

        var idVanMens2 = idVantTestMens2();

        assertThat(stat).anySatisfy(item -> {
            assertThat(item.id()).isEqualTo(idVanMens2);
            assertThat(item.naam()).isEqualTo("test2");
            assertThat(item.aantal()).isEqualTo(2);
            assertThat(item.totaal()).isEqualByComparingTo("300");
        });
    }
}