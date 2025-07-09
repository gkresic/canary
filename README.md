# Canary

MVP Java REST server.

Minimum requirements: Java 21 on path (or set `JAVA_HOME`)

Build: `./gradlew build`

Run: `canary-server/build/install/canary-server/bin/canary-server`

Test startup time: `date +"%Y-%m-%d %H:%M:%S.%N%:z"; <app-binary>`

Test max RSS: `/usr/bin/time -v <app-binary>`

### jlink version

Build: `./gradlew :canary-server:runtime`

Run: `canary-server/build/image/bin/canary-server`

## Exposed endpoints

By default, server listens on `localhost:8001`.

### `GET @ /status`

Returns app status.

```
curl --location "http://localhost:8001/status"
```

### `GET @ /metrics`

Prometheus metrics.

```
curl --location "http://localhost:8001/metrics"
```

### `GET @ /error`

Simulates an error in app (resulting in a customized error response).

```
curl --location "http://localhost:8001/error"
```

### `POST @ /payload`

"Uploads" new payload. Note that there's no backing store and th only effect is that uploaded payload is parsed from JSON and streamed back in response.

```
curl --location "http://localhost:8001/payload" \
--header "Content-Type: application/json" \
--data "{
	\"text\": \"hundred\",
	\"number\": 100
}"
```

### `GET @ /payload/<number>`

Retrieves payload with the given number. Payload's text is always set to `foo`.

```
curl --location "http://localhost:8001/payload/101"
```