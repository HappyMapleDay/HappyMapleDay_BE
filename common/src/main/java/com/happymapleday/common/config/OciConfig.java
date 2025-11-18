package com.happymapleday.common.config;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.InputStream;

@Slf4j
@Configuration
public class OciConfig {

    @Value("${oci.auth.user-ocid}")
    private String userOcid;

    @Value("${oci.auth.tenancy-ocid}")
    private String tenancyOcid;

    @Value("${oci.auth.fingerprint}")
    private String fingerprint;

    @Value("${oci.auth.private-key-path}")
    private String privateKeyPath;

    @Value("${oci.object-storage.region}")
    private String region;

    @Bean
    public AuthenticationDetailsProvider authenticationDetailsProvider() throws Exception {
        log.info("OCI 인증 설정 초기화 - Region: {}", region);
        
        InputStream privateKeyStream = new FileInputStream(privateKeyPath);
        
        return SimpleAuthenticationDetailsProvider.builder()
                .userId(userOcid)
                .tenantId(tenancyOcid)
                .fingerprint(fingerprint)
                .privateKeySupplier(() -> privateKeyStream)
                .region(com.oracle.bmc.Region.fromRegionCodeOrId(region))
                .build();
    }

    @Bean
    public ObjectStorage objectStorageClient(AuthenticationDetailsProvider provider) {
        log.info("ObjectStorage 클라이언트 생성");
        return ObjectStorageClient.builder()
                .build(provider);
    }
}


