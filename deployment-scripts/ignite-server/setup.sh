#! /bin/bash
kubectl create -f ignite-namespace.yaml
kubectl create -f service.yaml
kubectl create -f ignite-service-account.yaml
kubectl create -f cluster-role.yaml
kubectl create configmap ignite-config -n ignite --from-file=node-configuration.xml
kubectl apply -f ignite-wal-storage-class.yaml
kubectl apply -f ignite-persistance-storage-class.yaml
kubectl create -f statefulset.yaml
# exec into any server node and activate the cluster using below command
echo /opt/ignite/apache-ignite/bin/control.sh --set-state ACTIVE --yes