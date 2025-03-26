import os
import time


from invoke import task


PROJECT_ROOT = os.path.dirname(__file__)


@task
def build_frontend(c):
    with c.cd(f"{PROJECT_ROOT}/frontend"):
        c.run("npm run build")


@task
def build_backend(c):
    with c.cd(f"{PROJECT_ROOT}/backend"):
        c.run("./mvnw package -Dmaven.test.skip=true")


@task
def build(c):
    build_backend(c)
    build_frontend(c)


@task
def up(c):
    build(c)
    c.run("docker compose down")
    c.run("docker compose build")
    c.run("docker compose up -d")


@task
def docker_build(c):
    with c.cd(f"{PROJECT_ROOT}/frontend"):
        c.run("docker build -t gs/msc-frontend .")
    with c.cd(f"{PROJECT_ROOT}/backend"):
        c.run("docker build -t gs/msc-backend .")


@task
def podman_run_backend(c):
    c.run(
        "podman run -e SPRING_PROFILES_ACTIVE=test -p 9999:8080 --name msc-con -d msc-connector"
    )


@task
def podman_run_neo4j(c):
    c.run(
        'podman run -e NEO4J_apoc_export_file_enabled=true -e NEO4J_apoc_import_file_enabled=true -e NEO4J_apoc_import_file_use__neo4j__config=true -e NEO4J_PLUGINS=["apoc"] -e NEO4J_AUTH=neo4j/admin123 -p 7474:7474 -p 7687:7687 --name neo4j -d neo4j'
    )


@task
def podman_pod(c):
    c.run("podman pod stop msc-viewer")
    c.run("podman pod rm msc-viewer")

    c.run(
        "podman pod create --name msc-viewer -p 9999:80 -p 7474:7474 -p 7687:7687 -p 8888:8080"
    )
    c.run(
        "podman run --pod=msc-viewer -e SPRING_PROFILES_ACTIVE=test  --name msc-viewer-connector -d gs/msc-connector"
    )
    c.run(
        'podman run --pod=msc-viewer -e NEO4J_apoc_export_file_enabled=true -e NEO4J_apoc_import_file_enabled=true -e NEO4J_apoc_import_file_use__neo4j__config=true -e NEO4J_PLUGINS="[\\"apoc\\"]" -e NEO4J_AUTH=neo4j/admin123  --name msc-viewer-neo4j -d neo4j'
    )
    c.run("podman run --pod=msc-viewer --name msc-viewer-client -d gs/msc-client")
