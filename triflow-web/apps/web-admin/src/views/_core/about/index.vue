<script setup lang="ts">
import { Page } from '@vben/common-ui';

defineOptions({ name: 'About' });

declare global {
  const __VBEN_ADMIN_METADATA__: {
    authorEmail: string;
    authorName: string;
    authorUrl: string;
    buildTime: string;
    dependencies: Record<string, string>;
    description: string;
    devDependencies: Record<string, string>;
    homepage: string;
    license: string;
    repositoryUrl: string;
    version: string;
  };
}

const {
  buildTime = '',
  dependencies = {},
  devDependencies = {},
  license = 'MIT',
  version = '1.0.0',
} = __VBEN_ADMIN_METADATA__ || {};

const dependenciesItems = Object.keys(dependencies).map((key) => ({
  content: dependencies[key],
  title: key,
}));

const devDependenciesItems = Object.keys(devDependencies).map((key) => ({
  content: devDependencies[key],
  title: key,
}));
</script>

<template>
  <Page title="关于项目">
    <template #description>
      <div class="mt-4 flex items-center gap-6">
        <!-- Logo 展示 -->
        <div class="flex-shrink-0">
          <img
            src="/logo.svg"
            alt="Triflow Logo"
            class="h-24 w-24 object-contain"
          />
        </div>
        <!-- 项目描述 -->
        <p class="text-sm leading-6 text-foreground">
          <a
            href="https://github.com/glowxq/triflow"
            class="font-semibold text-primary hover:underline"
            target="_blank"
          >
            Triflow
          </a>
          是一个现代化的全栈企业级脚手架，支持三端开发：Server（Java）、Web（Vue）和
          App（UniApp）。采用最新技术栈，包括 Vue 3.5、Element Plus、Spring Boot
          3.5、MyBatis-Flex 等，提供完整的 RBAC 权限管理系统，开箱即用。
        </p>
      </div>
    </template>

    <div class="card-box p-5">
      <div>
        <h5 class="text-lg font-medium text-foreground">基本信息</h5>
      </div>
      <div class="mt-4">
        <dl class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
          <div class="border-t border-border px-4 py-6 sm:col-span-1 sm:px-0">
            <dt class="text-sm font-medium leading-6 text-foreground">
              版本号
            </dt>
            <dd class="mt-1 text-sm leading-6 text-foreground sm:mt-2">
              {{ version }}
            </dd>
          </div>
          <div class="border-t border-border px-4 py-6 sm:col-span-1 sm:px-0">
            <dt class="text-sm font-medium leading-6 text-foreground">
              开源许可协议
            </dt>
            <dd class="mt-1 text-sm leading-6 text-foreground sm:mt-2">
              {{ license }}
            </dd>
          </div>
          <div class="border-t border-border px-4 py-6 sm:col-span-1 sm:px-0">
            <dt class="text-sm font-medium leading-6 text-foreground">
              最后构建时间
            </dt>
            <dd class="mt-1 text-sm leading-6 text-foreground sm:mt-2">
              {{ buildTime || new Date().toLocaleString() }}
            </dd>
          </div>
          <div class="border-t border-border px-4 py-6 sm:col-span-1 sm:px-0">
            <dt class="text-sm font-medium leading-6 text-foreground">
              Github
            </dt>
            <dd class="mt-1 text-sm leading-6 text-foreground sm:mt-2">
              <a
                href="https://github.com/glowxq/triflow"
                class="text-primary hover:underline"
                target="_blank"
              >
                点击查看
              </a>
            </dd>
          </div>
          <div class="border-t border-border px-4 py-6 sm:col-span-1 sm:px-0">
            <dt class="text-sm font-medium leading-6 text-foreground">作者</dt>
            <dd class="mt-1 text-sm leading-6 text-foreground sm:mt-2">
              <a
                href="https://github.com/glowxq"
                class="text-primary hover:underline"
                target="_blank"
              >
                glowxq
              </a>
            </dd>
          </div>
        </dl>
      </div>
    </div>

    <div class="card-box mt-6 p-5">
      <div>
        <h5 class="text-lg font-medium text-foreground">技术栈</h5>
      </div>
      <div class="mt-4">
        <dl class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
          <div class="border-t border-border px-4 py-3 sm:col-span-1 sm:px-0">
            <dt class="text-sm text-foreground">后端框架</dt>
            <dd class="mt-1 text-sm text-foreground/80 sm:mt-2">
              Spring Boot 3.5 + MyBatis-Flex
            </dd>
          </div>
          <div class="border-t border-border px-4 py-3 sm:col-span-1 sm:px-0">
            <dt class="text-sm text-foreground">认证框架</dt>
            <dd class="mt-1 text-sm text-foreground/80 sm:mt-2">
              Sa-Token 1.44
            </dd>
          </div>
          <div class="border-t border-border px-4 py-3 sm:col-span-1 sm:px-0">
            <dt class="text-sm text-foreground">前端框架</dt>
            <dd class="mt-1 text-sm text-foreground/80 sm:mt-2">
              Vue 3.5 + TypeScript
            </dd>
          </div>
          <div class="border-t border-border px-4 py-3 sm:col-span-1 sm:px-0">
            <dt class="text-sm text-foreground">UI 组件</dt>
            <dd class="mt-1 text-sm text-foreground/80 sm:mt-2">
              Element Plus
            </dd>
          </div>
          <div class="border-t border-border px-4 py-3 sm:col-span-1 sm:px-0">
            <dt class="text-sm text-foreground">构建工具</dt>
            <dd class="mt-1 text-sm text-foreground/80 sm:mt-2">
              Vite 7 + Turbo
            </dd>
          </div>
          <div class="border-t border-border px-4 py-3 sm:col-span-1 sm:px-0">
            <dt class="text-sm text-foreground">移动端</dt>
            <dd class="mt-1 text-sm text-foreground/80 sm:mt-2">
              UniApp + Vue 3
            </dd>
          </div>
          <div class="border-t border-border px-4 py-3 sm:col-span-1 sm:px-0">
            <dt class="text-sm text-foreground">数据库</dt>
            <dd class="mt-1 text-sm text-foreground/80 sm:mt-2">
              MySQL 8 + Redis
            </dd>
          </div>
          <div class="border-t border-border px-4 py-3 sm:col-span-1 sm:px-0">
            <dt class="text-sm text-foreground">JDK 版本</dt>
            <dd class="mt-1 text-sm text-foreground/80 sm:mt-2">Java 25</dd>
          </div>
        </dl>
      </div>
    </div>

    <div v-if="dependenciesItems.length > 0" class="card-box mt-6 p-5">
      <div>
        <h5 class="text-lg font-medium text-foreground">生产环境依赖</h5>
      </div>
      <div class="mt-4">
        <dl class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
          <div
            v-for="item in dependenciesItems"
            :key="item.title"
            class="border-t border-border px-4 py-3 sm:col-span-1 sm:px-0"
          >
            <dt class="text-sm text-foreground">{{ item.title }}</dt>
            <dd class="mt-1 text-sm text-foreground/80 sm:mt-2">
              {{ item.content }}
            </dd>
          </div>
        </dl>
      </div>
    </div>

    <div v-if="devDependenciesItems.length > 0" class="card-box mt-6 p-5">
      <div>
        <h5 class="text-lg font-medium text-foreground">开发环境依赖</h5>
      </div>
      <div class="mt-4">
        <dl class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
          <div
            v-for="item in devDependenciesItems"
            :key="item.title"
            class="border-t border-border px-4 py-3 sm:col-span-1 sm:px-0"
          >
            <dt class="text-sm text-foreground">{{ item.title }}</dt>
            <dd class="mt-1 text-sm text-foreground/80 sm:mt-2">
              {{ item.content }}
            </dd>
          </div>
        </dl>
      </div>
    </div>
  </Page>
</template>
