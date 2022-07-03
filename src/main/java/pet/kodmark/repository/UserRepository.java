package pet.kodmark.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import pet.kodmark.model.Role;

public class UserRepository {

    private final static String CONFIGURE_FILE_NAME = "hibernate.cfg.xml";

    private static SessionFactory sessionFactory;

    public static void setUpDB() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure(CONFIGURE_FILE_NAME).build();
        Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
        sessionFactory = metadata.getSessionFactoryBuilder().build();
        fillRoleDB();
    }

    public static Session openConnection() {
        if (sessionFactory.isClosed()){
            setUpDB();
        }
        return sessionFactory.openSession();
    }


    public static void closeConnection() {
        sessionFactory.close();
    }

    public static void fillRoleDB(){
        Session session = openConnection();
        Transaction transaction = session.beginTransaction();
        Role role1 = new Role();
        role1.setName("админ");
        session.delete(role1);
        session.save(role1);
        Role role2 = new Role();
        role2.setName("оператор");
        session.delete(role2);
        session.save(role2);
        Role role3 = new Role();
        role3.setName("аналитик");
        session.delete(role3);
        session.save(role3);
        Role role4 = new Role();
        role4.setName("разработчик");
        session.delete(role4);
        session.save(role4);
        transaction.commit();
        session.close();
    }
}






