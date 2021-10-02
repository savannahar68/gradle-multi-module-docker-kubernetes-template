import argparse
import subprocess
import os
from distutils.dir_util import copy_tree

def execute_shell(command, exception_enabled=True):
    cmd = subprocess.Popen(command, stdout = subprocess.PIPE,
                           stderr = subprocess.PIPE,
                           shell=True)
    stdout, stderr = cmd.communicate()
    # Ignore exit code if stderr is empty to be close
    # to the previous behaviour of os.popen3
    if cmd.returncode > 0 and len(stderr) > 0:
        print("Error occurred while executing command: %s" % command)
        if exception_enabled:
            raise ValueError('Exception occurred: %s' % stderr)

    return stdout

def execute_kubectl(command, exception_enabled=True):
    return execute_shell(command, exception_enabled)

def replace_placeholder(path, placeholder, value, definition):
    print(definition)
    execute_shell("find %s -type f -name '*.yaml' | xargs -r sed -i 's!%s!%s!g'" % (path, placeholder, value))
    execute_shell("find %s -type f -name '*.txt' | xargs -r sed -i 's!%s!%s!g'" % (path, placeholder, value))

def create_service(namespace, client_id, path):
    replace_placeholder(path, "@@SERVICE_NAME@@", f"ignite-{client_id}-service",
                        "replace service name in service")
    replace_placeholder(path, "@@NAMESPACE@@", namespace,
                        "replace namespace in service")
    replace_placeholder(path, "@@CLIENT_ID@@", client_id,
                        "replace pod label name in service")

def create_deployment(version, namespace, client_id, client_name, path, server_service_name):
    replace_placeholder(path, "@@VERSION@@", version,
                        "replace version in deployment")
    replace_placeholder(path, "@@SERVER_SERVICE_NAME@@", server_service_name,
                        "replace service name in deployment")
    replace_placeholder(path, "@@NAMESPACE@@", namespace,
                        "replace namespace in deployment")
    replace_placeholder(path, "@@CLIENT_ID@@", client_id,
                        "replace client id in deployment")
    replace_placeholder(path, "@@CLIENT_NAME@@", client_name,
                        "replace client name in deployment")

def deploy_client(path, module):
    execute_kubectl(f"kubectl apply -f ./{path}service.yaml")
    execute_kubectl(f"kubectl apply -f ./{path}{module}-deployment.yaml")

def run_pipeline(module, namespace, client_name, version, deployment_root_path, server_service_name):
    path = f"{deployment_root_path}{module.lower()}/"
    client_id = f"{client_name}-{module.lower()}"
    copy_tree("deployment-scripts/ignite-client/deployment/", path)
    create_service(namespace, client_id, path)
    create_deployment(version, namespace, client_id, client_name, path, server_service_name)
    deploy_client(path, module)


def delete_client(module, namespace, client_name):
    execute_kubectl(f"kubectl delete service -n {namespace} ignite-{client_name}-{module.lower()}-service", False)
    execute_kubectl(f"kubectl delete deployment -n {namespace} {client_name}-{module.lower()}", False)

if(__name__ == '__main__'):
    # Initialize the parser
    parser = argparse.ArgumentParser(
        description="ignite client creation script"
    )

    # Add the parameters positional/optional
    parser.add_argument('-c','--create', action='store_true', help="Flag to create client")
    parser.add_argument('-d','--delete', action='store_true', help="Flag to delete client")
    parser.add_argument('-n','--namespace', help="Namespace of client", type=str)
    parser.add_argument('-ver','--version', help="Client Version", type=str, required=True)
    parser.add_argument('-cl','--client', help="Client Name", type=str, required=True)
    parser.add_argument('-s','--service', help="Service name of ignite server", type=str, default="ignite-service")
    parser.add_argument('-m', '--modules', help='comma delimited list of module names to be deployed', type=str, default="compute-engine,query-service")

    # Parse the arguments
    args = parser.parse_args()
    namespace = "ignite" # Default namespace is ignite
    version = args.version
    client_name = None
    if(args.namespace):
        namespace = args.namespace
    client_name = args.client
    server_service_name = args.service
    module_list = [module for module in args.modules.split(',')]
    deployment_root_path = "build/deployment/"
    if not os.path.exists(deployment_root_path):
        os.mkdir(deployment_root_path)

    if(args.create and args.delete):
        raise Exception("Use only one flag --create / --delete")
    elif(args.create):
        for module in module_list:
            run_pipeline(module, namespace, client_name, module+"-"+version, deployment_root_path, server_service_name)
        print("** Done creating client **")

    elif(args.delete):
        print(f"** Starting client deletion **")
        for module in module_list:
            delete_client(module, namespace, client_name)
        print("** Done deleting client **")

    else:
        raise Exception("No create or delete flag passed. Use --create to create client or --delete to delete client")
