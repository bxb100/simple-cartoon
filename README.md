## Docker compose

```yaml
version: '3'
services:
  cartoon:
    depends_on:
      - mongo
    image: bxb100/cartoon:1.0.0
    ports:
      - "8088:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATA_MONGODB_URI: mongodb://mongo/cartoon
    volumes:
      - /Volumes/ExtraDownload:/file
  cartoon-ui:
    image: bxb100/cartoon-ui:1.0.1
    ports:
      - "3000:3000"
    environment:
#	  iframe using
      ENVIRONMENT_VAR: http://localhost:8088
#	  SWR using
      API_HOST: http://cartoon:8080
  mongo:
    image: "mongo:4.2"
```
