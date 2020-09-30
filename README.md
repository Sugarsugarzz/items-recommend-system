### 1. 版本历史	

| 修订日期   | 版本号 | 修订人 | 备注                                         |
| ---------- | ------ | ------ | -------------------------------------------- |
| 2020.08.31 | 1.0    | 唐     |                                              |
| 2020.09.02 | 1.1    | 唐     | 补充算法介绍及数据库修改前提                 |
| 2020.09.24 | 1.2    | 唐     | 补充对专题、期刊和报告的支持，简化调用方式。 |
| 2020.09.29 | 1.3    | 唐     | 微服务化                                     |

### 2. 部署说明

- 部署前提

  需能够访问数据库服务器。

- 数据库前提

  - **app_user** 表增加 **pref_list**（text） 、 **wiki_pref_list**（text）、**subject_pref_list**（text）、**periodical_pref_list**（text）、**report_pref_list**（text） 字段，存储用户偏好。

  - **user_read_record** 表给 **user_id** 和 **ref_data_id** 增加普通索引。

  - 添加 **recommendations** 表，存放推荐结果。

    ```sql
    DROP TABLE IF EXISTS `recommendations`;
    CREATE TABLE `recommendations` (
      `id` int NOT NULL AUTO_INCREMENT,
      `user_id` int NOT NULL COMMENT '用户ID',
      `item_id` int NOT NULL COMMENT '推荐项ID',
      `info_type` int NOT NULL COMMENT '信息类型 1头条 2百科 3专题 4期刊 5报告',
      `derive_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '生成时间',
      PRIMARY KEY (`id`),
      UNIQUE KEY `unique_key` (`user_id`,`item_id`,`info_type`) USING BTREE
    ) ENGINE=InnoDB AUTO_INCREMENT=8524 DEFAULT CHARSET=utf8 COMMENT='推荐结果表';
    ```
    
  - 添加 **recommendations_default** 表，存放默认推荐结果，供新注册的账号使用。（没有用户ID字段）

    ```sql
    DROP TABLE IF EXISTS `recommendations_default`;
    CREATE TABLE `recommendations_default` (
      `id` int NOT NULL AUTO_INCREMENT,
      `item_id` int NOT NULL COMMENT '推荐项ID',
      `info_type` int NOT NULL COMMENT '信息类型 1头条 2百科 3专题 4期刊 5报告',
      `derive_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '生成时间',
      PRIMARY KEY (`id`),
      UNIQUE KEY `unique_key` (`item_id`,`info_type`) USING BTREE
    ) ENGINE=InnoDB AUTO_INCREMENT=4222 DEFAULT CHARSET=utf8 COMMENT='默认推荐结果表';
    ```

- 参数说明

  | 参数名  | 参数类型         | 说明             |
  | ------- | ---------------- | ---------------- |
  | userIDs | List&lt;Long&gt; | 特定用户的ID列表 |
  
- 调用示例

  1. 为所有用户执行一次推荐
  
  ```java
  new Recommender().executeInstantJobForAllUsers();
  ```
  
  2. 为特定用户执行一次推荐
  
  ```java
  List<Long> users = new ArrayList<>();
  users.add(1L);
  users.add(4L);
  new Recommender().executeInstantJobForCertainUsers(users);
  ```
  
- 使用说明

  建议每日定时调用一次。

### 3. 服务说明

- 定时每日零点执行一次对所有用户的推荐。

### 4. 推荐结果说明

​	推荐结果采用 **混排** 的方式。

​	推荐结果将存在 **recommendations** 表中，由 **info_type字段** 标注其所属的信息类型，**user_id字段** 标注其所属用户。

​	默认推荐结果（由热点推荐和随机推荐构成）存在 **recommendations_default** 表中，由 **info_type字段** 标注其所属的信息类型，无 user_id 字段，不区分用户，新注册的用户可直接从这个表里取推荐项。

### 5. 算法介绍

**使用离线方式的混合推荐：**

1. **基于用户的协同过滤推荐**   derive_algorithm = 0

   ```mermaid
   graph LR
   A[用户历史浏览记录] --> B[计算所有用户之间的相似度]
   B --> C[针对每个用户user]
   C --> D
   D[获取用户user的相似用户排序表] --> E[选择与user最相近的K个用户]
   E --> F[获取这K个用户浏览过,但user未浏览过的信息项]
   F --> C
   ```

   ​	**Mahout**：基于Java的数据挖掘与机器学习类库，提供推荐系统所需的分类、用户相似度计算，近邻用户计算等工具类。

2. **基于内容的推荐**    derive_algorithm = 1

   ```mermaid
   graph LR
   A[用户历史浏览记录] --> B[获取用户偏好关键词及TD-IDF值]
   B --> C[针对每个用户user]
   C --> D[抽取所有信息项的关键词及TF-IDF值]
   D --> F[计算用户偏好关键词及信息项关键词的匹配值]
   F --> E[得到匹配的信息项列表]
   E --> C
   ```

   - **定义内容相似的方式**

     从文本特征几个方面提取特征信息，进而对不同信息间的特征信息进行比较。

     常见的特征信息有：信息文本长度、信息所属话题类型、来源和关键词。

   - **提取信息关键词的方式**

     TF-IDF 算法：统计方法，用以估计一字词对于一个文本集或一个语料库中的其中一份文件的重要程度。字词的重要性随着它再文本中出现的次数成正比增加，但同时会随着它在语料库中的出现频率成反比下降。

   - **用户偏好构建方式**

     关键词  -- 从用户历史浏览记录中挖掘。

     1. 在库中为每个用户维护一个关键词列表。
     2. 用户浏览了某个模块的某个信息，利用TF-IDF算法提取出信息的K个关键词，以及对应的TF-IDF值（关键程度），并将这些存入到对应的关键词列表中。
     3. 如果用户中已有某关键词及对应TF-IDF值，则将TF-IDF值叠加，表示加强用户对该关键词的感兴趣程度。
     4. 为关键词列表设置一个衰减系数 **λ**，每天对用户的偏好关键词的TF-IDF值进行更新，减少关键词的收敛倾向。

   - **信息内容与用户偏好拟合度计算方式**

     基于用户的偏好关键词列表和某条信息的关键词列表，对两个Map的键匹配与值的运算即可。

     若有相同的键，则值相乘，多个相同键的值乘积累加，若无相同的值，值记为0。

     将拟合度最高的N个信息推送给用户。

     > 比如：
     >
     > 小白在“武器装备”头条模块的偏好关键词列表为 {歼20：100， 运8：200，运输舰：100...}
     >
     > “武器装备”头条模块中某个头条的关键词列表为 {运8：100，运输舰：50，飞机：20}
     >
     > 则小黑与该头条项的拟合度为 200 x 100 + 100 x 50 = 25000。

3. **基于热点的推荐**    derive_algorithm = 2

   ​	即`从所有用户浏览历史中，提取出近期被用户阅读最多的信息项`。

   ​	根据设定的基于热点推荐的数量 N，将这部分信息推荐给用户。

4. **随机补充推荐**    derive_algorithm = 3

   ​	根据设定的随机补充推荐的数量 N，从各类型随机取一些信息，然后取一些时效最新的信息，组成随机补充的部分，推荐给用户。

