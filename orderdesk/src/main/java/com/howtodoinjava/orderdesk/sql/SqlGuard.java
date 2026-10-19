package com.howtodoinjava.orderdesk.sql;

import java.util.Locale;
import java.util.regex.Pattern;

final class SqlGuard {

    private static final Pattern FORBIDDEN = Pattern.compile(
        "\\b(insert|update|delete|merge|drop|alter|create|truncate|grant|revoke|call)\\b");

    private SqlGuard() {
    }

    // Removes Markdown fences and a trailing semicolon that small models often add
    static String clean(String sql) {
        return sql.replace("```sql", "").replace("```", "").trim().replaceAll(";\\s*$", "");
    }

    // Rejects anything that is not one plain SELECT statement
    static void check(String sql) {
        String lower = sql.toLowerCase(Locale.ROOT);
        if (!lower.startsWith("select")) {
            throw new IllegalArgumentException("Only SELECT statements are allowed: " + sql);
        }
        if (lower.contains(";")) {
            throw new IllegalArgumentException("Only one statement is allowed: " + sql);
        }
        if (FORBIDDEN.matcher(lower).find()) {
            throw new IllegalArgumentException("The statement changes data: " + sql);
        }
    }
}
