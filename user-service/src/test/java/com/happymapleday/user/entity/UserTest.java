package com.happymapleday.user.entity;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class UserTest {

    @Test
    void testUserCreationWithDefaultRole() {
        // given
        String mainCharacterName = "testUser";
        String password = "password123";
        String nexonApiKey = "apiKey123";

        // when
        User user = new User(mainCharacterName, password, nexonApiKey);

        // then
        assertThat(user.getMainCharacterName()).isEqualTo(mainCharacterName);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getNexonApiKey()).isEqualTo(nexonApiKey);
        assertThat(user.getRole()).isEqualTo(UserRole.NORMAL);
        assertThat(user.isAdmin()).isFalse();
        assertThat(user.isNormal()).isTrue();
    }

    @Test
    void testUpdateRole() {
        // given
        User user = new User("testUser", "password", "apiKey");
        assertThat(user.getRole()).isEqualTo(UserRole.NORMAL);

        // when
        user.updateRole(UserRole.ADMIN);

        // then
        assertThat(user.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(user.isAdmin()).isTrue();
        assertThat(user.isNormal()).isFalse();
    }

    @Test
    void testUpdateRoleBackToNormal() {
        // given
        User user = new User("testUser", "password", "apiKey");
        user.updateRole(UserRole.ADMIN);
        assertThat(user.isAdmin()).isTrue();

        // when
        user.updateRole(UserRole.NORMAL);

        // then
        assertThat(user.getRole()).isEqualTo(UserRole.NORMAL);
        assertThat(user.isAdmin()).isFalse();
        assertThat(user.isNormal()).isTrue();
    }

    @Test
    void testIsAdminConvenienceMethod() {
        // given
        User normalUser = new User("normalUser", "password", "apiKey");
        User adminUser = new User("adminUser", "password", "apiKey");
        adminUser.updateRole(UserRole.ADMIN);

        // when & then
        assertThat(normalUser.isAdmin()).isFalse();
        assertThat(adminUser.isAdmin()).isTrue();
    }

    @Test
    void testIsNormalConvenienceMethod() {
        // given
        User normalUser = new User("normalUser", "password", "apiKey");
        User adminUser = new User("adminUser", "password", "apiKey");
        adminUser.updateRole(UserRole.ADMIN);

        // when & then
        assertThat(normalUser.isNormal()).isTrue();
        assertThat(adminUser.isNormal()).isFalse();
    }

    @Test
    void testUpdatePassword() {
        // given
        User user = new User("testUser", "oldPassword", "apiKey");
        String newPassword = "newPassword123";

        // when
        user.updatePassword(newPassword);

        // then
        assertThat(user.getPassword()).isEqualTo(newPassword);
    }

    @Test
    void testUpdateNexonApiKey() {
        // given
        User user = new User("testUser", "password", "oldApiKey");
        String newApiKey = "newApiKey123";

        // when
        user.updateNexonApiKey(newApiKey);

        // then
        assertThat(user.getNexonApiKey()).isEqualTo(newApiKey);
    }

    @Test
    void testUpdateMainCharacterName() {
        // given
        User user = new User("oldName", "password", "apiKey");
        String newName = "newName";

        // when
        user.updateMainCharacterName(newName);

        // then
        assertThat(user.getMainCharacterName()).isEqualTo(newName);
    }

    @Test
    void testRoleHandlesNullSafely() {
        // given
        User user = new User("testUser", "password", "apiKey");
        
        // when - 직접 role을 null로 설정 (실제로는 발생하지 않아야 하지만 안전성 테스트)
        user.updateRole(null);

        // then - null 체크가 있어야 함
        assertThat(user.getRole()).isNull();
        // 편의 메서드들이 null을 안전하게 처리하는지 확인
        assertThat(user.isAdmin()).isFalse();
        assertThat(user.isNormal()).isFalse();
    }
}

