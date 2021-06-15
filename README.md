# knative-handyman

## Installation

```shell
oc create -f manifests/amq-streams-og.yml
oc create -f manifests/openshift-serverless-og.yml

oc create -f manifests/amq-streams-operator-subscription.yml
oc create -f manifests/serverless-operator-subscription.yml

# Wait till knative-serving and knative-eventing projects are creates...
oc create -f manifests/knative-serving.yml -n knative-serving
oc create -f manifests/knative-eventing.yml -n knative-eventing
```

```shell
oc new-project handyman
oc create -f manifests/kafka-cluster.yml -n handyman
oc create -f manifests/kafka-topics.yml -n handyman
```