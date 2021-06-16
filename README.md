# knative-handyman

## Installation

### Deploy required Operators

```shell
oc create -f manifests/amq-streams-og.yml
oc create -f manifests/openshift-serverless-og.yml

oc create -f manifests/amq-streams-operator-subscription.yml
oc create -f manifests/serverless-operator-subscription.yml
```

### Install infrastructure

First, wait until `knative-serving` and `knative-eventing` projects are created after operators installation.

```shell 
oc create -f manifests/knative-serving.yml -n knative-serving
oc create -f manifests/knative-eventing.yml -n knative-eventing
oc create -f manifests/knative-kafka.yml -n knative-eventing
```

Deploy Kafka brokers in our project.

```shell
oc new-project handyman
oc create -f manifests/kafka-cluster.yml -n handyman
oc create -f manifests/kafka-topics.yml -n handyman
```

### Deploy application components

```shell
oc create -f manifests/handyman-ordering-deployment.yml -n handyman
oc create -f manifests/handyman-ordering-service.yml -n handyman
oc create -f manifests/handyman-ordering-route.yml -n handyman
```