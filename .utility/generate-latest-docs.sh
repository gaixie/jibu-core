#!/bin/bash
#
# DISCRIPTION: 通过 travis-ci 自动生成 javadoc 并发布到 github pages.
# see http://benlimmer.com/2013/12/26/automatically-publish-javadoc-to-gh-pages-with-travis-ci/ for details

if [ "$TRAVIS_REPO_SLUG" == "gaixie/jibu-core" ] && \
    [ "$TRAVIS_JDK_VERSION" == "oraclejdk8" ] && \
    [ "$TRAVIS_PULL_REQUEST" == "false" ]; then

    echo -e "Publishing javadoc & report...\n"

    cp -R build/docs/javadoc $HOME/javadoc-latest
    cp -R build/reports/tests $HOME/test-latest

    cd $HOME
    git config --global user.email "travis@travis-ci.org"
    git config --global user.name "travis-ci"
    git clone --quiet --branch=gh-pages https://${GH_TOKEN}@github.com/gaixie/jibu gh-pages > /dev/null

    cd gh-pages

    if [ "$TRAVIS_BRANCH" == "master" ]; then
        git rm -rf ./core-api/latest ./core-test/latest
        mkdir -p ./core-api ./core-test
        cp -Rf $HOME/javadoc-latest ./core-api/latest
        cp -Rf $HOME/test-latest ./core-test/latest
    else
        git rm -rf ./core-api/snapshot ./core-test/snapshot
        mkdir -p ./core-api ./core-test
        cp -Rf $HOME/javadoc-latest ./core-api/snapshot
        cp -Rf $HOME/test-latest ./core-test/snapshot
    fi

    git add -f .
    git commit -m "Lastest javadoc & report on successful travis build $TRAVIS_BUILD_NUMBER auto-pushed to gh-pages"
    git push -fq origin gh-pages > /dev/null

    echo -e "Published Javadoc & Report to gh-pages.\n"
fi
