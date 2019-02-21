#!/bin/bash
#变量设置
APPS=/apps/
SERVICE_NAME=DF-API-Quotes
NOWDATE="`date +%Y-%m-%d,%H:%m:%s`"
MYPATH="/apps/backup/"${SERVICE_NAME}
WEBPATH=${APPS}${SERVICE_NAME}
BACKUPPATH=${MYPATH}/${SERVICE_NAME}${NOWDATE}
cd ${APPS}
if [ ! -x "$MYPATH" ];then
        echo "mkdir backup path:"${MYPATH}
        mkdir -p "$MYPATH"
fi

if [ ! -x "$WEBPATH" ];then
        echo "not web"
else
        echo ":"${BACKUPPATH}
        zip -r ${BACKUPPATH}.zip ${SERVICE_NAME}
fi
