package sqlancer.sqlite3.gen.ddl;

import java.util.Arrays;

import sqlancer.IgnoreMeException;
import sqlancer.Query;
import sqlancer.QueryAdapter;
import sqlancer.Randomly;
import sqlancer.sqlite3.SQLite3Provider.SQLite3GlobalState;

public final class SQLite3DropTableGenerator {

    private SQLite3DropTableGenerator() {
    }

    public static Query dropTable(SQLite3GlobalState globalState) {
        if (globalState.getSchema().getTables(t -> !t.isView()).size() == 1) {
            throw new IgnoreMeException();
        }
        StringBuilder sb = new StringBuilder("DROP TABLE ");
        if (Randomly.getBoolean()) {
            sb.append("IF EXISTS ");
        }
        sb.append(globalState.getSchema().getRandomTableOrBailout(t -> !t.isView()).getName());
        return new QueryAdapter(sb.toString(),
                Arrays.asList("[SQLITE_ERROR] SQL error or missing database (foreign key mismatch",
                        "Abort due to constraint violation (FOREIGN KEY constraint failed)",
                        "SQL error or missing database"),
                true);

    }

}
