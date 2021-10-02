## Gradle Multi Module Docker Kubernetes Template Commands

### Deploy ignite server

##### Create ignite server

1. cd into deployment-scripts/ignite-server
2. run `source setup.sh`

##### Delete ignite server

1. cd into deployment-scripts/ignite-server
2. run `source delete-cluster.sh`

### Deploy modules to ignite namespace (Server namespace)

##### Create modules

1. From the root of gradle-multi-module-docker-kubernetes-template folder run the below commands
2. `./gradlew clean assemble :compute-engine:pushImageToEcr :query-service:pushImageToEcr`
3. From the above command take the version part eg `1.0.0-936c11e-SNAPSHOT`. PS - Don't copy the module part in version
4. Run command to deploy modules to above create server namespace. Replace arguments with appropriate values
5. `python3 deployment-scripts/ignite-client/deployment.py --create --namespace ignite --version 1.0.0-936c11e.dirty-SNAPSHOT --client test --modules compute-engine,query-service`

```

optional arguments:
  -h, --help            show this help message and exit
  -c, --create          Flag to create client
  -d, --delete          Flag to delete client
  -n NAMESPACE, --namespace NAMESPACE
                        Namespace of client
  -ver VERSION, --version VERSION
                        Client Version
  -cl CLIENT, --client CLIENT
                        Client Name
  -s SERVICE, --service SERVICE
                        Service name of ignite server
  -m MODULES, --modules MODULES

```

##### Delete modules

1. From the root of gradle-multi-module-docker-kubernetes-template folder run the below commands
2. Run command to deploy modules to above create server namespace. Replace arguments with appropriate values
3. `python3 deployment-scripts/ignite-client/deployment.py --delete --namespace ignite --version 1.0.0-936c11e.dirty-SNAPSHOT --client test --modules compute-engine,query-service`
