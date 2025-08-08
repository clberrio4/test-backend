package com.amarisTest.funds.facade;

import com.amarisTest.funds.dto.generic.FundDto;
import com.amarisTest.funds.dto.generic.RestGenericData;
import com.amarisTest.funds.mapper.FundMapper;
import com.amarisTest.funds.model.Fund;
import com.amarisTest.funds.service.FundService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FundFacade {
    private final FundService fundService;

    private final FundMapper fundMapper;

    public RestGenericData<Fund> createFund(RestGenericData<FundDto> input) {
        Fund  fund = fundMapper.toEntity(input.getData());
        return new RestGenericData<>(fundService.save(fund));
    }

    public RestGenericData<List<Fund>> getAll() {
        return new RestGenericData<>(fundService.getAll());
    }

    public RestGenericData<Fund> getFundById(String id) {
        return new RestGenericData<>(fundService.getById(id));
    }
}
