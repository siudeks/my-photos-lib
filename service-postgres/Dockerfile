# Use the official PostgreSQL image as the base
FROM postgres:16

# Install necessary packages including git
RUN apt-get update && apt-get install -y \
    postgresql-server-dev-15 \
    build-essential \
    git \
    && apt-get clean

# instal ldev package to avoid error
# 0.230 src/bitutils.c:1:10: fatal error: postgres.h: No such file or directory
# https://stackoverflow.com/questions/56724622/how-to-fix-postgres-h-file-not-found-problem
# which appears with poistgres 16
RUN apt install postgresql-server-dev-16


# Clone the pgvector repository
RUN git clone --branch v0.7.4 https://github.com/pgvector/pgvector.git /pgvector

# Build and install pgvector
RUN cd /pgvector && make && make install

# Clean up
RUN rm -rf /pgvector

# copy init script to well-know location. We use such script to add pgvector extension
ADD ./scripts/1_init_pgvector.sql /docker-entrypoint-initdb.d/
RUN chmod a+r /docker-entrypoint-initdb.d/*

# Expose the PostgreSQL port
EXPOSE 5432
