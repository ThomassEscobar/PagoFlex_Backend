CREATE TABLE pagos (
  id UUID PRIMARY KEY,
  compromiso_id UUID NOT NULL,
  usuario_id UUID NOT NULL,
  monto_clp BIGINT NOT NULL CHECK (monto_clp > 0),
  estado VARCHAR(20) NOT NULL CHECK (estado IN ('PAGADO','RECHAZADO')),
  clave_idempotencia VARCHAR(80) NOT NULL UNIQUE,
  numero_comprobante VARCHAR(30) NOT NULL UNIQUE,
  pagado_en TIMESTAMPTZ NOT NULL DEFAULT now(),
  creado_en TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX ix_pagos_usuario ON pagos(usuario_id);
CREATE INDEX ix_pagos_compromiso ON pagos(compromiso_id);