package com.tinqinacademy.myhotel.core.services.hotel.impl;

import com.tinqinacademy.myhotel.core.services.BedService;
import com.tinqinacademy.myhotel.persistence.models.entities.Bed;
import com.tinqinacademy.myhotel.persistence.repositories.BedRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BedServiceImpl implements BedService {

    private final BedRepository bedRepository;


    public BedServiceImpl(BedRepository bedRepository) {
        this.bedRepository = bedRepository;
    }

    @Override
    public Bed save(Bed bed) {
        return bedRepository.save(bed);
    }

    @Override
    public Optional<Bed> findById(UUID id) {
        return bedRepository.findById(id);
    }

    @Override
    public Bed update(Bed bed) {
        return bedRepository.update(bed);
    }

    @Override
    public void deleteById(UUID id) {
        bedRepository.deleteById(id);
    }

    @Override
    public List<Bed> findAll() {
        return bedRepository.findAll();
    }
}
