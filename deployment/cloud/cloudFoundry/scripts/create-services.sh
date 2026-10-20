cf create-service p.rabbitmq on-demand-plan rabbitmq -c '{"plugins": {"rabbitmq_stream": true}}'


cf create-service p.redis on-demand-cache  valkey

cf create-service postgres on-demand-postgres-db  postgres
cf create-service p-cloudcache extra-small gemfire


cf create-service  dataflow standard dataflow -c "{ \"messaging-data-service\": { \"name\": \"rabbitmq\"}, \"maven.remote-repositories.tanzu-dataflow.url\": \"https://packages.broadcom.com/artifactory/tanzu-dataflow-maven\", \"maven.remote-repositories.tanzu-dataflow.auth.username\": \"$BROADCOM_USERNAME\", \"maven.remote-repositories.tanzu-dataflow.auth.password\": \"$BROADCOM_PASSWORD\"}"


cf create-service ai-models tanzu-gemma-4-31B-it-qat ai-model-gemma

cf create-service ai-models tanzu-nomic-embed-text-v2 ai-model-vector-embedding
cf create-service ai-models tanzu-all-models ai-models
cf create-service-key ai-models  ai-models-key
cf service-key ai-models  ai-models-key

cf create-service-key ai-model-vector-embedding ai-model-vector-embedding-key

cf service-key ai-model-vector-embedding ai-model-vector-embedding-key
