#!/bin/bash

set -e
setValues() {
    targethost=""

    while getopts ":vtp" Option
    do
        case $Option in
            v) targethost="vagrant@svarut01";;
            t) targethost="svarutadm@kssusvarut01.usrv.ubergenkom.no";;
            p) targethost="loggleser@kssusvarut01.srv.bergenkom.no";;
            ?) usage; exit 0;;
        esac
    done

    if [ $# -eq 0 ]
    then
        usage
        exit 1
    fi

}

usage() {
    echo "Usage: $0 [-vtp] "
    echo
    echo "  -v    upload to vagrant"
    echo "  -t    upload to test"
    echo "  -p    upload to prod"
}

setValues $@


cd "$( dirname "${BASH_SOURCE[0]}" )"

projectversion=$(git describe --abbrev=0)
revision=$(git describe --long | sed "s/.*-\(.*\)-.*/\1/")
mkdir -p target

scp svarut-sak-import/target/svarut-sak-import-dist.zip ${targethost}:/filarkiv/svarut-external-modules/releases/svarut-sak-import-${projectversion}.zip
ssh ${targethost} "(cd /filarkiv/svarut-external-modules/releases && ln -sf svarut-sak-import-${projectversion}.zip svarut-sak-import-latest.zip)"
