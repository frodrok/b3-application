# B3-application

## Setup
Install maven (or use ./mvnw)

Execute these commands
```bash
mkdir -p ~/psql-data

podman run -tid -p 5432:5432 -v ~/psql-data:/var/lib/postgresql/data:Z \
    -e POSTGRES_PASSWORD=admin -e POSTGRES_USER=admin \
    --name postgres docker.io/library/postgres:14

PGPASSWORD=admin psql -h localhost -Uadmin
create database tasklist;
create user 'fredrik' with password 'b3';
grant all privileges on database tasklist to fredrik;
Exit postgres shell

./mvnw package -Dquarkus.package.jar.type=uber-jar
java -jar target/b3-application-1.0.0-SNAPSHOT-runner.jar

curl http://localhost:8080/tasklist
curl -XPOST --data '{"name": "Fredriks lista"}' \
  -H "Content-Type: application/json" http://localhost:8080/tasklist
 
```

## Deployment
```bash
./mvnw package
 podman build -f ./src/main/docker/Dockerfile.jvm -t root/b3-application:1.0.0-SNAPSHOT .
 podman run -tid -p 8080:8080 -e QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://host.containers.internal:5432/tasklist \
	root/b3-application:1.0.0-SNAPSHOT
curl http://localhost:8080/tasklist
```