# RouteDecoder

## Visão Geral
O **RouteDecoder** é um serviço de backend desenhado para processar arquivos CSV em lote contendo coordenadas geográficas (latitude e longitude) e enriquecê-los com o endereço físico correspondente por meio de Reverse Geocoding. 

Sua arquitetura foi pensada para suportar a alta volumetria típica de **operações de rastreamento e telemetria veicular**, garantindo escalabilidade horizontal e eficiência no processamento de milhares de pontos de rota.

## Arquitetura
A solução é construída sob uma arquitetura moderna e escalável, utilizando as seguintes tecnologias:
* **Java & Spring Boot:** Core da aplicação, garantindo robustez e facilidade de manutenção no ecossistema Spring.
* **Redis (Cache Distribuído):** Reduz drasticamente as requisições para a API de mapas, armazenando coordenadas já processadas e melhorando significativamente o tempo de resposta em trajetos com veículos parados ou em congestionamentos.
* **Nginx (Load Balancer):** Atua como balanceador de carga distribuindo as requisições de processamento de CSV entre múltiplas instâncias *stateless* da API Spring.
* **OpenStreetMap (Nominatim):** Provedor padrão para a resolução de endereços, com a interface da aplicação estruturada para facilitar a integração de novos provedores.
* **Docker & Docker Compose:** Containerização de toda a infraestrutura, isolando o ambiente e preparando o terreno para futuros deploys em nuvem.

## Estrutura do Projeto
```text
├── src/
│   ├── main/java/com/track2address/
│   │   ├── controllers/      # Endpoints da API REST para upload do CSV
│   │   ├── services/         # Lógica de negócio, leitura com Apache Commons CSV e Geocoding
│   │   ├── config/           # Configurações de injeção do Redis e RestTemplate
│   │   └── models/           # DTOs e representações de dados
├── docker-compose.yml        # Orquestração do Nginx, Redis e instâncias Spring
├── nginx.conf                # Configuração de rotas e balanceamento do Nginx
└── pom.xml                   # Gestão de dependências (Spring Web, Spring Data Redis, etc.)
```

## Como Executar (Ambiente Local)

1. Clone o repositório na sua máquina:
   ```bash
   git clone https://github.com/seu-usuario/track2address.git
   cd track2address
   ```
2. Suba a infraestrutura completa (Load Balancer, Cache e instâncias da API) via Docker:
   ```bash
   docker-compose up --build -d
   ```
3. A API estará disponível na porta `80` (roteada automaticamente pelo Nginx para a instância mais livre).

## Como Usar

Faça uma requisição `POST` enviando o arquivo CSV de coordenadas brutas geradas pelos equipamentos de rastreamento:

```bash
curl -X POST http://localhost/api/v1/geocode/batch \
  -F "file=@caminho/para/seu/arquivo-rotas.csv"
```

**Formato esperado do CSV de entrada:**
```csv
latitude,longitude
-19.916681,-43.934493
-19.865860,-43.971120
```

**Formato do CSV de saída gerado:**
```csv
latitude,longitude,endereco
-19.916681,-43.934493,"Praça Sete de Setembro, Centro, Belo Horizonte, MG"
-19.865860,-43.971120,"Estádio Mineirão, Pampulha, Belo Horizonte, MG"
```

## Próximos Passos (Backlog)
- [ ] Mapeamento formal de requisitos utilizando *User Stories* e *Use Cases*.
- [ ] Criação de diagramas estruturais da arquitetura atual utilizando **PlantUML**.
- [ ] Configuração de pipeline de CI/CD integrada ao GitHub Actions para garantir qualidade de código antes do deploy.
- [ ] Implementação de logs e métricas de operação para monitorar o tempo de leitura do lote e a taxa de acerto (*hit-rate*) do Redis.
