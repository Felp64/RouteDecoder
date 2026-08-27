# RouteDecoder

Backend Java/Spring Boot para processar CSV com `latitude`/`longitude`, executar reverse geocoding e devolver CSV enriquecido.

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
