package edu.northwestern.bioinformatics.bering.dialect;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This is based on the similar class from Hibernate
 *
 * @author Rhett Sutphin
 */
public class DialectFactory {
    private static final Map<String, String> SUBSTRING_MAPS
        = new LinkedHashMap<String, String>();
    static {
        // Use names instead of classes to avoid pulling in hibernate, etc., unless necessary
        SUBSTRING_MAPS.put("postgres", DialectFactory.class.getPackage().getName() + ".PostgreSQL");
        SUBSTRING_MAPS.put("oracle", DialectFactory.class.getPackage().getName() + ".Oracle");
        SUBSTRING_MAPS.put("hsql", DialectFactory.class.getPackage().getName() + ".Hsqldb");
        SUBSTRING_MAPS.put("sql server", DialectFactory.class.getPackage().getName() + ".SqlServer");
    }

    public static Dialect buildDialect(String dialectClassName) {
        String d = dialectClassName.trim();
        try {
            return (Dialect) Class.forName(d).newInstance();
        } catch (InstantiationException e) {
            throw new DialectException("Could not create an instance of dialect " + d, e);
        } catch (IllegalAccessException e) {
            throw new DialectException("Could not create an instance of dialect " + d, e);
        } catch (ClassNotFoundException e) {
            throw new DialectException("Could not find dialect class " + d, e);
        } catch (ClassCastException e) {
            throw new DialectException("Class " + d + " does not implement " + Dialect.class.getName(), e);
        }
    }

    public static Dialect guessDialect(String databaseProductName) {
        for (String nameSubstring : SUBSTRING_MAPS.keySet()) {
            if (databaseProductName.toLowerCase().indexOf(nameSubstring) >= 0) {
                return buildDialect(SUBSTRING_MAPS.get(nameSubstring));
            }
        }
        throw new UnguessableDialectException(databaseProductName);
    }
}
