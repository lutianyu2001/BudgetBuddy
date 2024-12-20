---
title: 个人支出追踪应用需求说明书

---

# 个人支出追踪应用需求说明书

## 0 重要信息

### 0.1 数据结构

#### 0.1.1 银行交易数据 SQL 表
 - id (int, **自增**, 主键)
 - account number (账户号码)
 - details (text, 交易信息)
 - amount (double, 交易金额)
 - store (text, 商户信息)
 - category (text, 类别)
 - date (datetime, 交易日期)

#### 0.1.2 银行用户信息 SQL 表
 - account number (int, **自增**, 主键)
 - password md5 (text, 密码 MD5 摘要)
 - name (text, 姓名)
 - balance (double, 余额)

#### 0.1.3 安卓APP交易数据结构
在0.1.1的基础上增加以下信息：
 - comments （text, 备注）
 - attachment （附件）

#### 0.1.4 用户数据结构
 - username （text，主键, app 用户名）
 - password md5 (text, app 密码 MD5 摘要)
 - bank account number (int, 银行账户号码)
 - bank password md5 (text, 银行账号密码 MD5 摘要)

### 0.2 前端要求
 - 所有字符串均需存储在 `res/values/strings.xml` 中, 便于多语言支持
 - 做了一部分的源代码供您参考, 主要是前端 layout 有很多已经写好的可以直接使用
 - 界面可参照 Wireframe 图片, 但 Wireframe 里的界面并不完整, 新写的界面要确保风格一致

### 0.3 后端要求

 - 安卓 app 本地存储数据 (包括用户注册信息): Room 或 SQLite
 - 除了安卓端以外, 还需要用python写一个简单的银行服务器, 只需要实现以下功能:
   - SQLite 数据库 (数据结构参照 0.1.1 和 0.1.2)
   - API 抓取交易信息 (HTTP POST, head: account number, password md5, 返回 JSON)

## 1 项目概述

这款应用旨在帮助用户, 特别是大学生, 更好地管理和追踪他们的日常支出。通过直观的界面和强大的分析功能, 用户可以清晰地了解自己的消费习惯, 做出更明智的财务决策。

## 2 核心功能模块

### 2.1 用户认证系统

#### 2.1.1 登录功能
用户可以通过以下方式访问他们的账户:
- 使用用户名和密码登录
- "记住我"功能 + 指纹等生物识别快速登录,避免频繁登录

为了确保账户安全, 系统会:
- 验证用户输入的正确性
- 保护用户的登录状态
- 在登录失败时提供清晰的错误提示

#### 2.1.2 注册流程
新用户注册过程设计得简单直观:
- 填写基本信息(用户名、邮箱、密码)

系统会实时检查:
- 密码强度是否达标
- 邮箱格式是否正确
- 所有必填信息是否完整

### 2.2 主要功能界面

#### 2.2.1 首页设计
首页作为用户最常访问的界面, 提供最重要的信息一览:
- 顶部显示当前总资产状况
- 清晰展示最近的收支记录
- 快速添加新交易的入口

用户可以:
- 下拉刷新获取最新数据
- 点击任何交易查看详情
- 通过底部导航快速切换功能

#### 2.2.2 交易记录详情
每笔交易的详细信息包括:
- 具体金额和交易类型
- 发生时间和地点
- 相关说明和标签
- 关联的图片或单据

用户可以方便地:
- 修改已有交易信息
- 删除错误记录
- 添加补充说明

### 2.3 数据分析功能

#### 2.3.1 支出分析
系统提供多维度的支出分析:
- 日常消费趋势图表
- 各类支出占比分析
- 预算执行情况跟踪

用户可以选择不同时间范围:
- 查看本月消费情况
- 比较历月支出变化
- 分析年度消费趋势

#### 2.3.2 数据可视化
通过直观的图表展示:
- 饼图显示支出构成
- 柱状图比较各月变化
- 折线图展示消费趋势

### 2.4 数据输入功能

#### 2.4.1 记账界面
设计简洁的记账界面:
- 金额输入键盘
- 常用分类快速选择
- 备注和图片附件功能

为提升记账效率:
- 提供常用场景快捷输入
- 消费地点自动定位

#### 2.4.2 导入银行交易信息
 - 只有一家银行叫 Sample Bank 可以选择, 输入 account number 和 password
 - 点击导入后连接0.2里提到的银行服务器API, 能获取到即视为导入成功
 - 不绑定该银行账号, 适用于单次导入

### 2.5 用户设置界面
 - 修改密码
 - 绑定银行账户
     - 只有一家银行叫 Sample Bank 可以选择, 输入 account number 和 password 存储信息
     - 点击绑定后尝试连接0.2里提到的银行服务器API获取数据, 能获取到即视为绑定成功
     - 绑定后银行账户上有两个按钮, 同步和删除, 点击同步即从银行导入交易信息

### 2.5 用户支持

#### 2.5.1 帮助中心
提供完善的用户支持:
- 常见问题解答
- 功能使用指南
- 提交问题反馈 (调用系统api发送邮件至指定邮箱)
