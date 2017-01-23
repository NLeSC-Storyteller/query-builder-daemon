#!/usr/bin/python
# -*- coding: utf-8 -*-

import sqlite3 as lite
import sys

con = None

try:
    input_file = open(str(sys.argv[3]), "rb")
    ablob = input_file.read()
    con = lite.connect(str(sys.argv[1]))
    cur = con.cursor()   
    stmt = "update queries set sstatus=1, result=? where id=" + str (sys.argv[2])
    print stmt
    cur.execute(stmt, [lite.Binary(ablob)])
    if (cur.rowcount == 0):
        stmt = "select count(status) from queries where id = " + str (sys.argv[2])
        cur.execute(stmt)
        rows = cur.fetchall()
        if (len(rows) == 1):
            stmt = "update queries set status=2 where id=" + str (sys.argv[2])
            cur.execute(stmt)
            if (cur.rowcount == 1):
                print "Update status to ERROR for query id " + str (sys.argv[2]) + " succeeded"
            else:
                print "Update status to ERROR for query id " + str (sys.argv[2]) + " failed"
        else:
            err = "Query ID " + str (sys.argv[2]) + " does not exist."
            print err
            stmt = "insert into queries (id, status, result) values(" + str (sys.argv[2]) + ", 2, ?)"
            cur.execute(stmt, [lite.Binary(err)])
    else:
        print "Update status to SUCCEDDED for query id " + str (sys.argv[2]) + " succeeded"

except lite.Error, e:
    stmt = "select status from queries where id = " + str (sys.argv[2])
    cur.execute(stmt)
    rows = cur.fetchall()
    if (len(rows) == 1):
        stmt = "update queries set status=2, result=? where id=" + str (sys.argv[2])
        cur.execute(stmt, [lite.Binary(e.args[0])])
    else:
        stmt = "insert into queries (id, status, result) values(" + str (sys.argv[2]) + ", 2, ?)"
        cur.execute(stmt, [lite.Binary(e.args[0])])
    print "Error %s:" % e.args[0]
    sys.exit(1)

finally:
    if input_file:
        input_file.close()
    if con:
        con.commit()
        con.close()
