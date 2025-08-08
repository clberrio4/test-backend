package com.amarisTest.funds.dto.generic;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FundDto {
    @NotBlank(message = "El nombre del fondo es obligatorio")
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres")
    private String name;

    @NotNull(message = "El monto mínimo de vinculación es obligatorio")
    @DecimalMin(value = "0", message = "El monto mínimo debe ser mayor que cero")
    private BigDecimal minAmount;

    @Size(max = 50, message = "La categoría no puede tener más de 50 caracteres")
    @NotNull(message = "La categoría es obligatorio")
    private String category;
}
