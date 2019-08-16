properties([
    parameters([
        //获取项目分支列表
        gitParameter(branch: '',
                     branchFilter: 'origin/(.*)',
                     defaultValue: 'master',
                     description: '',
                     name: 'branch',
                     quickFilterEnabled: false,
                     selectedValue: 'NONE',
                     sortMode: 'NONE',
                     tagFilter: '*',
                     type: 'PT_BRANCH')
    ])
])

node {

  def GIT_GROUP_NAME = "scm-base-backend"
  def GIT_APP_NAME = "petrel-easyrpt-oms"
  def APPLICATION_NAME = "petrel-easyrpt-oms-api"
  def TAG = ""
  environment {
      /*
       * 用户密码
       * Uses a Jenkins credential called "FOOCredentials" and creates environment variables:
       * "$GITCRE" will contain string "USR:PSW"
       * "$GITCRE_USR" will contain string for Username
       * "$GITCRE_PSW" will contain string for Password
       */
      GITCRE = credentials("3ffaaec1-630c-484e-bb13-948a04806ed3")
  }

  stage('Git clone and create tag'){
      println "begin Git clone and create tag!"
      //拉取源代码
      git branch: "${branch}", credentialsId: '3ffaaec1-630c-484e-bb13-948a04806ed3',
      url: "git@gitlab.wonhigh.cn:scm-base-backend/petrel-easyrpt-oms.git"

      //获取git tag标签, 并生成新标签
      sh '''#!/bin/bash

      git pull
      #latestTag=release/v1.0.1-001
      #git for-each-ref refs/tags --sort=-refname --format="%(refname)" |grep -w -m1 "build-.*-*" > tag.txt
      latestTag=$(git for-each-ref refs/tags --sort=-refname --format="%(refname)" |grep -w -m1 "build-.*-*")
      echo "latestTag: ${latestTag}"

      #echo 002
      let newVersion=$(expr ${latestTag##*-} + 1)
      echo "newVersion ${newVersion}"
      #demo: build-master-001
      prefix=${latestTag##*/}

      #demo: build-master-002
      newTag=${prefix%-*}"-"$(printf "%03d\\n" "${newVersion}")
      echo "newTag=${newTag}"

      git config --global user.email "yang.lei@belle.com.cn"
      git config --global user.name "yang.lei"
      git tag -a $newTag -m "build-${newTag}"
      git push origin --tags
      # success
      [ -n "${newTag}" ] &&  git checkout -b ${newTag} ||  { echo -e "切换至指定的tag的版本，tag：${newTag} 不存在或为空，请检查输入的tag!" && exit 111; }
      cd ..
      echo \'Git Push Tag success\'
      '''

      TAG=sh(script: 'git for-each-ref refs/tags --sort=-refname --format="%(refname)" |grep -w -m1 "build-.*-*"', returnStdout: true).trim()
      echo "tag: ${TAG}"

      println "get tag: ${TAG}"
  }


  stage('Maven Build Project And Docker Image'){
     sh "/usr/local/apache-maven-3.3.9/bin/mvn clean install -Dmaven.test.skip=true -U"
     sh "/usr/local/apache-maven-3.3.9/bin/mvn -f ./${APPLICATION_NAME} docker:build"
  }

  stage('deploy'){
      sshPublisher(publishers: [sshPublisherDesc(configName: '10.0.43.30(k8s)', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: '''cd /data/app_deploy
      sh app_deploy_dev.sh -p dev-${APPLICATION_NAME}  -v ${TAG}''', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: 'petrel-easyrpt-oms-api', remoteDirectorySDF: false, removePrefix: 'petrel-easyrpt-oms-api/target/maven-archiver', sourceFiles: 'petrel-easyrpt-oms-api/target/maven-archiver/pom.properties')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
  }
}
