package jikgong.global.config;

import com.amazonaws.services.s3.AmazonS3Client;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

public class MockS3Config extends S3Config{
    @Bean
    @Primary
    @Override
    public AmazonS3Client amazonS3Client() {
        return Mockito.mock(AmazonS3Client.class);
    }
}
