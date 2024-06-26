package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class jpashopApplication {
	public static void main(String[] args) {SpringApplication.run(jpashopApplication.class, args);}
	      //지연 로딩은 프록시를 통해 날라감
	@Bean //Hibernate5JakartaModule은 기본적으로 초기화 된 프록시 객체만 노출, 초기화 되지 않은 프록시 객체는 노출 하지 않도록 한다
	Hibernate5JakartaModule hibernate5JakartaModule() {
		Hibernate5JakartaModule hibernate5JakartaModule = new Hibernate5JakartaModule();
		//hibernate5JakartaModule.configure(Hibernate5JakartaModule.Feature.FORCE_LAZY_LOADING, true);
		//강제로 모두 다 지연 로딩을 하도록 함 //엔티티가 모두 노출되므로 좋지 않은 방법이다
		return new Hibernate5JakartaModule();
	}
}
