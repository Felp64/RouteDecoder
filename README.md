# RouteDecoder

## Visão Geral
O **RouteDecoder** é um backend em Java/Spring Boot para processar arquivos CSV com coordenadas (`latitude`, `longitude`), executar reverse geocoding e retornar um CSV enriquecido com endereço formatado.

A aplicação foi construída para ser **stateless** e escalar horizontalmente com múltiplas instâncias, usando Redis como cache distribuído e Nginx como load balancer.

## Arquitetura
- **Spring Boot**: API e processamento de arquivos
- **OpenStreetMap/Nominatim**: provedor padrão de reverse geocoding
- **Redis**: cache distribuído de coordenadas já processadas
- **Nginx**: balanceamento entre múltiplas instâncias da API
- **Docker Compose**: orquestração local da stack

## Estrutura

```text
src/main/java/com/felp64/routedecoder
├── RouteDecoderApplication.java
├── controller/ReverseGeocodingController.java
├── geocoding/
│   ├── NominatimReverseGeocodingClient.java
│   └── ReverseGeocodingClient.java
└── service/CsvReverseGeocodingService.java
```

## Endpoint

- `POST /api/reverse-geocoding/csv`
- `multipart/form-data` com campo `file`
- Retorno: arquivo `enriched-coordinates.csv`

## Subir com Docker Compose

```bash
docker compose up --build
```

Serviços:
- `app1` e `app2`: instâncias stateless da aplicação
- `redis`: cache distribuído para coordenadas
- `nginx`: load balancer disponível em `http://localhost:8080`
