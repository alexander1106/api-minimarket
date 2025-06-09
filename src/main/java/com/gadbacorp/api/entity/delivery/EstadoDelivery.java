package com.gadbacorp.api.entity.delivery;

public enum EstadoDelivery {
    PENDIENTE(0),
    EN_CAMINO(1),
    ENTREGADO(2),
    CANCELADO(3);

    private final int codigo;

    EstadoDelivery(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public static EstadoDelivery fromCodigo(int codigo) {
        for (EstadoDelivery estado : EstadoDelivery.values()) {
            if (estado.getCodigo() == codigo) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Código de estado no válido: " + codigo);
    }
}
