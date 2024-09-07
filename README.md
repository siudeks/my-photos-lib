
## Hints

### Ollama dependency: Check if Ollama is working
Assmption: OLLAMA_HOST points proper hots, in my case it is WSL ip address:
curl http://$OLLAMA_HOST:11434/api/tags

- check available updates:
  ```
  mvn versions:display-property-updates -ntp
  ```

### Dockerized dependencies:
Remember to run **docker compose up** before starting application or tests.

### Run from CLI
Application needs to have access to libheif, so you have to add extra application path
-Djava.library.path=/usr/lib/x86_64-linux-gnu/
as application is linked with libheif

### To small fonts in visualVM?
Add to ~~./bashrc:
export GDK_SCALE=2
export GDK_DPI_SCALE=0.75

## run UI for Ollama:
sudo docker run -d -p 3000:8080 \
    -v open-webui:/app/backend/data \
    --name open-webui \
    ghcr.io/open-webui/open-webui:main

## Inspirations:
- [I built an image search engine (Youtube)](https://www.youtube.com/watch?v=mBcBoGhFndY)

## Links
- [gotson nightmonkeys](https://github.com/gotson/NightMonkeys) to read jpg, png and heic images (java 22 required - see docs)
- https://www.youtube.com/watch?v=JTgl5GwrMu8
- https://docs.spring.io/spring-ai/reference/api/clients/ollama-chat.html
- [Improve ollama performance](https://github.com/ollama/ollama/issues/2742)
- https://medium.com/@gabrielrodewald/running-models-with-ollama-step-by-step-60b6f6125807

