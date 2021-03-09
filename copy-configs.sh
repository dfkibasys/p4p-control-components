#!/bin/bash

# Pass docker project path as parameter: 
# ./copy-configs.sh /usr/local/basys/docker
default_path=../docker
docker_path=${1:-$default_path}

cp -r ./configs/default/de.dfki.cos.basys.p4p.config.default.aas/{components,components_repo} $docker_path/demonstrator/data/aas-services/
cp -r ./configs/default/de.dfki.cos.basys.p4p.config.default.ccs/{components,components_repo} $docker_path/demonstrator/data/cc-server/
cp -r ./configs/default/de.dfki.cos.basys.p4p.config.default.platform/{components,components_repo,processes} $docker_path/demonstrator/data/service-platform/