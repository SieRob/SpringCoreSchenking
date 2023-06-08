package be.vdab.geld.domain;

import be.vdab.geld.exceptions.OnvoldoendeGeldException;

import java.math.BigDecimal;

public class Mens {
    private final long id;
    private final String naam;
    private BigDecimal geld;

    public Mens(long id, String naam, BigDecimal geld) {
        this.id = id;
        this.naam = naam;
        this.geld = geld;
    }

    public long getId() {
        return id;
    }

    public String getNaam() {
        return naam;
    }

    public BigDecimal getGeld() {
        return geld;
    }

    public void schenk(Mens aanMens, BigDecimal bedrag){
        if(geld.compareTo(bedrag)<0){
            throw new OnvoldoendeGeldException("Niet genoeg geld beschikbaar");
        }
        geld = geld.subtract(bedrag);
        aanMens.geld=aanMens.geld.add(bedrag);
    }
}
