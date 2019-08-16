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
  def HARBOR_NAME = "hub.wonhigh.cn/logistics/"

  def latestTag = ""
  environment {
      /*
       * 获取用户密码
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
      url: "git@gitlab.wonhigh.cn:${GIT_GROUP_NAME}/${GIT_APP_NAME}.git"

      //获取git tag标签, 并生成新标签
      sh '''#!/bin/bash
      currentDate=`date +%Y%m%d`
      #每天第一个版本,解决切换分支错误
      defaultFirstTagVersionPrefix="V"${currentDate}".001"
      #版本号前缀
      tagVersionPrefix="V"${currentDate}"."
      #默认版本号
      newTagVersion=${tagVersionPrefix}"000"
      #拉取最新源码
      #删除所有本地分支
      git tag -l | xargs git tag -d
      #从远程拉取所有信息
      git fetch origin --prune
      #获取最新版本号
      latestTag=$(git for-each-ref refs/tags --sort=-refname --format="%(refname)" |grep -w -m1 "V${currentDate}.*")
      echo "Get Project LatestTag: ${latestTag}"
      #是否存在标签
      if [ x"$latestTag" = x ]; then
          latestTag=${newTagVersion}
      fi

      #最小版本号0001
      let newMinVersion=$(expr ${latestTag##*.} + 1)
      echo "Create Project NewMinVersion: ${newMinVersion}"

      #新版本号例子: V20190815.001
      newTagVersion=${tagVersionPrefix}$(printf "%03d\\\\n" "${newMinVersion}")
      echo "NewTagVersion=${newTagVersion}"

      #设置Git全局属性
      git config --global user.email "yang.lei@belle.com.cn"
      git config --global user.name "yang.lei"
      git tag -a $newTagVersion -m "build ${newTagVersion}"
      git push origin --tags #推送所有本地tag到远程
      echo "推送标签成功：${newTagVersion}"

      git checkout -b ${newTagVersion}
      git checkout ${newTagVersion}

      cd ..
      echo "处理项目Tag标签成功"'''

      latestTag=sh(script: 'git for-each-ref refs/tags --sort=-refname --format="%(refname)" |grep -w -m1 "V*.*"', returnStdout: true).trim()
      latestTag=latestTag.substring(latestTag.lastIndexOf("/")+1)
      println "Git Latest Tag: ${latestTag}"
  }

  stage('Build Project And Docker Image'){
     sh "/usr/local/apache-maven-3.3.9/bin/mvn clean install -Dmaven.test.skip=true -U"
     sh "/usr/local/apache-maven-3.3.9/bin/mvn -f ./${APPLICATION_NAME} docker:build"
  }

  stage('Push Docker Image'){
     sh "docker tag ${HARBOR_NAME}${APPLICATION_NAME}:latest ${HARBOR_NAME}${APPLICATION_NAME}:${latestTag}"
     sh "docker push ${HARBOR_NAME}${APPLICATION_NAME}:${latestTag}"
  }

  stage('Deploy Run Images'){
      sshPublisher(publishers: [sshPublisherDesc(configName: '10.0.43.30(k8s)',
      transfers: [sshTransfer(cleanRemote: false, excludes: '',
      execCommand: "cd /data/app_deploy && sh app_deploy_dev.sh -p dev-${APPLICATION_NAME}  -v ${latestTag}",
      execTimeout: 120000, flatten: false, makeEmptyDirs: false, noDefaultExcludes: false,
      patternSeparator: '[, ]+', remoteDirectory: "${APPLICATION_NAME}", remoteDirectorySDF: false,
      removePrefix: "${APPLICATION_NAME}/target/maven-archiver",
      sourceFiles: "${APPLICATION_NAME}/target/maven-archiver/pom.properties")],
      usePromotionTimestamp: false, useWorkspaceInPromotion: false, verbose: false)])
  }
}
