package com.amarisTest.funds.dto.generic;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestGenericData<T> {
    @Valid
    @NotNull
    private T data;
}