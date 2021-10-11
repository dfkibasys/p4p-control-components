#!/bin/bash

# Pass docker project data path as parameter: 
# ./copy-configs.sh /usr/local/basys/docker/demonstrator/data
default_path=../docker/demonstrator/data
docker_path=${1:-$default_path}

aas_config_path=./configs/default/de.dfki.cos.basys.p4p.config.default.aas
ccs_config_path=./configs/default/de.dfki.cos.basys.p4p.config.default.ccs
platform_config_path=./configs/default/de.dfki.cos.basys.p4p.config.default.platform

# copy aas configs
cp -r $aas_config_path/{components,components_repo} $docker_path/aas-services/

# copy ccs configs
mkdir -p $docker_path/cc-server/{components_repo/{controlcomponents,services},components/services}
cp -r $ccs_config_path/components_repo/controlcomponents $docker_path/cc-server/components_repo/
cp -r $ccs_config_path/components_repo/services/docker_* $docker_path/cc-server/components_repo/services/
cp -r $ccs_config_path/components_repo/services/docker_* $docker_path/cc-server/components/services/
cp -r $ccs_config_path/components/*.json $docker_path/cc-server/components/

# copy platform configs
mkdir -p $docker_path/service-platform/{components_repo/{controlcomponents,services},components/services}
cp -r $platform_config_path/components_repo/services/docker_* $docker_path/service-platform/components/services/
cp -r $platform_config_path/components_repo/services/docker_* $docker_path/service-platform/components_repo/services/
cp -r $platform_config_path/components_repo/controlcomponents/docker_* $docker_path/service-platform/components_repo/controlcomponents/
cp -r $platform_config_path/components_repo/controlcomponents/docker_* $docker_path/service-platform/components/
cp -r $platform_config_path/processes $docker_path/service-platform/