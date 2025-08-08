package com.amarisTest.funds.dto.generic;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientSoftDataDto {
    @NotNull(message = "El monto mínimo de vinculación es obligatorio")
    @DecimalMin(value = "5000", message = "El monto mínimo debe ser mayor que cero")
    private BigDecimal balance;

    @NotNull(message = "No puede estar vacio email")
    @Email(message = "Debe ser un email valido")
    private String email;
}
