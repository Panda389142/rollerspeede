package com.rollerspeed.rollerspeed.service;

import com.rollerspeed.rollerspeed.model.GaleriaItem;
import com.rollerspeed.rollerspeed.repository.GaleriaItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GaleriaItemService {

    @Autowired
    private GaleriaItemRepository repository;

    public List<GaleriaItem> findAll() {
        return repository.findAll();
    }

    public Optional<GaleriaItem> findById(Long id) {
        return repository.findById(id);
    }

    public GaleriaItem save(GaleriaItem item) {
        return repository.save(item);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
