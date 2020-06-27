package com.livo.companies.cassandra;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.livo.companies.Companies;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraHelper.class);
    private static final SecureRandomNumberGenerator RANDOM_NUMBER_GENERATOR = new SecureRandomNumberGenerator();
    private static final int HASH_ITERATIONS = 3;
    private static Cluster cluster;
    private static Session session;
    private static PreparedStatement selectAllCompaniesStatement;
    private static PreparedStatement selectCompanyStatement;
    private static PreparedStatement updateCompanyStatement;
    private static PreparedStatement deleteCompanyStatement;
    private static PreparedStatement insertCompanyStatement;
    private static PreparedStatement updateCompanyPasswordStatement;
    private static PreparedStatement selectCompanyIdStatement;

    static {

        RANDOM_NUMBER_GENERATOR.setDefaultNextBytesSize(32);
    }

    private CassandraHelper() {
    }

    static void connect(String host, int port) {

        // Check if we are already connected
        if (cluster != null && !cluster.isClosed()) {

            LOGGER.debug("Already connected to the Cassandra cluster, ignoring...");

            return;
        }

        LOGGER.debug("Connecting to the Cassandra cluster at '{}:{}'...", host, port);

        cluster = Cluster.builder().addContactPoint(host).withPort(port).withoutJMXReporting().build();

        LOGGER.debug("Initiating session...");

        session = cluster.connect();

        try {

            LOGGER.debug("Creating KEYSPACE IF NOT EXISTS security  ...");

            session.execute("CREATE KEYSPACE IF NOT EXISTS security WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': 1 };");

            LOGGER.debug("Creating TABLE IF NOT EXISTS security.companies...");

            session.execute("CREATE TABLE IF NOT EXISTS security.companies (record_id int, company_id text, company_title text, create_date bigint, create_user int, login_perm boolean, password text, password_salt text, PRIMARY KEY (record_id));");
            session.execute("CREATE INDEX IF NOT EXISTS company_ids ON security.companies (company_id);");

            LOGGER.debug("Preparing statements...");

            selectAllCompaniesStatement = session.prepare("SELECT * FROM security.companies;");
            selectCompanyStatement = session.prepare("SELECT * FROM security.companies WHERE record_id = ?;");
            insertCompanyStatement = session.prepare("INSERT INTO security.companies (record_id, company_id, company_title, create_date, create_user, login_perm, password, password_salt) VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
            updateCompanyStatement = session.prepare("UPDATE security.companies SET company_id = ?, company_title = ?, create_date = ?, create_user = ?, login_perm = ? WHERE record_id = ?;");
            deleteCompanyStatement = session.prepare("DELETE FROM security.companies WHERE record_id = ?;");
            selectCompanyIdStatement = session.prepare("SELECT * FROM security.companies WHERE company_id = ?;");

            updateCompanyPasswordStatement = session.prepare("UPDATE security.companies SET company_id = ?, company_title = ?, create_date = ?, create_user = ?, login_perm = ?, password = ?, password_salt = ? WHERE record_id = ?;");

            ResultSet resultSet = session.execute(selectCompanyIdStatement.bind("LivoAny"));
            if(resultSet.all().size() == 0){
                
                LOGGER.debug("Inserting LivoAny and LivoUsers companies...");
                
                Companies company = new Companies("LivoAny".hashCode(), "LivoAny", "LivoAny", null, new Date().getTime(), false);
                insertCompany(company, "LivoAny");
                company = new Companies("LivoUsers".hashCode(), "LivoUsers", "LivoUsers", null, new Date().getTime(), false);
                insertCompany(company, "LivoUsers");
                
            }
            
        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);

            LOGGER.error("Failed to connect and setup the Cassandra server.");

            if (!cluster.isClosed()) {
                cluster.close();
            }

            cluster = null;
        }
    }

    static void disconnect() {

        LOGGER.debug("Closing session...");

        if (session != null && !session.isClosed()) {
            session.close();
        }

        LOGGER.debug("Disconnecting from the Cassandra cluster...");

        if (cluster != null && !cluster.isClosed()) {
            cluster.close();
        }
    }

    static Set<Companies> listCompanies() {
        
        LOGGER.debug("list all companies...");

        HashSet<Companies> results = new HashSet<>();

        ResultSet resultSet = session.execute(selectAllCompaniesStatement.bind());

        Companies company;

        for (Row row : resultSet) {

            company = createCompany(row);

            if (company != null) {

                results.add(company);
            }
        }

        return Collections.unmodifiableSet(results);
    }

    static Companies findCompany(Integer recordId) {

        if (recordId == null) {

            LOGGER.debug("Company Record ID to find is null; returning null...");

            return null;
        }

        LOGGER.debug("Looking up for company with Record ID '{}'...", recordId);

        BoundStatement statement = selectCompanyStatement.bind(recordId);

        Row row = session.execute(statement).one();

        return createCompany(row);
    }

    static void insertCompany(Companies company, String password) {

        Integer recordId = company.getCompanyId().hashCode();
        String companyId = company.getCompanyId();
        String companyTitle = company.getCompanyTitle();
        Long createDate = company.getCreateDate();
        Integer createUser = company.getCreateUser();
        Boolean loginPerm = company.getLoginPerm();

        String passwordSalt = generateRandomSalt();
        String hashedPassword = hashPassword(password, passwordSalt);

        BoundStatement statement = insertCompanyStatement.bind(recordId, companyId, companyTitle, createDate, createUser, loginPerm, hashedPassword, passwordSalt);

        session.execute(statement);
    }

    static void deleteCompany(Integer recordId) {

        BoundStatement statement = deleteCompanyStatement.bind(recordId);

        session.execute(statement);
    }

    static void updateCompany(Companies company) {

        BoundStatement statement = updateCompanyStatement.bind(company.getCompanyId(), company.getCompanyTitle(), company.getCreateDate(), company.getLoginPerm(), company.getCreateUser());

        session.execute(statement);
    }

    static void updateCompanyPassword(Companies company, String password) {

        String newSalt = generateRandomSalt();
        String newPassword = hashPassword(password, newSalt);

        String companyId = company.getCompanyId();
        String companyTitle = company.getCompanyTitle();
        Long createDate = company.getCreateDate();
        Integer createUser = company.getCreateUser();
        Boolean loginPerm = company.getLoginPerm();

        BoundStatement statement = updateCompanyPasswordStatement.bind(companyId, companyTitle, createDate, createUser, loginPerm, newPassword, newSalt, company.getRecordId());

        session.execute(statement);
    }

    private static String hashPassword(String password, String salt) {

        ByteSource passwordSource = new SimpleByteSource(password);
        ByteSource saltSource = new SimpleByteSource(Hex.decode(salt));

        Sha512Hash hash = new Sha512Hash(passwordSource, saltSource, HASH_ITERATIONS);

        return hash.toHex();
    }

    private static String generateRandomSalt() {

        return RANDOM_NUMBER_GENERATOR.nextBytes().toHex();
    }

    private static Companies createCompany(Row row) {

        if (row == null) {

            LOGGER.debug("Cassandra row is null.");

            return null;
        }

        if (row.isNull("record_id")) {

            LOGGER.error("Company Record ID column of Cassandra row is null.");

            return null;
        }
        
        if (row.isNull("company_id")) {

            LOGGER.error("Company ID column of Cassandra row is null.");

            return null;
        }

        Integer recordId = row.getInt("record_id");
        String companyId = row.getString("company_id");
        String companyTitle = row.getString("company_title");
        Long createDate = row.getLong("create_date");
        Integer createUser = row.getInt("create_user");
        Boolean loginPerm = row.getBool("login_perm");

        Companies company = new Companies(recordId, companyId, companyTitle, createUser, createDate, loginPerm);

        return company;
    }

}
