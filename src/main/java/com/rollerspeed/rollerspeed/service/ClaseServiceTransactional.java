package com.rollerspeed.rollerspeed.service;

import org.springframework.transaction.annotation.Transactional;

public interface ClaseServiceTransactional {
    @Transactional
    void crearClasesDeProba();
}
