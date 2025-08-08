package com.amarisTest.funds.mapper;

import com.amarisTest.funds.dto.generic.FundDto;
import com.amarisTest.funds.model.Fund;
import org.springframework.stereotype.Component;

@Component
public class FundMapper {
    public Fund toEntity(FundDto dto) {
        Fund fund = new Fund();
        fund.setName(dto.getName());
        fund.ensureId();
        return fund;
    }
}

