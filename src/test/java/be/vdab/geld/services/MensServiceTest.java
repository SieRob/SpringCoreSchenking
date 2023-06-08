package be.vdab.geld.services;

import be.vdab.geld.domain.Mens;
import be.vdab.geld.domain.Schenking;
import be.vdab.geld.exceptions.MensNietGevondenException;
import be.vdab.geld.exceptions.OnvoldoendeGeldException;
import be.vdab.geld.repositories.MensRepository;
import be.vdab.geld.repositories.SchenkingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MensServiceTest {
    //unitTest
    private MensService mensService;
    @Mock
    private MensRepository mensRepo; 
    @Mock
    private SchenkingRepository schenkingRepo;
    private Mens nathan, richard;
    
    @BeforeEach
    void beforeEach(){
        mensService = new MensService(mensRepo, schenkingRepo);
        nathan = new Mens(1, "Nathan", BigDecimal.TEN);
        richard= new Mens(1, "Richard", BigDecimal.TEN);
    }

    @Test
    void schenkingMetOnbestaandeVanMensMislukt() {
        assertThatExceptionOfType(MensNietGevondenException.class).isThrownBy(
                () -> mensService.schenk(new Schenking(1,2,BigDecimal.ONE))
        );
    }

    @Test void schenkingMetOnbestaandeAanMensMislukt() {
        when(mensRepo.findAndLockById(1)).thenReturn(Optional.of(nathan));
        assertThatExceptionOfType(MensNietGevondenException.class).isThrownBy(
            () ->  mensService.schenk(new Schenking(1, 2, BigDecimal.ONE))
        );
    }

    @Test
    void schenk() {
        when(mensRepo.findAndLockById(1)).thenReturn(Optional.of(nathan));
        when(mensRepo.findAndLockById(2)).thenReturn(Optional.of(richard));

        var schenking = new Schenking(1,2,BigDecimal.ONE);
        mensService.schenk(schenking);

        assertThat(nathan.getGeld()).isEqualByComparingTo("9");
        assertThat(richard.getGeld()).isEqualByComparingTo("11");

        verify(mensRepo).findAndLockById(1);
        verify(mensRepo).findAndLockById(2);
        verify(mensRepo).update(nathan);
        verify(mensRepo).update(richard);
        verify(schenkingRepo).create(schenking);
    }

    @Test
    void schenkingMetOnvoldoendeGeldMislukt() {
        when(mensRepo.findAndLockById(1)).thenReturn(Optional.of(nathan));
        when(mensRepo.findAndLockById(2)).thenReturn(Optional.of(richard));
        assertThatExceptionOfType(OnvoldoendeGeldException.class).isThrownBy(
                () -> mensService.schenk(new Schenking(1,2,BigDecimal.valueOf(11)))
        );
    }

    //Pagina 49 in cursus
}