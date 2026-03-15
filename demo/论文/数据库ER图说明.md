# 丽水非遗平台 数据库 ER 图说明

## 一、实体与字段一览

| 表名 | 中文名 | 主键 | 主要字段 |
|------|--------|------|----------|
| sys_user | 用户 | id | username, password, nickname, avatar, email, phone, role, create_time |
| ich_project | 非遗项目 | id | title, category, ich_type, description, images, approval_time, heritage_level |
| ich_activity | 非遗活动 | id | title, region, content, location, start_time, end_time, cover_image, create_time |
| activity_registration | 活动报名 | id | user_id, activity_id, register_time |
| ich_inheritor | 传承人 | id | name, region, intro, representative_works, story, avatar, create_time |
| ich_inheritance_story | 传承故事 | id | title, region, content, cover_image, create_time |
| ich_news | 非遗资讯 | id | title, region, content, cover_image, publish_time, source, create_time |
| user_behavior | 用户行为日志 | id | user_id, type, project_id, create_time |
| user_favorite_project | 用户收藏项目 | id | user_id, project_id, create_time |
| recommendation_weight | 推荐权重配置 | id | ich_type, base_weight, view_weight, favorite_weight |

---

## 二、关系说明（画 ER 图时的连线）

**如何把「所有表」都连起来：**  
有外键的表用**实线**连（用户—报名—活动、用户—收藏—项目、用户/项目—行为、推荐权重—项目）；没有外键的表（传承人、传承故事、非遗资讯、活动）用**虚线**连到「非遗项目」，标注「同业务域/按区县」，这样 10 张表就都在一张图里且都有连线。下面「三」的 Mermaid 图已按此画好。

**免费画图（不付费）：** 用 Cursor/VS Code 打开本文件，Markdown 预览即可看到图并截图；或打开 https://app.diagrams.net/ 手画并导出 PNG/SVG；也可把 Mermaid 代码粘贴到 Notion、飞书、GitHub 的 .md 里渲染后截图。

| 关系 | 类型 | 说明 |
|------|------|------|
| 用户 — 活动报名 | 1 : N | 一个用户可报名多条活动 |
| 活动 — 活动报名 | 1 : N | 一个活动可被多人报名 |
| 用户 — 用户收藏项目 | 1 : N | 一个用户可收藏多个项目 |
| 非遗项目 — 用户收藏项目 | 1 : N | 一个项目可被多用户收藏 |
| 用户 — 用户行为日志 | 1 : N | 一个用户产生多条行为记录（user_id 可空表示未登录） |
| 非遗项目 — 用户行为日志 | 1 : N | 按 project_id 关联（仅浏览项目时有值） |
| 推荐权重 — 非遗项目 | 逻辑关联 | recommendation_weight.ich_type 与 ich_project.ich_type 对应，非外键 |

**独立实体（无外键）：** 传承人、传承故事、非遗资讯、非遗活动、非遗项目（作为被引用方）。

---

## 三、Mermaid ER 图代码（全表相连，免费预览）

**用法（不付费）：**  
在 Cursor 里新建或打开一个 `.md` 文件，把下面整段代码粘贴进去，然后 **用 Markdown 预览打开**（或安装 Mermaid 插件后预览），图会直接显示，可截图或另存为图片。也可把同一段代码粘贴到 **Draw.io** 的「插入 → 高级 → Mermaid」、或 **Notion/飞书** 的 Mermaid 代码块里生成图。

图中约定：**实线** = 有外键；**虚线** = 逻辑关联（传承人、传承故事、非遗资讯、活动 与 非遗项目 按区县/业务域归为一类，便于把所有表连在一起）。

```mermaid
erDiagram
    sys_user ||--o{ activity_registration : "报名"
    ich_activity ||--o{ activity_registration : "被报名"
    sys_user ||--o{ user_favorite_project : "收藏"
    ich_project ||--o{ user_favorite_project : "被收藏"
    sys_user ||--o{ user_behavior : "产生"
    ich_project ||--o{ user_behavior : "被浏览"
    recommendation_weight -.-> ich_project : "按ich_type配置"
    ich_inheritor -.-> ich_project : "同业务域"
    ich_inheritance_story -.-> ich_project : "同业务域"
    ich_news -.-> ich_project : "同业务域"
    ich_activity -.-> ich_project : "同业务域"

    sys_user {
        bigint id PK
        varchar username
        varchar password
        varchar nickname
        varchar avatar
        varchar email
        varchar phone
        varchar role
        datetime create_time
    }

    ich_project {
        bigint id PK
        varchar title
        varchar category
        varchar ich_type
        text description
        text images
        datetime approval_time
        varchar heritage_level
    }

    ich_activity {
        bigint id PK
        varchar title
        varchar region
        text content
        varchar location
        datetime start_time
        datetime end_time
        varchar cover_image
        datetime create_time
    }

    activity_registration {
        bigint id PK
        bigint user_id FK
        bigint activity_id FK
        datetime register_time
    }

    ich_inheritor {
        bigint id PK
        varchar name
        varchar region
        text intro
        text representative_works
        text story
        varchar avatar
        datetime create_time
    }

    ich_inheritance_story {
        bigint id PK
        varchar title
        varchar region
        text content
        varchar cover_image
        datetime create_time
    }

    ich_news {
        bigint id PK
        varchar title
        varchar region
        text content
        varchar cover_image
        datetime publish_time
        varchar source
        datetime create_time
    }

    user_behavior {
        bigint id PK
        bigint user_id FK
        varchar type
        bigint project_id FK
        datetime create_time
    }

    user_favorite_project {
        bigint id PK
        bigint user_id FK
        bigint project_id FK
        datetime create_time
    }

    recommendation_weight {
        bigint id PK
        varchar ich_type
        double base_weight
        double view_weight
        double favorite_weight
    }
```

---

## 四、用 Draw.io 手画（免费、全表相连）

**网址：** https://app.diagrams.net/ （无需登录，选「设备上」保存即可）

### 步骤概要

1. **画 10 个实体矩形**  
   每个矩形写表名，下面列出主键和重要字段（可简写：id、主要列名）。

2. **画有外键的实线**
   - **用户** —(1)— **活动报名** —(N)— **活动**（线上标 1、N）
   - **用户** —(1)— **用户收藏项目** —(N)— **非遗项目**
   - **用户** —(1)— **用户行为** —(N)— **非遗项目**
   - **推荐权重** —(虚线)— **非遗项目**，旁注「按 ich_type 对应」

3. **画逻辑关联虚线**  
   传承人、传承故事、非遗资讯、非遗活动 各画一条**虚线**到「非遗项目」，旁注「同业务域/按区县」。

4. **检查**  
   每个表至少有一条线连到其他表，即得到「所有表都连在一起」的 ER 图。

### 布局建议

中间放 **用户** 和 **非遗项目**；左侧：活动、活动报名、传承人、传承故事、非遗资讯；右侧：用户收藏项目、用户行为、推荐权重。虚线可用不同颜色区分。

---

## 五、如何画 ER 图（论文用）

### 方式 1：Cursor / VS Code 预览（免费）
- 新建 `.md` 文件，粘贴「三」的 Mermaid 代码，用 Markdown 预览打开，截图或另存为图片插入论文。
- 若预览不显示 Mermaid，可安装扩展「Mermaid」或「Markdown Preview Mermaid Support」。

### 方式 2：Draw.io（免费，推荐手画）
- 打开 https://app.diagrams.net/ ，选「Entity Relation」形状库，按「四」的步骤画实体和实线/虚线，导出 PNG/SVG。

### 方式 3：PowerDesigner / MySQL Workbench
- 在 PowerDesigner 中新建「概念模型」或「物理模型」，把 10 张表建好，再拖关系线（用户—报名—活动、用户—收藏—项目等）。
- MySQL Workbench：数据库反向工程生成 EER 图后，可再按本说明补全「推荐权重」等表及关系说明。

### 方式 4：Word / Visio（本地已有软件时）
- 用矩形表示实体，椭圆表示属性（可选），菱形表示关系，直线连接并标注 1、N、M。
- 核心关系：用户 1—N 报名 N—1 活动；用户 1—N 收藏 N—1 非遗项目；用户/项目与行为日志 1—N。

---

## 六、简图（只画核心实体与关系）

若论文篇幅有限，可只画核心 ER 简图：

- **实体**：用户、非遗项目、非遗活动、传承人、传承故事、非遗资讯、活动报名、用户收藏项目、用户行为、推荐权重。
- **关系**：
  - 用户 —(报名)— 活动
  - 用户 —(收藏)— 非遗项目
  - 用户 —(产生)— 用户行为 —(关联)— 非遗项目
  - 推荐权重按「类型」与非遗项目逻辑关联。

把上述「三」的 Mermaid 代码里的不需要的实体删掉，只保留这几种，即可得到简图。
