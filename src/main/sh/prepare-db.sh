#!/bin/sh
createdb hh_hibernate_jdbc
createuser -P hh_hibernate_jdbc
psql -U hh_hibernate_jdbc -h localhost < ../resources/create-tables.sql
