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
    cur.execute('SELECT query from queries')
    stmt = "update queries set finished=0, result=? where id=" + str (sys.argv[2])
    print stmt
    cur.execute(stmt, [lite.Binary(ablob)])

    #cur.execute('select query, finished from queries')
    #data = cur.fetchone()
    #print "SQLite query result: %s" % str(data)

except lite.Error, e:
    print "Error %s:" % e.args[0]
    sys.exit(1)

finally:
    if input_file:
        input_file.close()
    if con:
        con.commit()
        con.close()
