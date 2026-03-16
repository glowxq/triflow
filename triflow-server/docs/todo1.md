1. 配置管理模块报错
   Uncaught (in promise) SyntaxError: The requested module '/src/api/system/index.ts?t=1769249589139' does not provide an export named 'updateConfig' (at index.vue:33:3)
2. 开关管理的 开关分类应该在后端定义一个枚举，并有一个接口通过枚举获取分类参数
3. 所有页面表单的启用禁用东应该封装成组件，使用固定的组件
4. 配置管理 增加默认配置/Users/glowxq/Documents/code/glowxq/triflow/triflow-server/base/src/main/resources/config/dev/oss.yml
   /Users/glowxq/Documents/code/glowxq/triflow/triflow-server/base/src/main/resources/config/dev/secret.yml 根据这两个文件获取
5. 文本管理获取分类树失败
   request-client.ts:150
   POST http://localhost:7200/api/base/textCategory/tree 500 (Internal Server Error)
   index.vue:152 获取分类树失败
   ﻿
6. 文本正文内容html（富文本）、markdown要增加对应的编辑器支持 并增加增加预览功能，预览功能要支持响应式 pc/移动端
7. 修复点击文本信息 编辑表单时候所属分类回显不出来的问题
8. 控制台报错，修复这个问题
   generate-routes-backend.ts:65 route component is invalid: /demos/element/index.vue
   {id: 201, parentId: 200, name: 'DemosElement', path: '/demos/element', component: '/demos/element/index', …}
   generate-routes-backend.ts:65 route component is invalid: /demos/form/index.vue
   {id: 202, parentId: 200, name: 'DemosForm', path: '/demos/form', component: '/demos/form/index', …}
