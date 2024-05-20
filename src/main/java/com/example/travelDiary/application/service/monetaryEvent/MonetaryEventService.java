package com.example.travelDiary.application.service.monetaryEvent;

import com.example.travelDiary.domain.model.wallet.Amount;
import com.example.travelDiary.domain.model.wallet.monetaryEvent.CurrencyConversion;
import com.example.travelDiary.domain.model.wallet.monetaryEvent.Expenditure;
import com.example.travelDiary.domain.model.wallet.monetaryEvent.Income;
import com.example.travelDiary.domain.model.wallet.monetaryEvent.MonetaryEvent;
import com.example.travelDiary.repository.persistence.wallet.monetaryEvent.MonetaryEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MonetaryEventService {

    @Autowired
    private MonetaryEventRepository monetaryEventRepository;

    public List<MonetaryEvent> getEventsUpToTimestamp(LocalDateTime timestamp) {
        return monetaryEventRepository.findByTimestampBeforeOrderByTimestamp(timestamp);
    }

    public Amount calculateNetBalance(LocalDateTime start, LocalDateTime end) {
        List<Expenditure> expenditures = monetaryEventRepository.findExpendituresBetween(start, end);
        List<Income> incomes = monetaryEventRepository.findIncomesBetween(start, end);

        Amount totalExpenditure = expenditures.stream()
                .map(Expenditure::getAmount)
                .reduce(Amount.ZERO, Amount::add);

        Amount totalIncome = incomes.stream()
                .map(Income::getAmount)
                .reduce(Amount.ZERO, Amount::add);

        return totalIncome.subtract(totalExpenditure);
    }

    public List<Expenditure> getAllExpenditures() {
        return monetaryEventRepository.findAllExpenditures();
    }

    public List<Income> getAllIncomes() {
        return monetaryEventRepository.findAllIncome();
    }

    public List<CurrencyConversion> getAllConversions() {
        return monetaryEventRepository.findAllCurrencyConversions();
    }

    public List<MonetaryEvent> getAllEvents() {
        return monetaryEventRepository.findAll();
    }
}
