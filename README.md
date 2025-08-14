
# 💾 SaveMoney$lot

**SaveMoney$lot** é um aplicativo de gerenciamento de finanças pessoais com uma pegada gamer. Ele ajuda usuários a rastrear despesas, definir orçamentos e monitorar hábitos de consumo — como se estivesse salvando o progresso do seu jogo financeiro!

**SaveMoney$lot** is a personal finance management app with a gamer vibe. It helps users track expenses, set budgets, and monitor spending habits — like saving progress in a financial game!

---

## 🎯 Funcionalidades | Features

- Rastreamento de despesas e receitas | Expense and income tracking  
- Criação e controle de orçamentos | Budget creation and control  
- Visualização de hábitos de consumo | Spending habit insights  
- Sistema de autenticação com JWT | JWT authentication system  
- Migrações de banco com Flyway | Database migrations with Flyway  
- Documentação automática com OpenAPI | API documentation with OpenAPI  

---

## 🚀 Tecnologias e Dependências | Technologies & Dependencies

### 📦 Dependências Principais | Main Dependencies

```groovy
implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
implementation 'org.springframework.boot:spring-boot-starter-web'
implementation 'org.springframework.boot:spring-boot-starter-security'
implementation 'org.flywaydb:flyway-core'
implementation 'org.flywaydb:flyway-database-postgresql'
implementation 'com.auth0:java-jwt:4.5.0'
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6'
```

### 🛠️ Suporte e Utilitários | Support & Utilities

```groovy
compileOnly 'org.projectlombok:lombok'
annotationProcessor 'org.projectlombok:lombok'
runtimeOnly 'org.postgresql:postgresql'
```

### 🧪 Testes | Testing

```groovy
testImplementation 'org.springframework.boot:spring-boot-starter-test'
testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
testImplementation 'org.springframework.security:spring-security-test'
```

---

### 📊 Cobertura de Testes | Test Coverage

O projeto utiliza **[JaCoCo](https://www.jacoco.org/jacoco/)** para gerar relatórios de cobertura de testes.
Você pode visualizar o html pelo caminho:
````
buid/reports/jacoco/index.html
````


---

## ⚙️ Requisitos | Requirements

- Java 17+
- Maven ou Gradle
- PostgreSQL
- Docker (opcional para ambiente local | optional for local setup)

## 📦 Como Rodar Localmente | How to Run Locally

1. Clone o projeto | Clone the repository:
   ```bash
   git clone https://github.com/AnGabSS/SaveMoneySlot
   cd save-money-slot
   ```

2. Configure o `.env` com suas variáveis de ambiente.  
   Configure `.env` with your environment variables.

3. Rode o projeto | Run the project:
   ```bash
   docker compose up -d --build
   ```

4. Acesse a API | Access the API:
   ```
   http://localhost:8080
   ```

---

## 📚 Documentação da API | API Documentation

Disponível em | Available at:

```
http://localhost:8080/swagger-ui.html
```

---

## 🕹️ Sobre o Nome | About the Name

**SaveMoney$lot** é um trocadilho com “Save Slot” — usado em jogos para salvar progresso — e “Money”, representando o controle financeiro.  
**SaveMoney$lot** is a pun combining “Save Slot” — used in games to save progress — and “Money”, representing financial control.

