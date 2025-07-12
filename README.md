# Canary

MVP Java REST server.

Minimum requirements: Java 21 on path (or set `JAVA_HOME`)

Build: `./gradlew build`

Run: `canary-server/build/install/canary-server/bin/canary-server`

Test startup time: `date +"%Y-%m-%d %H:%M:%S.%N%:z"; <app-binary>`

Test max RSS: `/usr/bin/time -v <app-binary>`

### Docker

Run: `docker run -it --publish 127.0.0.1:8000:8000 --publish 127.0.0.1:9000:9000 ghcr.io/gkresic/canary-server`

## Exposed endpoints

By default, server listens on `localhost` on ports `8000` (public endpoints) and `9000` (private endpoints).

### Public endpoints

These are meant to be publicly exposed on reverse proxy.

#### ► `GET @ /status`

Returns app status.

```
curl --location "http://localhost:8000/status"
```

#### ► `GET @ /error`

Simulates an error in app (resulting in a customized error response).

```
curl --location "http://localhost:8000/error"
```

#### ► `POST @ /payload`

"Uploads" new payload. Note that there's no backing store and th only effect is that uploaded payload is parsed from JSON and streamed back in response.

```
curl --location "http://localhost:8000/payload" \
--header "Content-Type: application/json" \
--data "{
	\"text\": \"hundred\",
	\"number\": 100
}"
```

####  ► `GET @ /payload/<number>`

Retrieves payload with the given number. Payload's text is always set to `foo`.

```
curl --location "http://localhost:8000/payload/101"
```

### Private endpoints

These are meant to be accessible only by internal tools (Prometheus, etc.)

#### ► `GET @ /metrics`

Prometheus metrics.

```
curl --location "http://localhost:9000/metrics"
```

