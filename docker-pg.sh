docker run --ulimit memlock=-1:-1 -it --rm=true --memory-swappiness=0 \
           --name postgres-quarkus-reactive -e POSTGRES_USER=antimonitor \
           -e POSTGRES_PASSWORD=antimonitor -e POSTGRES_DB=antimonitor \
           -p 5432:5432 postgres:10.5