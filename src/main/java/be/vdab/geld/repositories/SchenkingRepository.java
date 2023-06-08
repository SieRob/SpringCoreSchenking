package be.vdab.geld.repositories;

import be.vdab.geld.domain.Schenking;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SchenkingRepository {
    private final JdbcTemplate template;

    public SchenkingRepository(JdbcTemplate template) {
        this.template = template;
    }

    public void create(Schenking schenking){
        var sql = """
                INSERT INTO schenkingen(vanMensId, aanMensId, wanneer, bedrag)
                VALUES (?,?,?,?)
                """;

        template.update(sql, schenking.getVanMensId(), schenking.getAanMensId(), schenking.getWanneer(), schenking.getBedrag());
    }
}
