package be.vdab.geld.services;

import be.vdab.geld.domain.Mens;
import be.vdab.geld.domain.Schenking;
import be.vdab.geld.exceptions.MensNietGevondenException;
import be.vdab.geld.repositories.MensRepository;
import be.vdab.geld.repositories.SchenkingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Scanner;

@Service
@Transactional(readOnly = true)
public class MensService {
    private final MensRepository mensRepos;
    private final SchenkingRepository schenkingRepo;
    public MensService(MensRepository mensRepos, SchenkingRepository schenkingRepo) {
        this.mensRepos = mensRepos;
        this.schenkingRepo = schenkingRepo;
    }

    public List<Mens> findAll(){
        return mensRepos.findAll();
    }

    @Transactional
    public long create(Mens mens){
        return mensRepos.create(mens);
    }

    @Transactional
    public void schenk(Schenking schenking){
        var vanMensId = schenking.getVanMensId();
        var vanMens = mensRepos.findAndLockById(vanMensId)
                .orElseThrow(() -> new MensNietGevondenException(vanMensId));

        var aanMensId = schenking.getAanMensId();
        var aanMens = mensRepos.findAndLockById(aanMensId)
                .orElseThrow(() -> new MensNietGevondenException(aanMensId));

        vanMens.schenk(aanMens, schenking.getBedrag());
        mensRepos.update(vanMens);
        mensRepos.update(aanMens);
        schenkingRepo.create(schenking);
    }
}
