package com.glowxq.core.util.mapstruct;

import com.glowxq.common.core.util.MapStructUtils;
import com.glowxq.common.core.util.SpringUtils;
import io.github.linpeilie.annotations.ComponentModelConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MapStructUtils 单元测试
 *
 * @author glowxq
 * @since 2025-01-22
 */
@SpringBootTest(classes = MapStructUtilsTest.TestConfig.class)
class MapStructUtilsTest {

    @Test
    @DisplayName("单对象转换：DTO -> Entity")
    void testConvert_DtoToEntity() {
        // Given
        UserDTO dto = UserDTO.builder()
                             .username("zhangsan")
                             .email("zhangsan@example.com")
                             .age(25)
                             .status(1)
                             .build();

        // When
        User user = MapStructUtils.convert(dto, User.class);
        System.out.println(user);

        // Then
        assertNotNull(user);
        assertEquals("zhangsan", user.getUsername());
        assertEquals("zhangsan@example.com", user.getEmail());
        assertEquals(25, user.getAge());
        assertEquals(1, user.getStatus());
        assertNull(user.getId()); // DTO 中没有 id 字段
        assertNull(user.getCreateTime()); // DTO 中没有 createTime 字段
    }

    // ==================== convert(source, targetClass) 测试 ====================
    @Test
    @DisplayName("单对象转换：Entity -> VO")
    void testConvert_EntityToVo() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                        .id(1L)
                        .username("lisi")
                        .email("lisi@example.com")
                        .age(30)
                        .status(1)
                        .createTime(now)
                        .build();

        // When
        UserVO vo = MapStructUtils.convert(user, UserVO.class);

        // Then
        assertNotNull(vo);
        assertEquals(1L, vo.getId());
        assertEquals("lisi", vo.getUsername());
        assertEquals("lisi@example.com", vo.getEmail());
        assertEquals(30, vo.getAge());
        assertEquals(1, vo.getStatus());
        assertEquals(now, vo.getCreateTime());
    }
    @Test
    @DisplayName("单对象转换：源对象为 null 时返回 null")
    void testConvert_NullSource() {
        // Given
        UserDTO nullDto = null;

        // When
        User user = MapStructUtils.convert(nullDto, User.class);

        // Then
        assertNull(user);
    }
    @Test
    @DisplayName("更新已有对象：DTO -> Entity（保留原有字段）")
    void testConvert_UpdateExistingEntity() {
        // Given
        LocalDateTime originalTime = LocalDateTime.of(2025, 1, 1, 10, 0, 0);
        User existingUser = User.builder()
                                .id(100L)
                                .username("oldname")
                                .email("old@example.com")
                                .age(20)
                                .status(0)
                                .createTime(originalTime)
                                .build();

        UserDTO updateDto = UserDTO.builder()
                                   .username("newname")
                                   .email("new@example.com")
                                   .age(25)
                                   .status(1)
                                   .build();

        // When
        User updatedUser = MapStructUtils.convert(updateDto, existingUser);

        // Then
        assertSame(existingUser, updatedUser); // 确保是同一个对象
        assertEquals(100L, updatedUser.getId()); // id 保持不变
        assertEquals("newname", updatedUser.getUsername()); // 更新
        assertEquals("new@example.com", updatedUser.getEmail()); // 更新
        assertEquals(25, updatedUser.getAge()); // 更新
        assertEquals(1, updatedUser.getStatus()); // 更新
        assertEquals(originalTime, updatedUser.getCreateTime()); // createTime 保持不变
    }

    // ==================== convert(source, target) 测试 ====================
    @Test
    @DisplayName("更新已有对象：源对象为 null 时返回目标对象")
    void testConvert_UpdateWithNullSource() {
        // Given
        User existingUser = User.builder()
                                .id(1L)
                                .username("test")
                                .build();

        // When
        User result = MapStructUtils.convert(null, existingUser);

        // Then
        assertSame(existingUser, result);
        assertEquals("test", result.getUsername());
    }
    @Test
    @DisplayName("更新已有对象：目标对象为 null 时返回 null")
    void testConvert_UpdateWithNullTarget() {
        // Given
        UserDTO dto = UserDTO.builder()
                             .username("test")
                             .build();

        // When
        User result = MapStructUtils.convert(dto, (User) null);

        // Then
        assertNull(result);
    }
    @Test
    @DisplayName("列表转换：List<Entity> -> List<VO>")
    void testConvert_ListConversion() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        List<User> users = Arrays.asList(
            User.builder().id(1L).username("user1").email("user1@example.com").age(20).status(1).createTime(now).build(),
            User.builder().id(2L).username("user2").email("user2@example.com").age(25).status(1).createTime(now).build(),
            User.builder().id(3L).username("user3").email("user3@example.com").age(30).status(0).createTime(now).build()
        );

        // When
        List<UserVO> voList = MapStructUtils.convert(users, UserVO.class);

        // Then
        assertNotNull(voList);
        assertEquals(3, voList.size());

        assertEquals(1L, voList.get(0).getId());
        assertEquals("user1", voList.get(0).getUsername());

        assertEquals(2L, voList.get(1).getId());
        assertEquals("user2", voList.get(1).getUsername());

        assertEquals(3L, voList.get(2).getId());
        assertEquals("user3", voList.get(2).getUsername());
    }

    // ==================== convert(sourceList, targetClass) 测试 ====================
    @Test
    @DisplayName("列表转换：空列表返回空列表")
    void testConvert_EmptyList() {
        // Given
        List<User> emptyList = Arrays.asList();

        // When
        List<UserVO> result = MapStructUtils.convert(emptyList, UserVO.class);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    @Test
    @DisplayName("列表转换：null 列表返回 null")
    void testConvert_NullList() {
        // When
        List<UserVO> result = MapStructUtils.convert((List<User>) null, UserVO.class);

        // Then
        assertNull(result);
    }
    @Test
    @DisplayName("兼容性方法 copy() 测试")
    @SuppressWarnings("deprecation")
    void testCopy_Deprecated() {
        // Given
        UserDTO dto = UserDTO.builder()
                             .username("copytest")
                             .email("copy@example.com")
                             .build();

        // When
        User user = MapStructUtils.copy(dto, User.class);

        // Then
        assertNotNull(user);
        assertEquals("copytest", user.getUsername());
        assertEquals("copy@example.com", user.getEmail());
    }

    // ==================== 兼容性方法测试（已废弃但需确保可用）====================
    @Test
    @DisplayName("兼容性方法 copyList() 测试")
    @SuppressWarnings("deprecation")
    void testCopyList_Deprecated() {
        // Given
        List<User> users = Arrays.asList(
            User.builder().id(1L).username("user1").build(),
            User.builder().id(2L).username("user2").build()
        );

        // When
        List<UserVO> voList = MapStructUtils.copyList(users, UserVO.class);

        // Then
        assertNotNull(voList);
        assertEquals(2, voList.size());
        assertEquals("user1", voList.get(0).getUsername());
        assertEquals("user2", voList.get(1).getUsername());
    }
    @Test
    @DisplayName("双向转换：DTO -> Entity -> VO")
    void testBidirectionalConversion() {
        // Given
        UserDTO dto = UserDTO.builder()
                             .username("bidirectional")
                             .email("bi@example.com")
                             .age(35)
                             .status(1)
                             .build();

        // When: DTO -> Entity
        User user = MapStructUtils.convert(dto, User.class);
        user.setId(999L);
        user.setCreateTime(LocalDateTime.now());

        // Then: Entity -> VO
        UserVO vo = MapStructUtils.convert(user, UserVO.class);

        // Verify
        assertEquals(999L, vo.getId());
        assertEquals("bidirectional", vo.getUsername());
        assertEquals("bi@example.com", vo.getEmail());
        assertEquals(35, vo.getAge());
        assertEquals(1, vo.getStatus());
        assertNotNull(vo.getCreateTime());
    }

    // ==================== 双向转换测试 ====================

    /**
     * 测试配置类
     */
    @Configuration
    @ComponentModelConfig(componentModel = "spring")
    @Import(SpringUtils.class)
    @ComponentScan(
        basePackages = {
            "io.github.linpeilie",
            "com.glowxq.core.util.mapstruct"
        })
    static class TestConfig {
    }

}
