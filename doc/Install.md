# note
neo4j 第一次运行新库时 耗时长（第二次之后有缓存，会快一点，但时间长了会有很大的硬盘占用，可以删除库再新建）
```
初始化 节点限制 可以极大的加快载入速度
CREATE CONSTRAINT IF NOT EXISTS ON (c:Class) ASSERT c.ID IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS ON (c:Class) ASSERT c.NAME IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS ON (m:Method) ASSERT m.ID IS UNIQUE;
CREATE CONSTRAINT IF NOT EXISTS ON (m:Method) ASSERT m.SIGNATURE IS UNIQUE;
CREATE INDEX IF NOT EXISTS FOR (m:Method) ON (m.SUB_SIGNATURE);
:schema 查看表库
:sysinfo 查看数据库信息
```

是否存在参考价值

污染的可能性种类：
1. 函数的参数
2. 对象本身可控，那么他的类属性就可控了

我们可以排除的是什么？纯无法传递的函数确认
1. 函数无参数，并且对象本身又不可控
2. 可污染的两种情况涉及的对象，都不参与函数内部逻辑

最终保存的分析状态是什么？
1. 记录调用关系时，记录当前调用对象是否可控，函数的入参是否可控，
2. 如果函数存在返回值，这个返回值是否可控
3. 并且这个可控对应的位置/来源是什么

这里重要的是记录可控的来源，那么记在什么位置比较合适？记到methodRef的位置上

在利用链中，存在两种类型
1. 调用了sink函数的函数，这里我们只需判断是否符合我们sink函数的污染要求
2. 中间作为桥梁的函数，这里需要判断入参、this是否可控

而我们的指针分析是正向的，哪些情况是可以忽略，不做分析的

最终我们要达成的分析状态是怎么样的？
1. 检索出所有调用sink函数的函数
2. 通过保存的分析状态，判断这里调用sink函数的参数是否是可控的
3. 如果是可控的，找到可控对应的位置/来源，继续往上层找
4. 直到最后到达source点


TODO ： p_parseComponent 污点丢失