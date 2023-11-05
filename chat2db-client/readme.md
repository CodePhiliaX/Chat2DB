## 技术选型

1. 脚手架：umi v4
2. 组件库：antd v5
3. 状态管理库 dva
4. 图表库
5. 国际化

目录结构 tree ./ -L 2 -I node_modules

## 启动项目


强制使用 yarn，因为环境变量、lock 文件只维护了 yarn，npm/pnpm 可能会产生意想不到的 bug node 版本要求 16 以上 `npm i -g yarn` `yarn` `yarn run build:web:prod` `cp -r dist ../chat2db-server/chat2db-server-start/src/main/resources/static/front` (复制打包结果到指定目录。windows 可能命令不一样，可以手动复制下) 之后就可以启动后端了 `mvn clean package -B '-Dmaven.test.skip=true' -f chat2db-server/pom.xml`

启动前端项目调试 `yarn run start:web` 注意：因为 electron 包比较难下载，如果 yarn 时 electron 下载失败或超时，可以删除掉 chat2db-client/package.json 下的 electron，再次 yarn

## TS书写规范

  1. 所有的interface 与 type 必须已I开头
    `interface IState { name: string }` // good
    `interface State { name: string }` // bad

## 如何在 js 与 css 中使用颜色

具体转换在 /theme/index.ts 中的 injectThemeVar

- js 在 window.\_AppThemePack 中去取 eg：`window._AppThemePack.controlItemBgActive` // good
- css eg: `background: var(--control-item-bg-active)` // good
- css `color: #fff` // bad

## 如何使用国际化

所有 key 参考格式为 `模块名称.文案类型.文案描述`。若文案包含可变部分，可使用 `{1}`、`{2}`、`{3}` 代替。

`src/i18n/index.ts` 中默认导出 `i18n` 转换方法，可以将 key 转换为对应的实际文案。文案中的 `{1}` 将被替换为第二个入参，以此类推。例如：

```tsx
// 'home.tip.welcome': '欢迎您，{1}！'
i18n('home.tip.welcome', user.name); // => '欢迎您，张三！'
```

也可以使用 `src/i18n/index.ts` 中导出的 `i18nElement` 方法，可以将文案中的占位符替换为 JSX 元素。例如：

```tsx
i18nElement('home.tip.welcome', <b>{user.name}</b>); // => <>欢迎您，<b>张三</b>！</>'
```

```code
├── dist
│   ├── index.html
│   ├── layouts__index.async.js
│   ├── layouts__index.chunk.css
│   ├── p__docs.async.js
│   ├── p__index.async.js
│   └── umi.js
├── package.json
├── readme.md
├── release
│   ├── Chat2DB-1.0.0-arm64-mac.zip
│   ├── Chat2DB-1.0.0-arm64-mac.zip.blockmap
│   ├── Chat2DB-1.0.0-arm64.dmg
│   ├── Chat2DB-1.0.0-arm64.dmg.blockmap
│   ├── builder-debug.yml
│   ├── builder-effective-config.yaml
│   └── mac-arm64
├── src
│   ├── assets
│   ├── blocks
│   ├── components
│   ├── config
│   ├── constant
│   ├── layouts
│   ├── locales
│   ├── main
│   ├── models
│   ├── pages
│   ├── typings
│   └── utils
├── tsconfig.json
├── typings.d.ts
└── yarn.lock
```
