package com.roamgram.travelDiary.domain.model.wallet.aggregate;


import com.roamgram.travelDiary.domain.IdentifiableResource;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Income.class, name = "income"),
        @JsonSubTypes.Type(value = Expenditure.class, name = "expenditure"),
        @JsonSubTypes.Type(value = CurrencyConversion.class, name = "currency_conversion")
})
public interface MonetaryEvent extends IdentifiableResource {

}
