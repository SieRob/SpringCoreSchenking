package be.vdab.geld.domain;

import be.vdab.geld.exceptions.OnvoldoendeGeldException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class MensTest {
    private Mens rob, bart;

    @BeforeEach
    void beforeEach(){
        rob = new Mens(1, "Rob", BigDecimal.TEN);
        bart = new Mens(1, "Bart", BigDecimal.ONE);
    }

    @Test
    void schenk() {
        rob.schenk(bart, BigDecimal.ONE);
        assertThat(rob.getGeld()).isEqualByComparingTo("9");
        assertThat(bart.getGeld()).isEqualByComparingTo("2");
    }

    @Test
    void schenkenMisluktBijOnvoldoendeGeld(){
        assertThatExceptionOfType(OnvoldoendeGeldException.class).isThrownBy(
                () -> rob.schenk(bart, BigDecimal.valueOf(11))
        );
    }

    @Test
    void bijSchenkenMoetAanMensIngevuldZijn(){
        assertThatNullPointerException().isThrownBy(
                () ->  rob.schenk(null, BigDecimal.ONE)
        );
    }

    @Test
    void bijSchenkenMoetBedragIngevuldZijn(){
        assertThatNullPointerException().isThrownBy(() -> rob.schenk(bart, null)
        );
    }
}