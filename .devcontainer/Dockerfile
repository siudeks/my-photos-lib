FROM mcr.microsoft.com/devcontainers/java:1-21-bullseye

RUN sudo apt update
RUN sudo apt install libheif-dev -y # required to install pyheif in python

# we need address of host as there is OLLAMA instance
ENV OLLAMA_HOST=172.29.228.228
