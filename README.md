
## Hints

Check if Ollama is working
Asusmption: OLLAMA_HOST points proper hots, in my case it is WSL ip address
curl http://$OLLAMA_HOST:11434/api/tags

- check available updates:
  ```
  mvn versions:display-property-updates -ntp
  ```

## Inspirations:
- [I built an image search engine (Toutube)](https://www.youtube.com/watch?v=mBcBoGhFndY)

## Links
- https://docs.spring.io/spring-ai/reference/api/clients/ollama-chat.html
- [Improve ollama performance](https://github.com/ollama/ollama/issues/2742)
- https://medium.com/@gabrielrodewald/running-models-with-ollama-step-by-step-60b6f6125807

