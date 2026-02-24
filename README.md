# API de Gerenciamento de Cupons

Sistema de cupons de desconto com regras de negócio encapsuladas no domínio e arquitetura em camadas.

## Tecnologias

**Backend:** Java 8, Spring Boot 2.0, H2 Database, JUnit 4  
**Frontend:** Angular 5, TypeScript  
**Infraestrutura:** Docker, Docker Compose, Swagger

## Executar

```bash
# Primeira vez (dar permissão aos scripts)
chmod +x *.sh

# Usar scripts
./build.sh    # Build
./start.sh    # Iniciar
./stop.sh     # Parar
./test.sh     # Testar
```

Ou manualmente:
```bash
docker compose up -d
```

## Endpoints

| Método | URL | Descrição |
|--------|-----|-----------|
| POST | `/api/coupon` | Criar cupom |
| GET | `/api/coupon/{id}` | Buscar cupom por ID |
| DELETE | `/api/coupon/{id}` | Deletar cupom (soft delete) |

## Acessar

- **API:** http://localhost:8080/api
- **Swagger:** http://localhost:8080/api/swagger-ui.html
- **H2 Console:** http://localhost:8080/api/h2-console (JDBC URL: `jdbc:h2:mem:coupondb`)
- **Frontend:** http://localhost

## Testes

```bash
cd backend
mvn test
mvn jacoco:report
```

Cobertura de testes: **~80%**  
Relatório em: `backend/target/site/jacoco/index.html`

## Arquitetura

```
backend/src/main/java/com/coupon/api/
├── domain/          # Entidades e regras de negócio
├── application/     # Casos de uso (Use Cases)
├── infrastructure/  # Persistência e REST
├── dto/             # Data Transfer Objects
├── exception/       # Exceções customizadas
└── config/          # Configurações
```

## Regras de Negócio

**CREATE:**
- Código alfanumérico com exatamente 6 caracteres (caracteres especiais são removidos)
- Valor de desconto mínimo: 0.5
- Data de expiração não pode ser no passado
- Status inicial sempre ACTIVE

**DELETE:**
- Soft delete (preserva dados)
- Não permite deletar cupom já deletado

## Estrutura do Projeto

```
├── backend/         # API Spring Boot
├── frontend/        # Interface Angular
├── document/        # Documentação do desafio
└── docker-compose.yml
```
