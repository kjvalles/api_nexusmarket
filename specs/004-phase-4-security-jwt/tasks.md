# [TASKS-004] Tareas de Implementación — Fase 4: Seguridad y JWT

Lista de tareas accionables derivadas del plan técnico y mapeadas a los criterios de aceptación.

- [x] TASK-01 / AC-01: Implementar `BCryptPasswordServiceAdapter` en `application.adapters.security` implementando `PasswordServicePort`.
- [x] TASK-02 / AC-02: Implementar `JwtServiceAdapter` en `application.adapters.security` implementando `JwtServicePort` con JJWT 0.12.6 y claims estructurados (`sub`, `ver`, `role`, `email`).
- [x] TASK-03 / AC-03: Implementar `AuthenticatedUserPrincipal` en `application.adapters.security` implementando `UserDetails` y mapeando autoridades `ROLE_<ROL>`.
- [x] TASK-04 / AC-04: Implementar `JwtAuthenticationFilter` en `application.adapters.security` con extracción de Bearer token y validación de vigencia y versión (`ver == authTokenVersion`).
- [x] TASK-05 / AC-05: Implementar `SecurityConfig` en `application.adapters.security` configurando `SecurityFilterChain` stateless, CORS y restricción estricta de rutas por rol.
- [x] TASK-06 / AC-06: Implementar suite de pruebas unitarias de seguridad con JUnit 5 en `src/test/java/application/adapters/security/` (cifrado, tokens, versión y filtro).
- [x] TASK-07 / AC-07: Validar ejecución exitosa en Docker con `docker compose exec -T nexusmarcket-app mvn test` asegurando compilación limpia y cumplimiento de todas las puertas de calidad.
