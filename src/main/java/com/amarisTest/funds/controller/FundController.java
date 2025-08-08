package com.amarisTest.funds.controller;

import com.amarisTest.funds.dto.generic.FundDto;
import com.amarisTest.funds.dto.generic.RestGenericData;
import com.amarisTest.funds.facade.FundFacade;
import com.amarisTest.funds.model.Fund;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funds")
public class FundController {
    @Autowired
    private FundFacade fundFacade;

    @PostMapping
    public RestGenericData<Fund> createFund(@RequestBody RestGenericData<FundDto> input) {
        return fundFacade.createFund(input);
    }

    @GetMapping
    public RestGenericData<List<Fund>> getAll() {
        return fundFacade.getAll();
    }

    @GetMapping("/{id}")
    public RestGenericData<Fund> getFundById(@PathVariable String id) {
        return fundFacade.getFundById(id);
    }
}
