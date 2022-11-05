## Docker compose

```yaml
version: '3'
services:
  cartoon:
    depends_on:
      - mongo
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATA_MONGODB_URI: mongodb://mongo/cartoon
    volumes:
      - path:/container-path
  cartoon-ui:
    build: ./cartoon-ui
    ports:
      - "3000:3000"
    environment:
      NEXT_PUBLIC_API_HOST: http://current-host-ip:8080
      API_HOST: http://carton:8080
  mongo:
    image: "mongo:4.2"


```
