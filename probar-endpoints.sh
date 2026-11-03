#!/usr/bin/env bash
# Recorre todos los endpoints de la API. Uso: ./probar-endpoints.sh [BASE_URL]
BASE="${1:-http://localhost:8080}"
J='Content-Type: application/json'
SUF=$(date +%s | tail -c 7)

req() { # metodo ruta [body]  -> imprime "HTTP code" y cuerpo
  local m=$1 p=$2 b=$3
  echo ""; echo "### $m $p ${b:+ $b}"
  if [ -n "$b" ]; then out=$(curl -s -w '\n%{http_code}' -X "$m" -H "$J" -d "$b" "$BASE$p")
  else out=$(curl -s -w '\n%{http_code}' -X "$m" "$BASE$p"); fi
  BODY=$(echo "$out" | sed '$d'); CODE=$(echo "$out" | tail -1)
  echo "HTTP $CODE"; echo "$BODY"
}
id_of() { echo "$BODY" | grep -o '"id":[0-9]*' | tail -1 | cut -d: -f2; }
num_of() { echo "$BODY" | grep -o '"numeroCuenta":"[0-9]*"' | head -1 | cut -d'"' -f4; }

echo "===== CLIENTES ====="
req POST /api/clientes '{"tipoIdentificacion":"CC","numeroIdentificacion":"10'$SUF'","nombres":"Carlos","apellido":"Ramirez","correoElectronico":"carlos'$SUF'@gmail.com","fechaNacimiento":"1998-06-15"}'
C1=$(id_of)
req POST /api/clientes '{"tipoIdentificacion":"CC","numeroIdentificacion":"20'$SUF'","nombres":"Maria","apellido":"Lopez","correoElectronico":"maria'$SUF'@gmail.com","fechaNacimiento":"1995-03-20"}'
C2=$(id_of)
req GET /api/clientes
req GET /api/clientes/$C1
req PUT /api/clientes/$C1 '{"tipoIdentificacion":"CC","numeroIdentificacion":"10'$SUF'","nombres":"Carlos Andres","apellido":"Ramirez","correoElectronico":"carlos'$SUF'@gmail.com","fechaNacimiento":"1998-06-15"}'

echo; echo "===== CUENTAS ====="
req POST /api/cuentas '{"tipoCuenta":"AHORROS","clienteId":'$C1',"exentaGMF":false}'
A1=$(id_of); N1=$(num_of)
req POST /api/cuentas '{"tipoCuenta":"CORRIENTE","clienteId":'$C2',"exentaGMF":false}'
A2=$(id_of); N2=$(num_of)
req POST /api/cuentas '{"tipoCuenta":"AHORROS","clienteId":'$C2',"exentaGMF":true}'
A3=$(id_of)
req GET /api/cuentas
req GET /api/cuentas/$A1
req PATCH /api/cuentas/$A3/inactivar
req PATCH /api/cuentas/$A3/activar

echo; echo "===== TRANSACCIONES ====="
req POST /api/transacciones/consignacion '{"numeroCuenta":"'$N1'","monto":500000}'
req POST /api/transacciones/retiro '{"numeroCuenta":"'$N1'","monto":100000}'
req POST /api/transacciones/transferencia '{"cuentaOrigen":"'$N1'","cuentaDestino":"'$N2'","monto":200000}'
req GET /api/transacciones/cuenta/$A1/movimientos
req GET /api/transacciones/cuenta/$A2/movimientos

echo; echo "===== CASOS DE ERROR (reglas de negocio) ====="
echo "-- Retiro mayor al saldo en ahorros"
req POST /api/transacciones/retiro '{"numeroCuenta":"'$N1'","monto":99999999}'
echo "-- Cliente menor de edad"
req POST /api/clientes '{"tipoIdentificacion":"TI","numeroIdentificacion":"30'$SUF'","nombres":"Nino","apellido":"Pérez","correoElectronico":"nino'$SUF'@gmail.com","fechaNacimiento":"2015-01-01"}'
echo "-- Cancelar cuenta con saldo"
req PATCH /api/cuentas/$A1/cancelar
echo "-- Eliminar cliente con cuentas"
req DELETE /api/clientes/$C1

echo; echo "===== LIMPIEZA ====="
req PATCH /api/cuentas/$A3/cancelar
req DELETE /api/clientes/99999999
echo; echo "Listo."
