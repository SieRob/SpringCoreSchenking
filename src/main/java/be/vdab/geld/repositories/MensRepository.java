package be.vdab.geld.repositories;

import be.vdab.geld.domain.Mens;
import be.vdab.geld.dto.SchenkStatistiekPerMens;
import be.vdab.geld.exceptions.MensNietGevondenException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class MensRepository {
    private final JdbcTemplate template;

    public MensRepository(JdbcTemplate template) {
        this.template = template;
    }

    public long findAantal(){
        var sql = """
                SELECT COUNT(*)
                AS aantalMensen
                FROM mensen
                """;
        return template.queryForObject(sql, Long.class);
    }
    public void delete(long id){
        var sql = """
                DELETE FROM mensen
                WHERE id = ?
                """;
        template.update(sql,id);
    }

    public void update(Mens mens){
        var sql = """
                UPDATE mensen
                SET naam = ?, geld = ?
                WHERE id = ?
                """;
        if(template.update(sql, mens.getNaam(), mens.getGeld(), mens.getId())==0){
            throw new MensNietGevondenException(mens.getId());
        }
    }

    /*public void create(Mens mens){
        var sql = """
                INSERT INTO mensen (naam, geld)
                VALUES(?,?)
                """;

        template.update(sql, mens.getNaam(), mens.getGeld());
    }
    */

    public long create(Mens mens){
        var sql = """
                INSERT INTO mensen (naam, geld)
                VALUES(?,?)
                """;
        var keyHolder = new GeneratedKeyHolder();
        template.update(con -> {
            var stmt = con.prepareStatement(sql,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, mens.getNaam());
            stmt.setBigDecimal(2,mens.getGeld());
            return stmt;
            }, keyHolder);
        return keyHolder.getKey().longValue();

    }

    private final RowMapper<Mens> mensMapper =
            (result, rowNum) -> new Mens(
                    result.getLong("id"),
                    result.getString("naam"),
                    result.getBigDecimal("geld"));
    public List<Mens> findAll(){
        var sql = """
                SELECT id, naam, geld
                FROM mensen
                ORDER BY id
                """;
        return template.query(sql, mensMapper);
    }

    public List<Mens> findByGeldBetween (BigDecimal van, BigDecimal tot){
        var sql = """
                SELECT id, naam, geld
                FROM mensen
                WHERE geld BETWEEN ? AND ?
                ORDER BY geld
                """;
        return template.query(sql, mensMapper, van, tot);
    }

    public Optional<Mens> findById(long id){
        try {
            var sql = """
                    SELECT id, naam, geld
                    FROM mensen
                    WHERE id = ?
                    """;

            return Optional.of(template.queryForObject(sql, mensMapper, id));
        }catch (IncorrectResultSizeDataAccessException e){
            return Optional.empty();
        }
    }

    public Optional<Mens> findAndLockById(long id){
        try {
            var sql = """
                    SELECT id, naam, geld
                    FROM mensen
                    WHERE id = ?
                    FOR UPDATE
                    """;

            return Optional.of(template.queryForObject(sql, mensMapper, id));
        }catch (IncorrectResultSizeDataAccessException e){
            return Optional.empty();
        }
    }

    public List<SchenkStatistiekPerMens> findSchenkStatistiekPerMens(){

        //SQL zoals in cursus
        /**/
        var sql = """
                select mensen.id, naam, count(schenkingen.*) as aantal, sum(bedrag) as totaal
                from mensen LEFT outer join schenkingen
                on mensen.id = schenkingen.vanMensId
                group by mensen.id
                order by mensen.id
                """;
        /**/

        //SQL fix -> COUNT(*) of COUNT(schenkingen.id)
        /**/
        sql = """
                SELECT mensen.id, mensen.naam, COUNT(*) AS aantal, SUM(schenkingen.bedrag) AS totaal
                FROM mensen LEFT OUTER JOIN schenkingen
                ON mensen.id = schenkingen.vanMensId
                GROUP BY mensen.id
                ORDER BY mensen.id
                """;

        /**/

        //Test werkt -> RIGHT JOIN
        /**/
        sql = """
                select mensen.id, naam, count(*) as aantal, sum(bedrag) as totaal
                from mensen RIGHT join schenkingen
                on mensen.id = schenkingen.vanMensId
                group by mensen.id
                order by mensen.id
                """;
         /**/

        RowMapper<SchenkStatistiekPerMens> mapper = (result, rowNum)-> new SchenkStatistiekPerMens(
                result.getLong("id"), result.getString("naam"), result.getInt("aantal"), result.getBigDecimal("totaal"));
        return template.query(sql, mapper);
    }
}
