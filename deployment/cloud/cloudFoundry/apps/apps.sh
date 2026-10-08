cf push activity-app -f deployment/cloud/cloudFoundry/apps/activity-app/activity-app.yaml -b java_buildpack_offline -p applications/activity-app/target/activity-app-0.0.1-SNAPSHOT.jar


cf push activity-source -f deployment/cloud/cloudFoundry/apps/activity-source/activity-source.yaml -b java_buildpack_offline -p applications/activity-source/target/activity-source-0.0.1-SNAPSHOT.jar

cf push alert-app      -f deployment/cloud/cloudFoundry/apps/alert-app/alert-app.yaml -b java_buildpack_offline -p applications/alert-app/target/alert-app-0.0.1-SNAPSHOT.jar

cf push alert-critical      -f deployment/cloud/cloudFoundry/apps/alert-app/critical.yaml -b java_buildpack_offline -p applications/alert-app/target/alert-app-0.0.1-SNAPSHOT.jar

cf push alert-source      -f deployment/cloud/cloudFoundry/apps/alert-source/alert-source.yaml -b java_buildpack_offline -p applications/alert-source/target/alert-source-0.0.1-SNAPSHOT.jar

cf push alert-ai-processor      -f deployment/cloud/cloudFoundry/apps/alert-ai-processor/alert-ai-processor.yaml -b java_buildpack_offline -p applications/alert-ai-processor/target/alert-ai-processor-0.0.1-SNAPSHOT.jar

cf push generator-supplier-source   -f deployment/cloud/cloudFoundry/apps/generator-supplier-source/generator-supplier-source.yaml -b java_buildpack_offline -p applications/generator-supplier-source/target/generator-supplier-source-0.0.1-SNAPSHOT.jar