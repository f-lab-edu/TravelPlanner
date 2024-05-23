//package com.example.travelDiary.presentation.controller.monetaryEvent;
//
//import com.example.travelDiary.application.service.monetaryEvent.MonetaryEventService;
//import com.example.travelDiary.domain.model.wallet.Amount;
//import com.example.travelDiary.domain.model.wallet.monetaryEvent.CurrencyConversion;
//import com.example.travelDiary.domain.model.wallet.monetaryEvent.Expenditure;
//import com.example.travelDiary.domain.model.wallet.monetaryEvent.Income;
//import com.example.travelDiary.domain.model.wallet.monetaryEvent.MonetaryEvent;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@RestController
//@RequestMapping("/monetary-events")
//public class MonetaryEventController {
//
//    private final MonetaryEventService service;
//
//    @Autowired
//    public MonetaryEventController(MonetaryEventService service) {
//        this.service = service;
//    }
//
//    @GetMapping("/up-to/{timestamp}")
//    public List<MonetaryEvent> getEventsUpToTimestamp(@PathVariable String timestamp) {
//        LocalDateTime parsedTimestamp = LocalDateTime.parse(timestamp);
//        return service.getEventsUpToTimestamp(parsedTimestamp);
//    }
//
//    @GetMapping("/net-balance")
//    public Amount calculateNetBalance(@RequestParam String start, @RequestParam String end) {
//        LocalDateTime startTime = LocalDateTime.parse(start);
//        LocalDateTime endTime = LocalDateTime.parse(end);
//        return service.calculateNetBalance(startTime, endTime);
//    }
//
//    @GetMapping("/expenditures")
//    public List<Expenditure> getAllExpenditures() {
//        return service.getAllExpenditures();
//    }
//
//    @GetMapping("/incomes")
//    public List<Income> getAllIncomes() {
//        return service.getAllIncomes();
//    }
//
//    @GetMapping("/conversions")
//    public List<CurrencyConversion> getAllConversions() {
//        return service.getAllConversions();
//    }
//
//    @GetMapping("/all")
//    public List<MonetaryEvent> getAllEvents() {
//        return service.getAllEvents();
//    }
//}
