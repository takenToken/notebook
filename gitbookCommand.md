## 安装卸载
```bash
# 检查 npm 版本
npm -v
# 检查 node 版本
node -v
# 安装 gitbook-cli
sudo npm install -g gitbook-cli
# 检查 gitbook 版本
gitbook --version
gitbook -V
# 查看 gitbook 帮助
gitbook help
# 更新 gitbook
# gitbook update
# 卸载 gitbook
# npm uninstall -g gitbook
```

## gitbook常用命令
```bash
#初始化
gitbook init
#安装
gitbook install
#编译
gitbook build
#运行发布
gitbook serve
```

## gh-pages操作
```bash
#! /bin/bash

# 编译构建 gitbook
gitbook install
gitbook build
# 查远程分支
# git branch -r
# 删除本地 gh-pages 分支
git branch -D gh-pages
# 删除远端的 gh-pages 分支
git branch -r -d origin/gh-pages
git push origin :gh-pages
# 创建新的 gh-pages 分支
git checkout --orphan gh-pages
# 发布文件，整理与推送
git rm -f --cached -r .
sleep 5
git clean -df
sleep 5
# rm -rf *~
# echo "*~" > .gitignore
echo "_book" >> .gitignore
echo "node_modules" >> .gitignore
git add .gitignore
git commit -m "Ignore some files"
cp -r _book/* .
git add .
git commit -m "Publish book"
# 推送 gh-pages 分支
git push -u origin gh-pages
# 切回 master 分支
git checkout master
```

## 文档转换pdf
* [calibre 插件](https://calibre-ebook.com/download_osx)

```bash
# 创建软链接
ln -s /Applications/calibre.app/Contents/MacOS/ebook-convert /usr/local/bin

# 导出 PDF
gitbook pdf /Users/yanglei/01_git/github_me/declaimer /Users/yanglei/Downloads/declaimer.pdf
```
