package com.hmall.service.impl;

import cn.hutool.core.bean.BeanUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

public class HutoolTest {

    @Test
    void name() {
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor(staticName = "of")
    static class User {
        Long id;
        String name;
    }

    @Data
    static class User2 {
        String id;
        String name;
    }
}
