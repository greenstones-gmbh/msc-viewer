from fabric import task
from fabric import Connection
import os


jar_file = "msc-backend-0.0.1-SNAPSHOT.jar"
server_path = "/home/areamanager/server/msc-viewer"

PROJECT_ROOT = os.path.dirname(__file__)

sbbam_test = Connection(
    host="areamanager@areamanager-test.sbb.ch",
    connect_kwargs={"password": "managerTest"},
)


@task
def build_frontend(c):
    with c.cd(f"{PROJECT_ROOT}/frontend"):
        c.run("npm run build")


@task
def build_backend(c):
    with c.cd(f"{PROJECT_ROOT}/backend"):
        c.run("./mvnw clean package -Dmaven.test.skip=true", echo=True)


@task
def build(c):
    print(f"build")
    build_backend(c)
    build_frontend(c)


# @task()
# def up(c):
#     up_connector(c)
#     up_client(c)


@task()
def upload_backend(c):
    with sbbam_test.cd(server_path):
        print(f"remove old msc-backend.jar")
        sbbam_test.run(f"rm msc-backend.jar", warn=True, echo=True)
        print(f"upload {jar_file}")
        sbbam_test.put(
            f"./backend/target/{jar_file}",
            remote=server_path + "/msc-backend.jar",
        )


@task()
def upload_frontend(c):
    c.run("rm /tmp/msc-frontend.tar", warn=True)
    with c.cd("./frontend/dist"):
        c.run("bsdtar cvf /tmp/msc-frontend.tar .", echo=True)

    with sbbam_test.cd(server_path):
        print(f"remove old client")
        sbbam_test.run(f"rm msc-frontend.tar", warn=True, echo=True)

        print(f"upload frontend")
        sbbam_test.put(
            "/tmp/msc-frontend.tar",
            remote=server_path,
        )
        sbbam_test.run(f"ls")


@task()
def upload(c):
    upload_backend(c)
    upload_frontend(c)


@task()
def build_backend_image(c):
    with sbbam_test.cd(server_path):
        print(f"build podman backend image ")
        sbbam_test.run("podman rmi gs/msc-backend", warn=True)
        sbbam_test.run("podman build -t gs/msc-backend . -f ./Dockerfile-backend")


@task()
def build_frontend_image(c):
    with sbbam_test.cd(server_path):
        print(f"build podman frontend image ")
        sbbam_test.run("podman rmi gs/msc-frontend", warn=True)
        sbbam_test.run("podman build -t gs/msc-frontend . -f ./Dockerfile-frontend")

        # print(f"stop and start connector")
        # sbbam_test.run("podman stop msc-connector", warn=True)
        # sbbam_test.run("podman rm msc-connector", warn=True)
        # sbbam_test.run(
        #     "podman run --pod=msc-viewer -e SPRING_PROFILES_ACTIVE=test --name msc-connector -d gs/msc-connector"
        # )
        # print(f"connector updated!")


@task()
def build_images(c):
    build_backend_image(c)
    build_frontend_image(c)


# @task()
# def up_connector(c):
#     build_connector(c)
#     upload_connector(c)
#     update_connector(c)


# @task()
# def upload_client_1(c):
#     c.run("/tmp/msc-client.tar", warn=True)
#     with c.cd("./build"):
#         c.run("bsdtar cvf /tmp/msc-client.tar .", echo=True)

#     with sbbam_test.cd(server_path):
#         print(f"remove old client")
#         sbbam_test.run(f"rm -rf ./client", warn=True)
#         sbbam_test.run(f"mkdir ./client")
#         sbbam_test.run(f"chmod 755 -R ./client")
#         sbbam_test.run(f"chcon -t httpd_sys_content_t ./client")

#         print(f"upload client")
#         sbbam_test.put(
#             "/tmp/msc-client.tar",
#             remote=f"{server_path}/client",
#         )

#         sbbam_test.run(f"cd ./client && tar xfv msc-client.tar && rm msc-client.tar")
#         sbbam_test.run(f"ls")


# @task()
# def update_client(c):
#     with sbbam_test.cd(server_path):
#         print(f"build podman client image ")
#         sbbam_test.run("podman build -t gs/msc-client . -f ./Dockerfile-client")

#         print(f"stop and start connector")
#         sbbam_test.run("podman stop msc-client", warn=True)
#         sbbam_test.run("podman rm msc-client", warn=True)
#         sbbam_test.run("podman run --pod=msc-viewer --name msc-client -d gs/msc-client")
#         print(f"client updated!")


# @task()
# def up_client(c):
#     build_client(c)
#     upload_client(c)
#     update_client(c)


# @task()
# def stop_pod(c):
#     sbbam_test.run("podman pod stop msc-viewer", echo=True)


# @task()
# def rm_pod(c):
#     sbbam_test.run("podman pod rm msc-viewer", echo=True)


# @task()
# def start_pod(c):
#     sbbam_test.run("podman pod start msc-viewer", echo=True)


@task()
def create_pod(c):

    sbbam_test.run("podman pod stop msc-viewer", echo=True, warn=True)
    sbbam_test.run("podman pod rm msc-viewer", echo=True, warn=True)

    sbbam_test.run("podman pod create --name msc-viewer -p 9999:80", echo=True)
    sbbam_test.run(
        'podman run --pod=msc-viewer -e NEO4J_apoc_export_file_enabled=true -e NEO4J_apoc_import_file_enabled=true -e NEO4J_apoc_import_file_use__neo4j__config=true -e NEO4J_PLUGINS="[\\"apoc\\"]" -e NEO4J_AUTH=neo4j/admin123  --name msc-viewer-neo4j -d neo4j',
        echo=True,
    )

    sbbam_test.run(
        "podman run --pod=msc-viewer --name msc-frontend -d gs/msc-frontend", echo=True
    )
    sbbam_test.run(
        "podman run --pod=msc-viewer -v /tmp/msc-viewer:/tmp/msc-viewer:z -v /home/areamanager/server/msc-viewer/data:/msc-viewer-data:ro,z -v /home/areamanager/server/msc-viewer/spring-config:/spring-config:ro,z -e SPRING_PROFILES_ACTIVE=testam -e SPRING_CONFIG_ADDITIONAL_LOCATION=file:/spring-config/ --name msc-backend -d gs/msc-backend",
        echo=True,
    )


@task()
def rm_pod(c):
    sbbam_test.run("podman pod stop msc-viewer", echo=True, warn=True)
    sbbam_test.run("podman pod rm msc-viewer", echo=True, warn=True)


@task()
def deploy(c):
    build(c)
    upload(c)
    rm_pod(c)
    build_images(c)
    create_pod(c)


@task()
def redeploy(c):
    rm_pod(c)
    build_images(c)
    create_pod(c)


#         podman stop mycontainer1 && podman rm mycontainer1

# podman pod create --name msc-viewer -p 9999:80 -p 7474:7474 -p 7687:7687 -p 9998:8080
# podman run --pod=msc-viewer -e SPRING_PROFILES_ACTIVE=test  --name msc-viewer-connector -d gs/msc-connector
# podman run --pod=msc-viewer -e NEO4J_apoc_export_file_enabled=true -e NEO4J_apoc_import_file_enabled=true -e NEO4J_apoc_import_file_use__neo4j__config=true -e NEO4J_PLUGINS="[\\"apoc\\"]" -e NEO4J_AUTH=neo4j/admin123  --name msc-viewer-neo4j -d neo4j
# podman run --pod=msc-viewer --name msc-viewer-client -d gs/msc-client


# podman pod create --name msc-viewer -p 9999:8080
# podman run --pod=msc-viewer -e SPRING_PROFILES_ACTIVE=test --name msc-viewer-connector -d gs/msc-connector


# podman run -p 9999:8080 -d gs/msc-client

# podman run -e SPRING_PROFILES_ACTIVE=test -p 9999:8080 gs/msc-connector
# podman run -e SPRING_PROFILES_ACTIVE=test -p 9999:8080 --name msc-con -d msc-connector

# podman run -e NEO4J_apoc_export_file_enabled=true -e NEO4J_apoc_import_file_enabled=true -e NEO4J_apoc_import_file_use__neo4j__config=true -e NEO4J_PLUGINS=["apoc"] -e NEO4J_AUTH=neo4j/admin123 -p 7474:7474 -p 7687:7687 --name neo4j -d neo4j
