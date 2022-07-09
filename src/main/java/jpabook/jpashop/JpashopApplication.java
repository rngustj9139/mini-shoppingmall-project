package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

	@Bean // OrderSimpleApiController의 v1을 위해 사용됨, LAZY 로딩은 가짜 프록시 객체를 가져오므로 오류가 발생해 이걸 사용해야한다.
	Hibernate5Module hibernate5Module() {
		Hibernate5Module hibernate5Module = new Hibernate5Module();
//		hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true); // LAZY 로딩을 유지하면서 진짜 DB에 접근해 데이터를 가저오겠다.

		return hibernate5Module;
	}

}
