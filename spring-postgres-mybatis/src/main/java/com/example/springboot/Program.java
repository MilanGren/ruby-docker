package com.example.springboot;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.io.StringReader;
import java.sql.Connection;
import java.util.UUID;

@Component
public class Program {

    private final FrontaMapper frontaMapper;
    
    private final PlatformTransactionManager transactionManager;
    private final TransactionTemplate transactionTemplate;

    private SqlSessionFactory session;
    private Connection connection;

    public Program(FrontaMapper frontaMapper, DataSource dataSource, PlatformTransactionManager transactionManager, TransactionTemplate transactionTemplate) throws Exception {
        this.frontaMapper = frontaMapper;
		/*SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
		factoryBean.setDataSource(dataSource);
		session = factoryBean.getObject();*/

        connection = dataSource.getConnection(); //session.openSession().getConnection() ;

        //connection = session.openSession().getConnection();

        connection.setAutoCommit(false);
        this.transactionManager = transactionManager;

        this.transactionTemplate = transactionTemplate;

        //dataSource.getConnection().setAutoCommit(true);
    }

    // pokud se vyhodi exception, potom se .insert neaplikuje
    public String springPostgresMybatis() {
        TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            frontaMapper.insert(new FrontaMember(uuid(), uuid(), uuid(), uuid()));
            if (true) {
                throw new RuntimeException("this is my testing runtime exception (rollback should be invoked)");
            }
            transactionManager.commit(txStatus);
        } catch (Exception e) {
            transactionManager.rollback(txStatus);
            e.printStackTrace();
        }
        return "spring-postgres-mybatis ok";
    }


    @Transactional // pokud se vyhodi exception, potom se .insert neaplikuje (diky @Transactional)
    public String springPostgresMybatisTransactional() {
        frontaMapper.insert(new FrontaMember(uuid(), uuid(), uuid(), uuid()));
        if (true) {
            throw new RuntimeException("this is my testing runtime exception (rollback should be invoked)");
        }

        return "spring-postgres-mybatis-transactional ok";
    }

    // zatim lze provest commit jen connection.commit(). Skrze transactionManager mi to zatim nejde...
    public String springPostgres() {

        MyScriptRunner runner = new MyScriptRunner(connection);

        TransactionStatus txStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());

        String query = "INSERT INTO fronta0 (noderef, edid, davkaid, status) VALUES('" + uuid() + "' , '" + uuid() + "' , '" + uuid() + "' , '" + uuid() + "') ;";
        runner.runScript(new StringReader(query));

        //runner.commit();

        transactionManager.commit(txStatus);

        return "spring-postgres ok";
    }

    public String springPostgresMybatisTransactionTemplate() {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                try {
                    frontaMapper.insert(new FrontaMember(uuid(), uuid(), uuid(), uuid()));
                    if (true) {
                        throw new RuntimeException("this is my testing runtime exception (rollback should be invoked)");
                    }
                } catch (Exception e) {
                    transactionStatus.setRollbackOnly();
                }
            }
        });
        return "spring-postgres-mybatis-transactionTemplate ok";
    }

    public String uuid() {
        return UUID.randomUUID().toString();
    }

}
