#!/bin/bash

sudo -u postgres psql -c "alter user postgres with password 'postgres';" >> /home/out.txt
#psql -c "alter user postgres with password 'postgres';"
#sudo -u postgres psql -c "\i create_database.sql"

#echo "hello world!" >> /home/out.txt 