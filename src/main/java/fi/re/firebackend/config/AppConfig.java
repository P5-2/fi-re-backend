package fi.re.firebackend.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

// Database 설정
@Configuration
@MapperScan("fi.re.firebackend.dao")
public class AppConfig {

    @Bean
    public DataSource dataSource() {
        System.out.println("~~~ AppConfig dataSource()");

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/firedb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8&useUnicode=true");
        dataSource.setUsername("root");
        dataSource.setPassword("1234");
        dataSource.setInitialSize(5);
        dataSource.setMaxTotal(10);
        return dataSource;
    }

    @Bean
    public RestTemplate restTemplate() {
        System.out.println("~~~ AppConfig resttemplate");
        return new RestTemplate();
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        System.out.println("~~~ AppConfig sqlSessionFactory()");

        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:mapper/**/*.xml");

        // 디버깅: 로드된 리소스 출력
        for (Resource resource : resources) {
            System.out.println("Found resource: " + resource.getFilename());
        }

        sessionFactory.setMapperLocations(resources);
        sessionFactory.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);

        System.out.println("done");
        return sessionFactory.getObject();
    }


    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}

