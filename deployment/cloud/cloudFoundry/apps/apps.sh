cf push activity-app -f deployment/cloud/cloudFoundry/apps/activity-app/activity-app.yaml -b java_buildpack_offline -p applications/activity-app/target/activity-app-0.0.1-SNAPSHOT.jar


cf push activity-source -f deployment/cloud/cloudFoundry/apps/activity-source/activity-source.yaml -b java_buildpack_offline -p applications/activity-source/target/activity-source-0.0.1-SNAPSHOT.jar