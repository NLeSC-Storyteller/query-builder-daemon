#!/usr/bin/python
# -*- coding: utf-8 -*-

"""
Clear the SQLITE table queries of all queries.
"""

import sqlite3 as lite
import sys

def clear_from_table(db_name, username):
    """Clear the SQLITE table queries of all queries."""
    con = None
    try:
        con = lite.connect(str(db_name))
        cur = con.cursor()
        stmt = "DELETE FROM queries WHERE username=" + str(username)
        print(stmt)
        cur.execute(stmt)

    except lite.Error as err:
        print("Error %s:" % err.args[0])
        sys.exit(1)

    finally:
        if con:
            con.commit()
            con.close()

clear_from_table(sys.argv[1], sys.argv[2])
