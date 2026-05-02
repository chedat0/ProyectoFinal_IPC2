CREATE DATABASE proyfinal_IPC2;
USE proyfinal_IPC2;

SET FOREIGN_KEY_CHECKS = 0;
-- ============================================================
-- 01. USUARIO
-- ============================================================
CREATE TABLE usuario (
    id               INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    nombre_completo  VARCHAR(150)    NOT NULL,
    username         VARCHAR(50)     NOT NULL,
    correo           VARCHAR(150)    NOT NULL,
    password_hash    VARCHAR(255)    NOT NULL,
    telefono         VARCHAR(20)     NOT NULL,
    direccion        VARCHAR(255)    NOT NULL,
    cui              VARCHAR(15)     NOT NULL,
    fecha_nacimiento DATE            NOT NULL,
    tipo_usuario     ENUM('CLIENTE','FREELANCER','ADMINISTRADOR') NOT NULL,
    activo           BOOLEAN         NOT NULL DEFAULT TRUE,
    fecha_registro   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultima_sesion    DATETIME            NULL,
    CONSTRAINT pk_usuario           PRIMARY KEY (id),
    CONSTRAINT uq_usuario_username  UNIQUE (username),
    CONSTRAINT uq_usuario_correo    UNIQUE (correo),
    CONSTRAINT uq_usuario_cui       UNIQUE (cui)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
 
-- ============================================================
-- 02. CLIENTE
-- ============================================================
CREATE TABLE cliente (
    id               INT UNSIGNED    NOT NULL AUTO_INCREMENT,
    usuario_id       INT UNSIGNED    NOT NULL,
    descripcion      TEXT                NULL,
    sector           VARCHAR(100)        NULL,
    sitio_web        VARCHAR(255)        NULL,
    saldo_disponible DECIMAL(12,2)   NOT NULL DEFAULT 0.00,
    saldo_bloqueado  DECIMAL(12,2)   NOT NULL DEFAULT 0.00,
    perfil_completo  BOOLEAN         NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_cliente           PRIMARY KEY (id),
    CONSTRAINT uq_cliente_usuario   UNIQUE      (usuario_id),
    CONSTRAINT fk_cliente_usuario   FOREIGN KEY (usuario_id) REFERENCES usuario(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
 
-- ============================================================
-- 03. FREELANCER
-- ============================================================
CREATE TABLE freelancer (
    id                    INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    usuario_id            INT UNSIGNED  NOT NULL,
    descripcion           TEXT              NULL,
    nivel_experiencia     ENUM('JUNIOR','SEMI_SENIOR','SENIOR') NULL,
    tarifa_hora           DECIMAL(10,2)     NULL,
    saldo                 DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    calificacion_promedio DECIMAL(3,2)  NOT NULL DEFAULT 0.00,
    total_calificaciones  INT UNSIGNED  NOT NULL DEFAULT 0,
    perfil_completo       BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT pk_freelancer          PRIMARY KEY (id),
    CONSTRAINT uq_freelancer_usuario  UNIQUE      (usuario_id),
    CONSTRAINT fk_freelancer_usuario  FOREIGN KEY (usuario_id) REFERENCES usuario(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
 
-- ============================================================
-- 04. ADMINISTRADOR
-- ============================================================
CREATE TABLE administrador (
    id         INT UNSIGNED NOT NULL AUTO_INCREMENT,
    usuario_id INT UNSIGNED NOT NULL,
    CONSTRAINT pk_administrador         PRIMARY KEY (id),
    CONSTRAINT uq_administrador_usuario UNIQUE      (usuario_id),
    CONSTRAINT fk_administrador_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
 
-- ============================================================
-- 05. CATEGORIA
-- ============================================================
CREATE TABLE categoria (
    id             INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre         VARCHAR(100) NOT NULL,
    descripcion    TEXT             NULL,
    activa         BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_creacion DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_categoria        PRIMARY KEY (id),
    CONSTRAINT uq_categoria_nombre UNIQUE      (nombre)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
 
-- ============================================================
-- 06. HABILIDAD
-- ============================================================
CREATE TABLE habilidad (
    id             INT UNSIGNED NOT NULL AUTO_INCREMENT,
    categoria_id   INT UNSIGNED NOT NULL,
    nombre         VARCHAR(100) NOT NULL,
    descripcion    TEXT             NULL,
    activa         BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_creacion DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_habilidad           PRIMARY KEY (id),
    CONSTRAINT fk_habilidad_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
 
-- ============================================================
-- 07. PROYECTO
-- ============================================================
CREATE TABLE proyecto (
    id                  INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    cliente_id          INT UNSIGNED  NOT NULL,
    categoria_id        INT UNSIGNED  NOT NULL,
    titulo              VARCHAR(200)  NOT NULL,
    descripcion         TEXT          NOT NULL,
    presupuesto_maximo  DECIMAL(12,2) NOT NULL,
    fecha_limite        DATE          NOT NULL,
    estado              ENUM('ABIERTO','EN_PROGRESO','ENTREGA_PENDIENTE','COMPLETADO','CANCELADO')
                                      NOT NULL DEFAULT 'ABIERTO',
    fecha_publicacion   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_proyecto       PRIMARY KEY (id),
    CONSTRAINT fk_proy_cliente   FOREIGN KEY (cliente_id)   REFERENCES cliente(id),
    CONSTRAINT fk_proy_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
 
-- ============================================================
-- 08. PROPUESTA
-- ============================================================
CREATE TABLE propuesta (
    id                 INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    proyecto_id        INT UNSIGNED  NOT NULL,
    freelancer_id      INT UNSIGNED  NOT NULL,
    monto_ofertado     DECIMAL(12,2) NOT NULL,
    plazo_dias         INT UNSIGNED  NOT NULL,
    carta_presentacion TEXT          NOT NULL,
    estado             ENUM('PENDIENTE','ACEPTADA','RECHAZADA','RETIRADA')
                                     NOT NULL DEFAULT 'PENDIENTE',
    fecha_envio        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_respuesta    DATETIME          NULL,
    CONSTRAINT pk_propuesta       PRIMARY KEY (id),
    CONSTRAINT uq_propuesta_unica UNIQUE      (proyecto_id, freelancer_id),
    CONSTRAINT fk_prop_proyecto   FOREIGN KEY (proyecto_id)   REFERENCES proyecto(id),
    CONSTRAINT fk_prop_freelancer FOREIGN KEY (freelancer_id) REFERENCES freelancer(id),
    CONSTRAINT ck_prop_monto      CHECK       (monto_ofertado > 0),
    CONSTRAINT ck_prop_plazo      CHECK       (plazo_dias > 0)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
 
-- ============================================================
-- 09. CONTRATO
-- ============================================================
CREATE TABLE contrato (
    id                  INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    propuesta_id        INT UNSIGNED  NOT NULL,
    proyecto_id         INT UNSIGNED  NOT NULL,
    cliente_id          INT UNSIGNED  NOT NULL,
    freelancer_id       INT UNSIGNED  NOT NULL,
    monto               DECIMAL(12,2) NOT NULL,
    porcentaje_comision DECIMAL(5,2)  NOT NULL,
    comision_monto      DECIMAL(12,2) NOT NULL,
    estado              ENUM('ACTIVO','COMPLETADO','CANCELADO') NOT NULL DEFAULT 'ACTIVO',
    motivo_cancelacion  TEXT              NULL,
    fecha_inicio        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_fin           DATETIME          NULL,
    CONSTRAINT pk_contrato           PRIMARY KEY (id),
    CONSTRAINT uq_contrato_propuesta UNIQUE      (propuesta_id),
    CONSTRAINT uq_contrato_proyecto  UNIQUE      (proyecto_id),
    CONSTRAINT fk_cont_propuesta     FOREIGN KEY (propuesta_id)  REFERENCES propuesta(id),
    CONSTRAINT fk_cont_proyecto      FOREIGN KEY (proyecto_id)   REFERENCES proyecto(id),
    CONSTRAINT fk_cont_cliente       FOREIGN KEY (cliente_id)    REFERENCES cliente(id),
    CONSTRAINT fk_cont_freelancer    FOREIGN KEY (freelancer_id) REFERENCES freelancer(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
 
-- ============================================================
-- 10. ENTREGA
-- ============================================================
CREATE TABLE entrega (
    id             INT UNSIGNED NOT NULL AUTO_INCREMENT,
    contrato_id    INT UNSIGNED NOT NULL,
    descripcion    TEXT         NOT NULL,
    estado         ENUM('PENDIENTE','APROBADA','RECHAZADA') NOT NULL DEFAULT 'PENDIENTE',
    motivo_rechazo TEXT             NULL,
    fecha_entrega  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_revision DATETIME         NULL,
    CONSTRAINT pk_entrega       PRIMARY KEY (id),
    CONSTRAINT fk_entr_contrato FOREIGN KEY (contrato_id) REFERENCES contrato(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
 
-- ============================================================
-- 11. ENTREGA_ARCHIVO
-- ============================================================
CREATE TABLE entrega_archivo (
    id             INT UNSIGNED NOT NULL AUTO_INCREMENT,
    entrega_id     INT UNSIGNED NOT NULL,
    url_archivo    VARCHAR(500) NOT NULL,
    nombre_archivo VARCHAR(200) NOT NULL,
    CONSTRAINT pk_entrega_archivo PRIMARY KEY (id),
    CONSTRAINT fk_earc_entrega    FOREIGN KEY (entrega_id) REFERENCES entrega(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
 
-- ============================================================
-- 12. CALIFICACION
-- ============================================================
CREATE TABLE calificacion (
    id            INT UNSIGNED     NOT NULL AUTO_INCREMENT,
    contrato_id   INT UNSIGNED     NOT NULL,
    cliente_id    INT UNSIGNED     NOT NULL,
    freelancer_id INT UNSIGNED     NOT NULL,
    estrellas     TINYINT UNSIGNED NOT NULL,
    comentario    TEXT                 NULL,
    fecha         DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_calificacion     PRIMARY KEY (id),
    CONSTRAINT uq_calif_contrato   UNIQUE      (contrato_id),
    CONSTRAINT ck_calif_estrellas  CHECK       (estrellas BETWEEN 1 AND 5),
    CONSTRAINT fk_calif_contrato   FOREIGN KEY (contrato_id)   REFERENCES contrato(id),
    CONSTRAINT fk_calif_cliente    FOREIGN KEY (cliente_id)    REFERENCES cliente(id),
    CONSTRAINT fk_calif_freelancer FOREIGN KEY (freelancer_id) REFERENCES freelancer(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
 
-- ============================================================
-- 13. RECARGA_SALDO
-- ============================================================
CREATE TABLE recarga_saldo (
    id          INT UNSIGNED  NOT NULL AUTO_INCREMENT,
    cliente_id  INT UNSIGNED  NOT NULL,
    monto       DECIMAL(12,2) NOT NULL,
    fecha       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descripcion VARCHAR(255)  NOT NULL DEFAULT 'Recarga de saldo',
    CONSTRAINT pk_recarga_saldo PRIMARY KEY (id),
    CONSTRAINT ck_recarga_monto CHECK       (monto > 0),
    CONSTRAINT fk_rec_cliente   FOREIGN KEY (cliente_id) REFERENCES cliente(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
 
-- ============================================================
-- 14. CONFIGURACION_COMISION
-- ============================================================
CREATE TABLE configuracion_comision (
    id           INT UNSIGNED NOT NULL AUTO_INCREMENT,
    admin_id     INT UNSIGNED NOT NULL,
    porcentaje   DECIMAL(5,2) NOT NULL,
    fecha_inicio DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_fin    DATETIME         NULL,
    activa       BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_configuracion_comision PRIMARY KEY (id),
    CONSTRAINT ck_com_porcentaje         CHECK       (porcentaje BETWEEN 0.00 AND 100.00),
    CONSTRAINT fk_com_admin              FOREIGN KEY (admin_id) REFERENCES administrador(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
 
-- ============================================================
-- 15. FREELANCER_HABILIDAD (M:N)
-- ============================================================
CREATE TABLE freelancer_habilidad (
    freelancer_id INT UNSIGNED NOT NULL,
    habilidad_id  INT UNSIGNED NOT NULL,
    CONSTRAINT pk_freelancer_habilidad PRIMARY KEY (freelancer_id, habilidad_id),
    CONSTRAINT fk_freh_freelancer      FOREIGN KEY (freelancer_id) REFERENCES freelancer(id),
    CONSTRAINT fk_freh_habilidad       FOREIGN KEY (habilidad_id)  REFERENCES habilidad(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
 
-- ============================================================
-- 16. PROYECTO_HABILIDAD (M:N)
-- ============================================================
CREATE TABLE proyecto_habilidad (
    proyecto_id  INT UNSIGNED NOT NULL,
    habilidad_id INT UNSIGNED NOT NULL,
    CONSTRAINT pk_proyecto_habilidad PRIMARY KEY (proyecto_id, habilidad_id),
    CONSTRAINT fk_proyh_proyecto     FOREIGN KEY (proyecto_id)  REFERENCES proyecto(id),
    CONSTRAINT fk_proyh_habilidad    FOREIGN KEY (habilidad_id) REFERENCES habilidad(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
 
-- ============================================================
-- 17. SOLICITUD_HABILIDAD
-- ============================================================
CREATE TABLE solicitud_habilidad (
    id              INT UNSIGNED NOT NULL AUTO_INCREMENT,
    freelancer_id   INT UNSIGNED NOT NULL,
    admin_id        INT UNSIGNED     NULL,
    nombre          VARCHAR(100) NOT NULL,
    descripcion     TEXT             NULL,
    estado          ENUM('PENDIENTE','ACEPTADA','RECHAZADA') NOT NULL DEFAULT 'PENDIENTE',
    fecha_solicitud DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_respuesta DATETIME         NULL,
    CONSTRAINT pk_solicitud_habilidad PRIMARY KEY (id),
    CONSTRAINT fk_solhab_freelancer   FOREIGN KEY (freelancer_id) REFERENCES freelancer(id),
    CONSTRAINT fk_solhab_admin        FOREIGN KEY (admin_id)      REFERENCES administrador(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
 
-- ============================================================
-- 18. SOLICITUD_CATEGORIA
-- ============================================================
CREATE TABLE solicitud_categoria (
    id              INT UNSIGNED NOT NULL AUTO_INCREMENT,
    cliente_id      INT UNSIGNED NOT NULL,
    admin_id        INT UNSIGNED     NULL,
    nombre          VARCHAR(100) NOT NULL,
    descripcion     TEXT             NULL,
    estado          ENUM('PENDIENTE','ACEPTADA','RECHAZADA') NOT NULL DEFAULT 'PENDIENTE',
    fecha_solicitud DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_respuesta DATETIME         NULL,
    CONSTRAINT pk_solicitud_categoria PRIMARY KEY (id),
    CONSTRAINT fk_solcat_cliente      FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    CONSTRAINT fk_solcat_admin        FOREIGN KEY (admin_id)   REFERENCES administrador(id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
 
SET FOREIGN_KEY_CHECKS = 1;