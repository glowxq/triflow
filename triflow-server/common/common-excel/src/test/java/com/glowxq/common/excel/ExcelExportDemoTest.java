package com.glowxq.common.excel;

import cn.idev.excel.FastExcelFactory;
import cn.idev.excel.write.metadata.style.WriteCellStyle;
import com.glowxq.common.excel.demo.UserExcelVO;
import com.glowxq.common.excel.strategy.DefaultCellStyleStrategy;
import com.glowxq.common.excel.strategy.DefaultColumnWidthStyleStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Excel 导出功能演示测试
 * <p>
 * 本测试类演示如何使用 common-excel 模块进行 Excel 导出：
 * <ul>
 *     <li>基本的列表数据导出</li>
 *     <li>自定义样式策略</li>
 *     <li>列宽自动适配</li>
 * </ul>
 * </p>
 *
 * @author glowxq
 * @since 2024/01/01
 */
@DisplayName("Excel 导出功能演示")
class ExcelExportDemoTest {

    @TempDir
    Path tempDir;

    /**
     * 演示：基本的 Excel 导出
     * <p>
     * 这是最简单的导出方式，只需要指定输出流、实体类和数据列表。
     * </p>
     */
    @Test
    @DisplayName("基本导出示例")
    void testBasicExport() throws IOException {
        // 1. 准备测试数据
        List<UserExcelVO> dataList = createTestData();

        // 2. 创建输出文件
        File outputFile = tempDir.resolve("basic_export.xlsx").toFile();

        // 3. 执行导出
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            FastExcelFactory.write(fos, UserExcelVO.class)
                            .sheet("用户列表")
                            .doWrite(dataList);
        }

        // 4. 验证
        assertTrue(outputFile.exists(), "导出文件应该存在");
        assertTrue(outputFile.length() > 0, "导出文件不应为空");

        System.out.println("基本导出成功，文件路径: " + outputFile.getAbsolutePath());
    }

    /**
     * 演示：带样式的 Excel 导出
     * <p>
     * 使用 DefaultCellStyleStrategy 设置表头和内容样式，
     * 使用 DefaultColumnWidthStyleStrategy 实现列宽自动适配。
     * </p>
     */
    @Test
    @DisplayName("带样式的导出示例")
    void testStyledExport() throws IOException {
        // 1. 准备测试数据
        List<UserExcelVO> dataList = createTestData();

        // 2. 创建输出文件
        File outputFile = tempDir.resolve("styled_export.xlsx").toFile();

        // 3. 准备样式策略
        WriteCellStyle headStyle = new WriteCellStyle();
        WriteCellStyle contentStyle = new WriteCellStyle();
        DefaultCellStyleStrategy styleStrategy = new DefaultCellStyleStrategy(
                Arrays.asList(0, 1), headStyle, contentStyle);
        DefaultColumnWidthStyleStrategy widthStrategy = new DefaultColumnWidthStyleStrategy();

        // 4. 执行带样式的导出
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            FastExcelFactory.write(fos, UserExcelVO.class)
                            .registerWriteHandler(styleStrategy)
                            .registerWriteHandler(widthStrategy)
                            .sheet("用户列表")
                            .doWrite(dataList);
        }

        // 5. 验证
        assertTrue(outputFile.exists(), "导出文件应该存在");
        assertTrue(outputFile.length() > 0, "导出文件不应为空");

        System.out.println("带样式导出成功，文件路径: " + outputFile.getAbsolutePath());
    }

    /**
     * 演示：多 Sheet 导出
     * <p>
     * 将不同数据导出到同一个 Excel 文件的不同 Sheet 中。
     * </p>
     */
    @Test
    @DisplayName("多Sheet导出示例")
    void testMultiSheetExport() throws IOException {
        // 1. 准备测试数据
        List<UserExcelVO> devTeam = createDepartmentData("研发部");
        List<UserExcelVO> salesTeam = createDepartmentData("销售部");

        // 2. 创建输出文件
        File outputFile = tempDir.resolve("multi_sheet_export.xlsx").toFile();

        // 3. 执行多 Sheet 导出
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            var excelWriter = FastExcelFactory.write(fos, UserExcelVO.class).build();

            // 写入研发部数据
            var devSheet = FastExcelFactory.writerSheet(0, "研发部").build();
            excelWriter.write(devTeam, devSheet);

            // 写入销售部数据
            var salesSheet = FastExcelFactory.writerSheet(1, "销售部").build();
            excelWriter.write(salesTeam, salesSheet);

            excelWriter.finish();
        }

        // 4. 验证
        assertTrue(outputFile.exists(), "导出文件应该存在");
        assertTrue(outputFile.length() > 0, "导出文件不应为空");

        System.out.println("多Sheet导出成功，文件路径: " + outputFile.getAbsolutePath());
    }

    /**
     * 创建测试数据
     */
    private List<UserExcelVO> createTestData() {
        List<UserExcelVO> list = new ArrayList<>();

        list.add(UserExcelVO.builder()
                            .id(1L)
                            .username("张三")
                            .email("zhangsan@example.com")
                            .phone("13800138001")
                            .gender("0")
                            .status("正常")
                            .department("研发部")
                            .createTime(LocalDateTime.now())
                            .build());

        list.add(UserExcelVO.builder()
                            .id(2L)
                            .username("李四")
                            .email("lisi@example.com")
                            .phone("13800138002")
                            .gender("1")
                            .status("正常")
                            .department("研发部")
                            .createTime(LocalDateTime.now().minusDays(1))
                            .build());

        list.add(UserExcelVO.builder()
                            .id(3L)
                            .username("王五")
                            .email("wangwu@example.com")
                            .phone("13800138003")
                            .gender("0")
                            .status("禁用")
                            .department("销售部")
                            .createTime(LocalDateTime.now().minusDays(2))
                            .build());

        return list;
    }

    /**
     * 创建指定部门的测试数据
     */
    private List<UserExcelVO> createDepartmentData(String department) {
        List<UserExcelVO> list = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            list.add(UserExcelVO.builder()
                                .id((long) i)
                                .username(department + "员工" + i)
                                .email("user" + i + "@example.com")
                                .phone("1380013800" + i)
                                .gender(String.valueOf(i % 2))
                                .status("正常")
                                .department(department)
                                .createTime(LocalDateTime.now().minusDays(i))
                                .build());
        }

        return list;
    }

}
