1. 能否使用一些技术，自动扫描所有枚举，然后在base提供一个统一的接口，前端封装一个统一的组件获取枚举数据。所有枚举都会实现接口
public interface BaseEnum {
    String getCode();
    String getName();
}
枚举下拉框展示name字段，选中提交的时候是code的值，并且将这个规则整合到CONTRIBUTING.md中
2. 开关管理 增加验证码开关的数据，并且默认关闭，关闭后登录不再需要验证码
3. 根据CONTRIBUTING.md 优化后端代码，前端代码也进行优化按照最优解
4. 参考 ./start.sh 启动并且通过mcp进行测试所有功能
