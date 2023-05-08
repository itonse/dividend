package com.itonse.dividend.config;

import org.apache.commons.collections4.Trie;
import org.apache.commons.collections4.trie.PatriciaTrie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean    // 빈으로 트라이 관리
    public Trie<String, String> trie() {
        return new PatriciaTrie<>();
    }
}
