# 数据库表设计

# 1. 概述

此文档基于《0.需求说明书》和《1.总体架构设计》要求，定义企业内部培训系统MySQL及Redis数据库表设计，供开发人员参考。

# 2. MySQL表设计

## 2.1 t_auth表

**描述**：该表定义了系统用户的所有权限。**非必要，该表后期将不作任何修改和补充**。

| 字段      | 类型    | 解释               |
| --------- | ------- | ------------------ |
| id        | int     | （主键自增）权限ID |
| auth_name | varchar | （必须）权限表     |

## 2.2 t_user表

**描述**：该表定义了用户的账号和一般信息。

| 字段      | 类型    | 解释                             |
| --------- | ------- | -------------------------------- |
| id        | int     | （主键自增）用户ID               |
| username  | varchar | （必须）用户名                   |
| password  | varchar | （必须）密码                     |
| real_name | varchar | （必须）真实姓名                 |
| mobile    | varchar | （必须）手机号码                 |
| auth      | varchar | （必须）权限                     |
| extra     | varchar | （非必须）保留字段，便于后期拓展 |

## 2.3 t_dept表

**描述**：该表定义了部门名称和负责人ID。

| 字段      | 类型    | 解释                                   |
| --------- | ------- | -------------------------------------- |
| id        | int     | （主键自增）部门ID                     |
| dept_name | varchar | （必须）部门名称                       |
| head_id   | int     | （外键关联 t_user 表 id 字段）负责人ID |
| extra     | varchar | （非必须）保留字段，便于后期拓展       |

## 2.4 t_dept_worker表

**描述**：该表定义了部门下的所属成员。

| 字段    | 类型    | 解释                                 |
| ------- | ------- | ------------------------------------ |
| id      | int     | （自增）主键                         |
| dept_id | int     | （外键关联 t_dept 表 id 字段）部门ID |
| uid     | int     | （外键关联 t_user 表 id 字段）员工ID |
| extra   | varchar | （非必须）保留字段，便于后期拓展     |

## 2.5 t_resource_tag表

**描述**：该表定义了资源标签列表

| 字段     | 类型    | 解释                             |
| -------- | ------- | -------------------------------- |
| id       | int     | （自增）主键                     |
| tag_name | varchar | （必须）分类标签名称             |
| dept_id  | int     | （外键关联t_dept表id字段）部门ID |

## 2.6 t_resource_normal表

**描述**：该标签定义了一般资源的存放对象

| 字段          | 类型    | 解释                                         |
| ------------- | ------- | -------------------------------------------- |
| id            | int     | （自增）主键                                 |
| resource_name | varchar | （必须）资源名                               |
| resource_path | varchar | （必须）资源文件保存路径                     |
| dept_id       | int     | （外键关联t_dept表id字段）部门ID             |
| tag_id        | int     | （外键关联t_resource_tag表id字段）分类标签ID |
| up_id         | int     | （外键关联t_user表id字段）上传者ID           |
| up_datetime   | varchar | （必须）时间，yyyy-mm-dd hh:mm:ss            |

## 2.7 t_lesson表

**描述**：该表定义了一门课程所需的要素

| 字段         | 类型    | 解释                                |
| ------------ | ------- | ----------------------------------- |
| id           | int     | （自增）主键                        |
| lesson_name  | varchar | （必须）课程名                      |
| lesson_des   | varchar | （必须）课程描述                    |
| teacher_id   | int     | （外键关联t_user表id字段）教师ID    |
| lesson_state | tinyint | （默认为0，表示未发布）课程发布状态 |
| extra        | varchar | （可选）保留拓展字段                |

## 2.8 t_lesson_chapter表

**描述**：该表定义了一门课程的章节名称

| 字段         | 类型    | 解释                               |
| ------------ | ------- | ---------------------------------- |
| id           | int     | （自增）主键                       |
| chapter_name | varchar | （必须）章节名                     |
| lesson_id    | int     | （外键关联t_lesson表id字段）课程ID |

## 2.9 t_resource_lesson表

**描述**：该表定义了一门课程的教材。要素包括课程ID、教师ID、章节ID、资源路径、上传时间

| 字段          | 类型    | 解释                                       |
| ------------- | ------- | ------------------------------------------ |
| id            | int     | （自增）主键                               |
| lesson_id     | int     | （外键关联t_lesson表id字段）课程ID         |
| teacher_id    | int     | （外键关联t_user表id字段）教师ID           |
| chapter_id    | int     | （外键关联t_lesson_chapter表id字段）章节ID |
| resource_path | int     | （必须）资源文件路径                       |
| up_datetime   | varchar | （必须）上传时间                           |

## 2.10 t_lesson_test表

**描述**：该表定义了视频教材的测试题。

| 字段                | 类型    | 解释                                                 |
| ------------------- | ------- | ---------------------------------------------------- |
| id                  | int     | （自增）主键                                         |
| test_title          | varchar | （必须）测试题名称                                   |
| test_options_a      | varchar | （必须）选项A                                        |
| test_options_b      | varchar | （必须）选项B                                        |
| test_options_c      | varchar | （必须）选项C                                        |
| test_options_d      | varchar | （必须）选项D                                        |
| test_options_answer | varchar | （必须）正确选项，a,b,c,d，为多选时使用逗号分隔      |
| test_time           | int     | （必须）试题出现在视频的第几秒                       |
| resource_lesson_id  | int     | （必须）视频教材ID                                   |
| test_state          | varchar | （必须，只允许两个值）单选：radio，多选：mult_select |
| extra               | varchar | （可选）保留拓展字段                                 |

## 2.11 t_training_plan表

**描述**：该表定义了培训计划所需要素。

| 字段                | 类型    | 解释                                                         |
| ------------------- | ------- | ------------------------------------------------------------ |
| id                  | int     | （自增）主键                                                 |
| training_title      | varchar | （必须）培训计划标题                                         |
| training_purpose    | varchar | （必须）培训计划目的                                         |
| training_start_time | varchar | （必须）培训计划开始时间 yyyy-mm-dd                          |
| training_end_time   | varchar | （必须）培训计划结束时间 yyyy-mm-dd                          |
| dept_id             | int     | （外键关联t_dept表id字段）部门ID                             |
| training_state      | varchar | （必须）培训计划状态，timeout超时、end已结束、over已完成，ongoing正在进行 |
| extra               | varchar | （可选）保留拓展字段                                         |

## 2.12 t_training_plan_teacher表

**描述**：该表定义了培训计划的讲师

| 字段                | 类型 | 解释                                          |
| ------------------- | ---- | --------------------------------------------- |
| id                  | int  | （自增）主键                                  |
| training_teacher_id | int  | （外键关联t_user表id字段）讲师ID              |
| training_plan_id    | int  | （外键关联t_training_plan表id字段）培训计划ID |

## 2.13 t_training_plan_student表

**描述**：该表定义了培训计划的学员

| 字段                | 类型 | 解释                                          |
| ------------------- | ---- | --------------------------------------------- |
| id                  | int  | （自增）主键                                  |
| training_student_id | int  | （外键关联t_user表id字段）学员ID              |
| training_plan_id    | int  | （外键关联t_training_plan表id字段）培训计划ID |

# 3.Redis表设计

## 3.1 Training-User-Service-Token哈希表设计

**描述**：该表存放用户对应的access_token。能够根据username获取其键值通行令牌。

| 键       | 值           |
| -------- | ------------ |
| username | access_token |

## 3.2 Lesson-Chapter-List哈希表设计

**描述**：该表存放指定课程下的章节列表。

| 键        | 值         |
| --------- | ---------- |
| lesson_id | [课程列表] |

## 3.3 Plan-Teacher-List哈希表设计

**描述**：该表存放指定培训计划下的讲师列表。

| 键                               | 值         |
| -------------------------------- | ---------- |
| [计划ID]-[读取记录]-[第几条读起] | [教师列表] |

## 3.4 Plan-Student-List哈希表设计

**描述**：该表存放指定培训计划下的员工列表。

| 键                               | 值         |
| -------------------------------- | ---------- |
| [计划ID]-[读取记录]-[第几条读起] | [员工列表] |

