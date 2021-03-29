FROM postgres:latest

COPY docker/*.sql /docker-entrypoint-initdb.d/

#HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD psql -d leagueid -U docker
#HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD psql -d core_tournament -U docker

EXPOSE 5432

CMD ["postgres", "-c", "max_prepared_transactions=100"]
