package com.objects.marketbridge;

import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

//@EnableJpaAuditing -> JpaAuditingConfig.class 파일로 (common/config) 따로 분리하였습니다 (컨트롤러 테스트시 문제발생)
// [주의] 만일 @SpringBootTest 대신 @DataJpaTest 을 사용하신다면
// JpaAuditing관련 에러가 발생하므로,
// 해당 테스트 코드클래스에는 @Import(JpaAuditingConfig.class) 를 해주셔야합니다.
@SpringBootApplication
@EnableScheduling
public class MarketbridgeApplication {
	public static void main(String[] args) {
		SpringApplication.run(MarketbridgeApplication.class, args);
	}
	@Bean
	Hibernate5JakartaModule hibernate5Module() {
		return new Hibernate5JakartaModule();
	}

	@PostConstruct
	public void setTime() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
}