mkdir -p ~/.ollama-models


 podman run -it --rm \
    -v ~/.ollama-models:/root/.ollama \
    -p 11434:11434 \
    --name ollama \
    ollama/ollama

#docker run -it --rm \
#  -v ~/.ollama-models:/root/.ollama \
#  -p 11434:11434 \
#  --name ollama \
#  ollama/ollama