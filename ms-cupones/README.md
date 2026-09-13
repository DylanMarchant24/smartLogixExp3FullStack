# ms-cupones

Microservicio del sistema SmartLogix encargado de la validacion de codigos de descuento
y el calculo de la rebaja aplicable sobre un monto de compra.

## Responsabilidad

- Administrar cupones: codigo unico, tipo de descuento (PORCENTAJE o MONTO_FIJO), valor y vigencia.
- Validar un codigo de cupon contra un monto de compra, calculando la rebaja y el monto final.
- Desactivar cupones manualmente.

## Reglas de negocio

- Un cupon PORCENTAJE no puede superar 100.
- Un cupon MONTO_FIJO nunca rebaja mas que el propio monto de la compra.
- Un cupon solo es valido si esta activo y la fecha actual esta dentro de su rango de vigencia.

## Endpoints principales

| Metodo | Ruta                       | Descripcion                                           |
|--------|-----------------------------|--------------------------------------------------------|
| POST   | /cupones                    | Crea un nuevo cupon                                     |
| GET    | /cupones                    | Lista todos los cupones                                 |
| GET    | /cupones/{id}                | Obtiene el detalle de un cupon                          |
| PATCH  | /cupones/{id}/desactivar     | Desactiva un cupon                                      |
| POST   | /cupones/validar             | Valida un codigo contra un monto y calcula la rebaja    |

## Acceso via API Gateway

Todas las rutas quedan expuestas a traves del gateway bajo el prefijo `/api/cupones/**`.

## Configuracion

Puerto: `8091`. Se registra en Eureka como `ms-cupones`. Requiere una base de datos MySQL
`smartlogix_cupones` (se crea automaticamente si no existe segun la config de `application.properties`).
