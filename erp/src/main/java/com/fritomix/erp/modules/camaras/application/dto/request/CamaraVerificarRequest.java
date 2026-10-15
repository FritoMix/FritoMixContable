package com.fritomix.erp.modules.camaras.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record CamaraVerificarRequest(
        /** Lista de líneas con la cantidad físicamente verificada */
        @NotNull
        @Valid
        List<LineaVerificadaRequest> lineas,

        @Size(max = 2000)
        String observacionesGenerales
) {

    public record LineaVerificadaRequest(

            @NotNull
            Long detalleId,

            @NotNull
            BigDecimal cantidadVerificada,

            /** Obligatorio si cantidadVerificada != cantidadDespachada */
            @Size(max = 50)
            String tipoInconsistencia,

            @Size(max = 2000)
            String observacionLinea
    ) {}
}
