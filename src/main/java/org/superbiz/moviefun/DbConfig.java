package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DbConfig {

    @Bean
    public DataSource albumsDataSource(
            @Value("${moviefun.datasources.albums.url}") String url,
            @Value("${moviefun.datasources.albums.username}") String username,
            @Value("${moviefun.datasources.albums.password}") String password
    ) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        HikariDataSource ds = new HikariDataSource();
        ds.setDataSource(dataSource);
        return ds;
    }

    @Bean
    public DataSource moviesDataSource(
            @Value("${moviefun.datasources.movies.url}") String url,
            @Value("${moviefun.datasources.movies.username}") String username,
            @Value("${moviefun.datasources.movies.password}") String password
    ) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(url);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        HikariDataSource ds = new HikariDataSource();
        ds.setDataSource(dataSource);
        return ds;
    }

    @Bean
    public HibernateJpaVendorAdapter hibernateAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        adapter.setGenerateDdl(true);
        adapter.setDatabase(Database.MYSQL);
        return adapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean albumsLocalContainerEntityManagerFactoryBean(DataSource albumsDataSource, HibernateJpaVendorAdapter adapter) {
        LocalContainerEntityManagerFactoryBean albumsBean = new LocalContainerEntityManagerFactoryBean();
        albumsBean.setDataSource(albumsDataSource);
        albumsBean.setJpaVendorAdapter(adapter);
        albumsBean.setPackagesToScan("org.superbiz.moviefun");
        albumsBean.setPersistenceUnitName("albums");
        return albumsBean;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean moviesLocalContainerEntityManagerFactoryBean(DataSource moviesDataSource, HibernateJpaVendorAdapter adapter) {
        LocalContainerEntityManagerFactoryBean moviesBean = new LocalContainerEntityManagerFactoryBean();
        moviesBean.setDataSource(moviesDataSource);
        moviesBean.setJpaVendorAdapter(adapter);
        moviesBean.setPackagesToScan("org.superbiz.moviefun");
        moviesBean.setPersistenceUnitName("movies");
        return moviesBean;
    }

    @Bean
    public PlatformTransactionManager albumsPlatformTransactionManager(@Qualifier("albumsLocalContainerEntityManagerFactoryBean") LocalContainerEntityManagerFactoryBean albumsLocalContainerEntityManagerFactoryBean) {
        JpaTransactionManager manager = new JpaTransactionManager();
        manager.setEntityManagerFactory(albumsLocalContainerEntityManagerFactoryBean.getObject());
        return manager;
    }

    @Bean
    public PlatformTransactionManager moviesPlatformTransactionManager(@Qualifier("moviesLocalContainerEntityManagerFactoryBean") LocalContainerEntityManagerFactoryBean moviesLocalContainerEntityManagerFactoryBean) {
        JpaTransactionManager manager = new JpaTransactionManager();
        manager.setEntityManagerFactory(moviesLocalContainerEntityManagerFactoryBean.getObject());
        return manager;
    }

}
