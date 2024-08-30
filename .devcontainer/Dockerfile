FROM mcr.microsoft.com/devcontainers/java:1-21-bullseye

RUN sudo apt update
RUN sudo apt install libheif-dev -y # required to install pyheif in python and use  https://github.com/gotson/NightMonkeys/tree/main/imageio-heif

# we need address of host as there is OLLAMA instance
ENV OLLAMA_HOST=172.29.228.228
