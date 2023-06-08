package be.vdab.geld.console;

import be.vdab.geld.domain.Mens;
import be.vdab.geld.domain.Schenking;
import be.vdab.geld.exceptions.MensNietGevondenException;
import be.vdab.geld.exceptions.OnvoldoendeGeldException;
import be.vdab.geld.services.MensService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class MyRunner implements CommandLineRunner {

    private final MensService mensService;

    public MyRunner(MensService mensService) {
        this.mensService = mensService;
    }

    @Override
    public void run(String... args) throws Exception {
        mensService.findAll().forEach(mens -> System.out.println(mens.getNaam() + " - " + mens.getGeld()));

        var scanner = new Scanner(System.in);
        /*System.out.print("Naam:");
        var naam = scanner.nextLine();
        System.out.print("Geld:");
        var geld = scanner.nextBigDecimal();
        var mens = new Mens(0,naam,geld);
        var nieuweId = mensService.create(mens);
        System.out.println("Id van deze persoon: " + nieuweId);*/
        System.out.println();

        System.out.print("Id van de mens:");
        var vanMensId = scanner.nextInt();
        System.out.print("Id van de mens:");
        var aanMensId = scanner.nextInt();
        System.out.print("Bedrag:");
        var bedrag = scanner.nextBigDecimal();

        try {
            var schenking = new Schenking(vanMensId, aanMensId, bedrag);
            mensService.schenk(schenking);
        }catch (IllegalArgumentException e){
            System.err.println(e.getMessage());
        }catch (MensNietGevondenException e){
            System.err.println("Scheking mislukt. Mens ontbreekt. Id: " + e.getId());
        }catch (OnvoldoendeGeldException e){
            System.err.println("Schenking mislukt. Onvoldoende geld");
        }
    }
}
