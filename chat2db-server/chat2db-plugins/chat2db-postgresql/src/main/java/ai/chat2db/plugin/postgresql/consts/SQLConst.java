package ai.chat2db.plugin.postgresql.consts;

public class SQLConst {
    public static String FUNCTION_SQL =
            """
            CREATE OR REPLACE FUNCTION showcreatetable(namespace character varying, tablename character varying)
                   RETURNS character varying AS

                   $BODY$
                   declare
                   tableScript character varying default '';

                   begin
                   -- columns
                 tableScript := tableScript || ' CREATE TABLE '|| tablename|| ' ( '|| chr(13)||chr(10);
                 
                 IF (
                    SELECT COUNT(*)  FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = namespace AND TABLE_NAME = tablename
                 ) > 0 THEN
                     tableScript := tableScript || array_to_string(
                         array(
                             SELECT ' ' || concat_ws(' ',fieldName, fieldType, defaultValue, isNullStr ) as column_line
                             FROM (
                                 SELECT a.attname as fieldName, format_type(a.atttypid,a.atttypmod) as fieldType, CASE WHEN
                                     (SELECT substring(pg_catalog.pg_get_expr(B.adbin, B.adrelid) for 128)
                                     FROM pg_catalog.pg_attrdef B WHERE B.adrelid = A.attrelid AND B.adnum = A.attnum AND A.atthasdef) IS NOT NULL THEN
                                     'DEFAULT '|| (SELECT substring(pg_catalog.pg_get_expr(B.adbin, B.adrelid) for 128)
                                                     FROM pg_catalog.pg_attrdef B WHERE B.adrelid = A.attrelid AND B.adnum = A.attnum AND A.atthasdef)
                                     ELSE
                                     ''
                                     END as defaultValue, (case when a.attnotnull=true then 'not null' else 'null' end) as isNullStr
                                 FROM pg_attribute a
                                 WHERE attstattarget=-1
                                 AND attrelid = (
                                     SELECT c.oid
                                     FROM pg_class c, pg_namespace n
                                     WHERE c.relnamespace=n.oid
                                     AND n.nspname =namespace
                                     AND relname =tablename
                                 )
                             ) as string_columns
                         ),','||chr(13)||chr(10)) || ',';
                 END IF;
                 
                   
                   -- 约束
                   tableScript:= tableScript || chr(13)||chr(10) || array_to_string(
                   array(
                   select concat(' CONSTRAINT ',conname ,c ,u,p,f) from (
                   select conname,
                   case when contype='c' then ' CHECK('|| ( select findattname(namespace,tablename,'c') ) ||')' end as c ,
                   case when contype='u' then ' UNIQUE('|| ( select findattname(namespace,tablename,'u') ) ||')' end as u ,
                   case when contype='p' then ' PRIMARY KEY ('|| ( select findattname(namespace,tablename,'p') ) ||')' end as p ,
                   case when contype='f' then ' FOREIGN KEY('|| ( select findattname(namespace,tablename,'u') ) ||') REFERENCES '||
                   (select p.relname from pg_class p where p.oid=c.confrelid ) || '('|| ( select
                   findattname(namespace,tablename,'u') ) ||')' end as f
                   from pg_constraint c
                   where contype in('u','c','f','p') and conrelid=(
                   select oid from pg_class where relname=tablename and relnamespace =(
                   select oid from pg_namespace where nspname = namespace
                   )
                   )
                   ) as t
                   ) ,',' || chr(13)||chr(10) ) || chr(13)||chr(10) ||' ); ';

                   -- indexs
                   -- CREATE UNIQUE INDEX pg_language_oid_index ON pg_language USING btree (oid); -- table pg_language


                   --
                   /** **/
                   --- 获取非约束索引 column
                   -- CREATE UNIQUE INDEX pg_language_oid_index ON pg_language USING btree (oid); -- table pg_language
                   tableScript:= tableScript || chr(13)||chr(10) || chr(13)||chr(10) || array_to_string(
                   array(
                   select 'CREATE INDEX ' || indexrelname || ' ON ' || tablename || ' USING btree '|| '(' || attname || ');' from (
                   SELECT
                   i.relname AS indexrelname , x.indkey,

                   ( select array_to_string (
                   array(
                   select a.attname from pg_attribute a where attrelid=c.oid and a.attnum in ( select unnest(x.indkey) )

                   )
                   ,',' ) )as attname

                   FROM pg_class c
                   JOIN pg_index x ON c.oid = x.indrelid
                   JOIN pg_class i ON i.oid = x.indexrelid
                   LEFT JOIN pg_namespace n ON n.oid = c.relnamespace
                   WHERE c.relname=tablename and i.relname not in
                   ( select constraint_name from information_schema.key_column_usage where table_name=tablename )
                   )as t
                   ) ,','|| chr(13)||chr(10));


                   -- COMMENT COMMENT ON COLUMN sys_activity.id IS '主键';
                   tableScript:= tableScript || chr(13)||chr(10) || chr(13)||chr(10) || array_to_string(
                   array(
                   SELECT 'COMMENT ON COLUMN ' || 'namespace.tablename' || '.' || a.attname ||' IS '|| ''''|| d.description ||''''
                   FROM pg_class c
                   JOIN pg_description d ON c.oid=d.objoid
                   JOIN pg_attribute a ON c.oid = a.attrelid
                   WHERE c.relname=tablename
                   AND a.attnum = d.objsubid),';'|| chr(13)||chr(10)) ;

                   return tableScript;

                   end
                   $BODY$ LANGUAGE plpgsql;

                   CREATE OR REPLACE FUNCTION findattname(namespace character varying, tablename character varying, ctype character
                   varying)
                   RETURNS character varying as $BODY$

                   declare
                   tt oid ;
                   aname character varying default '';

                   begin
                   tt := oid from pg_class where relname= tablename and relnamespace =(select oid from pg_namespace where
                   nspname=namespace) ;
                   aname:= array_to_string(
                   array(
                   select a.attname from pg_attribute a
                   where a.attrelid=tt and a.attnum in (
                   select unnest(conkey) from pg_constraint c where contype=ctype
                   and conrelid=tt and array_to_string(conkey,',') is not null
                   )
                   ),',');

                   return aname;
                   end
                   $BODY$ LANGUAGE plpgsql""".indent(1);
}
