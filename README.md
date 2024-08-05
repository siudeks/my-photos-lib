
## Hints

Check if Ollama is working
Assmption: OLLAMA_HOST points proper hots, in my case it is WSL ip address:
curl http://$OLLAMA_HOST:11434/api/tags

- check available updates:
  ```
  mvn versions:display-property-updates -ntp
  ```

## run UI for Ollama:
sudo docker run -d -p 3000:8080 \
    -v open-webui:/app/backend/data \
    --name open-webui \
    ghcr.io/open-webui/open-webui:main

## Inspirations:
- [I built an image search engine (Toutube)](https://www.youtube.com/watch?v=mBcBoGhFndY)

## Links
- https://docs.spring.io/spring-ai/reference/api/clients/ollama-chat.html
- [Improve ollama performance](https://github.com/ollama/ollama/issues/2742)
- https://medium.com/@gabrielrodewald/running-models-with-ollama-step-by-step-60b6f6125807

