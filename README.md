# spas-flow-parent
可视化工作流编排

## 项目结构
  - spas-flow-core: 项目bean对象
  - spas-flow-sample-parent: 项目示例
  - spas-flow-sdk: 项目核心业务逻辑
  - spas-flow-spring-boot-starter: 项目引入包
  - spas-flow-dependencies: 项目版本引用
  - spas-flow-framework-parent: 项目工具类
  - spas-flow-web: 工作流web端编辑
  
## 架构设计和界面
工作流模版引入支持 yml > db > apollo,具体引用参考 FlowDefintionRegistry 实现类

工作流支持: bean节点、service节点、condition节点、method节点、subflow节点 

bean节点：根据规则构造转换对象 

method节点：本地方法调用（支持全路径、spring管理对象），其中支持个性化、动态参数调用 

service节点：远程调用节点，现支持http、dubbo，后续会对接gRPC、thrift等其他RPC框架 

condition节点： 条件节点，支持个性化求值表达式，基于[aviator](https://www.yuque.com/boyan-avfmj/aviatorscript)实现

subflow节点：子工作流节点

###业务执行流程图
![可视化工作流](https://raw.githubusercontent.com/JeromeLiuLly/spas-flow-parent/master/doc/设计图.png)


###功能界面图
![可视化工作流](https://raw.githubusercontent.com/JeromeLiuLly/spas-flow-parent/master/doc/可视化工作流.png)  
  
## 使用示例 
```
<dependency>
    <groupId>com.candao.spas</groupId>
    <artifactId>spas-flow-spring-boot-starter</artifactId>
    <version>${revision}</version>
</dependency>
```
