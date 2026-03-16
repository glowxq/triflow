# Web 组件开发规范详解

## 1. 表单组件

### Element Plus 原生表单校验

```vue
<script setup lang="ts">
import { reactive, ref } from 'vue';
import type { FormInstance, FormRules } from 'element-plus';

const formRef = ref<FormInstance>();

const form = reactive({
  username: '',
  email: '',
  phone: '',
});

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' },
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' },
  ],
};

async function handleSubmit() {
  const valid = await formRef.value?.validate();
  if (!valid) return;
  // 提交逻辑
}
</script>

<template>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
    <el-form-item label="用户名" prop="username">
      <el-input v-model="form.username" placeholder="请输入用户名" />
    </el-form-item>
    <el-form-item label="邮箱" prop="email">
      <el-input v-model="form.email" placeholder="请输入邮箱" />
    </el-form-item>
    <el-form-item label="手机号" prop="phone">
      <el-input v-model="form.phone" placeholder="请输入手机号" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="handleSubmit">提交</el-button>
    </el-form-item>
  </el-form>
</template>
```

---

## 2. VXE Table 表格组件

优先使用 VXE Table 处理大数据量场景：

```vue
<script setup lang="ts">
import { useVbenVxeGrid } from '@vben/plugins/vxe-table';
import type { VxeGridProps } from '#/adapter/vxe-table';

const gridOptions: VxeGridProps = {
  columns: [
    { type: 'checkbox', width: 50 },
    { field: 'username', title: '用户名', minWidth: 120 },
    { field: 'email', title: '邮箱', minWidth: 180 },
    {
      field: 'status',
      title: '状态',
      width: 100,
      slots: { default: 'status' },
    },
    { field: 'createTime', title: '创建时间', width: 180 },
    {
      title: '操作',
      width: 150,
      fixed: 'right',
      slots: { default: 'action' },
    },
  ],
  proxyConfig: {
    ajax: {
      query: async ({ page }) => {
        const res = await getUserPage({
          pageNum: page.currentPage,
          pageSize: page.pageSize,
        });
        return { result: res.data, total: res.total };
      },
    },
  },
};

const [Grid, gridApi] = useVbenVxeGrid({ gridOptions });
</script>

<template>
  <Grid>
    <template #status="{ row }">
      <StatusTag :status="row.status" />
    </template>
    <template #action="{ row }">
      <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
      <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
    </template>
  </Grid>
</template>
```

---

## 3. Hooks 开发规范

### 命名规范

- 文件名：`use-xxx.ts`
- 函数名：`useXxx`

### 标准模板

```typescript
// composables/use-user-list.ts
import { ref, onMounted } from 'vue';
import type { Ref } from 'vue';
import { getUserPage } from '#/api/modules/user';
import type { UserVO, UserQueryDTO } from '#/api/modules/user';

interface UseUserListOptions {
  /** 是否立即加载 */
  immediate?: boolean;
  /** 默认分页大小 */
  defaultPageSize?: number;
}

interface UseUserListReturn {
  list: Ref<UserVO[]>;
  loading: Ref<boolean>;
  total: Ref<number>;
  refresh: () => Promise<void>;
  reset: () => Promise<void>;
}

export function useUserList(
  options: UseUserListOptions = {},
): UseUserListReturn {
  const { immediate = true, defaultPageSize = 10 } = options;

  const list = ref<UserVO[]>([]);
  const loading = ref(false);
  const total = ref(0);

  const query = ref<UserQueryDTO>({
    pageNum: 1,
    pageSize: defaultPageSize,
  });

  async function refresh() {
    loading.value = true;
    try {
      const res = await getUserPage(query.value);
      list.value = res.data;
      total.value = res.total;
    } finally {
      loading.value = false;
    }
  }

  async function reset() {
    query.value.pageNum = 1;
    await refresh();
  }

  if (immediate) {
    onMounted(refresh);
  }

  return { list, loading, total, refresh, reset };
}
```

### Hooks 规则

- 明确定义 Options 和 Return 接口类型
- 支持 `immediate` 选项控制是否立即加载
- 提供 `refresh` 和 `reset` 方法
- 使用 `loading` 状态管理加载中

---

## 4. 错误边界组件

```vue
<script setup lang="ts">
import { onErrorCaptured, ref } from 'vue';

const error = ref<Error | null>(null);

onErrorCaptured((err) => {
  error.value = err;
  return false; // 阻止向上传播
});
</script>

<template>
  <div v-if="error" class="error-boundary">
    <p>出错了：{{ error.message }}</p>
    <el-button @click="error = null">重试</el-button>
  </div>
  <slot v-else />
</template>
```

---

## 5. 测试规范

### 单元测试

```typescript
import { describe, it, expect } from 'vitest';
import { mount } from '@vue/test-utils';
import StatusTag from '../StatusTag.vue';

describe('StatusTag', () => {
  it('renders enabled status correctly', () => {
    const wrapper = mount(StatusTag, {
      props: { status: 1 },
    });
    expect(wrapper.text()).toContain('启用');
  });

  it('renders disabled status correctly', () => {
    const wrapper = mount(StatusTag, {
      props: { status: 0 },
    });
    expect(wrapper.text()).toContain('禁用');
  });
});
```

### 运行测试

```bash
pnpm test:unit
```
