package com.amarisTest.funds.dto.generic;

import com.amarisTest.funds.model.enumField.TransactionType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
    @NotBlank(message = "El ID del cliente es obligatorio")
    private String customerId;

    @NotBlank(message = "El ID del fondo es obligatorio")
    private String fundId;

    @NotBlank(message = "El tipo de transacción es obligatorio (APERTURA o CANCELACION)")
    private TransactionType status;

}
