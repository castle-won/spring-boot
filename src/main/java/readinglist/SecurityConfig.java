package readinglist;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Example;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	private ReaderRepository readerRepository;
	@Autowired
	private UserDetailsService userService;
	
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/").access("hasRole('READER')")	//READER 권한 필요
				.antMatchers("/**").permitAll()
			
			.and()
			
			.formLogin()
				.loginPage("/login") //로그인 폼 경로 설정
				.failureUrl("/login?error=true");
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth.userDetailsService(new UserDetailsService() {
			
			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				Reader reader = new Reader();
				reader.setUsername(username);
				Example<Reader> userExample = Example.of(reader);
				return readerRepository.findOne(userExample).orElse(null);
			}
		}).passwordEncoder(NoOpPasswordEncoder.getInstance());
	}
}
