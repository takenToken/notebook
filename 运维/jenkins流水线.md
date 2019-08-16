[jenkins资料](https://jenkins.io/zh/doc/book)
[pipeline](https://blog.csdn.net/M2l0ZgSsVc7r69eFdTj/article/details/80970737)

### Jenkins pipeline
* 安装插件,建议使用jenkins2.x以上版本
    - Pipeline Maven Integration
    - Pipeline NPM Integration
    -
* 新建项目选择流水线(pipeline)
* pipeline语法参考jenkins资料

```bash
properties([
    parameters([
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
       * Uses a Jenkins credential called "FOOCredentials" and creates environment variables:
       * "$GITCRE" will contain string "USR:PSW"
       * "$GITCRE_USR" will contain string for Username
       * "$GITCRE_PSW" will contain string for Password
       */
      GITCRE = credentials("3ffaaec1-630c-484e-bb13-948a04806ed3")
  }

  stage('Git clone and create tag'){
      git branch: "${branch}", credentialsId: '3ffaaec1-630c-484e-bb13-948a04806ed3',
      url: "git@gitlab.wonhigh.cn:scm-base-backend/petrel-easyrpt-oms.git"

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
      echo \'Git Push Tag success\''''
  }

  stage('Maven Build Project And Docker Image'){
     sh "/usr/local/apache-maven-3.3.9/bin/mvn clean install -Dmaven.test.skip=true -U"
     sh "/usr/local/apache-maven-3.3.9/bin/mvn -f ./${APPLICATION_NAME} docker:build"
  }

  stage('deploy'){
      env.APPLICATION_NAME="$APPLICATION_NAME"
      sshPublisher(publishers: [sshPublisherDesc(configName: '10.0.43.30(k8s)', transfers: [sshTransfer(cleanRemote: false, excludes: '', execCommand: '''cd /data/app_deploy
      sh app_deploy_dev.sh -p dev-${env.APPLICATION_NAME}  -v ${TAG}''', execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false, patternSeparator: '[, ]+', remoteDirectory: 'petrel-easyrpt-oms-api', remoteDirectorySDF: false, removePrefix: 'petrel-easyrpt-oms-api/target/maven-archiver', sourceFiles: 'petrel-easyrpt-oms-api/target/maven-archiver/pom.properties')], usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
  }
}

```


### jenkins pipeline 获取shell值

```bash
//获取标准输出
//第一种
result = sh returnStdout: true ,script: "<shell command>"
result = result.trim()
//第二种
result = sh(script: "<shell command>", returnStdout: true).trim()
//第三种
sh "<shell command> > commandResult"
result = readFile('commandResult').trim()

//获取执行状态
//第一种
result = sh returnStatus: true ,script: "<shell command>"
result = result.trim()
//第二种
result = sh(script: "<shell command>", returnStatus: true).trim()
//第三种
sh '<shell command>; echo $? > status'
def r = readFile('status').trim()

//无需返回值，仅执行shell命令
//最简单的方式
sh '<shell command>'
```
