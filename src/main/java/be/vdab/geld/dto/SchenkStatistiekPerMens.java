package be.vdab.geld.dto;

import java.math.BigDecimal;

public record SchenkStatistiekPerMens (long id, String naam, int aantal, BigDecimal totaal){

}
