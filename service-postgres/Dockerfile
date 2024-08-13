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

# Set environment variables
ENV POSTGRES_USER=your_user
ENV POSTGRES_PASSWORD=your_password
ENV POSTGRES_DB=your_db

# Expose the PostgreSQL port
EXPOSE 5432
