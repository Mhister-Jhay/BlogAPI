package com.blog;

import com.blog.model.Role;
import com.blog.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class JhayBlogApplication implements ApplicationRunner {
	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}
	private final RoleRepository roleRepository;
	public static void main(String[] args) {
		SpringApplication.run(JhayBlogApplication.class, args);
	}


	@Override
	public void run(ApplicationArguments args) throws Exception {
		Role role1 = Role.builder().name("SUPER_ADMIN").build();
		Role role2 = Role.builder().name("ADMIN").build();
		Role role3 = Role.builder().name("USER").build();
		roleRepository.save(role1);
		roleRepository.save(role2);
		roleRepository.save(role3);
	}
}
