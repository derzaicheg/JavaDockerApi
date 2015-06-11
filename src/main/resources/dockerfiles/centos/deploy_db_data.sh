#!/bin/bash

sudo -u postgres psql -c "alter user postgres with password 'postgres';" >> out.txt
sudo -u postgres psql -c "\i create_database.sql" 