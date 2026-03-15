# 数据库 E-R 图（全表相连）

在 Cursor 里用 **Markdown 预览** 打开本文件即可看到图，然后截图或另存为图片（无需付费网站）。  
或用 **Draw.io**：打开 https://app.diagrams.net/ → 插入 → 高级 → Mermaid → 粘贴下面代码。

```mermaid
erDiagram
    sys_user ||--o{ activity_registration : "报名"
    ich_activity ||--o{ activity_registration : "被报名"
    sys_user ||--o{ user_favorite_project : "收藏"
    ich_project ||--o{ user_favorite_project : "被收藏"
    sys_user ||--o{ user_behavior : "产生"
    ich_project ||--o{ user_behavior : "被浏览"
    recommendation_weight -.-> ich_project : "按ich_type"
    ich_inheritor -.-> ich_project : "同业务域"
    ich_inheritance_story -.-> ich_project : "同业务域"
    ich_news -.-> ich_project : "同业务域"
    ich_activity -.-> ich_project : "同业务域"

    sys_user {
        bigint id PK
        varchar username
        varchar password
        varchar nickname
        varchar role
        datetime create_time
    }

    ich_project {
        bigint id PK
        varchar title
        varchar category
        varchar ich_type
        text description
        datetime approval_time
        varchar heritage_level
    }

    ich_activity {
        bigint id PK
        varchar title
        varchar region
        text content
        datetime start_time
        datetime end_time
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
        datetime create_time
    }

    ich_inheritance_story {
        bigint id PK
        varchar title
        varchar region
        text content
        datetime create_time
    }

    ich_news {
        bigint id PK
        varchar title
        varchar region
        text content
        datetime publish_time
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

- **实线**：有外键（用户↔报名↔活动、用户↔收藏↔项目、用户/项目↔行为）。
- **虚线**：逻辑关联（推荐权重按类型、传承人/故事/资讯/活动与非遗项目同业务域）。
