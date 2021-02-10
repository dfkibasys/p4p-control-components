# BaSys 4.2 P4P-Demonstrator
This project constitutes a set of control component and service implementations 
for Industrie 4.0 components located at the DFKI MRK4.0 Lab as part of [Power4Production](https://power4production.de/) 
along with associated configurations (asset administration shells, control component server, platform) of BaSys 4.2.
## Getting Started
These instructions will get you a copy of the project up and running on your local machine in an Eclipse IDE for development and testing purposes.

### Prerequisites

1) Install the BaSys 4.2  Workbench including this project. To this end, follow these instructions: [Eclipse integration](https://basys.dfki.dev/gitlab/i40/basys/eclipse-integration).

2) Install the BaSys 4.2 docker stacks as described [here](https://basys.dfki.dev/gitlab/i40/basys/docker). Note: Regarding the dockerized demonstration environment, make sure that running containers
do not interfere with your workbench setup, i.e. simply stop the BaSys-related containers for Administration Shell Management, Control Components, and the BaSys service platform.

### Overview
Once you have sucessfully setup the project in your Eclipse IDE, you will find sub-projects therein configuring
- Asset administration shell management (aas)
- Control Component Server (ccs)
- BaSys 4.2 service platform (platform)

<img src='/docs/basys4.2-p4p-demonstrator.png?raw=true' width='75%' height='75%'>

## Configuration
### Asset Administration Shell Management (aas)
Here, you define the asset administration shells and associated submodels of I40 components
which will be accessible via the AAS registry under `localhost:4999/api/v1/registry`. The
following folders (visible in the Eclipse Package Explorer) are of importance:

- `components`: Includes a set of `.properties`-files for asset administration shells (`aas` subfolder) and
                associated submodels (`submodels`)
- `components_repo`: Set of pre-configured aas and submodels which can be used in the `components` folder

### Control Component Server (ccs)
In this project, control components for which an instance is automatically generated in the OPC-UA server (`opc.tcp://localhost:12685/basys`)
are specified. The following folders are of importance:
- `certs`: Set of trusted, rejected and issued certificates of OPC-UA clients for accessing the OPC-UA server
- `components`: Set of `.json`-files configuring important properties of an asset such as its id, name, execution mode, ids of aas/submodels and service endpoints
- `components_repo`: Set of pre-defined control-component configs which can be used in the `components` folder

### BaSys Service Platform (platform)
Here, you configure important service components of the BaSys Service platform. Important folders:
- `components`: in the `services` subfolder, you find important service configurations, i.e. for the process execution manager and the  mqtt gateway
- `components_repo`: set of pre-defined configs which can be used in the `components` folder 
- `processes`: set of production and test processes in `.bpmn` format

## Usage
### Starting the P4P-Demonstrator 
In order to start the P4P-Demonstrator from within the Eclipse IDE for testing purposes proceed as follows.
Each subproject (aas, ccs, platform) provides an own `.launch` file which can be started via `Right-Click --> Run As`.
Start all three of them in the following order: 
1) aas
2) ccs
3) platform
After a first launch, the three mentioned run configurations should appear in the run section.

<img src='/docs/basys4.2-p4p-demonstrator-startup.png?raw=true' width='75%' height='75%'>

### Inspecting the OPC-UA Server
coming soon
### Starting Processes
coming soon

## Docker
coming soon



## Contributing
Authors

* **Jonas Mohr** - Profile at [DFKI](https://www.dfki.de/web/ueber-uns/mitarbeiter/person/jomo02)
* **Sönke Knoch** - Profile at [DFKI](https://www.dfki.de/web/ueber-uns/mitarbeiter/person/sokn01)
* **Daniel Porta** - Profile at [DFKI](https://www.dfki.de/web/ueber-uns/mitarbeiter/person/dapo01/)
* **Andreas Luxenburger** - Profile at [DFKI](https://www.dfki.de/web/ueber-uns/mitarbeiter/person/anlu01/)