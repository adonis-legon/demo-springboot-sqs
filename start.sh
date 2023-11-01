LOCAL_ENVIRONMENT=${1:-dev}

. local-env-$LOCAL_ENVIRONMENT.sh

docker-compose up -d
docker-compose logs -f