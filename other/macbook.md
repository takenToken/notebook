
### MacOS解决too many open files

```bash
#第一列为项的名称，第二列为软件限制，第三列为硬件限制
launchctl limit
#修改
sudo launchctl limit maxfiles 1024 unlimited
```
