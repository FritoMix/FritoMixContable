package com.fritomix.erp.modules.camaras.application.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Reglas puras del flujo de estados del módulo de Cámaras.
 * Mismo patrón que DispatchFlow — sin dependencias de Spring.
 */
public final class CamaraFlow {

    public static final String PENDIENTE_CAMARAS = "PENDIENTE_CAMARAS";
    public static final String EN_VERIFICACION   = "EN_VERIFICACION";
    public static final String OBSERVADA         = "OBSERVADA";
    public static final String APROBADA          = "APROBADA";
    public static final String RECHAZADA         = "RECHAZADA";

    public static final String EVENTO_CREADO     = "CREADO";
    public static final String EVENTO_INICIADO   = "INICIADO";
    public static final String EVENTO_VERIFICADO = "VERIFICADO";
    public static final String EVENTO_OBSERVADO  = "OBSERVADO";
    public static final String EVENTO_APROBADO   = "APROBADO";
    public static final String EVENTO_RECHAZADO  = "RECHAZADO";
    public static final String EVENTO_REABIERTO  = "REABIERTO";
    public static final String EVENTO_ENVIADO    = "ENVIADO_A_CAMARAS";

    public static final String LINEA_PENDIENTE   = "PENDIENTE";
    public static final String LINEA_OK          = "OK";
    public static final String LINEA_DIFERENCIA  = "DIFERENCIA";

    /** Estados desde los que se puede operar (no están cerrados definitivamente) */
    private static final Set<String> ESTADOS_CERRADOS = Set.of(APROBADA);

    /** Transiciones permitidas: estado_actual → estados_destino_válidos */
    private static final Map<String, Set<String>> TRANSICIONES = Map.of(
            PENDIENTE_CAMARAS, Set.of(EN_VERIFICACION),
            EN_VERIFICACION,   Set.of(APROBADA, OBSERVADA, RECHAZADA),
            OBSERVADA,         Set.of(EN_VERIFICACION, RECHAZADA),
            RECHAZADA,         Set.of(EN_VERIFICACION)
    );

    /** Causas de inconsistencia válidas */
    public static final Set<String> TIPOS_INCONSISTENCIA = Set.of(
            "FALTANTE", "SOBRANTE", "REFERENCIA_INCORRECTA",
            "DANO_PRODUCTO", "ERROR_DESPACHO", "DIFERENCIA_INVENTARIO", "OTRO"
    );

    private CamaraFlow() {}

    public static boolean isEstadoCerrado(String estado) {
        return ESTADOS_CERRADOS.contains(estado);
    }

    public static boolean isEstadoValido(String estado) {
        return TRANSICIONES.containsKey(estado) || APROBADA.equals(estado) || RECHAZADA.equals(estado);
    }

    /**
     * Valida que la transición de estado sea permitida.
     * Lanza IllegalArgumentException si no lo es.
     */
    public static void validateTransition(String estadoActual, String estadoNuevo) {
        if (APROBADA.equals(estadoActual)) {
            throw new IllegalArgumentException(
                    "La ficha ya está APROBADA y no puede modificarse.");
        }
        Set<String> permitidos = TRANSICIONES.get(estadoActual);
        if (permitidos == null || !permitidos.contains(estadoNuevo)) {
            throw new IllegalArgumentException(
                    "Transición no permitida de [" + estadoActual + "] a [" + estadoNuevo + "].");
        }
    }

    /**
     * Valida que todas las líneas de verificación estén completas (cantidad_verificada registrada).
     */
    public static void validateLineasCompletas(List<String> estadosLinea) {
        boolean hayPendiente = estadosLinea.stream()
                .anyMatch(LINEA_PENDIENTE::equals);
        if (hayPendiente) {
            throw new IllegalArgumentException(
                    "Existen líneas sin verificar. Debe registrar la cantidad verificada para todos los productos.");
        }
    }

    /**
     * Valida que no haya líneas con diferencia antes de aprobar.
     */
    public static void validateSinDiferencias(List<String> estadosLinea) {
        boolean hayDiferencia = estadosLinea.stream()
                .anyMatch(LINEA_DIFERENCIA::equals);
        if (hayDiferencia) {
            throw new IllegalArgumentException(
                    "Existen líneas con diferencias. Registre observaciones y use 'Observar' o resuelva las diferencias antes de aprobar.");
        }
    }

    public static boolean isTipoInconsistenciaValido(String tipo) {
        return tipo != null && TIPOS_INCONSISTENCIA.contains(tipo);
    }
}
