# antimonitor-web project

## Used library

This project uses 
 - [Quarkus](https://quarkus.io/)
 - [Jooq](https://quasar.dev/)
 - [Quasar framework](https://quasar.dev/)
 - [Postgres](https://quasar.dev/) 

## Running the application in dev mode

### prerequisite

Before launch or build, install : 
- OpenJdk 11
- Maven 3.6
- Docker environement
- nodejs >=12
- yarn

### Launch the database

Execute the shell script :
```/shell script
./docker-pg.sh
```

### Launch the quarkus server 

Run your application in dev mode that enables live coding using:
```/shell script
./mvnw compile quarkus:dev
```

### launch the front-end 

In an terminal : 
- Go in the [web](web) directory
- Install the libs with `yarn`
- Execute the dev server with `yarn run dev`

After that, the application must be running on [http://localhost:9999/](http://localhost:9999/)

## building an docker image of the web application
In the root directory, execute :

```/shell script
docker build -t cgenin/antimonitor .
```


## building and running an docker image with database

In the root directory, execute :
```/shell script
docker-compose up
```
