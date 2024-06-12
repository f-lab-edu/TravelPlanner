package com.example.travelDiary.application.service.wallet;

import com.example.travelDiary.domain.model.wallet.aggregate.CurrencyConversion;
import com.example.travelDiary.domain.model.wallet.aggregate.Expenditure;
import com.example.travelDiary.domain.model.wallet.aggregate.Income;
import com.example.travelDiary.repository.persistence.wallet.MonetaryEventEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



@Service
public class MonetaryDomainQueryService {

    private final MonetaryEventEntityRepository repository;

    @Autowired
    public MonetaryDomainQueryService(MonetaryEventEntityRepository repository) {
        this.repository = repository;
    }

    public Page<Income> getAllIncome(Integer pageSize, Integer pageNumber) {
        Pageable page = PageRequest.of(pageNumber, pageSize);
        return repository.findAllIncomes(page);
    }

    public Page<Expenditure> getAllExpenditure(Integer pageSize, Integer pageNumber){
        Pageable page = PageRequest.of(pageNumber, pageSize);
        return repository.findAllExpenditure(page);
    }

    public Page<CurrencyConversion> getAllCurrencyConversion(Integer pageSize, Integer pageNumber){
        Pageable page = PageRequest.of(pageNumber, pageSize);
        return repository.findAllCurrencyConversion(page);
    }
}