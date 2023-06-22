package com.alibaba.druid.sql.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alibaba.druid.DbType;

public class MysqlUtils extends SQLParserUtils {

    public static List<String> sp(String sql, DbType dbType) {
        List<String> tables = new ArrayList<>();

        Lexer lexer = createLexer(sql, dbType);
        lexer.nextToken();



        for (; lexer.token != Token.EOF; ) {
            switch (lexer.token) {
                case SEMI:

                    System.out.println(lexer.token);

                    break;
                default:
                    break;
            }

            lexer.nextToken();

        }


        return new ArrayList<>(tables);
    }


    public static List<String> splitAndRemoveComment(String sql, DbType dbType) {
        if (dbType == null) {
            dbType = DbType.other;
        }

        boolean containsCommentAndSemi = false;
        {
            Lexer lexer = createLexer(sql, dbType);
            lexer.config(SQLParserFeature.SkipComments, false);
            lexer.config(SQLParserFeature.KeepComments, true);

            while (lexer.token != Token.EOF) {
                if (lexer.token == Token.LINE_COMMENT
                    || lexer.token == Token.MULTI_LINE_COMMENT
                    || lexer.token == Token.SEMI) {
                    containsCommentAndSemi = true;
                    break;
                }
                lexer.nextToken();
            }

            if (!containsCommentAndSemi) {
                return Collections.singletonList(sql);
            }
        }

        {
            Lexer lexer = createLexer(sql, dbType);
            lexer.nextToken();

            boolean script = false;
            if (dbType == DbType.odps && lexer.token == Token.VARIANT) {
                script = true;
            }

            if (script || lexer.identifierEquals("pai") || lexer.identifierEquals("jar")) {
                return Collections.singletonList(sql);
            }
        }

        List list = new ArrayList();

        Lexer lexer = createLexer(sql, dbType);
        lexer.config(SQLParserFeature.SkipComments, false);
        lexer.config(SQLParserFeature.KeepComments, true);

        boolean set = false, paiOrJar = false;
        int start = 0;
        Token token = lexer.token;
        for (; lexer.token != Token.EOF; ) {
            if (token == Token.SEMI) {
                int len = lexer.startPos - start;
                if (len > 0) {
                    String lineSql = sql.substring(start, lexer.startPos);
                    String splitSql = set
                        ? removeLeftComment(lineSql, dbType)
                        : removeComment(lineSql, dbType
                        ).trim();
                    if (!splitSql.isEmpty()) {
                        list.add(splitSql);
                    }
                }
                start = lexer.startPos + 1;
                set = false;
            } else if (token == Token.MULTI_LINE_COMMENT) {
                int len = lexer.startPos - start;
                if (len > 0) {
                    String splitSql = removeComment(
                        sql.substring(start, lexer.startPos),
                        dbType
                    ).trim();
                    if (!splitSql.isEmpty()) {
                        list.add(splitSql);
                    }
                }
                lexer.nextToken();
                token = lexer.token;
                start = lexer.startPos;
                continue;
            } else if (token == Token.CREATE) {
                lexer.nextToken();

                if (lexer.token == Token.FUNCTION || lexer.identifierEquals("FUNCTION")) {
                    lexer.nextToken();
                    lexer.nextToken();
                    if (lexer.token == Token.AS) {
                        lexer.nextToken();
                        if (lexer.token == Token.LITERAL_CHARS) {
                            lexer.nextToken();
                            token = lexer.token;
                            continue;
                        }
                    }
                    lexer.startPos = sql.length();
                    break;
                }

                token = lexer.token;
                continue;
            } else if (set && token == Token.EQ && dbType == DbType.odps) {
                lexer.nextTokenForSet();
                token = lexer.token;
                continue;
            } else if (dbType == DbType.odps
                && lexer.ch != '.'
                && (lexer.identifierEquals("pai") || lexer.identifierEquals("jar"))) {
                paiOrJar = true;
            }

            if (lexer.identifierEquals("USING")) {
                lexer.nextToken();
                if (lexer.identifierEquals("jar")) {
                    lexer.nextToken();
                }
            }

            if (lexer.token == Token.SET) {
                set = true;
            }

            if (lexer.identifierEquals("ADD") && (dbType == DbType.hive || dbType == DbType.odps)) {
                lexer.nextToken();
                if (lexer.identifierEquals("JAR")) {
                    lexer.nextPath();
                }
            } else {
                lexer.nextToken();
            }
            token = lexer.token;
        }

        if (start != sql.length() && token != Token.SEMI) {
            int end = lexer.startPos;
            if (end > sql.length()) {
                end = sql.length();
            }
            String splitSql = sql.substring(start, end).trim();
            if (!paiOrJar) {
                splitSql = removeComment(splitSql, dbType).trim();
            } else {
                if (splitSql.endsWith(";")) {
                    splitSql = splitSql.substring(0, splitSql.length() - 1).trim();
                }
            }
            if (!splitSql.isEmpty()) {
                list.add(splitSql);
            }
        }

        return list;
    }

}
