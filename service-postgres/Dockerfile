# Use the official PostgreSQL image as the base
FROM postgres:15

# Install necessary packages including git
RUN apt-get update && apt-get install -y \
    postgresql-server-dev-15 \
    build-essential \
    git \
    && apt-get clean

# Clone the pgvector repository
RUN git clone https://github.com/pgvector/pgvector.git /pgvector

# Build and install pgvector
RUN cd /pgvector && make && make install

# Clean up
RUN rm -rf /pgvector

# copy init script to well-know location. We use such script to add pgvector extension
ADD ./scripts/1_init_pgvector.sql /docker-entrypoint-initdb.d/
RUN chmod a+r /docker-entrypoint-initdb.d/*

# Expose the PostgreSQL port
EXPOSE 5432
