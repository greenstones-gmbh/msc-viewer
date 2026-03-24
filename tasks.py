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
def start_backend(c):
    c.run("docker compose up -d  msc-neo4j")
    with c.cd(f"{PROJECT_ROOT}/backend"):
        c.run("./mvnw spring-boot:run -Dspring-boot.run.profiles=a")
