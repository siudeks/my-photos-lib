FROM mcr.microsoft.com/devcontainers/base:ubuntu

# As we need libheif 1.16+, we need to add additional PPA
# more: https://ubuntuhandbook.org/index.php/2024/01/install-latest-libheif-ubuntu/
RUN sudo apt-get update 
RUN sudo apt-get install software-properties-common -y # install add-apt-repository
RUN sudo add-apt-repository ppa:strukturag/libheif
RUN sudo add-apt-repository ppa:strukturag/libde265
RUN sudo apt-get update 

# required by python (pyheif) and java (https://github.com/gotson/NightMonkeys/tree/main/imageio-heif)
RUN sudo apt install libheif-dev -y

# it installs heif-convert - CLI tool to convert heic -> jpg, used atm to create local copy of HEIC file
# more: https://linuxnightly.com/convert-heif-images-to-jpg-or-png-on-linux/
RUN sudo apt install libheif-examples -y

# require graphviz to run PlantUML vscode plugin
# more: https://plantuml.com/graphviz-dot
RUN sudo apt install graphviz -y

# fix loading heic files errors:
# libXtst.so.6: cannot open shared object file: No such file or directory
RUN sudo apt-get install libxtst6 -y
# libXi.so.6: cannot open shared object file: No such file or directory
RUN sudo apt-get install libxi6 -y

# we need address of host as there is OLLAMA instance
ENV OLLAMA_HOST=172.29.228.228
