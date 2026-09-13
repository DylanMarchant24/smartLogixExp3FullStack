# ms-usuarios corregido

- Puerto: 8092 (se puede cambiar si el equipo definió otro).
- Base de datos MySQL: `db_usuarios`.
- La base y sus tablas se crean automáticamente con `createDatabaseIfNotExist=true` y `ddl-auto=update`.
- La contraseña no se devuelve en las respuestas HTTP.
- `ultimoLogin` permite registrar cuándo un usuario inició sesión.

Endpoints:
- GET `/api/usuarios`
- GET `/api/usuarios/{id}`
- GET `/api/usuarios/email/{email}`
- POST `/api/usuarios`
- POST `/api/usuarios/registrar-login` body: `{ "email": "correo@ejemplo.com" }`
- DELETE `/api/usuarios/{id}` (borrado lógico)

Importante: el endpoint de registrar-login debe ser llamado por el flujo de autenticación después de validar correctamente las credenciales/JWT. No reemplaza todavía el login de Azure.