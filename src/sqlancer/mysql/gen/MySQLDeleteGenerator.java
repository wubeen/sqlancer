package sqlancer.mysql.gen;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import sqlancer.Query;
import sqlancer.QueryAdapter;
import sqlancer.Randomly;
import sqlancer.mysql.MySQLErrors;
import sqlancer.mysql.MySQLGlobalState;
import sqlancer.mysql.MySQLSchema.MySQLTable;
import sqlancer.mysql.MySQLVisitor;

public class MySQLDeleteGenerator {

    private final StringBuilder sb = new StringBuilder();
    private final MySQLGlobalState globalState;

    public MySQLDeleteGenerator(MySQLGlobalState globalState) {
        this.globalState = globalState;
    }

    public static Query delete(MySQLGlobalState globalState) {
        return new MySQLDeleteGenerator(globalState).generate();
    }

    private Query generate() {
        MySQLTable randomTable = globalState.getSchema().getRandomTable();
        MySQLExpressionGenerator gen = new MySQLExpressionGenerator(globalState).setColumns(randomTable.getColumns());
        Set<String> errors = new HashSet<>();
        sb.append("DELETE");
        if (Randomly.getBoolean()) {
            sb.append(" LOW_PRIORITY");
        }
        if (Randomly.getBoolean()) {
            sb.append(" QUICK");
        }
        if (Randomly.getBoolean()) {
            sb.append(" IGNORE");
        }
        // TODO: support partitions
        sb.append(" FROM ");
        sb.append(randomTable.getName());
        if (Randomly.getBoolean()) {
            sb.append(" WHERE ");
            sb.append(MySQLVisitor.asString(gen.generateExpression()));
            MySQLErrors.addExpressionErrors(errors);
        }
        errors.addAll(Arrays.asList("doesn't have this option",
                "Truncated incorrect DOUBLE value" /*
                                                    * ignore as a workaround for https://bugs.mysql.com/bug.php?id=95997
                                                    */, "Truncated incorrect INTEGER value",
                "Truncated incorrect DECIMAL value", "Data truncated for functional index"));
        // TODO: support ORDER BY
        return new QueryAdapter(sb.toString(), errors);
    }

}
