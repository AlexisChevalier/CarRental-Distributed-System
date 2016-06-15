#!/usr/bin/env bash
set -e

#build
echo -e "\033[31m Building...\033[0m"
javac -d src -cp src/mpi.jar:src/bouncycastleprov-jdk15on-154.jar:src/gson-2.6.2.jar:src/sqlite-jdbc-3.8.11.2.jar:src/ormlite-core-4.49-SNAPSHOT.jar:src/ormlite-jdbc-4.49-SNAPSHOT.jar -sourcepath src src/com/vehiclerental/Main.java

#deploy classes
echo -e "\033[31m Deploying...\033[0m"
scp -r ./out/production/Server/com/* <SERVER_DETAILS>:<PROJECT_FOLDER>/java/bin/com/
#deploy keystore
scp -r ./carrental.keystore <SERVER_DETAILS>:<PROJECT_FOLDER>/java/
#deploy libraries
scp -r ./src/bouncycastleprov-jdk15on-154.jar <SERVER_DETAILS>:<PROJECT_FOLDER>/java/bin/
scp -r ./src/gson-2.6.2.jar <SERVER_DETAILS>:<PROJECT_FOLDER>/java/bin/
scp -r ./src/sqlite-jdbc-3.7.2.jar <SERVER_DETAILS>:<PROJECT_FOLDER>/java/bin/
scp -r ./src/ormlite-core-4.49-SNAPSHOT.jar <SERVER_DETAILS>:<PROJECT_FOLDER>/java/bin/
scp -r ./src/ormlite-jdbc-4.49-SNAPSHOT.jar <SERVER_DETAILS>:<PROJECT_FOLDER>/java/bin/

#running
echo -e "\033[31m Running...\033[0m"
ssh <SERVER_DETAILS> 'cd <PROJECT_FOLDER>/java && ./killjavampi.sh' || true
ssh <SERVER_DETAILS> 'cd <PROJECT_FOLDER>/java && /shared/openmpi/bin/mpirun -n 5 --hostfile hostfile java -classpath bin:bin/bouncycastleprov-jdk15on-154.jar:bin/gson-2.6.2.jar:bin/sqlite-jdbc-3.7.2.jar:bin/ormlite-core-4.49-SNAPSHOT.jar:bin/ormlite-jdbc-4.49-SNAPSHOT.jar com.vehiclerental.Main 5106'