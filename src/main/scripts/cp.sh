#!/bin/bash
#变量设置
PROJECT=/opt/project/java/
SERVICE_NAME=DF-API-Quotes
NAME=${PROJECT}${SERVICE_NAME}-bin.zip
APPS=/apps/
PROJECTPATHSCEPIRT=${APPS}${SERVICE_NAME}/
cd ${PROJECT}
if [ ! -f "$NAME" ];then
   echo "not"${NAME}
else
    #echo "unzip : "${NAME}
    #unzip -o ${NAME}
    echo "cp  project to apps:"${SERVICE_NAME}
    cp -rf ${SERVICE_NAME} ${APPS}
    echo "chmod 777: "${PROJECTPATHSCEPIRT}"scrpit"

    chmod 777 ${PROJECTPATHSCEPIRT}/test/*.sh
    chmod 777 ${PROJECTPATHSCEPIRT}/prod/*.sh
    chmod 777 ${PROJECTPATHSCEPIRT}/dev/*.sh
    sed -i 's/\r$//' ${PROJECTPATHSCEPIRT}/test/*.sh
    sed -i 's/\r$//' ${PROJECTPATHSCEPIRT}/prod/*.sh
    sed -i 's/\r$//' ${PROJECTPATHSCEPIRT}/dev/*.sh
fi