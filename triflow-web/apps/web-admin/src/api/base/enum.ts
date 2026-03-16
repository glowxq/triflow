/**
 * 枚举管理 API
 * @description 统一枚举查询接口，支持前端缓存
 */
import { requestClient } from '#/api/request';

const BASE_URL = '/base/public/enums';

/**
 * 枚举选项类型
 */
export interface EnumItem {
  /** 枚举编码 */
  code: string;
  /** 枚举名称 */
  name: string;
}

/**
 * 枚举缓存
 * @description 避免重复请求相同枚举
 */
const enumCache = new Map<string, EnumItem[]>();

/**
 * 获取指定枚举的选项列表
 * @param enumClassName 枚举类简称（如 StatusEnum）
 * @returns 枚举选项列表
 */
export async function getEnumOptions(
  enumClassName: string,
): Promise<EnumItem[]> {
  // 优先从缓存获取
  const cached = enumCache.get(enumClassName);
  if (cached) {
    return cached;
  }

  // 请求接口
  const data = await requestClient.get<EnumItem[]>(
    `${BASE_URL}/${enumClassName}`,
  );

  // 缓存结果
  enumCache.set(enumClassName, data);

  return data;
}

/**
 * 获取所有可用的枚举名称
 * @returns 枚举名称集合
 */
export async function getAllEnumNames(): Promise<string[]> {
  const data = await requestClient.get<string[]>(BASE_URL);
  return data;
}

/**
 * 批量获取多个枚举的选项列表
 * @param enumClassNames 枚举类名数组
 * @returns 枚举选项映射表
 */
export async function getBatchEnumOptions(
  enumClassNames: string[],
): Promise<Record<string, EnumItem[]>> {
  // 找出未缓存的枚举
  const uncached = enumClassNames.filter((name) => !enumCache.has(name));

  if (uncached.length > 0) {
    // 请求未缓存的枚举
    const data = await requestClient.get<Record<string, EnumItem[]>>(
      `${BASE_URL}/batch/${uncached.join(',')}`,
    );

    // 缓存结果
    for (const [name, items] of Object.entries(data)) {
      enumCache.set(name, items);
    }
  }

  // 从缓存构建返回结果
  const result: Record<string, EnumItem[]> = {};
  for (const name of enumClassNames) {
    const items = enumCache.get(name);
    if (items) {
      result[name] = items;
    }
  }

  return result;
}

/**
 * 清除枚举缓存
 * @param enumClassName 指定枚举名称，不传则清除所有
 */
export function clearEnumCache(enumClassName?: string): void {
  if (enumClassName) {
    enumCache.delete(enumClassName);
  } else {
    enumCache.clear();
  }
}

/**
 * 根据 code 获取枚举名称
 * @param enumClassName 枚举类简称
 * @param code 枚举编码
 * @returns 枚举名称，若未找到则返回 code
 */
export async function getEnumNameByCode(
  enumClassName: string,
  code: string,
): Promise<string> {
  const items = await getEnumOptions(enumClassName);
  const item = items.find((i) => i.code === code);
  return item?.name ?? code;
}
