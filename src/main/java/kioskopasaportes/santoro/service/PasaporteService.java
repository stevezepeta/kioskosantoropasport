package kioskopasaportes.santoro.service;

import kioskopasaportes.santoro.model.Pasaporte;

import java.util.List;

public interface PasaporteService {

    Pasaporte create(Pasaporte pasaporte);
    Pasaporte update(Long id, Pasaporte pasaporte);
    Pasaporte findById(Long id);
    Pasaporte findByNumero(String numero);
    List<Pasaporte> findAll();
}
