package application.domain.enums;


public enum UserRole {

    /** Comprador: realiza búsquedas, gestiona carrito y confirma pedidos */
    BUYER,

    /** Vendedor: registra y administra sus productos e inventario */
    SELLER,

    /** Operador Logístico: gestiona bodegas, empaque y despachos */
    LOGISTICS_OPERATOR,

    /** Administrador: registra vendedores, administra bodegas y usuarios */
    ADMINISTRATOR,

    /** Supervisor: perfil de solo consulta y seguimiento operativo */
    SUPERVISOR
}
