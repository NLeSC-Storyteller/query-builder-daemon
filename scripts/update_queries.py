#!/usr/bin/python
# -*- coding: utf-8 -*-

"""
Update the SQLITE table queries with the returned JSON file from the Xenon command.
"""

import sqlite3 as lite
import sys

def update_table(db_name, query_id, input_file_name):
    """Update the SQLITE table queries with the returned JSON file from the Xenon command."""
    con = None
    try:
        input_file = open(str(input_file_name), "rb")
        ablob = input_file.read()
        con = lite.connect(str(db_name))
        cur = con.cursor()
        stmt = "update queries set status=1, result=? where id=" + str(query_id)
        print(stmt)
        cur.execute(stmt, [ablob])
        if cur.rowcount == 0:
            stmt = "select count(status) from queries where id = " + str(query_id)
            cur.execute(stmt)
            rows = cur.fetchall()
            if len(rows) == 1:
                stmt = "update queries set status=2 where id=" + str(query_id)
                cur.execute(stmt)
                if cur.rowcount == 1:
                    print("Update status to ERROR for query id " + str(query_id) + " succeeded")
                else:
                    print("Update status to ERROR for query id " + str(query_id) + " failed")
            else:
                err = "Query ID " + str(query_id) + " does not exist."
                print(err)
                stmt = """insert into queries (id, status, result)
                        values(""" + str(query_id) + """, 2, ?)"""
                cur.execute(stmt, [err])
        else:
            print("Update status to SUCCEEDED for query id " + str(query_id) + " succeeded")

    except lite.Error as err:
        stmt = "select status from queries where id = " + str(query_id)
        cur.execute(stmt)
        rows = cur.fetchall()
        if len(rows) == 1:
            stmt = "update queries set status=2, result=? where id=" + str(query_id)
            cur.execute(stmt, [err.args[0]])
        else:
            stmt = "insert into queries (id, status, result) values(" + str(query_id) + ", 2, ?)"
            cur.execute(stmt, [err.args[0]])
        print("Error %s:" % err.args[0])
        sys.exit(1)

    finally:
        if input_file:
            input_file.close()
        if con:
            con.commit()
            con.close()

update_table(sys.argv[1], sys.argv[2], sys.argv[3])
