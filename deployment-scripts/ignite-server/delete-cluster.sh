#! /bin/bash
kubectl delete sts ignite -n ignite
kubectl delete service ignite-service -n ignite
kubectl delete sa ignite -n ignite
kubectl delete clusterrole -n ignite ignite
kubectl delete clusterrolebinding -n ignite ignite
kubectl delete configmap ignite-config -n ignite
kubectl delete sc -n ignite ignite-persistence-storage-class
kubectl delete sc -n ignite ignite-wal-storage-class
kubectl delete namespace ignite