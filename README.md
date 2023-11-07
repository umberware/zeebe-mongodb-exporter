[![License](https://img.shields.io/badge/License-MIT-coral.svg)](./LICENSE)
[![Zeebe](https://img.shields.io/badge/Zeebe-8.3.1-blue.svg)](https://github.com/camunda/zeebe)

# Zeebe Mongo DB Exporter

The [Zeebe Engine](https://github.com/camunda/zeebe) is an awesome way to automate and bring to your BPMN processes the life and all the 
events of a process, bpmn events or anything else can be shared in a BI for an example or any other mechanism that allow you to apply intelligence into 
your data or in a process. In the Zeebe, the default way to export these data is with the ElasticSearch Exporter, but why not **MongoDB?**

This project is an alternative to export and use the data in the **MongoDB**.

## Summary

- [Configuring](#configuring)
- [All Arguments](#arguments-and-default-values)

## Configuring

This is a standard exporter built using as example the [ElasticSearch Exporter](https://github.com/camunda/zeebe/tree/main/exporters/elasticsearch-exporter).

In this section, you will see how to configure the exporter for a [Helm Chart](https://github.com/camunda/camunda-platform-helm/tree/main/charts/camunda-platform#global-parameters) and also,
a docker compose project or a kubernetes cluster without Helm.

### In a Camunda Helm Chart

If you are using the [Camunda Helm Chart](https://docs.camunda.io/docs/self-managed/platform-deployment/helm-kubernetes/deploy/), you only need to create
a custom yaml that's will override the **Zeebe** section with the configuration below:

```
zeebe:
    enabled: true
    podSecurityContext:
        runAsUser: 1000
    containerSecurityContext:
        runAsUser: 1000
        readOnlyRootFilesystem: false
    initContainers:
        - name: init-mongodb-exporter
          image: busybox:1.36
          command: [ '/bin/sh', '-c' ]
          gargs: [ 'wget --no-check-certificate "https://github.com/umberware/zeebe-mongodb-exporter/releases/download/8.3.1/zeebe-mongodb-exporter-8.3.1.jar" -O /exporters/zeebe-mongodb-exporter.jar;']
          volumeMounts:
            - name: exporters
              mountPath: /exporters/
    env:
        - name: ZEEBE_BROKER_EXPORTERS_MONGO_JARPATH
          value: /exporters/zeebe-mongodb-exporter.jar
        - name: ZEEBE_BROKER_EXPORTERS_MONGO_CLASSNAME
          value: io.zeebe.exporter.mongo.MongoExporter
        - name: ZEEBE_BROKER_EXPORTERS_MONGO_ARGS_CONNECTION_URI
          value: "mongodb+srv://localhost:27017?retryWrites=true&w=majority"
        - name: ZEEBE_BROKER_EXPORTERS_MONGO_ARGS_CONNECTION_DATABASE
          value: "zeebe"
  ```
The above snippet, will download the release version of the exporter and put in the Zeebe directory.

In the `env` section we told to the Zeebe where are the exporter and the main class for the **MongoDBExporter**.

Also, with the arguments: `ZEEBE_BROKER_EXPORTERS_MONGO_ARGS_CONNECTION_URI` and `ZEEBE_BROKER_EXPORTERS_MONGO_ARGS_CONNECTION_DATABASE`, we informed to the MongoDBExporter the URL to connect and the database to use.

To RUN, you can just install or upgrade your HELM chart with the new configuration like:
`helm install YOUR_PROJECT_NAME camunda/camunda-platform -f THE_YAML_FILE_WITH_THE_CODE_ABOVE.yml`

You can also configure what records you want to export to your MongoDB database using arguments like:
```
   env:
       - name: ZEEBE_BROKER_EXPORTERS_MONGO_ARGS_DATA_PROCESS
         value: false
       - name: ZEEBE_BROKER_EXPORTERS_MONGO_ARGS_DATA_PROCESS_INSTANCE
         value: true
```
Check the full arguments list and the default values [here](#arguments-and-default-values) 

### In a DockerCompose

Todo

### In a kubernetes without Helm

Todo

## Arguments and Default Values

To reassign the arguments values in the Zeebe, you need to pass following this example:

*Example: To override the decision export argument to not export the data anymore*
 ```
    env:
       - name: ZEEBE_BROKER_EXPORTERS_MONGO_ARGS_DATA_DECISION
         value: false
 ```
*Example: To override the process instance export argument to not export the data anymore*
 ```
    env:
       - name: ZEEBE_BROKER_EXPORTERS_MONGO_ARGS_DATA_PROCESS_INSTANCE
         value: false
 ```
Below the default values for the **EventRecordType** arguments:
```
    dataType.command = false;
    dataType.event = true;
    dataType.rejection = false;
```

Below the default values for the **RecordType** arguments:
```
    data.decision = true;
    data.decisionEvaluation = true;
    data.decisionRequirements = true;
    data.deployment = true;
    data.error = true;
    data.incident = true;
    data.job = true;
    data.jobBatch = false;
    data.message = true;
    data.messageBatch = false;
    data.messageSubscription = true;
    data.process = true;
    data.processInstance = true;
    data.processInstanceBatch = false;
    data.processInstanceCreation = true;
    data.processInstanceModification = true;
    data.processMessageSubscription = true;
    data.variable = true;
    data.variableDocument = true;
    data.checkpoint = false;
    data.timer = true;
    data.messageStartEventSubscription = true;
    data.processEvent = false;
    data.deploymentDistribution = true;
    data.escalation = true;
    data.signal = true;
    data.signalSubscription = true;
    data.resourceDeletion = true;
    data.commandDistribution = true;
    data.form = true;
```






























