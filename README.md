# debugger

本工具可以用来替换运行中的java进程的 class文件，无需重启，无需启动前加任何参数

1.attach-cli 是打好包的client端，里面readme有讲解client端的用法

2.attach-server 是打好包的server端，里面readme有讲解server端的用法

3.JavaAgent 是client源码

4.JavaAgentServer 是server源码


注意：本工具和jacoco的冲突，发现jacoco会在每个类上默认加入$jacocoInit方法，因此会抛java.lang.UnsupportedOperationException: class redefinition failed: attempted to change the schema (add/remove fields)的异常，而且addTransformer了如果不removeTransformer新的是不会生效的。
