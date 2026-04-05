# Library Vue Frontend

基于 Vue 3 + Vite 的图书馆前端，包含管理员与普通用户两套功能页面。

## 已实现功能

- 登录/注册页面
- 统一蓝色主题布局（页头 + 左侧导航）
- 普通用户：图书中心、我的借阅、借书/还书
- 管理员：用户管理、图书管理、借阅管理、操作日志
- 路由守卫与基于 `role` 的页面权限控制

## 目录说明

- `src/layouts/MainLayout.vue`: 系统主框架（Header + Aside）
- `src/router/index.js`: 路由与权限守卫
- `src/stores/auth.js`: 登录状态管理
- `src/api/`: Axios 接口封装
- `src/views/`: 各业务页面

## 运行

```bash
npm install
npm run dev
```

默认通过 Vite 代理请求后端：`/api -> http://localhost:8090`。

## 构建

```bash
npm run build
```
