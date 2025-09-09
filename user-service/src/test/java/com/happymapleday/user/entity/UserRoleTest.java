package com.happymapleday.user.entity;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class UserRoleTest {

    @Test
    void testUserRoleValues() {
        // given & when & then
        assertThat(UserRole.values()).hasSize(2);
        assertThat(UserRole.values()).containsExactly(UserRole.NORMAL, UserRole.ADMIN);
    }

    @Test
    void testUserRoleDescriptions() {
        // given & when & then
        assertThat(UserRole.NORMAL.getDescription()).isEqualTo("일반 사용자");
        assertThat(UserRole.ADMIN.getDescription()).isEqualTo("관리자");
    }

    @Test
    void testIsAdminMethod() {
        // given & when & then
        assertThat(UserRole.NORMAL.isAdmin()).isFalse();
        assertThat(UserRole.ADMIN.isAdmin()).isTrue();
    }

    @Test
    void testIsNormalMethod() {
        // given & when & then
        assertThat(UserRole.NORMAL.isNormal()).isTrue();
        assertThat(UserRole.ADMIN.isNormal()).isFalse();
    }

    @Test
    void testValueOf() {
        // given & when & then
        assertThat(UserRole.valueOf("NORMAL")).isEqualTo(UserRole.NORMAL);
        assertThat(UserRole.valueOf("ADMIN")).isEqualTo(UserRole.ADMIN);
        
        // Invalid value should throw exception
        assertThatThrownBy(() -> UserRole.valueOf("INVALID"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testEnumName() {
        // given & when & then
        assertThat(UserRole.NORMAL.name()).isEqualTo("NORMAL");
        assertThat(UserRole.ADMIN.name()).isEqualTo("ADMIN");
    }
}

