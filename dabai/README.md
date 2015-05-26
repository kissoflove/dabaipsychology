###　　　　　　　　　　　　　　　　　　　　　 Author:Youzi
###　　　　　　　　　 　　　　　　　　　 E-mail:3110210631@qq.com
####　　　　　　　　　　　　　　　　　　　　　　　　　　　[查看效果图](http://zhidu.coding.io/) 
****
一直想做一款用户喜欢，自己也喜欢的产品，但是总找不到方向，于是自己经常在各个Android应用市场下载一些APP，自己推敲研究，总结到了一些设计方面的经验。于是动手做了一个阅读类的APP，取名指读。很多时候做手机客户端的同学为获取服务端的数据而发愁，这里说的服务端数据指的是数据源，也许你会说可以用jsoup去抓取网页数据啊，确实，jsoup是个不错的工具，但是考虑到数据源的可控性上就不如指读的数据源来的靠谱了。只要你会简单的git命令，那么你完全可以让数据自定义，想了解具体操作方式的同学可以在coding上给我发私信。现在先把客户端给大家开源了。

 
===========================

![Image text](https://coding.net/u/youzi/p/CodingServer/git/raw/master/CodingServerData/img/onion-monkey-emoji_01.png)开源是一种精神，我觉得有必要坚持下去，开源既能提高自己的技能，也有可能帮助到他人，何乐而不为。　　　

###		 构建app
gradle 采用2.3 版本，在项目中有一个local.properties用来修改 sdk 路径  
使用gradle clean 清除 构建app 文件  
gradle build 构建app 会生成 debug 和 release app  
gradle assemblerelease 只生成 release app  

如果大家没有安装gradle window 下 执行  
gradlew.bat clean  
gradlew.bat build  
gradlew.bat assemblerelease  
linux 下执行  
./gradlew clean  
./gradlew build  
./gradlew assemblerelease  
这样就可以轻松完成 app 构建，再也不用eclipse 这样累赘的工具来打包了